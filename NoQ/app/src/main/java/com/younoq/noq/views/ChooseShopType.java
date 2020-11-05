package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ChooseShopType extends AppCompatActivity {

    SaveInfoLocally saveInfoLocally;
    public static String TAG = "ChooseShopType Activity";
    TextView tv_in_store_title, tv_in_store, tv_takeaway, tv_home_delivery, tv_store_name;
    CardView cv_in_store, cv_takeaway, cv_home_delivery;
    private boolean in_store, takeaway, home_delivery;
    LinearLayout ll_delivery, ll_takeaway, ll_instore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shop_type);

        saveInfoLocally = new SaveInfoLocally(this);
        tv_in_store = findViewById(R.id.acs_card1_tv_2);
        tv_in_store_title = findViewById(R.id.acs_card1_tv_1);
        tv_takeaway = findViewById(R.id.acs_card2_tv_2);
        tv_home_delivery = findViewById(R.id.acs_card3_tv_2);
        tv_store_name = findViewById(R.id.cst_store_name);
        cv_in_store = findViewById(R.id.acs_card1);
        cv_takeaway = findViewById(R.id.acs_card2);
        cv_home_delivery = findViewById(R.id.acs_card3);
        ll_delivery = findViewById(R.id.acs_linear_layout_delivery);
        ll_takeaway = findViewById(R.id.acs_linear_layout_takeaway);
        ll_instore = findViewById(R.id.acs_linear_layout_instore);

        Intent in = getIntent();
        in_store = in.getBooleanExtra("in_store", false);
        takeaway = in.getBooleanExtra("takeaway", false);
        home_delivery = in.getBooleanExtra("home_delivery", false);

        retrieve_shop_details();

    }

    private void retrieve_shop_details() {

        final String store_name = saveInfoLocally.getStoreName() + ", " + saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        final String store_id = saveInfoLocally.get_store_id();
        final String type = "verify_store";
        String res = "";

        final String tmp = "In_Store : " + in_store + ", Takeaway : " + takeaway + ", Home_Delivery : "+home_delivery;
        Log.d(TAG, tmp);

//         Checking If In_store is Available
        if(in_store){
            ll_instore.setVisibility(View.GONE);
            if(store_id.equals("3")){
                tv_in_store.setText(R.string.cst_in_school_desc);
                tv_in_store_title.setText(R.string.cst_in_school);
            } else {
                tv_in_store.setText(R.string.cst_in_shop_desc);
            }
        }
        else{
            ll_instore.setVisibility(View.VISIBLE);
            tv_in_store.setText(R.string.cst_coming_soon);
        }
        // Checking If Takeaway is Available
        if(takeaway) {
            ll_takeaway.setVisibility(View.GONE);
            tv_takeaway.setText(R.string.cst_takeaway_desc);
        }
        else {
            tv_takeaway.setText(R.string.cst_coming_soon);
            ll_takeaway.setVisibility(View.VISIBLE);
        }
        // Checking If Home Delivery is Available
        if(home_delivery) {
            ll_delivery.setVisibility(View.GONE);
            tv_home_delivery.setText(R.string.cst_home_delivery_desc);
        }
        else {
            tv_home_delivery.setText(R.string.cst_coming_soon);
            ll_delivery.setVisibility(View.VISIBLE);
        }

    }

    public void inStore(View view) {
        if (in_store){
            // Setting the ShoppingMethod in SharedPreferences
            saveInfoLocally.setShoppingMethod("InStore");
//            Log.d(TAG, "In Store Clicked");
            Intent in = new Intent(this, BarcodeScannerActivity.class);
            in.putExtra("Type", "Product_Scan");
            startActivity(in);

        }
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void takeaway(View view) {
        if (takeaway) {
            // Setting the ShoppingMethod in SharedPreferences
            saveInfoLocally.setShoppingMethod("Takeaway");
//            Log.d(TAG, "Takeaway Clicked");
            Intent in = new Intent(this, ProductsCategory.class);
            in.putExtra("shoppingMethod", "Takeaway");
            startActivity(in);

        }
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void homeDelivery(View view) {
        if (home_delivery){
            // Setting the ShoppingMethod in SharedPreferences
            saveInfoLocally.setShoppingMethod("HomeDelivery");

            final String user_addr = saveInfoLocally.getUserAddress();
            Intent in;
            if(user_addr.equals(" ") || user_addr.equals("")){
                in = new Intent(this, UserAddress.class);
            } else {
                in = new Intent(this, ProductsCategory.class);
            }
            in.putExtra("shoppingMethod", "HomeDelivery");
            startActivity(in);
        }
        else
            Toast.makeText(this, "This Facility isn't available yet", Toast.LENGTH_SHORT).show();
    }

    public void showInterestInStore(View view) {

        final String type = "update_interested";
        final String store_id = saveInfoLocally.get_store_id();
        try {
            new AwsBackgroundWorker(this).execute(type, "in_store", store_id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Thanks for Showing your Interest", Toast.LENGTH_SHORT).show();
        ll_takeaway.setVisibility(View.INVISIBLE);

    }

    public void showInterestTakeaway(View view) {
//        Log.d(TAG, "Shown Interest in Takeaway");final String type = "update_interested";
        final String type = "update_interested";
        final String store_id = saveInfoLocally.get_store_id();
        try {
            new AwsBackgroundWorker(this).execute(type, "takeaway", store_id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Thanks for Showing your Interest", Toast.LENGTH_SHORT).show();
        ll_takeaway.setVisibility(View.INVISIBLE);
    }

    public void showInterestDelivery(View view) {
//        Log.d(TAG, "Shown Interest in Home Delivery");
        final String type = "update_interested";
        final String store_id = saveInfoLocally.get_store_id();
        try {
            new AwsBackgroundWorker(this).execute(type, "home_delivery", store_id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Thanks for Showing your Interest", Toast.LENGTH_SHORT).show();
        ll_delivery.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        final String phone = saveInfoLocally.getPhone();
        final String storeList = saveInfoLocally.getCategoryStores();
        Intent in = new Intent(this, StoresNoq.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", false);
        in.putExtra("storesList", storeList);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }
}
