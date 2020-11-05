package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noq.R;
import com.younoq.noq.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChoosePartnerDelivery extends AppCompatActivity {

    private final String TAG = "ChoosePartnerDeliveryActivity";
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_partner_delivery);

        saveInfoLocally = new SaveInfoLocally(this);

    }

    public void zomato(View view) {
        // Setting the ShoppingMethod in SharedPreferences
        saveInfoLocally.setShoppingMethod("Zomato");
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", "Zomato");
        startActivity(in);
    }

    public void swiggy(View view) {
        // Setting the ShoppingMethod in SharedPreferences
        saveInfoLocally.setShoppingMethod("Swiggy");
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", "Swiggy");
        startActivity(in);
    }

    public void dunzo(View view) {
        // Setting the ShoppingMethod in SharedPreferences
        saveInfoLocally.setShoppingMethod("Dunzo");
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", "Dunzo");
        startActivity(in);
    }

    public void other(View view) {
        // Setting the ShoppingMethod in SharedPreferences
        saveInfoLocally.setShoppingMethod("Other");
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", "Other");
        startActivity(in);
    }

    public void noq(View view) {

        saveInfoLocally.setShoppingMethod("NoQ");
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", "NoQ");
        startActivity(in);

    }
}