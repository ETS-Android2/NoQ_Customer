package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.younoq.noq.R;
import com.younoq.noq.adapters.StoresAdapter;
import com.younoq.noq.classes.Store;
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

public class StoresNoq extends AppCompatActivity {

    private JSONArray jsonArray1, jsonArray2;
    private JSONObject jobj11, jobj12;
    private List<Store> StoreList;
    private StoresAdapter storesAdapter;
    private RecyclerView recyclerView;
    private String TAG = "StoresNoq", storeList;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_noq);

        StoreList = new ArrayList<>();
        recyclerView = findViewById(R.id.sn_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saveInfoLocally = new SaveInfoLocally(this);

        Intent in = getIntent();
        storeList = in.getStringExtra("storesList");

        retrieve_current_stores();;

    }

    public void retrieve_current_stores(){

        final String type = "retrieve_stores_data";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, storeList).get();
            Log.d(TAG, "Result : " + res);

            jsonArray1 = new JSONArray(res);

            for (int i = 0; i < jsonArray1.length(); i++){

                JSONObject obj = jsonArray1.getJSONObject(i);

                StoreList.add(
                        new Store(
                                obj.get("store_id").toString(),
                                obj.get("store_name").toString(),
                                obj.get("store_addr").toString(),
                                obj.get("store_city").toString(),
                                obj.get("pin").toString(),
                                obj.get("store_state").toString(),
                                obj.get("store_country").toString(),
                                obj.get("phone_no").toString(),
                                obj.get("store_image").toString(),
                                obj.get("in_store").toString().toLowerCase().equals("true"),
                                obj.get("takeaway").toString().toLowerCase().equals("true"),
                                obj.get("home_delivery").toString().toLowerCase().equals("true"),
                                Integer.parseInt(obj.get("delivery_charge").toString()),
                                Integer.parseInt(obj.get("min_charge").toString()),
                                Integer.parseInt(obj.get("max_charge").toString()),
                                Integer.parseInt(obj.get("delivery_duration").toString())
                        )
                );

            }

            storesAdapter = new StoresAdapter(this, StoreList);
            recyclerView.setAdapter(storesAdapter);


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", false);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }
}
