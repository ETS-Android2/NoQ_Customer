package com.younoq.noq.views;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.younoq.noq.R;
import com.younoq.noq.adapters.StoreCategoryAdapter;
import com.younoq.noq.adapters.StoresAdapter;
import com.younoq.noq.classes.Store;
import com.younoq.noq.classes.StoreCategory;
import com.younoq.noq.companypolicy.AboutUs;
import com.younoq.noq.companypolicy.ContactUs;
import com.younoq.noq.companypolicy.PrivacyPolicy;
import com.younoq.noq.companypolicy.RefundPolicy;
import com.younoq.noq.companypolicy.TermsAndConditions;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class MyProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MyProfileActivity";

    private TextView tvv1, tvv2, nav_img, tv_bonus_amt, tv_city_name;
    private SaveInfoLocally saveInfoLocally;
    private String phone, city_name, city_area;
    private Boolean exit = false;

    private JSONArray jsonArray1,  jsonArray2;
    private JSONArray jsonArray;
    private JSONObject jobj1, jobj2;

    /*    ProgressBar progressBar; */
    private RecyclerView recyclerView;
    private List<StoreCategory> storeCategoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        saveInfoLocally = new SaveInfoLocally(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        /* To get the status of the Header in the Navigation View. */
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Intent in = getIntent();
        phone = in.getStringExtra("Phone");
        final boolean isDirectLogin = in.getBooleanExtra("isDirectLogin", false);
        Log.d(TAG, "isDirectLogin in MyProfile : "+isDirectLogin);

        /* Generating the SessionID for the Current Session. */
        final String session_id = generate_session_id();
        saveInfoLocally.setSessionID(session_id);

        /* Fetching Elements in Navigation Drawer. */
        tvv1 = headerView.findViewById(R.id.text_view1);
        tvv2 = headerView.findViewById(R.id.text_view2);
        nav_img = headerView.findViewById(R.id.mp_img_txt);

        tv_city_name = findViewById(R.id.mp_city_name);

        /* progressBar = findViewById(R.id.mp_spin_kit);
        Sprite cubeGrid = new CubeGrid();
        progressBar.setIndeterminateDrawable(cubeGrid); */

        recyclerView = findViewById(R.id.mp_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        storeCategoriesList = new ArrayList<>();

        setUserDetails();

        retrieve_stores_categories();

        fetch_referral_amt();

    }

    private String generate_session_id() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void setUserDetails() {

        /* Retrieving the City Name from the SharedPreferences */
        city_name = saveInfoLocally.getStoreCity();
        city_area = saveInfoLocally.getStoreCityArea();

        final String cityArea = city_name + ", " + city_area;
        tv_city_name.setText(cityArea);

        try {

            final String type = "retrieve_data";
            String data = new BackgroundWorker(this).execute(type, phone).get();
            System.out.println(data);

            jsonArray = new JSONArray(data);
            jobj1 = jsonArray.getJSONObject(0);
            if(!jobj1.getBoolean("error")) {
                jobj2 = jsonArray.getJSONObject(1);

                final String uname = jobj2.getString("name");
                final String email = jobj2.getString("email");
                final String addr = jobj2.getString("address");

                saveInfoLocally.setUserName(uname);
                saveInfoLocally.setEmail(email);
                saveInfoLocally.setReferralNo(jobj2.getString("referral_phone_number"));
                saveInfoLocally.setUserAddress(addr);

                final String[] name_credentials = uname.split(" ", 2);
                String na;
                if (name_credentials.length >= 2) {
                    final String f = name_credentials[0];
                    final String l = name_credentials[1];
                    na = String.valueOf(f.charAt(0)) + l.charAt(0);
                } else {
                    final String f = name_credentials[0];
                    na = String.valueOf(f.charAt(0));
                }

                nav_img.setText(na);
                tvv1.setText(uname);
                tvv2.setText(email);

            }

        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void fetch_referral_amt(){

        final String type = "retrieve_referral_amt";
        final String phone = saveInfoLocally.getPhone();
        try {

            final String res = new AwsBackgroundWorker(this).execute(type, phone).get();
            String ref_bal;
            try
            {
                jsonArray = new JSONArray(res);
                jobj1 = jsonArray.getJSONObject(0);
                if(!jobj1.getBoolean("error")){
                    jobj2 = jsonArray.getJSONObject(1);
                    ref_bal = jobj2.getString("referral_balance");
                    Log.d(TAG, "Referral Amount Balance : "+ref_bal);
                    /* Saving the Referral_Amount_Balance to SharedPreferences to be used in CartActivity */
                    saveInfoLocally.setReferralBalance(ref_bal);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void retrieve_stores_categories(){

        final String type = "retrieve_stores_categories";
        try {
            final String res = new AwsBackgroundWorker(this).execute(type, city_name, city_area).get();
            Log.d(TAG, "Stores Categories : " + res);

            jsonArray1 = new JSONArray(res);
            for(int index = 0; index < jsonArray1.length(); index++){

                jobj1 = jsonArray1.getJSONObject(index);
                final String category_name = jobj1.getString("category");

                final String image_name = jobj1.getString("image_name");

                List<String> storeList = new ArrayList<>();

                jsonArray2 = jobj1.getJSONArray("stores");
                for(int index1 = 0; index1 < jsonArray2.length(); index1++){

                    storeList.add(jsonArray2.get(index1).toString());

                }

                final boolean available = jobj1.getString("availability").equals("true");

                storeCategoriesList.add(
                        new StoreCategory(
                                category_name,
                                image_name,
                                available,
                                storeList
                        )
                );

            }

        } catch (ExecutionException | JSONException | InterruptedException e) {
            e.printStackTrace();
        }

        StoreCategoryAdapter storeCategoryAdapter = new StoreCategoryAdapter(this, storeCategoriesList);
        recyclerView.setAdapter(storeCategoryAdapter);


    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            if (exit) {
                moveTaskToBack(true);
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about_us) {
            Intent in = new Intent(MyProfile.this, AboutUs.class);
            startActivity(in);
        } else if (id == R.id.nav_contact) {
            Intent in = new Intent(MyProfile.this, ContactUs.class);
            startActivity(in);
        } else if (id == R.id.nav_terms_cond) {
            Intent in = new Intent(MyProfile.this, TermsAndConditions.class);
            startActivity(in);
        } else if (id == R.id.nav_privacy_policy) {
            Intent in = new Intent(MyProfile.this, PrivacyPolicy.class);
            startActivity(in);
        } else if (id == R.id.nav_refund_policy) {
            Intent in = new Intent(MyProfile.this, RefundPolicy.class);
            startActivity(in);
        } else if (id == R.id.nav_last_five_txns) {
            Intent in = new Intent(MyProfile.this, LastFiveTxns.class);
            in.putExtra("Phone", phone);
            startActivity(in);
        } else if (id == R.id.nav_logout) {

            final String type = "set_logout_flag";
            try {
                final String res = new BackgroundWorker(this).execute(type, phone, "True").get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            saveInfoLocally.clear_all();
            saveInfoLocally.setPrevPhone(phone);
            saveInfoLocally.setHasFinishedIntro();
            Intent in = new Intent(this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);

        } else if (id == R.id.profile) {
            Intent in = new Intent(MyProfile.this, UserProfile.class);
            /* in.putExtra("activity", "MP"); */
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SelectCity(View view) {

        /* Setting the City to blank. */
        saveInfoLocally.setStoreCity("");

        Intent in = new Intent(view.getContext(), CitySelect.class);
        in.putExtra("Phone", phone);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }
}
