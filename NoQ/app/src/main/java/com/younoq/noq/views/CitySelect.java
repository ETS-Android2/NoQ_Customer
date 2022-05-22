package com.younoq.noq.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.younoq.noq.R;
import com.younoq.noq.adapters.CityAdapter;
import com.younoq.noq.adapters.CityAreaAdapter;
import com.younoq.noq.classes.City;
import com.younoq.noq.classes.CityArea;
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

public class CitySelect extends AppCompatActivity {

    private CityAdapter cityAdapter;
    private RecyclerView recyclerView, cityAreaRecyclerView;
    private List<City> citiesList;
    private final String TAG = "CitySelect";
    private JSONArray jsonArray1, jsonArray, jsonArray2;
    private JSONObject jobj, jobj1, jobj2;
    private String phone;
    private ConstraintLayout layout_bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private CityAreaAdapter cityAreaAdapter;
    private List<CityArea> cityAreaList;
    private TextView tv_city_name;
    private SaveInfoLocally saveInfoLocally;
    private Dialog bonus_dialog;
    private ImageView im_bd_exit;
    private boolean isDirectLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);

        recyclerView = findViewById(R.id.city_recyclerView);
        saveInfoLocally = new SaveInfoLocally(this);
        tv_city_name = findViewById(R.id.bs_ca_city_name);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        citiesList = new ArrayList<>();
        cityAreaList = new ArrayList<>();

        cityAreaRecyclerView = findViewById(R.id.bs_ca_recyclerView);
        cityAreaRecyclerView.setHasFixedSize(true);
        cityAreaRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        layout_bottomSheet = findViewById(R.id.bs_ca_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(layout_bottomSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        isDirectLogin = in.getBooleanExtra("isDirectLogin", false);

        bonus_dialog = new Dialog(this);

        retrieve_cities();

        // If the app is opened for the First Time, and there is No DirectLogin to the App.
        if (saveInfoLocally.isFirstLogin() && !isDirectLogin){

//            showBonusDialog();

        }

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

    private void retrieveCityAreas(String city_name) {

        final String type = "retrieve_city_areas";

        try {

            final String res = new AwsBackgroundWorker(this).execute(type, city_name).get();
            Log.d(TAG, city_name + " City Areas : " +res);

            jsonArray = new JSONArray(res.trim());

            Log.d(TAG, "CityArea List Before  : "+cityAreaList);

            if (cityAreaList.size() > 0) {
                cityAreaList.clear();
                cityAreaAdapter.notifyDataSetChanged();
            }

            for (int i = 0; i < jsonArray.length(); i ++) {

                jsonArray2 = jsonArray.getJSONArray(i);
//                Log.d(TAG, jsonArray2.getString(0));
                final String city_area = jsonArray2.getString(0);

                if(! city_area.equals("")) {

                    cityAreaList.add(new
                            CityArea(
                            jsonArray2.getString(0)
                    ));

                }

            }

            Log.d(TAG, "CityArea List After  : "+cityAreaList);

            cityAreaAdapter = new CityAreaAdapter(this, cityAreaList, city_name, phone, isDirectLogin);
            cityAreaRecyclerView.setAdapter(cityAreaAdapter);

            tv_city_name.setText(city_name);

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void retrieve_cities() {

        final String type = "retrieve_cities";
        try {

            final String res = new AwsBackgroundWorker(this).execute(type).get();
            Log.d(TAG, "Cities List : "+res);

            jsonArray1 = new JSONArray(res);
            for(int index = 0; index < jsonArray1.length(); index++){
               jobj = jsonArray1.getJSONObject(index);

               Log.d(TAG, "City Name : "+ jobj.getString("city"));

               citiesList.add(
                      new City(
                              jobj.getString("city"),
                              jobj.getString("exists")
                      )
               );
            }

            cityAdapter = new CityAdapter(this, citiesList);
            recyclerView.setAdapter(cityAdapter);

            cityAdapter.setOnItemClickListener(new CityAdapter.onItemClickListener() {
                @Override
                public void onCitySelect(String city_name) {
                    retrieveCityAreas(city_name);
                }
            });

        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
