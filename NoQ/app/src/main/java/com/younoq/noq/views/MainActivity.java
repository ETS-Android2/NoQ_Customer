package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.younoq.noq.R;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class MainActivity extends AppCompatActivity{

    EditText et;
    Button btn;
    ProgressBar progressBar;
    private Boolean exit = false;

    public static final String TAG = "MainActivity";
    public static final String Otp = "com.example.noq_1.OTP";
    private Logger logger;

    SaveInfoLocally save_data = new SaveInfoLocally(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.main_parent));

        et = findViewById(R.id.et_phone);
        btn = findViewById(R.id.btn_cont);

        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        logger = new Logger(this);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        final String num = save_data.getPhone();

        /* Storing the Logs in the Logger. */
        logger.writeLog(TAG, "onCreate()", "User opened the App\n");

        if(num.length() == 13) {
            Direct_Login(num);
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onCreate()","Retrieved the User's No. from SharedPreferences : "+num+"\n");
        } else {
            final String nu = save_data.getPrevPhone();
            et.setText(nu.replace("+91", ""));
            /* et.setText(nu.replace("+44", "")); */
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onCreate()","User has logged Out, Displaying the Last Used No. : "+nu+"\n");
            /* et.setText(nu.replace("+44", "")); */
        }

    }

    public void Direct_Login(String num){

        /* Storing the Logs in the Logger. */
        logger.writeLog(TAG, "Direct_Login()","User is logged In Already So, Direct Login\n");

        /* Intent in = new Intent(MainActivity.this, MyProfile.class); */
        Intent in = new Intent(MainActivity.this, Covid19.class);
        in.putExtra("Phone", num);
        in.putExtra("activity", "MP");
        startActivity(in);

    }

    public String generatePIN()
    {

        /*generate a 4 digit integer 1000 <10000 */
        int randomPIN = (int)(Math.random()*9000)+1000;

        return String.valueOf(randomPIN);

    }

    private boolean isNumber(String phone) {

        try {
            Double.parseDouble(phone);
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "isNumber()","Verifying the No. Entered by the User\n");
            return true;
        } catch (NumberFormatException e) {
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG,"isNumber()", e.getMessage());
            return false;
        }

    }

    public void onContinue(View v) throws ExecutionException, InterruptedException {

        final boolean isFirstTime = save_data.getBoolean("MainActivityFirstTime");

        if (isFirstTime) {

            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onContinue()","User Clicked on Continue Button\n");
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onContinue()","OnContinue() Func. called\n");

            final String phone = "+91"+et.getText().toString().trim();
            /* final String phone = "+44"+et.getText().toString().trim(); */
            /* final String phone = et.getText().toString().trim(); */

            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onContinue()","Added Country Code to the User's Entered No : "+phone+"\n");

            et.setError(null);

            View focusView = null;

            if ( phone.length() < 4 ) {

                /* Setting the onContinueMainActivity to false. */
                save_data.setBoolean("MainActivityFirstTime", true);

                et.setError(getString(R.string.blank_phone));
                focusView = et;
                /* Storing the Logs in the Logger. */
                logger.writeLog(TAG, "onContinue()","The entered phone no.'s length is < 4\n");

            } else {

                if ( phone.length() == 13 && isNumber(phone)) {

                    /* Storing the Logs in the Logger. */
                    logger.writeLog(TAG, "onContinue()","Verified the phone no. entered by the User\n");

                    progressBar.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Phone in MainActivity : "+phone);

                    final String otp = generatePIN();
                    /* final String otp = "0070"; */
                    final String type = "send_msg";
                    final String msg = otp + " is your NoQ Verification Code.Don't Share it with other people.The code is valid for only 5 minutes.";
                    new BackgroundWorker(this).execute(type, msg, phone);

                    /* Storing the Logs in the Logger. */
                    logger.writeLog(TAG, "onContinue()","BackgroundWorker 'send_msg' Called, OTP Sent to the User\n");

                    Intent in = new Intent(MainActivity.this, OTPConfirmActivity.class);

                    if(UserExistsInDB(phone)){
                        in.putExtra("next_activity", "MP");
                        Log.d(TAG, "User Exists in ServerDB");
                        /* Storing the Logs in the Logger. */
                        logger.writeLog(TAG, "onContinue()","User Exists in ServerDB, NextActivity -> MP\n");
                    } else {
                        in.putExtra("next_activity", "UCA");
                        Log.d(TAG, "User Doesn't Exists in ServerDB");
                        /* Storing the Logs in the Logger. */
                        logger.writeLog(TAG, "onContinue()","User Doesn't Exists in ServerDB, NextActivity -> UCA\n");
                    }

                    /* Setting the onContinueMainActivity to false. */
                    save_data.setBoolean("MainActivityFirstTime", false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);

                            in.putExtra("Phone", phone);
                            in.putExtra(Otp, otp);
                            /* Storing the Logs in the Logger. */
                            logger.writeLog(TAG, "onContinue()","Values in Intent Phone : "+phone+", Otp : "+otp+", Next Activity -> OTPConfirmActivity\n");
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);

                        }
                    }, 1000);

    /*                } */

                } else {

                    /* Setting the onContinueMainActivity to false. */
                    save_data.setBoolean("MainActivityFirstTime", true);

                    et.setError(getString(R.string.invalid_phone_number));
                    focusView = et;
                    /* Storing the Logs in the Logger. */
                    logger.writeLog(TAG, "onContinue()","Phone no. entered by the User contains non-numeric characters/ or it's length is not 13.\n");

                }
            }

        }

    }

    /* Return True if the Current no. provided by user is same as Previous no.(if Present, otherwise
     returns False) stored in SharedPref, else False.
    private boolean CompareCurrentWithPrev(String phone){

        final String prev_num = save_data.getPrevPhone();
        if(prev_num.length() == 13) {
            if (prev_num.equals(phone)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    } */

    private boolean UserExistsInDB(String Phone) throws ExecutionException, InterruptedException {

        /* Storing the Logs in the Logger. */
        logger.writeLog(TAG, "UserExistsInDB()","UserExistsInDB() Func. called\n");

        final String type = "verify_user";
        String res = new BackgroundWorker(this).execute(type, Phone).get();
        /* Storing the Logs in the Logger. */
        logger.writeLog(TAG, "UserExistsInDB()","Verified the User in the ServerDB\n");
        Boolean b = Boolean.parseBoolean(res.trim());

        String TAG = "MainActivity";
        Log.d(TAG, "in UserAlreadyExists : result = " + res.length());

        if( b ){
            return true;
        } else {
            return false;
        }
    }

/*    private void saveLoginDetails(String Phone) {

        save_data.removeNumber();
        save_data.saveLoginDetails(Phone);

    } */

    public void setupUI(View view) {

        /* Set up touch listener for non-text box views to hide keyboard. */
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    return false;
                }
            });
        }

        /*If a layout container, iterate over children and seed recursion. */
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()","User Exited the App\n");
            moveTaskToBack(true);
        } else {
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "onBackPressed()","User Pressed Back\n");

            /* Setting the onContinueMainActivity to false. */
            save_data.setBoolean("MainActivityFirstTime", true);

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
