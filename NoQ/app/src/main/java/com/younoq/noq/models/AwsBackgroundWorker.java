package com.younoq.noq.models;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class AwsBackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    StringBuilder result = new StringBuilder();
    DBHelper db;
    SaveInfoLocally saveInfoLocally;
    private String TAG = "AwsBackgroundWorker";

    public AwsBackgroundWorker(Context context) {

        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String type = params[0];

        if (type.equals("Send_Invoice_Mail")){

            Log.d(TAG, "Currently not in Use");
//            db = new DBHelper(context);
//            saveInfoLocally = new SaveInfoLocally(context);
//            ArrayList<List> Invoice = new ArrayList<>();
//
//            final String phone = saveInfoLocally.getPhone();
//            final String uname = saveInfoLocally.getUserName();
//            final String store_name = saveInfoLocally.getStoreName();
//            final String store_addr = saveInfoLocally.getStoreAddress();
//            final String store_id = saveInfoLocally.get_store_id();
//            final String time = params[1];
//            final String final_amt = params[2];
//            final String comment = params[3];
//            final String r_no = params[4];
//
//            final String[] dt = time.split(" ");
//            final String TAG = "AwsBackgroundWorker";
//            Log.d(TAG, "Invoice Mail Date : "+dt[0]+ " and TIme: "+dt[1]);
//
//            List<String> details = new ArrayList<>();
//            details.add(uname);
//            details.add(store_name);
//            details.add(r_no);
//            details.add(store_addr);
//            details.add(dt[0]);
//            details.add(dt[1]);
//            details.add(final_amt);
//            details.add(comment);
//            details.add(store_id);
//
//            JSONArray jsDetails = new JSONArray(details);
//            Log.d(TAG, "Invoice Email Details : "+jsDetails.toString());
//
//            Cursor res = db.retrieveData();
//            if(res.getCount() == 0){
//                return "0";
//            } else {
//                while(res.moveToNext()){
//                    List<String> Product = new ArrayList<>();
//
//                    Product.add(res.getString(4));
//                    Product.add(res.getString(8));
//                    Product.add(res.getString(3));
//                    Product.add(res.getString(6));
//
//                    Invoice.add(Product);
//                }
//            }
//
//            JSONArray jsArray = new JSONArray(Invoice);
//            Log.d(TAG, "Invoice Email Products : "+jsArray.toString());
//
//            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/aws/email_send.php";
//
//            try {
//
//                String line = "";
//
//                URL url = new URL(insert_data_url);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setRequestMethod("POST");
//                httpURLConnection.setDoOutput(true);
//                httpURLConnection.setDoInput(true);
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8")+"&"+
//                        URLEncoder.encode("msg_details", "UTF-8") + "=" + URLEncoder.encode(jsDetails.toString(), "UTF-8")+"&"+
//                        URLEncoder.encode("prod_data", "UTF-8") + "=" + URLEncoder.encode(jsArray.toString(), "UTF-8");
//                bufferedWriter.write(post_data);
//                bufferedWriter.flush();
//                bufferedWriter.close();
//                outputStream.close();
//                InputStream inputStream = httpURLConnection.getInputStream();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
//                while ((line = bufferedReader.readLine()) != null) {
//                    result.append(line + "\n");
//                }
//                bufferedReader.close();
//                httpURLConnection.disconnect();
//
//                return result.toString();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        } else if (type.equals("greet_user")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/greet_user.php";
            Log.d("AwsBackgroundActivity", "Greet User in AwsBackgroundActivity");

            try {

                final String phone = params[1];
                final String fname = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(fname, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_referral_amt")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_referral_amt.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_referral_balance")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/call_update_ref_bal.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_referral_used")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/update_referral_used.php";

            try {

                final String phone = params[1];
                final String ref_bal_used = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8")+"&"+
                        URLEncoder.encode("ref_used", "UTF-8") + "=" + URLEncoder.encode(ref_bal_used, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_stores_data")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_stores.php";

            try {

                String line = "";

                final String storesList = params[1];

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("storesList", "UTF-8") + "=" + URLEncoder.encode(storesList, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_bonus_amt")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/update_bonus_amt.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_products_list")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/fetchProducts.php";

            try {

                final String store_id = params[1];
                final String category_name = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8")+"&"+
                                URLEncoder.encode("category_name", "UTF-8") + "=" + URLEncoder.encode(category_name, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_categories")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_categories_new.php";

            try {

                final String store_id = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_interested")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/update_interested.php";

            try {

                final String interested_in = params[1];
                final String store_id = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("interested_in", "UTF-8") + "=" + URLEncoder.encode(interested_in, "UTF-8")+"&"+
                                URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("Send_Retailer_Invoice_Msg")){

            db = new DBHelper(context);
            saveInfoLocally = new SaveInfoLocally(context);
            ArrayList<List> Invoice = new ArrayList<>();

            final String retailer_phone = saveInfoLocally.getRetailer_Phone_No();
            final String phone = saveInfoLocally.getPhone();
            final String uname = saveInfoLocally.getUserName();
            final String store_name = saveInfoLocally.getStoreName();
            final String store_addr = saveInfoLocally.getStoreAddress();
            final String curr_store_id = saveInfoLocally.get_store_id();
            final String user_address = saveInfoLocally.getUserAddress();
            final String time = params[1];
            final String final_amt = params[2];
            final String r_no = params[3];
            final String tot_retail_price = params[4];

            final String[] dt = time.split(" ");
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Invoice Date : "+dt[0]+ " and Time: "+dt[1]);

            // Retrieving the ShoppingMethod from the SharedPreferences.
            final String shoppingMethod = saveInfoLocally.getShoppingMethod();

            List<String> details = new ArrayList<>();
            details.add(store_name);
            details.add(store_addr);
            details.add(shoppingMethod);
            details.add(uname);
            details.add(phone);
            details.add(dt[0]);
            details.add(dt[1]);
            details.add(r_no);
            details.add(tot_retail_price);
            details.add(final_amt);
            details.add(user_address);

            JSONArray jsDetails = new JSONArray(details);
            Log.d(TAG, "Invoice SMS Details : "+jsDetails.toString());

            Cursor res = db.retrieveData(curr_store_id, shoppingMethod);
            if(res.getCount() == 0){
                return "0";
            } else {
                while(res.moveToNext()){

                    final String sid = res.getString(1);
                    Log.d(TAG, "In Send_Invoice_Msg Current Store_ID : "+curr_store_id+" Product Store_ID : "+sid);
                    // Check to ensure only those products are sent to Invoice Msg, which belong to the Current Store_ID.
                    if(curr_store_id.equals(sid)) {

                        List<String> Product = new ArrayList<>();

                        Product.add(res.getString(2));
                        Product.add(res.getString(4));
                        Product.add(res.getString(7));
                        Product.add(res.getString(3));
                        Product.add(res.getString(6));

                        Invoice.add(Product);

                    }
                }
            }

            JSONArray jsArray = new JSONArray(Invoice);
            Log.d(TAG, "Invoice SMS Products : "+jsArray.toString());

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/send_retailer_invoice_sms.php";

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(retailer_phone, "UTF-8")+"&"+
                        URLEncoder.encode("msg_details", "UTF-8") + "=" + URLEncoder.encode(jsDetails.toString(), "UTF-8")+"&"+
                        URLEncoder.encode("prod_data", "UTF-8") + "=" + URLEncoder.encode(jsArray.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_stores_categories")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_stores_category_new1.php";

            final String city = params[1];
            final String city_area = params[2];

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8") + "&" +
                        URLEncoder.encode("city_area", "UTF-8") + "=" + URLEncoder.encode(city_area, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_cities")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_cities.php";

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                bufferedWriter.write(post_data);
//                bufferedWriter.flush();
//                bufferedWriter.close();
//                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_address")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/update_address.php";

            final String phone = params[1];
            final String address = params[2];

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8")+"&"+
                                URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("upload_logs")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/upload_logs.php";

            saveInfoLocally = new SaveInfoLocally(context);

            final String file_path = saveInfoLocally.getLogFilePath();
            Log.d(TAG, "Log File Path : "+file_path);

            int serverResponseCode = 0;

            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            File selectedFile = new File(file_path);
//
//        String[] parts = selectedFilePath.split("/");
//        final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {
                return null;
            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(insert_data_url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", file_path);

                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + file_path + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
                        Log.d(TAG, "File Upload completed.\n\n You can see the uploaded file here: \n\n");
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.d(TAG, "URL error!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Cannot Read/Write File!");
                }
                return String.valueOf(serverResponseCode);
            }

        } else if (type.equals("retrieve_city_areas")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieve_city_areas.php";

            final String city = params[1];

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("city", "UTF-8") + "=" + URLEncoder.encode(city, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("sendRefundAndReturnSms")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/sendRefundSms.php";
            Log.d("BackgroundActivity", "Send_SMS in BackgroundActivity");

            try {

                final String msg = params[1];
                final String phone = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("updateRefundInfo")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/updateRefundInfo.php";
            Log.d("BackgroundActivity", "Send_SMS in BackgroundActivity");

            try {

                final String receipt_no = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("receipt_no", "UTF-8") + "=" + URLEncoder.encode(receipt_no, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
