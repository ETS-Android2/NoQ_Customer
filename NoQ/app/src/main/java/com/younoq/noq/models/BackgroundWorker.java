package com.younoq.noq.models;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.younoq.noq.networkhandler.NetworkApi;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    StringBuilder result = new StringBuilder();
    DBHelper db;
    SaveInfoLocally saveInfoLocally;
    private static String TAG = "BackgroundActivity";

    public BackgroundWorker(Context context) {

        this.context = context;

    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder result1 = new StringBuilder();
        String type = params[0];
        final String base_url = NetworkApi.API_URL + "/DB/";

        if (type.equals("update_ref")) {

            String insert_data_url = base_url + "update_ref_user.php";
            Log.d("BackgroundActivity", "Update_Ref in BackgroundActivity");

            try {

                final String phone = params[1];
                final String ref_no = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("referrer", "UTF-8") + "=" + URLEncoder.encode(ref_no, "UTF-8");
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

                final String TAG = "Background Worker";
                Log.d(TAG, "Update_Ref result : "+result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("send_msg")){

            String insert_data_url = base_url + "Amazon/send_message.php";
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
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode("OTP : "+msg, "UTF-8");
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

        } else if (type.equals("verify_user")){

            String insert_data_url = base_url + "verify_user.php";

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
                String post_data = URLEncoder.encode("phone", "UtF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
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

                final String TAG = "MainActivity";
                Log.d(TAG, result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("store_user")){

            String insert_data_url = base_url + "insert_data.php";

            try {

                final String full_name = params[1];
                final String email = params[2];
                final String phone = params[3];
                final String ref_no = params[4];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("full_name", "UtF-8") + "=" + URLEncoder.encode(full_name, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("ref_no", "UTF-8") + "=" + URLEncoder.encode(ref_no, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("verify_store")) {

            String insert_data_url = base_url + "verify_store_new.php";

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
                String post_data = URLEncoder.encode("store_id", "UtF-8") + "=" + URLEncoder.encode(store_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result1.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("verify_product")) {

            String insert_data_url = base_url + "verify_product_new.php";

            try {

                final String product_id = params[1];
                final String store_id = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("product_id", "UtF-8") + "=" + URLEncoder.encode(product_id, "UTF-8")+"&"+
                        URLEncoder.encode("store_id", "UTF-8") + "=" + URLEncoder.encode(store_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result1.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("Store_Basket")){

            db = new DBHelper(context);
            final String user_phone_no = params[1];
            saveInfoLocally = new SaveInfoLocally(context);

            final String curr_sid = saveInfoLocally.get_store_id();

            ArrayList<List> Products = new ArrayList<>();

            /* Retrieving the ShoppingMethod from the SharedPreferences. */
            final String shoppingMethod = saveInfoLocally.getShoppingMethod();

            Cursor res = db.retrieveData(curr_sid,  shoppingMethod);
            if(res.getCount() == 0){
                return "0";
            } else {
                while(res.moveToNext()){

                    final String s_id = res.getString(1);
                    Log.d(TAG, "In Store_Basket Current Store_ID : "+curr_sid+" Product Store_ID : "+s_id);
                    /* Check to ensure only those products are stored in Basket_Table, which belong to the Current Store_ID. */
                    if (curr_sid.equals(s_id)) {

                        List<String> Product = new ArrayList<>();

                        Product.add(user_phone_no);  /* User */
                        Product.add(saveInfoLocally.getSessionID());  /* Session_ID */
                        Product.add(s_id);  /* Store_ID */
                        Product.add(res.getString(2));  /* Barcode */
                        Product.add(res.getString(3));  /* Number_of_items */
                        Product.add(res.getString(5));  /* Mrp_per_item */

                        double tot = Double.parseDouble(res.getString(3)) * Double.parseDouble(res.getString(5));
                        Product.add(String.valueOf(tot));  /* Total_Mrp */

                        Product.add(res.getString(7));  /* Retailer_Price_per_item */

                        tot = Double.parseDouble(res.getString(3)) * Double.parseDouble(res.getString(7));
                        Product.add(String.valueOf(tot));  /* Total_Retailer_Price */

                        Product.add(res.getString(8));  /* Our_Price_per_item */
                        Product.add(res.getString(9));  /* Discount_per_item */

                        tot = Double.parseDouble(res.getString(3)) * Double.parseDouble(res.getString(8));
                        Product.add(String.valueOf(tot));  /* Total_Our_Price */

                        tot = Double.parseDouble(res.getString(3)) * Double.parseDouble(res.getString(9));
                        Product.add(String.valueOf(tot));  /* Total_Discount */

                        Product.add(res.getString(13));

                        Products.add(Product);

                    }
                }
            }

            JSONArray jsArray = new JSONArray(Products);
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Array : "+jsArray.toString());
            String insert_data_url = base_url + "store_in_basket.php";

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("products", "UtF-8") + "=" + URLEncoder.encode(jsArray.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                Log.d(TAG, "Products Added to Basket_Table : "+result1.toString());
                return result1.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("re-verify_checksum")){

            String insert_data_url = base_url + "Paytm/txn_status_api.php";

            try {

                final String order_id = params[1];
                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("o_id", "UtF-8") + "=" + URLEncoder.encode(order_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                final String TAG = "BackgroundWorker";
                Log.d(TAG, "Re-Verification Results : "+result1.toString());
                return result1.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_data")) {

            String retrieve_data_url = base_url + "retrieve_data.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
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

        }  else if (type.equals("Store_Invoice")) {

            saveInfoLocally = new SaveInfoLocally(context);

            final String shoppingMethod = saveInfoLocally.getShoppingMethod();

            final String phone = params[1];
            final String total_mrp = params[2];
            final String total_discount = params[3];
            final String total_amt = params[4];
            final String referral_amt_used = params[5];
            final String comment = params[6];
            final String txn_id = params[7];
            final String order_id = params[8];
            final String pay_mode = params[9];
            final String total_retail_price = params[10];
            final String total_our_price = params[11];

            List<String> details = new ArrayList<>();
            details.add(txn_id);
            details.add(order_id);
            details.add(pay_mode);
            details.add(phone);
            details.add(saveInfoLocally.getSessionID());
            details.add(saveInfoLocally.get_store_id());
            details.add(total_mrp);
            details.add(total_retail_price);
            details.add(total_our_price);
            details.add(total_discount);
            details.add(referral_amt_used);
            details.add(total_amt);

            String tot_savings = String.valueOf(Double.valueOf(referral_amt_used) + Double.valueOf(total_discount));
            details.add(tot_savings);
            details.add(comment);
            details.add(shoppingMethod);

            JSONArray jsArray = new JSONArray(details);
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Invoice Details : "+jsArray.toString());

            final Logger logger = new Logger(context);
            /* Storing the Logs in the Logger. */
            logger.writeLog(TAG, "Store_Invoice()","Invoice Receipt : " + details.toString() +  "\n");


            String retrieve_data_url = base_url + "store_in_invoice.php";

            try {

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("details", "UTF-8")+"="+URLEncoder.encode(jsArray.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
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

        }  else if (type.equals("Send_Invoice_Msg")){

            saveInfoLocally = new SaveInfoLocally(context);

            /* Retrieving the ShoppingMethod from the SharedPreferences. */
            final String shoppingMethod = saveInfoLocally.getShoppingMethod();

            final String phone = saveInfoLocally.getPhone();
            final String curr_store_id = saveInfoLocally.get_store_id();
            final String time = params[1];
            final String final_amt = params[2];
            final String r_no = params[3];
            final String tot_retail_price = params[4];
            final String ref_bal_used = params[5];
            final String to_our_price = params[6];
            final String delivery_charge = params[7];

            final String[] dt = time.split(" ");
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Invoice Date : "+dt[0]+ " and Time: "+dt[1]);

            /* Calculating the Total Amt, Amt. Paid and Amt. to Pay. */
            final String tot_amt = String.valueOf(Double.parseDouble(tot_retail_price) + Double.parseDouble(delivery_charge));

            final String amt_paid = String.valueOf(Double.parseDouble(tot_retail_price) - Double.parseDouble(to_our_price) + Double.parseDouble(ref_bal_used));

            final String amt_to_pay = String.valueOf(Double.parseDouble(to_our_price) - Double.parseDouble(ref_bal_used) + Double.parseDouble(delivery_charge));

            List<String> details = new ArrayList<>();
            details.add(curr_store_id);
            details.add(shoppingMethod);
            details.add(r_no);
            details.add(dt[0]);
            details.add(dt[1]);
            details.add(tot_amt);
            details.add(amt_paid);
            details.add(amt_to_pay);
            details.add(final_amt);

            JSONArray jsDetails = new JSONArray(details);
            Log.d(TAG, "Invoice SMS Details : "+jsDetails.toString());

            String insert_data_url = base_url + "Amazon/sendInvoiceSms_v2.php";

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
                        URLEncoder.encode("msg_details", "UTF-8") + "=" + URLEncoder.encode(jsDetails.toString(), "UTF-8");
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

        } else if (type.equals("set_logout_flag")) {

            String retrieve_data_url = base_url + "logout_flag.php";

            try {

                final String phone = params[1];
                final String flag = params[2];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")+"&"+
                        URLEncoder.encode("flag", "UTF-8")+"="+URLEncoder.encode(flag, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
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

        } else if (type.equals("retrieve_last_txns")) {

            String retrieve_data_url = base_url + "last_five_txns.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
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

        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
