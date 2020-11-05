package com.younoq.noq.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class SaveToBasket {

    public static final String PREFS_NAME = "SaveToBasket";
    public static final String PREFS_PRODUCT_NAME_KEY = "Product_Key";
    public static final String PREFS_PRODUCT_BARCODE = "Barcode";
    public static final String PREFS_PRODUCT_MRP_KEY = "MRP";
    public static final String PREFS_PRODUCT_RETAILER_PRICE_KEY = "Retailer_Price";
    public static final String PREFS_PRODUCT_OUR_PRICE_KEY = "Our_Price";
    public static final String PREFS_PRODUCT_TOTAL_DISCOUNT_KEY = "Total_Discount";
    public static final String PREFS_PRODUCT_QUANTITY = "Product_Quantity";

    Context context;

    public SaveToBasket(Context context) {
        this.context = context;
    }

    public void add_to_temp_basket(String data, int qty){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        try {

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jobj = jsonArray.getJSONObject(1);

            editor.putString(PREFS_PRODUCT_BARCODE, jobj.getString("Barcode"));
            editor.putString(PREFS_PRODUCT_NAME_KEY, jobj.getString("Product_Name"));
            editor.putString(PREFS_PRODUCT_MRP_KEY, jobj.getString("MRP"));
            editor.putString(PREFS_PRODUCT_RETAILER_PRICE_KEY, jobj.getString("Retailers_Price"));
            editor.putString(PREFS_PRODUCT_OUR_PRICE_KEY, jobj.getString("Our_Price"));
            editor.putString(PREFS_PRODUCT_TOTAL_DISCOUNT_KEY, jobj.getString("Total_Discount"));
            editor.putString(PREFS_PRODUCT_QUANTITY, String.valueOf(qty));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.apply();
    }

    public String[] get_from_temp_basket(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String[] data = new String[7];
        data[0] = sharedPreferences.getString(PREFS_PRODUCT_BARCODE, "");
        data[1] = sharedPreferences.getString(PREFS_PRODUCT_NAME_KEY, "");
        data[2] = sharedPreferences.getString(PREFS_PRODUCT_MRP_KEY, "");
        data[3] = sharedPreferences.getString(PREFS_PRODUCT_RETAILER_PRICE_KEY, "");
        data[4] = sharedPreferences.getString(PREFS_PRODUCT_OUR_PRICE_KEY, "");
        data[5] = sharedPreferences.getString(PREFS_PRODUCT_TOTAL_DISCOUNT_KEY, "");
        data[6] = sharedPreferences.getString(PREFS_PRODUCT_QUANTITY, "");
        return data;

    }

}
