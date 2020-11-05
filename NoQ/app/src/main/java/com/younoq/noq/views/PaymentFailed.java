package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.younoq.noq.R;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.models.SaveInfoLocally;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class PaymentFailed extends AppCompatActivity {

    TextView tv1, tv_receipt_no, tv_final_amt, tv_you_saved, tv_shop_details, tv_timestamp, tv_total_items, tv_pay_method, tv_order_type;
    SaveInfoLocally saveInfoLocally;
    DBHelper dbHelper;
    Bundle txnReceipt;
    ArrayList<String> txnData;
    private String ref_bal_used;
    SimpleDateFormat inputDateFormat, outputDateFormat, timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_receipt_no = findViewById(R.id.pf_receipt_no);
        tv_you_saved = findViewById(R.id.pf_you_saved);
        tv_final_amt = findViewById(R.id.pf_final_amt);
        tv_shop_details = findViewById(R.id.pf_shop_name);
        tv_timestamp = findViewById(R.id.pf_timestamp);
        tv_total_items = findViewById(R.id.pf_total_items);
        tv_pay_method = findViewById(R.id.pf_pay_method);
        tv_order_type = findViewById(R.id.pf_order_type);
        txnReceipt = new Bundle();
        txnData = new ArrayList<>();
        inputDateFormat = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);
        outputDateFormat = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        Intent in = getIntent();
        ref_bal_used = in.getStringExtra("referral_balance_used");
        txnReceipt = in.getExtras();
        txnData = txnReceipt.getStringArrayList("txnReceipt");

        setPaymentDetails();

    }

    void setPaymentDetails() {

        // Setting TxnDetails.
        final String store_addr = saveInfoLocally.getStoreName() + ", " + saveInfoLocally.getStoreAddress();
        tv_shop_details.setText(store_addr);

        final String time = txnData.get(6);
        try {
            Date date = inputDateFormat.parse(time);

            final String timestamp = outputDateFormat.format(date) + ", " + timeFormat.format(date);
            tv_timestamp.setText(timestamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String shoppingMethod = saveInfoLocally.getShoppingMethod();
        if(shoppingMethod.equals("InStore"))
            tv_order_type.setText(R.string.ps_in_store);
        else if(shoppingMethod.equals("Takeaway"))
            tv_order_type.setText(shoppingMethod);
        else if(shoppingMethod.equals("HomeDelivery"))
            tv_order_type.setText(R.string.ps_home_delivery);

        tv_receipt_no.setText(txnData.get(0));
//        final String ref_amt = "₹" + txnData.get(3);
//        tv_ref_amt.setText(ref_amt);
        final String retail_price = "₹" + txnData.get(2);
//        tv_retailers_price.setText(retail_price);
        final String savings_by_us = "₹ " + (Double.parseDouble(txnData.get(2)) - Double.parseDouble(txnData.get(5)));
        tv_you_saved.setText(savings_by_us);
        final String final_amt = "₹" + txnData.get(5);
        tv_final_amt.setText(final_amt);

        String pay_method = txnData.get(7);
        if(pay_method.equals("[Referral_Used]"))
            pay_method = "Bonus";
        tv_pay_method.setText(pay_method);
        tv_total_items.setText(txnData.get(8));

    }

    public void Go_to_Cart(View view) {
        Intent in = new Intent(this, CartActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    public void Go_to_Profile(View view) {

        dbHelper = new DBHelper(this);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Do you want to Exit the In Store Shopping?")
                .setMessage(R.string.bs_exit_in_store_msg)
                .setPositiveButton(R.string.bs_exit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Deleting all the Products stored in the Local Database.
                        dbHelper.Delete_all_rows();
                        // Retrieving the Store Shopping methods related Info, from SharedPreferences.
                        final boolean in_store = saveInfoLocally.getIs_InStore();
                        final boolean takeaway = saveInfoLocally.getIs_Takeaway();
                        final boolean home_delivery = saveInfoLocally.getIs_Home_Delivery();

                        Intent in = new Intent(PaymentFailed.this, ChooseShopType.class);
                        in.putExtra("in_store", in_store);
                        in.putExtra("takeaway", takeaway);
                        in.putExtra("home_delivery", home_delivery);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        in.putExtra("Phone", phone);
                        startActivity(in);
                    }
                })
                .setNegativeButton(R.string.bs_exit_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(BarcodeScannerActivity.this, "Don't Exit", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(PaymentFailed.this, CartActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        in.putExtra("Phone", phone);
                        startActivity(in);
                    }
                })
                .show();
//        final String phone = saveInfoLocally.getPhone();
//        Intent in = new Intent(PaymentFailed.this, MyProfile.class);
//        in.putExtra("Phone", phone);
//        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(PaymentFailed.this, CartActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        in.putExtra("Phone", phone);
        startActivity(in);
    }

    public void lastfivetxns(View view) {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, LastFiveTxns.class);
        in.putExtra("Phone", phone);
        startActivity(in);

    }
}
