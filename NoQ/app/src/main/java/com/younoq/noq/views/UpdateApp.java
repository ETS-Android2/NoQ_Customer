package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noq.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UpdateApp extends AppCompatActivity {

    private final String TAG = "UpdateAppActivity";
    private TextView tv_app_version, tv_app_new_version;
    private String app_version, new_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        tv_app_new_version = findViewById(R.id.ua_app_new_version);
        tv_app_version = findViewById(R.id.ua_app_version);

        Intent in = getIntent();
        app_version = "(" + in.getStringExtra("app_version");
        new_version = in.getStringExtra("new_version") + ")";

        tv_app_version.setText(app_version);
        tv_app_new_version.setText(new_version);

    }


    public void onUpdate(View view) {

        final String appPackageName = "com.younoq.noq";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}