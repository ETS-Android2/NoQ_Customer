package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.SaveInfoLocally;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class PaymentSuccess extends AppCompatActivity {

    private SaveInfoLocally saveInfoLocally;
    private TextView tv1, tv_receipt_no, tv_order_type, tv_final_amt, tv_you_saved, tv_shop_details, tv_timestamp, tv_total_items, tv_pay_method, tv_thanks;
    private String ref_bal_used, delivery_dur, calc_referral_bal;
    private int delivery_duration;
    private DBHelper db;
    private Bundle txnReceipt;
    private ArrayList<String> txnData;
//    ReceiptAdapter receiptAdapter;
//    RecyclerView recyclerView;
    SimpleDateFormat inputDateFormat, outputDateFormat, timeFormat;
    private String TAG = "PaymentSuccess Activity";
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        saveInfoLocally = new SaveInfoLocally(this);

        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        outputDateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        tv1 = findViewById(R.id.tv_succ);
        tv_receipt_no = findViewById(R.id.ps_receipt_no);
//        tv_ref_amt = findViewById(R.id.ps_referral_amt);
//        tv_retailers_price = findViewById(R.id.ps_retailer_price);
        tv_you_saved = findViewById(R.id.ps_you_saved);
        tv_final_amt = findViewById(R.id.ps_final_amt);
        tv_shop_details = findViewById(R.id.ps_shop_name);
        tv_timestamp = findViewById(R.id.ps_timestamp);
        tv_total_items = findViewById(R.id.ps_total_items);
        tv_pay_method = findViewById(R.id.ps_pay_method);
        tv_order_type = findViewById(R.id.ps_order_type);
        tv_thanks = findViewById(R.id.tv_p_thanks);
        db = new DBHelper(this);
        txnReceipt = new Bundle();
        txnData = new ArrayList<>();
        logger = new Logger(this);

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","onCreate() method called.\n");

//        recyclerView = findViewById(R.id.ps_recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent in = getIntent();
        calc_referral_bal = in.getStringExtra("calc_referral_balance");
        ref_bal_used = in.getStringExtra("referral_balance_used");
        txnReceipt = in.getExtras();
        txnData = txnReceipt.getStringArrayList("txnReceipt");
        // Pushing Updates to DB/
        pushUpdatesToDatabase();

        showPaymentDetails();

        // Deleting all the Products from the Database.
        db.Delete_all_rows();
        db.close();

    }

    public void pushUpdatesToDatabase() {

        final String phone = saveInfoLocally.getPhone();
        try {
            // Pushing the Referral_Amount_Used to Users_Table.
            final String type = "update_referral_used";
            final String res = new AwsBackgroundWorker(this).execute(type, phone, ref_bal_used).get();
            // Removing the Extra Space from the Fetched Result.
            final String check = res.trim();
            if(!check.equals("FALSE")) {
                // Now, when the Referral_Amount_Used is updated, now we can calculate the Referral_Amount_Balance.
                final String type1 = "update_referral_balance";
                final String res1 = new AwsBackgroundWorker(this).execute(type1, phone).get();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showPaymentDetails() {

        // Resetting the TotalItemsInCart in SharedPreferences.
        saveInfoLocally.setTotalItemsInCart(0);

        // Setting the Updated Referral_Balance to SharedPreferences.
        saveInfoLocally.setReferralBalance(String.valueOf(calc_referral_bal));

        final String sid = saveInfoLocally.get_store_id();
        if (sid.equals("3")) {
            tv1.setText(R.string.ps_school);
            tv1.setTextSize(20);
        }

        // Setting TxnDetails.
        final String store_addr = saveInfoLocally.getStoreName() + ", " + saveInfoLocally.getStoreAddress();
        tv_shop_details.setText(store_addr);

        final String shoppingMethod = saveInfoLocally.getShoppingMethod();
        if(shoppingMethod.equals("InStore"))
            tv_order_type.setText(R.string.ps_in_store);
        else if(shoppingMethod.equals("Takeaway")){

            if (!sid.equals("3")){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_thanks.setText(R.string.ps_takeaway_desc);
                    }
                }, 2000);
            }
            tv_order_type.setText(shoppingMethod);

        } else if(shoppingMethod.equals("HomeDelivery")){

            // Retrieving the Delivery Duration.
            delivery_duration = saveInfoLocally.getStoreDeliveryDuration();
            Log.d(TAG, "Delivery Duration : "+delivery_duration);
            String timeUnit = "";
            int delivery_time_hours = 0, delivery_time_mins = 0;
            delivery_dur = "Delivery in ";

            if(delivery_duration >= 0 && delivery_duration < 60){

                delivery_dur += delivery_duration + " mins";

            }
            else if(delivery_duration >= 60){

                delivery_time_hours = delivery_duration / 60;
                delivery_time_mins = delivery_duration % 60;

                if(delivery_time_mins == 0){
                    delivery_dur += delivery_time_hours + " hour";
                } else {
                    delivery_dur += delivery_time_hours + " hr " + delivery_time_mins + " mins";
                }
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_thanks.setText(delivery_dur);
                }
            }, 2000);
            tv_order_type.setText(R.string.ps_home_delivery);

        }

        final String time = txnData.get(6);
        try {
            Date date = inputDateFormat.parse(time);

            final String timestamp = outputDateFormat.format(date) + ", " + timeFormat.format(date);
            tv_timestamp.setText(timestamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_receipt_no.setText(txnData.get(0));

        // Total Savings = Total_Discount + Referral_Balance_Used.
        String savings_by_us = "₹ " + (Double.parseDouble(txnData.get(1)) + Double.parseDouble(txnData.get(3)));
        tv_you_saved.setText(savings_by_us);

        final String final_amt = "₹" + txnData.get(5);
        tv_final_amt.setText(final_amt);

        String pay_method = txnData.get(7);
        if(pay_method.equals("[Referral_Used]"))
            pay_method = "Bonus";
        tv_pay_method.setText(pay_method);

        tv_total_items.setText(txnData.get(8));

    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentSuccess.this, MyProfile.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", false);
        startActivity(in);
    }

    public void Go_to_Shop_Type(View view) {
        // Generating new Session ID, if User Clicks on Continue button
        final String phone = saveInfoLocally.getPhone();
        try {
            final String sess = toHexString(getSHA(getRandomString()+phone+getRandomString()));
            Log.d(TAG, "Session Id : "+sess);
            saveInfoLocally.setSessionID(sess);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Retrieving the Store Shopping methods related Info, from SharedPreferences.
        final boolean in_store = saveInfoLocally.getIs_InStore();
        final boolean takeaway = saveInfoLocally.getIs_Takeaway();
        final boolean home_delivery = saveInfoLocally.getIs_Home_Delivery();

        Intent in = new Intent(PaymentSuccess.this, ChooseShopType.class);
        in.putExtra("in_store", in_store);
        in.putExtra("takeaway", takeaway);
        in.putExtra("home_delivery", home_delivery);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentSuccess.this, MyProfile.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", false);
        startActivity(in);
    }

    public void lastfivetxns(View view) {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, LastFiveTxns.class);
        in.putExtra("Phone", phone);
        startActivity(in);

    }

    private String getRandomString(){
        int n = 4;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private byte[] getSHA(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(str.getBytes(StandardCharsets.UTF_8));
    }

    private String toHexString(byte[] strHash){
        BigInteger num = new BigInteger(1, strHash);
        StringBuilder hexString = new StringBuilder(num.toString(16));
        while(hexString.length() < 32){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
