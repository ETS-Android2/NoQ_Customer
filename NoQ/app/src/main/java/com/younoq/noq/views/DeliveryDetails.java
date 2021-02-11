package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.textfield.TextInputLayout;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.younoq.noq.R;
import com.younoq.noq.models.Api;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.PChecksum;
import com.younoq.noq.models.PConstants;
import com.younoq.noq.models.Paytm;
import com.younoq.noq.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on 31/05/20.
 */

public class DeliveryDetails extends AppCompatActivity {

    private TextView tv_user_address, tv_delivery_duration, tv_total_amt, tv_ref_amt, tv_delivery_charges, tv_final_amt, tv_discounted_amt, tv_max_delivery_charge;
    private String total_amt, ref_amt, user_addr, txnAmount, tot_retailer_price, tot_our_price, total_discount, total_mrp;
    private String category_name, shoppingMethod, coming_from;
    private int min_charge, max_charge, delivery_charge = 0, item_qty = 0, delivery_duration;
    private double final_amt, ref_bal_used;
    private SaveInfoLocally saveInfoLocally;
    private String TAG = "DeliveryDetails";
    private JSONArray jsonArray;
    private JSONObject jobj1, jobj2;
    private Bundle txnReceipt;
    private ArrayList<String> txnData;
    private TextInputLayout edit_address;
    private ProgressBar progressBar;
    private LinearLayout free_homeDelivery_linearlayout;
    private Button btn_payment;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        tv_user_address = findViewById(R.id.dd_user_address);
        tv_delivery_duration = findViewById(R.id.dd_delivery_time);
        tv_total_amt = findViewById(R.id.dd_tot_amt);
        tv_ref_amt = findViewById(R.id.dd_ref_bal);
        tv_delivery_charges = findViewById(R.id.dd_delivery_charges);
        tv_final_amt = findViewById(R.id.dd_final_amt);
        edit_address = findViewById(R.id.dd_edit_address);
        tv_discounted_amt = findViewById(R.id.dd_discounted_amt);
        free_homeDelivery_linearlayout = findViewById(R.id.dd_free_hd_linearlayout);
        tv_max_delivery_charge = findViewById(R.id.dd_max_charge);
        btn_payment = findViewById(R.id.dd_payment_button);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        txnReceipt = new Bundle();
        txnData = new ArrayList<>();
        logger = new Logger(this);

        saveInfoLocally = new SaveInfoLocally(this);

        edit_address.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Setting the User Address TextView to be Editable */
                tv_user_address.setEnabled(true);
                edit_address.setEndIconVisible(false);
            }
        });

        tv_user_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        /* the user is done typing. */
                        Log.d(TAG, "User has Finished Typing");
                        edit_address.setEndIconVisible(true);
                        edit_address.setEndIconDrawable(R.drawable.icon_check);
                        v.setEnabled(false);
                        return true;
                    }
                }
                return false;
            }
        });

        Intent in = getIntent();
        total_amt = in.getStringExtra("total_amt");
        tot_retailer_price = in.getStringExtra("total_retailer_price");
        tot_our_price = in.getStringExtra("total_our_price");
        total_mrp = in.getStringExtra("total_mrp");
        total_discount = in.getStringExtra("total_discount");
        item_qty = in.getIntExtra("total_items", 0);
        shoppingMethod = in.getStringExtra("shoppingMethod");
        category_name = in.getStringExtra("category_name");
        coming_from = in.getStringExtra("coming_from");

        showDeliveryDetails();

    }

    public void AddMore(View view) {

        Intent in;
        /* If coming from Products Category Screen then, go back there. */
        if(coming_from.equals("ProductCategory")){
            in  = new Intent(DeliveryDetails.this, ProductsCategory.class);
        } else {
            in  = new Intent(DeliveryDetails.this, ProductsList.class);
            in.putExtra("coming_from", "Cart");
            in.putExtra("category_name", category_name);
        }
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);

    }

    void showDeliveryDetails() {

        /* Setting the values for the UI elements */
        user_addr = saveInfoLocally.getUserAddress();
        tv_user_address.setText(user_addr);

        final String temp = "₹" + total_amt;
        tv_total_amt.setText(temp);

        /* Retrieving the Delivery Duration. */
        delivery_duration = saveInfoLocally.getStoreDeliveryDuration();
        Log.d(TAG, "Delivery Duration : "+delivery_duration);
        String timeUnit = "";
        int delivery_time_hours = 0, delivery_time_mins = 0;
        String delivery_dur = "Delivery in ";

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
        Log.d(TAG, delivery_dur);

        tv_delivery_duration.setText(delivery_dur);

        ref_amt = saveInfoLocally.getReferralBalance();
        final String ref_text = "- ₹"+ref_amt;
        tv_ref_amt.setText(ref_text);

        /* Calculating the Amount to be Paid */
        final double tot_amt = Double.parseDouble(total_amt);
        final double ref_bal = Double.parseDouble(ref_amt);
        Log.d(TAG, "Referral Bal : "+ref_bal+", Total Amt : "+tot_amt);
        if(ref_bal >= tot_amt){
            final_amt = 0;
            ref_bal_used = tot_amt;
        } else {
            final_amt = tot_amt - ref_bal;
            ref_bal_used = ref_bal;
        }

        Log.d(TAG, "Referral Bal Used : "+ref_bal_used);

        final String tpm = "₹" + final_amt;
        tv_discounted_amt.setText(tpm);

        /* Retrieving the Delivery related info from the SharedPreferences. */
        delivery_charge = 0;
        min_charge = saveInfoLocally.getMinCharge();
        max_charge = saveInfoLocally.getMaxCharge();

        final String tpm1 = "₹" + max_charge;
        tv_max_delivery_charge.setText(tpm1);

        /* Checking the Delivery Charge applicable on the Order */
        if(tot_amt >= min_charge && tot_amt < max_charge){

            delivery_charge = saveInfoLocally.getDeliveryCharge();

        }
        /* Checking if the Order Amount is greater than Max Delivery Charge. */
        if(tot_amt >= max_charge) {
            free_homeDelivery_linearlayout.setVisibility(View.GONE);
        } else {
            free_homeDelivery_linearlayout.setVisibility(View.VISIBLE);
        }

        final String tmp = "+ ₹" + delivery_charge;
        tv_delivery_charges.setText(tmp);

        final_amt += delivery_charge;
        final String fin_amt = "₹" + final_amt;
        tv_final_amt.setText(fin_amt);

        if (final_amt > 0)
            btn_payment.setText(R.string.cont);
        else if (final_amt == 0)
            btn_payment.setText(R.string.payment);

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, CartActivity.class);
        if(coming_from.equals("ProductCategory")){
            in.putExtra("comingFrom", "ProductCategory");
        } else {
            in.putExtra("comingFrom", "Cart");
        }
        in.putExtra("shoppingMethod", shoppingMethod);
        in.putExtra("category_name", category_name);
        startActivity(in);
    }

    private String generateTxn_Order() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void Make_Payment(View view) {

        Double b;
        /* Value of Referral_Balance */
        b = Double.valueOf(ref_amt);

        final String entered_addr = tv_user_address.getText().toString().trim();

        /* Retrieving the Address Entered by the User */
        if(!entered_addr.equals("")){

            /* Showing the Progressbar. */
            progressBar.setVisibility(View.VISIBLE);
            final String phone = saveInfoLocally.getPhone();

            try {

                final String type = "update_address";
                new AwsBackgroundWorker(this).execute(type, phone, entered_addr).get();

                Toast.makeText(this, "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                /* Saving the User's Address in SharedPreferences. */
                saveInfoLocally.setUserAddress(entered_addr);

                progressBar.setVisibility(View.GONE);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {

            tv_user_address.setError("Please enter an address");

        }

        /* Calculating the Referral_balance to be Stored in SharedPreference. */
        final Double cal_ref_bal = b - ref_bal_used;
        Log.d(TAG, "Updated Referral Amount : "+cal_ref_bal);

        if (final_amt > 0) {

            Intent in = new Intent(this, PaymentMethod.class);

            /* Retrieving the Final Amt from the UI. */
            String f_amt  = tv_final_amt.getText().toString();
            f_amt = f_amt.replace("₹", "");

            in.putExtra("total_amt", f_amt);
            in.putExtra("total_retailer_price", tot_retailer_price);
            in.putExtra("total_mrp", total_mrp);
            in.putExtra("total_discount", total_discount);
            in.putExtra("total_our_price", tot_our_price);
            in.putExtra("total_items", item_qty);
            in.putExtra("category_name", category_name);
            in.putExtra("shoppingMethod", shoppingMethod);
            in.putExtra("coming_from", coming_from);
            in.putExtra("referral_bal_used", String.valueOf(ref_bal_used));
            in.putExtra("referral_bal", String.valueOf(cal_ref_bal));
            in.putExtra("delivery_charge", String.valueOf(delivery_charge));

            startActivity(in);

        } else if (final_amt == 0) {

            /* Setting the Updated Referral_Balance to SharedPreferences. */
            saveInfoLocally.setReferralBalance(String.valueOf(cal_ref_bal));
            /* Setting txnAmount's value to final_amt. */
            txnAmount = String.valueOf(final_amt);
            /* Doing all the things to be done after Successful Payment(which is already done here :-)..) */
            afterPaymentConfirm(String.valueOf(ref_bal_used), generateTxn_Order(), generateTxn_Order(), "[Referral_Used]");
            /* Redirect to Payment Successful Page. */
            Log.d(TAG, "Sending the User to PaymentSuccess Activity");
            Intent in = new Intent(this, PaymentSuccess.class);
            in.putExtra("referral_balance_used", String.valueOf(ref_bal_used));
            /* As the Order_Type could be In_Store or Takeaway. */
            in.putExtra("delivery_charge", String.valueOf(delivery_charge));
            in.putExtras(txnReceipt);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            /* Make the Progressbar Invisible */
            progressBar.setVisibility(View.GONE);
            startActivity(in);

        }

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

        /* Retrieving the User_Phone_NO from SharedPreferences. */
        final String user_phone_no = saveInfoLocally.getPhone();

        try {
            /* Now Inserting the Products list into the Basket_Table. */
            final String type = "Store_Basket";
            final String res = new BackgroundWorker(this).execute(type, user_phone_no).get();
            /* Now Inserting the Transaction Details into the Invoice_Table. */
            final String type3 = "Store_Invoice";

            /* TODO:// Add Code to fetch comments when Store_ID = "3", for now its ""; */
            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, total_mrp, total_discount, txnAmount, ref_bal_used, "", Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "afterPaymentConfirm()","Result Back from Invoice Table : " + rest +  "\n");

            /* Verifying if the Push to Basket_Table was Successful or not. */
            boolean b = Boolean.parseBoolean(res.trim());
            if (b) {

                /* Verifying if the Push to Invoice Table was Successful or not. */
                rest = rest.trim();
                if (!rest.equals("FALSE")) {
                    /* Retrieve the details from the result of the Invoice Push. */
                    String receipt_no = "";
                    String final_user_amt = "";
                    String tot_retail_price = "";
                    String to_our_price = "";
                    String tot_discount = "";
                    String time = "";
                    String comment = "";
                    try {
                        jsonArray = new JSONArray(rest);
                        jobj1 = jsonArray.getJSONObject(0);
                        if (!jobj1.getBoolean("error")) {
                            jobj2 = jsonArray.getJSONObject(1);
                            receipt_no = jobj2.getString("r_no");
                            final_user_amt = jobj2.getString("final_amt");
                            tot_retail_price = jobj2.getString("tot_retail_price");
                            to_our_price = jobj2.getString("tot_our_price");
                            tot_discount = jobj2.getString("tot_discount");
                            time = jobj2.getString("time");
                            Log.d(TAG, "Time : "+time);
                            comment = jobj2.getString("comment");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /* As the Order_Type is HomeDelivery. */
                    final String d_charge = String.valueOf(delivery_charge);
                    /* If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user. */
                    final String type4 = "Send_Invoice_Msg";
                    final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, receipt_no, tot_retail_price, ref_bal_used, to_our_price, d_charge).get();

                    /* Checking if the shoppingMethod is whether Takeaway or Home_Delivery. */
                    if(shoppingMethod.equals("Takeaway") || shoppingMethod.equals("HomeDelivery")){
                        /* Then we have to send the Invoice_Msg to the Retailer also. */
                        Log.d(TAG, "Sending Retailer Invoice Sms");
                        final String type5 = "Send_Retailer_Invoice_Msg";
                        final String sms = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, receipt_no, tot_retail_price, ref_bal_used, to_our_price, d_charge).get();
                    }

                    /* Storing the Details in txnData ArrayList. */
                    txnData.add(receipt_no);
                    txnData.add(tot_discount);
                    txnData.add(tot_retail_price);
                    txnData.add(ref_bal_used);
                    txnData.add(to_our_price);
                    txnData.add(final_user_amt);
                    txnData.add(time);
                    txnData.add(Pay_Mode);
                    txnData.add(String.valueOf(item_qty));
                    /* Adding the txnData ArrayList to txnReceipt Bundle. */
                    txnReceipt.putStringArrayList("txnReceipt", txnData);
                    Log.d(TAG, "Stored Required Details in Bundle");

                }

            }
        } catch (NullPointerException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}