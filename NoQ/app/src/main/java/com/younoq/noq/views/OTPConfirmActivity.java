package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.younoq.noq.R;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class OTPConfirmActivity extends AppCompatActivity {

    EditText otp;
    ProgressBar progressBar;
    TextView p_no, tv_change_mob_no;
    private Logger logger;

    Button cont, resend;
    ImageView re_enter;
    String phone = "";
    static String checkOTP = "";
    static String otp_pin = "";
    static String next_activity = "";
    SaveInfoLocally save_data;
    final String TAG = "OTPConfirmActivity";

    public static final String Save_User_Data = "com.example.noq1.SAVE_USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);
        setupUI(findViewById(R.id.main_parent));

        otp = findViewById(R.id.otp_enter);

        otp.setFocusable(true);
        otp.setFocusableInTouchMode(true);
        otp.requestFocus();

        cont = findViewById(R.id.btn_continue);
        resend = findViewById(R.id.btn_resend_otp);
//        re_enter = findViewById(R.id.btn_re_enter_no);
        tv_change_mob_no = findViewById(R.id.otp_change_mob_no);
        p_no = findViewById(R.id.op_tv_2);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        progressBar.setVisibility(View.INVISIBLE);
        save_data = new SaveInfoLocally(this);
        logger = new Logger(this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("Phone");
        final String user_no = " " + phone;
        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","User's Phone No. in getIntent : "+user_no+"\n");

        // Setting the onContinueMainActivity to false.
        save_data.setBoolean("MainActivityFirstTime", true);

        // Putting an Underline under the Phone No.
        SpannableString no = new SpannableString(user_no);
        no.setSpan(new UnderlineSpan(), 0, user_no.length(), 0);
        p_no.setText(no);
        final String change_mob_no = "Click Here!";
        // Putting an Underline under the Click Here!.
        SpannableString click_here = new SpannableString(change_mob_no);
        click_here.setSpan(new UnderlineSpan(), 0, change_mob_no.length(), 0);
        tv_change_mob_no.setText(click_here);

        final String TAG = "OTPConfirmActivity";
        Log.d(TAG, "Phone No in OTPConfirmActivity : "+phone);
        checkOTP = intent.getStringExtra(MainActivity.Otp);
        next_activity = intent.getStringExtra("next_activity");

    }

    public String generatePIN()
    {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        return String.valueOf(randomPIN);

    }

    private boolean isNumber(String phone) {

        try {
            Double.parseDouble(phone);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public void verify_otp(String checkOTP) {

//        final Boolean save_user_details = intent.getBooleanExtra(MainActivity.Save_User_Data, true);
        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "verify_otp()","verify_otp() Func. called.\n");

        View focusView;
        Log.d(TAG, "OTP in verify_otp : "+checkOTP);

        final String check_otp = otp.getText().toString();

        if ( check_otp.equals(checkOTP) ) {

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "verify_otp()","OTP Matched");

            Intent in;

            if (next_activity.equals("MP")){

                // Means the User Exists in DP, so change the logout_flag to False
                final String type = "set_logout_flag";
                try {
                    final String res = new BackgroundWorker(this).execute(type, phone, "False").get();
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "verify_otp()","BackgroundWorker 'set_logout_flag' Called, Logout Flag Set in the ServerDB\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG,"verify_otp()", e.getMessage());
                }

                in = new Intent(OTPConfirmActivity.this, Covid19.class);
                // Storing the Logs in the Logger.
                logger.writeLog(TAG, "verify_otp()","Going to Covid19 Activity and Storing the User's Phone No. in SharedPreferences.\n");
                saveLoginDetails(phone);

            } else {

                in = new Intent(OTPConfirmActivity.this, UserCredentialsActivity.class);
                // Storing the Logs in the Logger.
                logger.writeLog(TAG, "verify_otp()","Going to UserCredentialsActivity Activity\n");

            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.GONE);
                    in.putExtra("Phone", phone);
                    in.putExtra("activity", "MP");
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "verify_otp()","Values in Intent Phone : "+phone+", Activity : MP\n");
                    startActivity(in);

                }
            }, 1000);

        } else {

            // Setting the OTPConfirmFirstTime to true.
            save_data.setBoolean("OTPConfirmFirstTime", true);

            progressBar.setVisibility(View.INVISIBLE);
            otp.setError(getString(R.string.invalid_otp));
            otp.setText("");
            focusView = otp;
            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "verify_otp()","Entered OTP Doesn't Match\n");

        }

    }

    public void OnContinue(View v) {

        final boolean isFirstTime = save_data.getBoolean("OTPConfirmFirstTime");

        if (isFirstTime) {

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "OnContinue()","User Clicked on Continue Button\n");
            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "OnContinue()","OnContinue() Func. called\n");

            otp.setError(null);

            progressBar.setVisibility(View.VISIBLE);

            View focusView;

            final String check_otp = otp.getText().toString().trim();
            Log.d(TAG, "Otp length : "+check_otp.length());

//        if (TextUtils.isEmpty(check_otp)) {
            if ( check_otp.length() == 0 || !isNumber(check_otp)) {

                // Storing the Logs in the Logger.
                logger.writeLog(TAG, "OnContinue()","OTP Entered by the User has some non-numeric characters/or it's length == 0\n");

                // Setting the OTPConfirmFirstTime to false.
                save_data.setBoolean("OTPConfirmFirstTime", true);

                progressBar.setVisibility(View.INVISIBLE);
                otp.setError(getString(R.string.blank_otp));
                focusView = otp;

            } else {

                Log.d(TAG, "OTP in OnContinue : "+checkOTP);
                // Storing the Logs in the Logger.
                logger.writeLog(TAG, "OnContinue()","Verified OTP Entered by the User\n");

                // Setting the OTPConfirmFirstTime to false.
                save_data.setBoolean("OTPConfirmFirstTime", false);
                verify_otp(checkOTP);

            }

        }

    }

    public void OnResend(View v) {

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "OnResend()","User Clicked on ResendOTP\n");
        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "OnResend()","OnResend() Func. called\n");

        otp_pin = generatePIN();
//        otp_pin = "0070";
        otp.setText("");
        otp.setError(null);

        Log.d(TAG, "Phone No. in OnResend : "+phone);

        progressBar.setVisibility(View.VISIBLE);
        final String type = "send_msg";
        final String msg = otp_pin + " is your NoQ Verification Code.Don't Share it with other people.The code is valid for only 5 minutes.";
        try {
            String result = new BackgroundWorker(this).execute(type, msg, phone).get();
            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "OnResend()","BackgroundWorker 'send_msg' Called, OTP Resent Successfully\n");
        } catch (Exception e) {
            e.printStackTrace();
            // Storing the Logs in the Logger.
            logger.writeLog(TAG,"OnResend()", e.getMessage());
        }

        // Setting the OTPConfirmFirstTime to false.
        save_data.setBoolean("OTPConfirmFirstTime", true);

        checkOTP = otp_pin;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, 500);
        Log.d(TAG, "OTP in OnResend : "+checkOTP);

    }


    public void goToLanding(View view) {

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "goToLanding()","User Clicked on Change Phone No.\n");
        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "goToLanding()","goToLanding() Func. called, Going to MainActivity\n");

        // Setting the OTPConfirmFirstTime to true.
        save_data.setBoolean("MainActivityFirstTime", true);

        Intent in = new Intent(OTPConfirmActivity.this, MainActivity.class);
        startActivity(in);

    }

    @Override
    public void onBackPressed() {
        // Setting the OTPConfirmFirstTime to true.
        save_data.setBoolean("MainActivityFirstTime", true);
        super.onBackPressed();
    }

    private void saveLoginDetails(String Phone) {

        save_data.removeNumber();
        save_data.saveLoginDetails(Phone);

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(OTPConfirmActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
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
}
