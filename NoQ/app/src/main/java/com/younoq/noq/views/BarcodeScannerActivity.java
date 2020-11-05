package com.younoq.noq.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.Result;
import com.younoq.noq.R;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.models.SaveInfoLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class BarcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView, mScannerView;

    Button go_to_basket;
    TextView tv;

    public static String type = "";
    public static String activity = "";
    SharedPreferences sharedPreferences;
    SaveInfoLocally saveInfoLocally;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        scannerView = findViewById(R.id.zxscan);
        mScannerView = new ZXingScannerView(this);
        scannerView.addView(mScannerView);

        saveInfoLocally = new SaveInfoLocally(this);
        go_to_basket = findViewById(R.id.bs_go_to_basket);
        tv = findViewById(R.id.bs_scan_what);

        Intent in = getIntent();
        type = in.getStringExtra("Type");

        final String TAG = "BarcodeScanner";
        Log.d(TAG, "Barcode Scan Type : "+type);

        if(type.equals("Product_Scan")){
            tv.setText(R.string.scan_prod);
        } else {
            go_to_basket.setVisibility(View.INVISIBLE);
            tv.setText(R.string.scan_store);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( checkPermission() ) {

                System.out.println("Just Nothing");
//                Toast.makeText(this, "NoQ has Permission to ACCESS your CAMERA", Toast.LENGTH_SHORT).show();

            } else {

                requestPermission();

            }
        }

    }

    private boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(BarcodeScannerActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);

    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length > 0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted){

                        Toast.makeText(this, "NoQ has Permission to ACCESS your CAMERA", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if(shouldShowRequestPermissionRationale(CAMERA)) {

                                displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                    }
                                });
                                return;
                            }

                        }
                    }
                }
                break;
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkPermission()) {

                if(scannerView == null) {

                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);

                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {

                requestPermission();

            }
        }

    }


    public void OnDestroy() {

        super.onDestroy();
        scannerView.stopCamera();

    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {

        new AlertDialog.Builder(BarcodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void Go_to_Basket(View view) {

        Intent in = new Intent(this, CartActivity.class);
        in.putExtra("shoppingMethod", "InStore");
        startActivity(in);

    }

    public void showAlert(String scanResult){

        final String TAG = "BarcodeScanner";
        Log.d(TAG, "starting showAlert scanResult : "+scanResult);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                scannerView.resumeCameraPreview(BarcodeScannerActivity.this);

            }
        });
//        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
//                startActivity(in);
//
//            }
//        });
        if (type.equals("Product_Scan")){
            builder.setMessage("Product Not Found! Please Try Again");
        } else if (type.equals("Store_Scan")){
            builder.setMessage("No Store Found! Please Try Again");
        }
        AlertDialog alert = builder.create();
        alert.show();

        Log.d(TAG, "ending showAlert scanResult : "+scanResult);

    }


    @Override
    public void handleResult(Result result) {

        final String scanResult = result.getText();
        final String TAG = "BarcodeScanner";
        Log.d(TAG, "Barcode Scan Result : "+scanResult);

        if (type.equals("Product_Scan")){

            final String type = "verify_product";
            String res = "";
            boolean flag = false;

            try {
                final String sid = saveInfoLocally.get_store_id();

                res = new BackgroundWorker(this).execute(type, scanResult, sid).get();
                Log.d(TAG, "Product Scan Result : "+res+" length : "+res.length());
                JSONArray jsonArray = new JSONArray(res);
                JSONObject jobj = jsonArray.getJSONObject(0);
                if(!jobj.getBoolean("error")){
                    flag = true;
                } else {
                    showAlert(scanResult);
                }
            } catch (ExecutionException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }

            if(flag){
                Intent in = new Intent(BarcodeScannerActivity.this, ProductDetails.class);
                in.putExtra("result", res);
                in.putExtra("barcode", scanResult);
                in.putExtra("comingFrom", "BarcodeScan");
                in.putExtra("shoppingMethod", "InStore");
                startActivity(in);
            }

        } else if (type.equals("Store_Scan")) {

            final String type = "verify_store";
            String res = "";
            boolean flag = false;

            try {
                res = new BackgroundWorker(this).execute(type, scanResult).get();
                Log.d(TAG, "Store Scan Result : "+res+" length : "+res.length());
                JSONArray jsonArray = new JSONArray(res);
                JSONObject jobj = jsonArray.getJSONObject(0);
                if(!jobj.getBoolean("error")){
                    flag = true;
                } else {
                    showAlert(scanResult);
                }
            } catch (ExecutionException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }

            if(flag){
                Intent in = new Intent(BarcodeScannerActivity.this, ShopDetails.class);
                in.putExtra("activity", "BSA");
                in.putExtra("result", res);
                in.putExtra("barcode", scanResult);
                startActivity(in);
            }

        }

    }

    @Override
    public void onBackPressed() {
        if(type.equals("Product_Scan")){
            dbHelper = new DBHelper(this);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Do you want to Exit the In Store Shopping?")
                    .setMessage(R.string.bs_exit_in_store_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.bs_exit_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Deleting all data.
                            dbHelper.Delete_all_rows();
                            // Retrieving the Store Shopping methods related Info, from SharedPreferences.
                            final boolean in_store = saveInfoLocally.getIs_InStore();
                            final boolean takeaway = saveInfoLocally.getIs_Takeaway();
                            final boolean home_delivery = saveInfoLocally.getIs_Home_Delivery();
                            Intent in = new Intent(BarcodeScannerActivity.this, ChooseShopType.class);
                            in.putExtra("in_store", in_store);
                            in.putExtra("takeaway", takeaway);
                            in.putExtra("home_delivery", home_delivery);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            in.putExtra("Type", "Store_Scan");
//                            in.putExtra("Phone", phone);
                            startActivity(in);
                        }
                    })
                    .setNegativeButton(R.string.bs_exit_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(BarcodeScannerActivity.this, "Don't Exit", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } else if(type.equals("Store_Scan")){

            final String phone = saveInfoLocally.getPhone();
            Intent in = new Intent(this, MyProfile.class);
            in.putExtra("Phone", phone);
            startActivity(in);

        } else{
            super.onBackPressed();
        }
    }
}