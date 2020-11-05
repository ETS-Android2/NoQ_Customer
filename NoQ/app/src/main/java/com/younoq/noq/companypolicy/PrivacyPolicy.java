package com.younoq.noq.companypolicy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
    }

    public void go_back(View view) {
        super.onBackPressed();
    }
}
