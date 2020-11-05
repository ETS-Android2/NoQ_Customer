package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.service.autofill.SaveInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.younoq.noq.R;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.SaveInfoLocally;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ReferralSuccessfulActivity extends AppCompatActivity {

//    AnimationDrawable success_disp;
//    ImageView img;
    Button btn;

    public static final String Type = "com.example.noq_1.TYPE";

    public static final String NAME = "com.example.noq_1.NAME";
    private Logger logger;

    private SaveInfoLocally saveInfoLocally;
    private Dialog bonus_dialog;
    private ImageView im_bd_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_successful);

//        img = findViewById(R.id.image);
//        img.setBackgroundResource(R.drawable.animation);
//        success_disp = (AnimationDrawable)img.getBackground();
        btn = findViewById(R.id.btn_succ);
        logger = new Logger(this);

//        success_disp.start();

        Intent in = getIntent();
        final String name = in.getStringExtra(UserCredentialsActivity.Name);
        final String phone = in.getStringExtra("Phone");
        final String TAG = "ReferralSuccessful";
        Log.d(TAG, "Phone No in ReferralSuccessfulActivity : "+phone);
        final String email = in.getStringExtra(UserCredentialsActivity.Email);

        // Storing Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Values in getIntent -> name : "+name+", phone : "+phone+", email : "+email+"\n");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(ReferralSuccessfulActivity.this, NoqStores.class);
                Intent in = new Intent(ReferralSuccessfulActivity.this, Covid19.class);
                in.putExtra("Phone", phone);
                in.putExtra("activity", "UCA");
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // Storing Logs in the Logger.
                logger.writeLog(TAG, "onCreate()","Values in Intent to Covid19 Activity -> phone : "+phone+"\n");
                startActivity(in);

            }
        });

        saveInfoLocally = new SaveInfoLocally(this);

        bonus_dialog = new Dialog(this);

        showBonusDialog();

        // Setting the FirstLoginStatus to false.
        saveInfoLocally.setFirstLoginStatus(false);

    }

    private void showBonusDialog() {

        bonus_dialog.setContentView(R.layout.bonus_amt_dialog);

        im_bd_exit = bonus_dialog.findViewById(R.id.bad_close);

        im_bd_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bonus_dialog.dismiss();
            }
        });

        bonus_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bonus_dialog.show();

    }

}
