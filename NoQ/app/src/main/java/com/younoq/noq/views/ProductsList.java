package com.younoq.noq.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.younoq.noq.R;
import com.younoq.noq.adapters.BottomSheetCategoryAdapter;
import com.younoq.noq.adapters.ProductListAdapter;
import com.younoq.noq.classes.Category;
import com.younoq.noq.classes.Product;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ProductsList extends AppCompatActivity {

    TextView tv_store_name, tv_category_name;
    String store_id, store_name, category_name, shoppingMethod, coming_from;
    Button btn_categories;
    private CoordinatorLayout coordinatorLayout;
    private String TAG ="ProductList";
    SaveInfoLocally saveInfoLocally;
    JSONArray jsonArray, jsonArray1, jsonArray2;
    JSONObject jobj;
    List<Product> productList;
    RecyclerView recyclerView, bs_recyclerview;
    ProductListAdapter productListAdapter;
    LinearLayout layout_bottomsheet;
    BottomSheetBehavior sheetBehavior;
    List<Category> categoriesList;
    BottomSheetCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        tv_store_name = findViewById(R.id.apl_store_name);
        tv_category_name = findViewById(R.id.apl_category_name);
        saveInfoLocally = new SaveInfoLocally(this);
        recyclerView = findViewById(R.id.apl_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_categories = findViewById(R.id.apc_btn_categories);
        coordinatorLayout = findViewById(R.id.cpl_coordinator_layout);

        categoriesList = new ArrayList<>();

        bs_recyclerview = findViewById(R.id.bd_bottomsheet_recyclerview);
        bs_recyclerview.setHasFixedSize(true);
        bs_recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        layout_bottomsheet = findViewById(R.id.bd_bottomSheet);
        sheetBehavior = BottomSheetBehavior.from(layout_bottomsheet);

        Intent in= getIntent();
        category_name = in.getStringExtra("category_name");
        shoppingMethod = in.getStringExtra("shoppingMethod");
        coming_from = in.getStringExtra("coming_from");

        retrieve_categories();

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        Log.d(TAG, "BottomSheet Expanded");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.d(TAG, "BottomSheet Collapsed");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btn_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    Log.d(TAG, "Expanding BottomSheet");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    Log.d(TAG, "Collapsing BottomSheet");
                }
            }
        });

        productList = new ArrayList<>();

        store_id = saveInfoLocally.get_store_id();
        store_name = saveInfoLocally.getStoreName() +", "+ saveInfoLocally.getStoreAddress();
        tv_store_name.setText(store_name);

        tv_category_name.setText(category_name);

        retrieve_products_list();

    }

    void retrieve_categories() {

        final String store_id = saveInfoLocally.get_store_id();
        final String type= "retrieve_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id).get();
            Log.d(TAG, "Result in ProductList"+res);

            jsonArray = new JSONArray(res);

            for(int i = 0; i < jsonArray.length(); i++){

                jsonArray1 = jsonArray.getJSONArray(i);
//                Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0));
                final int times_purchased = Integer.parseInt(jsonArray1.getString(2));
                categoriesList.add(
                        new Category(
                                jsonArray1.getString(0),
                                jsonArray1.getString(1),
                                jsonArray1.getString(3),
                                times_purchased
                        )
                );

                categoryAdapter = new BottomSheetCategoryAdapter(this, categoriesList, shoppingMethod);
                bs_recyclerview.setAdapter(categoryAdapter);

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    void retrieve_products_list() {

        final String type = "retrieve_products_list";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, store_id, category_name).get();
            Log.d(TAG, "Products list : "+res);

            jsonArray2 = new JSONArray(res);

            for(int i = 0; i < jsonArray2.length(); i++){

                jobj = jsonArray2.getJSONObject(i);
//                Log.d(TAG, "Item - "+i+" "+jsonArray1.getString(0));
                productList.add(
                        new Product(
                                0,
                                jobj.getString("store_id"),
                                jobj.getString("barcode"),
                                jobj.getString("product_name"),
                                jobj.getString("mrp"),
                                jobj.getString("retailer_price"),
                                "0",
                                jobj.getString("our_price"),
                                jobj.getString("total_discount"),
                                "0",
                                jobj.getString("has_image"),
                                jobj.getString("quantity"),
                                jobj.getString("category"),
                                shoppingMethod
                        )
                );

            }

            productListAdapter = new ProductListAdapter(this, productList, shoppingMethod, coordinatorLayout);
            recyclerView.setAdapter(productListAdapter);


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void Go_to_Basket(View view) {
        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("category_name", category_name);
        in.putExtra("comingFrom", "ProductList");
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);
    }

    public void Go_to_Profile(View view) {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, ProductsCategory.class);
        in.putExtra("shoppingMethod", shoppingMethod);
        startActivity(in);
//        if(coming_from.equals("Cart")){
//            Intent in = new Intent(this, ProductsCategory.class);
//            in.putExtra("shoppingMethod", shoppingMethod);
//            startActivity(in);
//        } else {
//            super.onBackPressed();
//        }
    }
}
