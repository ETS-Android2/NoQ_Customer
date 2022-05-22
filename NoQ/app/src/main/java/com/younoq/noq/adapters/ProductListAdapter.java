package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.younoq.noq.classes.Product;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.models.Utilities;
import com.younoq.noq.networkhandler.NetworkApi;
import com.younoq.noq.views.ProductDetails;
import com.younoq.noq.R;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {

    List<Product> productList;
    Context context;
    private CoordinatorLayout coordinatorLayout;
    private String TAG = "ProductListAdapter";
    private String shoppingMethod;
    private Utilities utilities;

    public ProductListAdapter(Context ctx, List<Product> list, String sMethod, CoordinatorLayout clayout) {

        this.context = ctx;
        productList = list;
        utilities = new Utilities(ctx);
        shoppingMethod = sMethod;
        coordinatorLayout = clayout;

    }

    @NonNull
    @Override
    public ProductListAdapter.ProductListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_list_card, parent, false);
        return new ProductListAdapter.ProductListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ProductListAdapterViewHolder holder, int position) {

        Product product = productList.get(position);

        final String product_name = product.getProduct_name();
        holder.tv_product_name.setText(product_name);

        /* final String product_price = "₹" + product.getRetailers_price(); */
        holder.tv_product_price.setText(product.getOur_price());

        String prod_discount = product.getTotal_discount();

        if(Integer.parseInt(prod_discount) > 0){

            holder.tv_prod_mrp.setVisibility(View.VISIBLE);
            holder.tv_prod_mrp_rupees_symbol.setVisibility(View.VISIBLE);
            holder.tv_prod_discount.setVisibility(View.VISIBLE);

            prod_discount = "Save ₹" + product.getTotal_discount();
            holder.tv_prod_discount.setText(prod_discount);

            holder.tv_prod_mrp.setText(product.getMrp());
            holder.tv_prod_mrp.setPaintFlags(holder.tv_prod_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {

            holder.tv_prod_mrp.setVisibility(View.GONE);
            holder.tv_prod_mrp_rupees_symbol.setVisibility(View.GONE);
            holder.tv_prod_discount.setVisibility(View.GONE);

        }

        String res = product.hasImage();

        final boolean has_image = res.toLowerCase().equals("true");
        final String b_code = product.getBarcode();

        String sid = product.getStore_id();

        if (has_image) {

            String url;

            String img_name = product.getBarcode();
            img_name += ".png";

            if(sid.equals("3")){
                url = NetworkApi.API_URL +  "/school_images/" + img_name;
                Picasso.get()
                        .load(url)
                        .fit()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.iv_prod_img);
            } else {
                url = NetworkApi.API_URL + "/images/product_images/" + img_name;
                Log.d(TAG, url);
                /* Picasso.get()
                        .load(url)
                        .fit()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.iv_prod_img); */

                Glide.with(this.context)
                        .load(url)
                        .fitCenter()
                        .into(holder.iv_prod_img);

            }

        } else {

            holder.iv_prod_img.setImageResource(R.drawable.not_found);

        }

        /* Retrieving the Available Quantity for the Product. */
        holder.available_quantity = Integer.parseInt(product.getQuantity());
        Log.d(TAG, product_name + " Available Qty : "+holder.available_quantity);

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.p_qty < holder.available_quantity){
                    holder.p_qty += 1;
                    /* displaying the Update msg to the USer. */
                    holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                } else {
                    Toast.makeText(v.getContext(), "You have reached the max. available qty for this product.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.p_qty > 1){
                    holder.p_qty -= 1;
                    /* displaying the Update msg to the USer. */
                    holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                } else {
                    Toast.makeText(v.getContext(), "You have reached the minimum limit for this Item.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Retrieve Product's Quantity from Local Database if Present. */
        Log.d(TAG, "Barcode : "+b_code+" Shopping Method : "+shoppingMethod);
        final boolean prod_exists_in_db = holder.dbHelper.product_exists(b_code, sid, shoppingMethod);

        if(prod_exists_in_db){

            Log.d(TAG, product.getProduct_name()+" exists in local DB");
            String product_qty_in_db = "0";
            Cursor data = holder.dbHelper.getProductQuantity(sid, b_code, shoppingMethod);

            while(data.moveToNext()){
                product_qty_in_db = data.getString(3);
                Log.d(TAG, data.getString(4)+", Quantity : "+product_qty_in_db);
            }

            holder.product_qty_in_db = Integer.parseInt(product_qty_in_db);
            Log.d(TAG, product.getProduct_name()+" Qty in DB : "+holder.product_qty_in_db);
            holder.tv_prod_display_qty.setText(product_qty_in_db);

        } else {

            Log.d(TAG, product.getProduct_name()+" Doesn't exists in local DB");
            holder.product_qty_in_db = 0;
            holder.tv_prod_display_qty.setText("0");

        }

        /* If Product's Quantity is more than one, then only show the options related to add to cart. */
        if (holder.available_quantity >= 1){

            holder.tv_product_status.setVisibility(View.GONE);
            holder.tv_prod_display_qty.setVisibility(View.VISIBLE);
            holder.tv_prod_qty.setVisibility(View.VISIBLE);
            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
            holder.selective_linear_layout.setVisibility(View.VISIBLE);
            holder.iv_add_to_basket.setVisibility(View.VISIBLE);

        } else {

            holder.selective_linear_layout.setVisibility(View.GONE);
            holder.tv_prod_qty.setVisibility(View.GONE);
            holder.tv_prod_display_qty.setVisibility(View.GONE);
            holder.tv_product_status.setVisibility(View.VISIBLE);
            holder.iv_add_to_basket.setVisibility(View.INVISIBLE);

        }

        holder.iv_add_to_basket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, holder.available_quantity + " > 1 && (("+holder.p_qty+" + "+holder.product_qty_in_db+") <= "+holder.available_quantity+"))");
                if (holder.available_quantity >= 1 && ((holder.p_qty + holder.product_qty_in_db) <= holder.available_quantity)) {

                    List<String> prod = new ArrayList<>();
                    final String b_code = product.getBarcode();
                    final String p_name = product.getProduct_name();
                    final String msg = p_name + " Added";

                    /* Increasing the Product's Display Qty */
                    holder.tv_prod_display_qty.setText(String.valueOf(holder.p_qty + holder.product_qty_in_db));
                    /* Increasing the Product's Qty in Database */
                    holder.product_qty_in_db += holder.p_qty;

                    DBHelper dbHelper = new DBHelper(v.getContext());
                    SaveInfoLocally saveInfoLocally = new SaveInfoLocally(v.getContext());

                    /* Updating the Value of the total_items_in_cart */
                    int total_items_in_cart = saveInfoLocally.getTotalItemsInCart();
                    total_items_in_cart += holder.p_qty;
                    /* Setting the new value to the total_items_in_cart */
                    saveInfoLocally.setTotalItemsInCart(total_items_in_cart);

                    prod.add(product.getStore_id());
                    prod.add(product.getBarcode());
                    prod.add(p_name);
                    prod.add(product.getMrp());
                    prod.add(product.getRetailers_price());
                    prod.add(product.getOur_price());
                    prod.add(product.getTotal_discount());
                    prod.add(product.hasImage());
                    prod.add(product.getCategory());
                    prod.add(product.getQuantity());
                    prod.add(shoppingMethod);

                    final String sid = saveInfoLocally.get_store_id();
                    Log.d(TAG, "Store Id : "+sid);
                    boolean product_exists = false;

                    if(!b_code.equals(" ")){
                        product_exists = dbHelper.product_exists(b_code, sid, shoppingMethod);
                    } else {
                        Toast.makeText(v.getContext(), "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                    }
                    if(product_exists){

                        boolean isUpdated = dbHelper.update_product(b_code, sid, holder.p_qty, shoppingMethod);
                        Log.d(TAG, "isUpdated : "+isUpdated);
                        if(isUpdated){

                            utilities.showTopSnackBar(context, coordinatorLayout, msg, R.color.BLUE);
                            /* Resetting the Value of Product_Quantity as the Product has been added to basket. */
                            holder.p_qty = 1;
                            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                        } else {
                            Toast.makeText(v.getContext(), "Error!! Kindly Try Again..", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        boolean isInserted = dbHelper.insertProductData(prod, holder.p_qty);
                        Log.d(TAG, "isInserted : "+isInserted);
                        if (isInserted){

                            utilities.showTopSnackBar(context, coordinatorLayout, msg, R.color.BLUE);
                            /* Resetting the Value of Product_Quantity as the Product has been added to basket. */
                            holder.p_qty = 1;
                            holder.tv_prod_qty.setText(String.valueOf(holder.p_qty));
                        }else{
                            Toast.makeText(v.getContext(), "Some Problem Occurred, Please Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }

                } else {

                    Toast.makeText(v.getContext(), "Sorry! The required quantity isn't available", Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        int p_qty = 1, available_quantity = 0, product_qty_in_db = 0;
        TextView tv_product_name, tv_product_price, tv_product_status, tv_prod_qty, tv_prod_display_qty, tv_prod_mrp,
                tv_prod_mrp_rupees_symbol, tv_prod_discount;

        ImageView iv_add_to_basket, iv_prod_img, iv_add, iv_delete;
        LinearLayout selective_linear_layout;
        DBHelper dbHelper;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_product_name = itemView.findViewById(R.id.plc_p_name);
            tv_product_price = itemView.findViewById(R.id.plc_p_price);
            tv_product_status = itemView.findViewById(R.id.plc_prod_status);
            tv_prod_qty = itemView.findViewById(R.id.plc_prod_qty);
            iv_add_to_basket = itemView.findViewById(R.id.plc_add_to_basket);
            iv_prod_img = itemView.findViewById(R.id.plc_product_img);
            selective_linear_layout = itemView.findViewById(R.id.plc_in_stock_linear_layout);
            iv_add = itemView.findViewById(R.id.plc_add);
            iv_delete = itemView.findViewById(R.id.plc_delete);
            tv_prod_display_qty = itemView.findViewById(R.id.plc_prod_display_qty);
            tv_prod_mrp = itemView.findViewById(R.id.plc_p_mrp);
            tv_prod_mrp_rupees_symbol = itemView.findViewById(R.id.plc_p_mrp_rupees_symbol);
            tv_prod_discount = itemView.findViewById(R.id.plc_p_discount);

            dbHelper = new DBHelper(context);

            iv_prod_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int position = getAdapterPosition();
                    Bundle prodData = new Bundle();
                    ArrayList<String> prodDetails = new ArrayList<>();

                    Product product = productList.get(position);

                    if(position != RecyclerView.NO_POSITION) {

                        prodDetails.add(product.getStore_id());
                        prodDetails.add(product.getBarcode());
                        prodDetails.add(product.getProduct_name());
                        prodDetails.add(product.getMrp());
                        prodDetails.add(product.getRetailers_price());
                        prodDetails.add(product.getOur_price());
                        prodDetails.add(product.getTotal_discount());
                        prodDetails.add(product.hasImage());
                        prodDetails.add(product.getCategory());
                        /* prodDetails.add(String.valueOf(p_qty)); */
                        prodDetails.add(product.getQuantity());
                        prodDetails.add(shoppingMethod);
                        prodDetails.add(product.getCategory());

                        prodData.putStringArrayList("productDetails", prodDetails);

                    }

                    Intent in = new Intent(v.getContext(), ProductDetails.class);
                    in.putExtra("comingFrom", "ProductList");
                    in.putExtra("shoppingMethod", shoppingMethod);
                    /* Making Sure there are no Issues regarding this in Future. */
                    if(prodData.size() > 0)
                        in.putExtras(prodData);
                    v.getContext().startActivity(in);

                }
            });

        }
    }

}
