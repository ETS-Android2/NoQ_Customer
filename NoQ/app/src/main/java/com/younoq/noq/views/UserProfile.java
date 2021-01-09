package com.younoq.noq.views;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.views.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class UserProfile extends AppCompatActivity {

    TextView tv_im_name, tv_name, tv_email, tv_referral_amt, tv_total_savings, tv_phone_no,
            tv_app_version;
    SaveInfoLocally saveInfoLocally;
    private JSONObject jobj1, jobj2;
    private JSONArray jsonArray;

    public final String TAG = "NoQStores";
    public String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tv_im_name = findViewById(R.id.ns_tv_name);
        tv_app_version = findViewById(R.id.ns_app_version);
        tv_name = findViewById(R.id.ns_username);
        /* tv_total_savings = findViewById(R.id.ns_total_savings); */
        tv_referral_amt = findViewById(R.id.ns_referral_amt);
        tv_phone_no = findViewById(R.id.ns_phone_no);
        tv_email = findViewById(R.id.ns_email);

        saveInfoLocally = new SaveInfoLocally(this);

        fetch_referral_amt();
        /* Fetching the User Details */
        fetch_User_Details();

    }

    public void fetch_User_Details() {

        final String ref_bal = saveInfoLocally.getReferralBalance();
        final String bal = "₹"+ref_bal;
        tv_email.setText(saveInfoLocally.getEmail());
        tv_phone_no.setText(saveInfoLocally.getPhone());
        tv_referral_amt.setText(bal);

        final String uname = saveInfoLocally.getUserName();
        tv_name.setText(uname);

        final String[] name_credentials = uname.split(" ", 2);
        String na;
        if (name_credentials.length >= 2) {
            final String f = name_credentials[0];
            final String l = name_credentials[1];
            na = String.valueOf(f.charAt(0)) + l.charAt(0);
        } else {
            final String f = name_credentials[0];
            na = String.valueOf(f.charAt(0));
        }

        tv_im_name.setText(na);

        try {

            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = "v"+ pInfo.versionName;
            tv_app_version.setText(version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void fetch_referral_amt(){

        final String type = "retrieve_referral_amt";
        final String phone = saveInfoLocally.getPhone();
        try {

            final String res = new AwsBackgroundWorker(this).execute(type, phone).get();
            String ref_bal;
            try
            {
                jsonArray = new JSONArray(res);
                jobj1 = jsonArray.getJSONObject(0);
                if(!jobj1.getBoolean("error")){
                    jobj2 = jsonArray.getJSONObject(1);
                    ref_bal = jobj2.getString("referral_balance");
                    Log.d(TAG, "Referral Amount Balance : "+ref_bal);
                    /* Saving the Referral_Amount_Balance to SharedPreferences to be used in CartActivity */
                    saveInfoLocally.setReferralBalance(ref_bal);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) {
        super.onBackPressed();
    }

    public void Logout(View view) {

        final String type = "set_logout_flag";
        final String phone = saveInfoLocally.getPhone();
        try {
            final String res = new BackgroundWorker(this).execute(type, phone, "True").get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        saveInfoLocally.clear_all();
        saveInfoLocally.setPrevPhone(phone);
        saveInfoLocally.setHasFinishedIntro();
        Intent in = new Intent(this, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }
}
