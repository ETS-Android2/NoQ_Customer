package com.younoq.noq.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "Temp_Basket.db";
    private static final String TABLE_PRODUCTS = "Products_Table";
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, Store_ID TEXT, Barcode TEXT, Number_of_Items INTEGER," +
            " Product_Name TEXT, MRP TEXT, Total_Amount TEXT, Retailers_Price TEXT, Our_Price TEXT, Total_Discount TEXT, Has_Image TEXT, Quantity TEXT, ShoppingMethod TEXT, Category TEXT)";
    /* Product Table's Columns */
    private static final String prod_col_1 = "Store_ID";
    private static final String prod_col_2 = "Barcode";
    private static final String prod_col_3 = "Number_of_Items";
    private static final String prod_col_4 = "Product_Name";
    private static final String prod_col_5 = "MRP";
    private static final String prod_col_6 = "Total_Amount";
    private static final String prod_col_7 = "Retailers_Price";
    private static final String prod_col_8 = "Our_Price";
    private static final String prod_col_9 = "Total_Discount";
    private static final String prod_col_10 = "Has_Image";
    private static final String prod_col_11 = "Quantity";
    private static final String prod_col_12 = "ShoppingMethod";
    private static final String prod_col_13 = "Category";
    private Logger logger;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
        logger = new Logger(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "DB Version Changed, Recreating the Products Table");
        /* Storing Logs in the Logger. */
        logger.writeLog(TAG, "onUpgrade()","DB Version Changed, Recreating the Products Table\n");
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCTS);
        onCreate(db);
    }

    public boolean insertData(String data, String sid, int p_qty, String shoppingMethod){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double tot_amt = 0.0;
        contentValues.put(prod_col_1, sid);

        try{

            JSONArray jsonArray = new JSONArray(data);
            JSONObject jobj = jsonArray.getJSONObject(1);
            contentValues.put(prod_col_2, jobj.getString("Barcode"));
            contentValues.put(prod_col_3, p_qty);
            contentValues.put(prod_col_4, jobj.getString("Product_Name"));
            tot_amt = p_qty*Double.parseDouble(jobj.getString("Our_Price"));
            contentValues.put(prod_col_5, jobj.getString("MRP"));
            contentValues.put(prod_col_6, String.valueOf(tot_amt));
            contentValues.put(prod_col_7, jobj.getString("Retailers_Price"));
            contentValues.put(prod_col_8, jobj.getString("Our_Price"));
            contentValues.put(prod_col_9, jobj.getString("Total_Discount"));
            contentValues.put(prod_col_10, jobj.getString("has_image"));
            contentValues.put(prod_col_11, jobj.getString("quantity"));
            contentValues.put(prod_col_12, shoppingMethod);
            contentValues.put(prod_col_13, jobj.getString("category"));

        }catch(Exception e){
            e.printStackTrace();
        }

        long res = db.insert(TABLE_PRODUCTS, null, contentValues);
        final String TAG = "DBHelper";
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean insertProductData(List<String> prod, int qty) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double tot_amt = 0.0;

        contentValues.put(prod_col_1, prod.get(0));
        contentValues.put(prod_col_2, prod.get(1));
        contentValues.put(prod_col_3, qty);
        contentValues.put(prod_col_4, prod.get(2));
        /* Using Our Price. */
        tot_amt = qty*Double.parseDouble(prod.get(5));
        contentValues.put(prod_col_5, prod.get(3));
        contentValues.put(prod_col_6, String.valueOf(tot_amt));
        contentValues.put(prod_col_7, prod.get(4));
        contentValues.put(prod_col_8, prod.get(5));
        contentValues.put(prod_col_9, prod.get(6));
        contentValues.put(prod_col_10, prod.get(7));
        contentValues.put(prod_col_11, prod.get(9));
        contentValues.put(prod_col_12, prod.get(10));
        contentValues.put(prod_col_13, prod.get(8));

        long res = db.insert(TABLE_PRODUCTS, null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public Cursor retrieveData(String sid, String shoppingMethod){
        SQLiteDatabase db = this.getWritableDatabase();
        /* Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Store_ID="+sid+" AND ShoppingMethod =?", new String[]{shoppingMethod}); */
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Store_ID =? AND ShoppingMethod =?", new String[]{sid, shoppingMethod});
        return res;
    }

    public void Delete_all_rows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_PRODUCTS);
    }

    public void DeleteData_by_id(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_PRODUCTS+" WHERE id = "+id);
    }

    public Boolean product_exists(String b_code, String sid, String sMethod){
        SQLiteDatabase db = this.getWritableDatabase();
        /* Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Barcode="+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = ?", new String[]{sMethod}); */
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Barcode =? AND Store_ID =? AND ShoppingMethod =?", new String[]{b_code, sid, sMethod});
        /* Log.d(TAG, "Product_Exists : "+res.getCount()); */
        return res.getCount() > 0;
    }

    public Boolean update_product(String b_code, String sid, int qty, String sMethod){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Number_of_Items = Number_of_Items + "+qty+" WHERE Barcode = "+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = '"+sMethod+"'");
            db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Total_Amount = Number_of_Items * Our_Price WHERE Barcode = "+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = '"+sMethod+"'");
            return true;
        } catch(SQLException e) {
            return false;
        }
    }

    public Boolean update_quantity(String operation, String b_code, String sid, String sMethod){

        SQLiteDatabase db = this.getWritableDatabase();
        try{
            if(operation.equals("increase"))
                db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Number_of_Items = Number_of_Items + 1 WHERE Barcode = "+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = '"+sMethod+"'");
            else if(operation.equals("decrease"))
                db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Number_of_Items = Number_of_Items - 1 WHERE Barcode = "+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = '"+sMethod+"'");

            db.execSQL("UPDATE "+TABLE_PRODUCTS+" SET Total_Amount = Number_of_Items * Our_Price WHERE Barcode = "+b_code+" AND Store_ID="+sid+" AND ShoppingMethod = '"+sMethod+"'");
            return true;
        } catch(SQLException e) {
            return false;
        }

    }

    public Cursor getProductQuantity(String sid, String b_code, String sMethod) {

        SQLiteDatabase db = this.getWritableDatabase();
        /* Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Store_ID = "+sid+" AND Barcode = "+b_code+" AND ShoppingMethod =?", new String[]{sMethod}); */
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_PRODUCTS+" WHERE Store_ID =? AND Barcode =? AND ShoppingMethod =?", new String[]{sid, b_code, sMethod});
        return res;

    }

}
