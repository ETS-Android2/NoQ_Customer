package com.younoq.noq.companypolicy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ContactUs extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        coordinatorLayout = findViewById(R.id.acs_coordinator_layout);
        saveInfoLocally = new SaveInfoLocally(this);

    }

    public void go_back(View view) {
        super.onBackPressed();
    }

    public void showTopSnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg, int color) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, color));
        final Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        for (int i = 0; i < snackBarLayout.getChildCount(); i++) {
            View parent = snackBarLayout.getChildAt(i);
            if (parent instanceof LinearLayout) {
                ((LinearLayout) parent).setRotation(180);
                break;
            }
        }
        snackbar.show();

    }

    public void contactUs(View view) {

        final String type = "sendRefundAndReturnSms";
        final String phone = saveInfoLocally.getPhone();
        final String name = saveInfoLocally.getUserName();
        final String sent_to = "+919158911702";

        try {

        final String msg = name + ", Phone No : " + phone +
                ", has some queries, kindly get in touch with them asap.";
        final String res = new AwsBackgroundWorker(this).execute(type, msg, sent_to).get();

        showTopSnackBar(this, coordinatorLayout, "Your request has been received, we will get back to you soon!", R.color.BLUE);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
