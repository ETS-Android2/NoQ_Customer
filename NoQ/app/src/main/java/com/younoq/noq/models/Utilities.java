package com.younoq.noq.models;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.snatik.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utilities {

    private final String TAG = "Utilities";
    private SimpleDateFormat dateFormat;
    private Context context;
    private Storage storage;
    private Logger logger;

    public Utilities(Context ctx) {
        this.context = ctx;
        this.storage = new Storage(this.context);
        this.logger = new Logger(this.context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    public void showTopSnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg, int color) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
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

}
