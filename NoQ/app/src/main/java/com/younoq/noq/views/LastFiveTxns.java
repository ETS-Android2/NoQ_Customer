package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.younoq.noq.R;
import com.younoq.noq.adapters.TxnAdapter;
import com.younoq.noq.classes.Txn;
import com.younoq.noq.models.BackgroundWorker;
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

public class LastFiveTxns extends AppCompatActivity {

    TextView tv;
    public RecyclerView recyclerView;
    public String phone;
    private SaveInfoLocally saveInfoLocally;
    private final String TAG = "LastFiveTxnsActivity";
    JSONArray jsonArray1,  jsonArray2, productsArray;
    JSONObject jobj11, jobj12;
    List<Txn> txnList;
    TxnAdapter txnAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_five_txns);

        tv = findViewById(R.id.lft_tv1);
        saveInfoLocally = new SaveInfoLocally(this);
        recyclerView = findViewById(R.id.lft_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txnList = new ArrayList<>();

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");

        retrieve_last_five_txns();

    }

    private void retrieve_last_five_txns() {

        try {

            final String type = "retrieve_last_txns";
            String res = new BackgroundWorker(this).execute(type, phone).get();
            Log.d(TAG, "Last Five Txns : "+res);

            jsonArray1 = new JSONArray(res);
            jobj11 = jsonArray1.getJSONObject(0);
            if(!jobj11.getBoolean("error")){
                System.out.println("Output Is True");

                jobj12 = jsonArray1.getJSONObject(1);
                jsonArray2 = jobj12.getJSONArray("data");
                for (int i = 0; i < jsonArray2.length(); i++){

                    JSONObject obj = jsonArray2.getJSONObject(i);
                    /* Extracting the Products Array from the result. */
                    productsArray = obj.getJSONArray("products");

                    final String dd = obj.get("delivery_duration").toString();
                    int delivery_duration = 0;

                    if (!dd.equals(""))
                        delivery_duration = Integer.parseInt(dd);

                    txnList.add(
                            new Txn(
                                    obj.get("receipt_no").toString(),
                                    obj.get("payment_mode").toString(),
                                    obj.get("total_savings").toString(),
                                    obj.get("timestamp").toString(),
                                    obj.get("total_items").toString(),
                                    obj.get("final_amt").toString(),
                                    obj.get("store_addr").toString(),
                                    obj.get("store_name").toString(),
                                    obj.get("store_city").toString(),
                                    obj.get("store_state").toString(),
                                    obj.get("order_type").toString(),
                                    obj.get("refund_initiated").toString(),
                                    delivery_duration,
                                    productsArray
                            )
                    );

                }

                txnAdapter = new TxnAdapter(this, txnList);
                recyclerView.setAdapter(txnAdapter);

                System.out.println("After Setting Adapter");
                System.out.println("Txn List : "+txnList);

            } else {

                tv.setVisibility(View.GONE);
                Toast.makeText(this, "There are No Transactions From your Account.", Toast.LENGTH_SHORT).show();

            }


        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", true);
        startActivity(in);

    }

    @Override
    public void onBackPressed() {

        final String phone = saveInfoLocally.getPhone();
        Intent in = new Intent(this, MyProfile.class);
        in.putExtra("Phone", phone);
        in.putExtra("isDirectLogin", true);
        startActivity(in);

    }
}
