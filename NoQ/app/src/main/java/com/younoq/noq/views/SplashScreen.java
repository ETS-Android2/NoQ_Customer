package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        progressBar = findViewById(R.id.spin_kit);
//        Sprite cubeGrid = new CubeGrid();
//        progressBar.setIndeterminateDrawable(cubeGrid);

//        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent in = new Intent(SplashScreen.this, IntroActivity.class);
//                progressBar.setVisibility(View.GONE);
                startActivity(in);
                finish();
            }
        }, 1000);
    }
}
