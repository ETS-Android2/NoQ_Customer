package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.younoq.noq.R;
import com.younoq.noq.models.Api;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.PChecksum;
import com.younoq.noq.models.PConstants;
import com.younoq.noq.models.Paytm;
import com.younoq.noq.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.SaveInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class PaymentMethod extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private String total_amt, txnAmount, tot_retailer_price, tot_our_price, total_discount, total_mrp, ref_bal_used, referral_bal;
    private int item_qty = 0;
    private String category_name, shoppingMethod, coming_from;
    private LinearLayout ll_paytm_pay, ll_cash_pay;
    private SaveInfoLocally saveInfoLocally;
    private RadioButton rb_paytm, rb_cash;
    final String TAG = "PaymentMethod";
    private ArrayList<String> txnData;
    private ProgressBar progressBar;
    private JSONObject jobj1, jobj2;
    private JSONArray jsonArray;
    private Bundle txnReceipt;
    private double final_amt;
    private Button pay_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        saveInfoLocally = new SaveInfoLocally(this);
        ll_paytm_pay = findViewById(R.id.pm_paytm_pay);
        rb_paytm = findViewById(R.id.pm_rb_paytm_pay);
        ll_cash_pay = findViewById(R.id.pm_cash_pay);
        rb_cash = findViewById(R.id.pm_rb_cash_pay);
        pay_btn = findViewById(R.id.pm_pay_btn);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        txnReceipt = new Bundle();
        txnData = new ArrayList<>();

        ll_paytm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean isChecked = rb_paytm.isChecked();

                rb_cash.setChecked(false);
                rb_paytm.setChecked(!isChecked);

            }
        });

        ll_cash_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final boolean isChecked = rb_cash.isChecked();

                rb_paytm.setChecked(false);
                rb_cash.setChecked(!isChecked);

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
        ref_bal_used = in.getStringExtra("referral_bal_used");
        referral_bal = in.getStringExtra("referral_bal");

        Log.d(TAG, "Referral Bal Used : "+ref_bal_used+", Referral Bal : "+referral_bal);

        final String tpm = "Pay ₹" + total_amt;
        final_amt = Double.parseDouble(total_amt);
        pay_btn.setText(tpm);

    }

    public void go_back(View view) {
        super.onBackPressed();
    }

    private String generateTxn_Order() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void MakePayment(View view) {

        if (rb_paytm.isChecked()) {

            // If Total_amount is Greater then Referral_Balance, then Proceed to Payment from Paytm.
            generateCheckSum();

        } else if (rb_cash.isChecked()) {

            // else go to Payment_Successful Page.

            // Setting the Updated Referral_Balance to SharedPreferences.
            saveInfoLocally.setReferralBalance(referral_bal);
            // Setting txnAmount's value to final_amt.
            txnAmount = String.valueOf(final_amt);
            // Doing all the things to be done after Successful Payment(which is already done here :-)..)
            afterPaymentConfirm(ref_bal_used, generateTxn_Order(), generateTxn_Order(), "[Payment By Cash]");
            // Redirect to Payment Successful Page.
            Log.d(TAG, "Sending the User to PaymentSuccess Activity");
            Intent in = new Intent(this, PaymentSuccess.class);
            in.putExtra("referral_balance_used", ref_bal_used);
            in.putExtras(txnReceipt);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Make the Progressbar Invisible
            progressBar.setVisibility(View.GONE);
            startActivity(in);

        }

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

        // Retrieving the User_Phone_NO from SharedPreferences.
        final String user_phone_no = saveInfoLocally.getPhone();

        try {
            // Now Inserting the Products list into the Basket_Table.
            final String type = "Store_Basket";
            final String res = new BackgroundWorker(this).execute(type, user_phone_no).get();
            // Now Inserting the Transaction Details into the Invoice_Table.
            final String type3 = "Store_Invoice";

            // TODO:// Add Code to fetch comments when Store_ID = "3", for now its "";
            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, total_mrp, total_discount, txnAmount, ref_bal_used, "", Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

            // Verifying if the Push to Basket_Table was Successful or not.
            boolean b = Boolean.parseBoolean(res.trim());
            if (b) {

                // Verifying if the Push to Invoice Table was Successful or not.
                rest = rest.trim();
                if (!rest.equals("FALSE")) {
                    // Retrieve the details from the result of the Invoice Push.
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
                    // If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user.
                    final String type4 = "Send_Invoice_Msg";
                    final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, comment, receipt_no, tot_retail_price, ref_bal_used, tot_discount, to_our_price).get();

                    // Checking if the shoppingMethod is whether Takeaway or Home_Delivery.
//                    if(shoppingMethod.equals("Takeaway") || shoppingMethod.equals("HomeDelivery")){
                    // Then we have to send the Invoice_Msg to the Retailer also.
                    Log.d(TAG, "Sending Retailer Invoice Sms");
                    final String type5 = "Send_Retailer_Invoice_Msg";
                    final String sms = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, receipt_no, tot_retail_price).get();
//                    }

                    // Storing the Details in txnData ArrayList.
                    txnData.add(receipt_no);
                    txnData.add(tot_discount);
                    txnData.add(tot_retail_price);
                    txnData.add(ref_bal_used);
                    txnData.add(to_our_price);
                    txnData.add(final_user_amt);
                    txnData.add(time);
                    txnData.add(Pay_Mode);
                    txnData.add(String.valueOf(item_qty));
                    // Adding the txnData ArrayList to txnReceipt Bundle.
                    txnReceipt.putStringArrayList("txnReceipt", txnData);
                    Log.d(TAG, "Stored Required Details in Bundle");
                    // Sending an Email to our official Account containing this Invoice Details.
                    // Currently Not Working.
//                    final String type5 = "Send_Invoice_Mail";
//                    final String email_res = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, comment, receipt_no).get();
//                    Log.d(TAG, "AWS_SES Response : " + email_res);
                }
//                dbHelper = new DBHelper(this);
//                // Now after the Re-Verification of Payment, Deleting all the Products Stored in the DB.
//                dbHelper.Delete_all_rows();
            }
        } catch (NullPointerException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void generateCheckSum() {

        txnAmount = String.valueOf(final_amt);

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                PConstants.M_ID,
                PConstants.CHANNEL_ID,
                txnAmount,
                PConstants.WEBSITE,
                PConstants.CALLBACK_URL,
                PConstants.INDUSTRY_TYPE_ID
        );

        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();

        //creating a call object from the apiService
        Call<PChecksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                c_url,
                paytm.getIndustryTypeId()
        );

        Log.d(TAG, "Paytm CustomerId : "+paytm.getCustId());
        Log.d(TAG, "Paytm OrderId : "+paytm.getOrderId());

        //making the call to generate checksum
        call.enqueue(new Callback<PChecksum>() {
            @Override
            public void onResponse(Call<PChecksum> call, Response<PChecksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<PChecksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service for Staging.
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production.
        PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PConstants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();
        Log.d(TAG, "Callback URL : "+c_url);
        paramMap.put("CALLBACK_URL", c_url);
        paramMap.put("CHECKSUMHASH", checksumHash);


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {

        Log.d(TAG, "From onTransactionResponse : "+bundle.toString());
        try {
            // Retrieving the Txn_Details from the TxnResponse i.e., bundle.
            final String txn_status = bundle.get("STATUS").toString();
            final String order_id = bundle.get("ORDERID").toString();
            final String Txn_ID = bundle.get("TXNID").toString();
            final String Order_ID = bundle.get("ORDERID").toString();
            final String Pay_Mode = bundle.get("PAYMENTMODE").toString();
            // Verifying if the Transaction was Successful or not.
            if (txn_status.equals("TXN_SUCCESS")) {

                // Forwarding the Order_id to the Server for the Re-Verification Process.
                final String type1 = "re-verify_checksum";
                final String res1 = new BackgroundWorker(this).execute(type1, order_id).get();

                // Converting the result String in form of JSONObject from the response to JSONObject.
                JSONObject jobj = new JSONObject(res1);
                // Extracting the required value from JSONObject.
                final String status = jobj.getString("STATUS");
                Log.d(TAG, "Re-verify Status : " + status);

                // Re-Verifying if the Transaction was Successful or not.
                if (status.equals("TXN_SUCCESS")) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
                    // Doing all the things to be done after Successful Payment.
                    afterPaymentConfirm(ref_bal_used, Txn_ID, Order_ID, Pay_Mode);
                    // Saving the Referral_Balance Value to SharedPreference.
                    saveInfoLocally.setReferralBalance("0");
                    // Intent to PaymentSuccess Activity.
                    Intent in = new Intent(this, PaymentSuccess.class);
                    in.putExtra("referral_balance_used", ref_bal_used);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                } else {
                    // If the Re-verification of the Txn Fails.
                    Toast.makeText(this, "Payment Verification Failed.", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(PaymentMethod.this, PaymentFailed.class);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                }

            } else {
                // If the Txn Fails.
                Toast.makeText(this, "Payment Failed.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(PaymentMethod.this, PaymentFailed.class);
                in.putExtras(txnReceipt);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            }
        } catch (NullPointerException | JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Intent in;
        if(shoppingMethod.equals("InStore")){
            in  = new Intent(this, CartActivity.class);
            in.putExtra("shoppingMethod", shoppingMethod);
        } else if(shoppingMethod.equals("Takeaway") || shoppingMethod.equals("HomeDelivery")){
            in  = new Intent(this, CartActivity.class);
            if(coming_from.equals("ProductCategory")){
                in.putExtra("comingFrom", "ProductCategory");
            } else {
                in.putExtra("comingFrom", "ProductList");
                in.putExtra("category_name", category_name);
            }
            in.putExtra("shoppingMethod", shoppingMethod);
        } else{
            in = new Intent();
        }
//        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

//        Intent in = new Intent(this, CartActivity.class);
//        in.putExtra("shoppingMethod", shoppingMethod);
//        in.putExtra("category_name", category_name);
//        startActivity(in);
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.d(TAG, "Transaction Failed : "+bundle.toString());
//        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}