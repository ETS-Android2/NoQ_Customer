package com.younoq.noq.views;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.younoq.noq.R;
import com.younoq.noq.adapters.MyPageFragmentAdapter;
import com.younoq.noq.models.SaveInfoLocally;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class IntroActivity extends FragmentActivity {

    ViewPager viewPager;
    TabLayout tabIndicator;
    ImageView next_btn;
    Button getStarted;
    Animation btnAnim;
    SaveInfoLocally saveInfoLocally;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveInfoLocally = new SaveInfoLocally(this);

        if(!saveInfoLocally.isFirstLogin() || saveInfoLocally.hasFinishedIntro()) {

            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(in);
            finish();

        }

        Log.d("IntroActivity", "not Found");

        setContentView(R.layout.activity_intro);

        next_btn = findViewById(R.id.ia_next_btn);
        tabIndicator = findViewById(R.id.tabLayout);
        getStarted = findViewById(R.id.ia_btn_get_started);
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_animation);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPageFragmentAdapter(this));
        tabIndicator.setupWithViewPager(viewPager);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos = viewPager.getCurrentItem();
                if (pos < 2){
                    pos++;
                    viewPager.setCurrentItem(pos);
                }

                if (pos == 2) {
                    // After this we will be in the last Screen.
                    prepareLastScreen();
                }

            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

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

                // Setting the Flag as True.
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

        getStarted.setAnimation(btnAnim);

    }

//    private void changeFirstLoginStatus() {
//
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("isIntroOpened",true);
//        editor.apply();
//
//    }
}
