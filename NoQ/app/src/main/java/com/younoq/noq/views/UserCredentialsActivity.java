package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.BackgroundWorker;
import com.younoq.noq.models.Logger;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class UserCredentialsActivity extends AppCompatActivity {

    Button regbtn;
    EditText full_name, referral_no, email;
    TextView tv, tv1;
    ProgressBar progressBar;
    String User_number;
    private Logger logger;

    String verified = "";

    SaveInfoLocally save_data = new SaveInfoLocally(this);
    final String TAG = "UserCredentialsActivity";

    public static final String Name = "com.example.noq.NAME";
    public static final String Email = "com.example.noq.EMAIL";
    public static String Pno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);
        setupUI(findViewById(R.id.main_parent));

        final String otp_success = "OTP Verified Successfully";

        regbtn = findViewById(R.id.reg_btn);

        full_name = findViewById(R.id.uca_full_name);
        full_name.setFocusable(true);
        full_name.setFocusableInTouchMode(true);
        full_name.requestFocus();

        referral_no = findViewById(R.id.uca_ref_no);
        email = findViewById(R.id.uca_email);
        logger = new Logger(this);

        progressBar = findViewById(R.id.spin_kit);
        Sprite wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);

        progressBar.setVisibility(View.INVISIBLE);

//        tv = findViewById(R.id.otp_success);
//        tv1 = findViewById(R.id.tv_disp);

//        tv.setText(otp_success);
//        tv.setVisibility(View.VISIBLE);

        Intent in = getIntent();
        User_number = in.getStringExtra("Phone");
        Log.d(TAG, "Phone No in UCA : "+User_number);

        // Setting the OTPConfirmFirstTime to false.
        save_data.setBoolean("OTPConfirmFirstTime", true);

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","User's phone no. in getIntent : "+User_number+"\n");

//        tv1.setText(User_number);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                tv.setVisibility(View.INVISIBLE);
//            }
//        }, 3000);

    }

    public void Register(View v) throws ExecutionException, InterruptedException {

        final boolean isFirstTime = save_data.getBoolean("UCAFirstTime");

        if (isFirstTime) {

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "Register()","User Clicked on Continue Button\n");
            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "Register()","Register() Func. called\n");

            final String f_name = full_name.getText().toString().trim();
            final String user_email = email.getText().toString().trim();
            Pno = referral_no.getText().toString().trim();

            full_name.setError(null);
            referral_no.setError(null);

            View focusView = null;

            Boolean flag = true;
            Boolean flag_phone = false;

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "Register()","flag_phone = false, flag = true.\n");

            long id1;

            if (TextUtils.isEmpty(f_name)) {

                full_name.setError(getString(R.string.required));
                focusView = full_name;
                // Storing the Logs in the Logger.
                logger.writeLog(TAG, "Register()","Full Name Entered by user is empty\n");

            } else {

                progressBar.setVisibility(View.VISIBLE);
                final Intent intent;

                if ( !TextUtils.isEmpty(Pno) ) {

                    Pno = "+91"+Pno;
//                Pno = "+44"+Pno;

                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","Added the Country Code to the Entered Referral No. : "+Pno+"\n");
//
                    if (Pno.length() == 13 ) {

                        if(!User_number.equals(Pno)) {

                            // Storing the Logs in the Logger.
                            logger.writeLog(TAG, "Register()","Phone No. Entered by the User and Referral No. aren't equal.\n");

                            final String type1 = "verify_user";

                            verified = new BackgroundWorker(this).execute(type1, Pno).get();
                            // Storing the Logs in the Logger.
                            logger.writeLog(TAG, "Register()","BackgroundWorker 'verify_user' called.\n");

                            Boolean b = Boolean.parseBoolean(verified.trim());
                            Log.d(TAG, "before_Verification : result = " +verified.length());

                            if ( b ) {

                                // Storing the Logs in the Logger.
                                logger.writeLog(TAG, "Register()","Referral No. Entered by the User Exists in the ServerDB. Going to ReferralSuccessful Activity\n");

//                        tv.setVisibility(View.VISIBLE);
                                // Storing the Logs in the Logger.
                                logger.writeLog(TAG, "Register()","flag_phone is set to true.\n");
                                // Referee exists in the DB, so update Referral Details.
                                flag_phone = true;

                                intent = new Intent(UserCredentialsActivity.this, ReferralSuccessfulActivity.class);

                            } else {

                                // Storing the Logs in the Logger.
                                logger.writeLog(TAG, "Register()","Referral No. Entered by the User Doesn't Exists in the ServerDB. Going to ReferralUnsuccessful Activity\n");

                                intent = new Intent(UserCredentialsActivity.this, ReferralUnsuccessfulActivity.class);

                            }

                        } else {

                            // Storing the Logs in the Logger.
                            logger.writeLog(TAG, "Register()","Phone No. Entered by the User and Referral No. are equal, set flag to false.\n");
                            referral_no.setError("Referral no. can't be same as your number!!");
                            flag = false;
                            intent = new Intent();

                        }



                    } else {

                        // Setting the OTPConfirmFirstTime to false.
                        save_data.setBoolean("UCAFirstTime", true);

                        // Storing the Logs in the Logger.
                        logger.writeLog(TAG, "Register()","Phone No. Entered isn't equal to 13 digits, set flag to false.\n");
                        referral_no.setError(getString(R.string.invalid_phone_number));
                        focusView = referral_no;
                        intent = new Intent();
                        flag = false;

                    }

                } else {

                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","Referral No. wasn't entered, Going to Covid19 Activity\n");
//                intent = new Intent(UserCredentialsActivity.this, NoqStores.class);
                    intent = new Intent(UserCredentialsActivity.this, Covid19.class);
                }

                if ( flag ) {

                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","Referral No. entered by the User is verified, so moving to further tasks.\n");

                    final String type2 = "store_user";
                    String verify = new BackgroundWorker(this).execute(type2, f_name, user_email, User_number, Pno).get();
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'store_user' called.\n");

                    final String type3 = "greet_user";
                    String verify1 = new AwsBackgroundWorker(this).execute(type3, User_number, f_name).get();
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'greet_user' called.\n");

                    // -------------------- Temporary Bonus Rs.100 For Each User. ----------------------
                    final String type4 = "update_bonus_amt";
                    String verify2 = new AwsBackgroundWorker(this).execute(type4, User_number).get();
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'update_bonus_amt' called.\n");

                    if ( flag_phone ) {
                        final String type = "update_ref";
                        String update = new BackgroundWorker(this).execute(type, User_number, Pno).get();
                        // Storing the Logs in the Logger.
                        logger.writeLog(TAG, "Register()","BackgroundWorker 'update_ref' called on both the User's No. and the Referral No. Provided.\n");

                        // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the Referred No.
                        final String type1 = "update_referral_balance";
                        final String isUpdated = new AwsBackgroundWorker(this).execute(type1, Pno).get();
                        Log.d(TAG, "Updated Referee's Referral_Balance : "+isUpdated);
                        // Storing the Logs in the Logger.
                        logger.writeLog(TAG, "Register()","as the user has entered the referral no. so, BackgroundWorker 'update_bonus_amt' called on referral no. provided. -> Result : "+isUpdated+"\n");

//                    // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No.
//                    final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get();
//                    Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1);
                    }

                    // -------------------- Temporary Bonus Rs.100 For Each User. ----------------------
                    // Calling the Stored Procedure in DB for updating the Referral_Balance Column, for the User No.
                    final String type1 = "update_referral_balance";
                    final String isUpdated1 = new AwsBackgroundWorker(this).execute(type1, User_number).get();
                    Log.d(TAG, "Updated User's Referral_Balance : "+isUpdated1);
                    // Setting the OTPConfirmFirstTime to false.
                    save_data.setBoolean("UCAFirstTime", false);
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","BackgroundWorker 'update_bonus_amt' called on User no. -> Result : "+isUpdated1+"\n");

                    saveLoginDetails(User_number);
                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","Saved User Login Details in SharedPreferences.\n");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(GONE);
//                        tv.setVisibility(GONE);

                            Toast.makeText(UserCredentialsActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            intent.putExtra(Name, f_name);
                            intent.putExtra(Email, user_email);
                            intent.putExtra("Phone", User_number);
                            intent.putExtra("activity", "UCA");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Log.d(TAG, "After_Verification : result = " + verified);
                            // Storing the Logs in the Logger.
                            logger.writeLog(TAG, "Register()","Values in Intent -> Name : "+f_name+", Email : "+user_email+", Phone_No. : "+User_number+", Activity : UCA.\n");
                            startActivity(intent);

                        }
                    }, 600);

                } else {

                    // Storing the Logs in the Logger.
                    logger.writeLog(TAG, "Register()","Referral Entered By User is Invalid, hence don't go anywhere.\n");
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }

        }

    }

    private void saveLoginDetails(String Phone) {

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "saveLoginDetails()","saveLoginDetails() Func. called.\n");
        save_data.removeNumber();
        save_data.saveLoginDetails(Phone);

    }

    @Override
    public void onBackPressed() {
        // Setting the OTPConfirmFirstTime to true.
        save_data.setBoolean("OTPConfirmFirstTime", true);
        // Setting the MainActivityFirstTime to true.
        save_data.setBoolean("MainActivityFirstTime", true);
        super.onBackPressed();
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UserCredentialsActivity.this);
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
