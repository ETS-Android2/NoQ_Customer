package com.younoq.noq.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noq.R;
import com.younoq.noq.models.AwsBackgroundWorker;
import com.younoq.noq.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class UserAddress extends AppCompatActivity {

    private EditText address;
    private String shoppingMethod;
    private SaveInfoLocally saveInfoLocally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address);

        address = findViewById(R.id.aua_add_address);
        saveInfoLocally = new SaveInfoLocally(this);

        Intent in = getIntent();
        shoppingMethod = in.getStringExtra("shoppingMethod");

    }

    public void addAddress(View view) {

        final String entered_addr = address.getText().toString().trim();
        final String user_addr = saveInfoLocally.getUserAddress();

        if(!entered_addr.equals("")){

            final String phone = saveInfoLocally.getPhone();

            try {

                final String type = "update_address";
                new AwsBackgroundWorker(this).execute(type, phone, entered_addr).get();

                Toast.makeText(this, "Address Added Successfully", Toast.LENGTH_SHORT).show();
                // Saving the User's Address in SharedPreferences.
                saveInfoLocally.setUserAddress(entered_addr);

                Intent in = new Intent(this, ProductsCategory.class);
                in.putExtra("shoppingMethod", shoppingMethod);
                startActivity(in);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } else {

            address.setError("Please Enter an Address");

        }

    }
}