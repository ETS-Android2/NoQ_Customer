package com.younoq.noq.companypolicy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class TermsAndConditions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
    }

    public void go_back(View view) {
        super.onBackPressed();
    }
}
