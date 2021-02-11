package com.younoq.noq.views;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.younoq.noq.R;
import com.younoq.noq.adapters.MyPageFragmentAdapter;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class IntroActivity extends FragmentActivity {

    final String TAG = "IntroActivity";
    ViewPager viewPager;
    TabLayout tabIndicator;
    ImageView next_btn;
    Button getStarted;
    Animation btnAnim, first_slide_pt, second_slide_pt, third_slide_pt;
    SaveInfoLocally saveInfoLocally;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveInfoLocally = new SaveInfoLocally(this);

        /* Retrieving the app's version. */
        try {

            final String type = "retrieveAppVersion";
            final String res = new AwsBackgroundWorker(this).execute(type).get();

            JSONArray jsonArray = new JSONArray(res.trim());
            JSONObject jobj = jsonArray.getJSONObject(1);

            final String curr_app_version = jobj.getString("App_Version");
            Log.d(TAG, "Current App Version : "+curr_app_version);

            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            saveInfoLocally.set_app_version(version);
            Log.d(TAG, "App Version : "+version);

            final double device_app_version = Double.parseDouble(version);
            final double current_app_version = Double.parseDouble(curr_app_version);

            if (device_app_version < current_app_version) {

                finish();
                Intent in = new Intent(IntroActivity.this, UpdateApp.class);
                in.putExtra("app_version", version);
                in.putExtra("new_version", curr_app_version);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);

            } else {

                if(!saveInfoLocally.isFirstLogin() || saveInfoLocally.hasFinishedIntro()) {

                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                    finish();

                }

            }


        } catch (PackageManager.NameNotFoundException | ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        /* if(!saveInfoLocally.isFirstLogin() || saveInfoLocally.hasFinishedIntro()) {

            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(in);
            finish();

        } */

        Log.d("IntroActivity", "not Found");

        setContentView(R.layout.activity_intro);

        next_btn = findViewById(R.id.ia_next_btn);
        tabIndicator = findViewById(R.id.tabLayout);
        getStarted = findViewById(R.id.ia_btn_get_started);
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_animation);

        first_slide_pt = AnimationUtils.loadAnimation(this, R.anim.second_slide_first_pt);
        second_slide_pt = AnimationUtils.loadAnimation(this, R.anim.second_slide_first_pt);
        second_slide_pt.setStartOffset(500);
        third_slide_pt = AnimationUtils.loadAnimation(this, R.anim.second_slide_first_pt);
        third_slide_pt.setStartOffset(1000);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPageFragmentAdapter(this));
        tabIndicator.setupWithViewPager(viewPager);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos = viewPager.getCurrentItem();

                LinearLayout first_ll = viewPager.findViewById(R.id.ascf_first_point);
                first_ll.setAnimation(first_slide_pt);

                if (pos < 2){
                    pos++;
                    viewPager.setCurrentItem(pos);
                }

                if (pos == 2) {
                    /* After this we will be in the last Screen. */
                    prepareLastScreen();
                }

            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                LinearLayout first_ll = viewPager.findViewById(R.id.ascf_first_point);
                first_ll.startAnimation(first_slide_pt);
                LinearLayout second_ll = viewPager.findViewById(R.id.ascf_second_point);
                second_ll.startAnimation(second_slide_pt);
                LinearLayout third_ll = viewPager.findViewById(R.id.ascf_third_point);
                third_ll.startAnimation(third_slide_pt);

                if (tab.getPosition() == 2) {
                    prepareLastScreen();
                } else {
                    showOptions();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Setting the Flag as True. */
                saveInfoLocally.setHasFinishedIntro();

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();

            }
        });


    }

    private void showOptions() {

        next_btn.setVisibility(View.VISIBLE);
        getStarted.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);

    }

    private void prepareLastScreen() {

        next_btn.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        getStarted.startAnimation(btnAnim);

    }

}
