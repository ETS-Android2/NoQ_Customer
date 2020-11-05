package com.younoq.noq.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.younoq.noq.classes.Product;
import com.younoq.noq.models.DBHelper;
import com.younoq.noq.R;
import com.younoq.noq.models.SaveInfoLocally;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context ctx;
    private List<Product> ProductList;
    public static final String TAG = "ProductAdapter";
    public Double tot = 0.0;
    private onItemClickListener mListener;
    SharedPreferences sharedPreferences;
    DBHelper dbHelper;
    SaveInfoLocally saveInfoLocally;

    public interface  onItemClickListener{
//        void onItemClick(int position);
        void onDeleteClick(int position, int id, double our_price, double mrp, int qty, double retail_price, double discount, boolean delete);

        void onDecreaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount, boolean delete);

        void onIncreaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public ProductAdapter(Context ctx, List<Product> productList) {
        this.ctx = ctx;
        ProductList = productList;
        dbHelper = new DBHelper(ctx);
        saveInfoLocally = new SaveInfoLocally(ctx);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = ProductList.get(holder.getAdapterPosition());

        String img_name = product.getBarcode();
        img_name += ".png";

        // Fetching the Available Quantity from the Product Details.
        Log.d(TAG, product.getProduct_name()+"'s Quantity Available' : "+product.getQuantity());
        holder.quantity_available = Integer.parseInt(product.getQuantity());

        String res = product.hasImage();
//        Log.d(TAG, "Product has Image ? : "+res);

        final boolean has_image = res.toLowerCase().equals("true");

        if (has_image) {

            String sid = product.getStore_id();
            String url;

            if(sid.equals("3")){
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/school_images/"+img_name;
                Picasso.get()
                        .load(url)
                        .fit()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.im);
            } else {
                url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/images/" + img_name;
                Picasso.get()
                        .load(url)
                        .fit()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.im);
            }

        }

        holder.tv_prod_name.setText(product.getProduct_name());
        final String mrp = "₹"+product.getOur_price();
        holder.tv_prod_price.setText(mrp);
//        Log.d(TAG, " Product : "+product.getProduct_name()+" current_qty : "+qyt);
        tot = Double.parseDouble(product.getTot_amt());
        final String tot1 = "₹" + tot;

        final String qty = product.getCurrent_qty();
        holder.p_qty = Integer.parseInt(qty);
        holder.tv_prod_qty.setText(qty);
        // Retrieving the ShoppingMethod from SharedPreferences.
        final String shoppingMethod = saveInfoLocally.getShoppingMethod();

        holder.im_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                // User can only add products up to the Available quantity.
                if(holder.p_qty < holder.quantity_available){
                    final boolean qty_updated = dbHelper.update_quantity("increase",product.getBarcode(), product.getStore_id(), shoppingMethod);
                    Log.d(TAG, "Quantity Updated :"+qty_updated);
                    if(qty_updated){
                        holder.p_qty += 1;
                        Log.d(TAG, "Increased 1 from :"+product.getProduct_name());
                        final String qty = Integer.toString(holder.p_qty);
                        holder.tv_prod_qty.setText(qty);
                        // Updating the Current Quantity of the Product in Product class, as when the Item
                        // gets recycled it was fetching the old value of Current Qty of the Product.
                        product.setCurrent_qty(String.valueOf(holder.p_qty));
                    } else {
                        flag = false;
                        Toast.makeText(v.getContext(), "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                    }
                    // Updating the Amount in the Cart.
                    if(mListener != null && flag){
                        if(position != RecyclerView.NO_POSITION){
                            final int id = product.getId();
                            final double our_price = Double.parseDouble(product.getOur_price());
                            final double mrp = Double.parseDouble(product.getMrp());
                            final double retail_price = Double.parseDouble(product.getRetailers_price());
                            final double discount = Double.parseDouble(product.getTotal_discount());
                            mListener.onIncreaseQuantity(position, id, mrp, our_price, retail_price, discount);
                        }
                    }
                } else {
                    Toast.makeText(v.getContext(), "You have reached the max. available qty for this product.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.im_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if(holder.p_qty > 1){
                    final boolean qty_updated = dbHelper.update_quantity("decrease",product.getBarcode(), product.getStore_id(), shoppingMethod);
                    Log.d(TAG, "Quantity Updated :"+qty_updated);
                    if(qty_updated){
                        holder.p_qty -= 1;
                        Log.d(TAG, "Deleted 1 from :"+product.getProduct_name());
                        final String qty = Integer.toString(holder.p_qty);
                        holder.tv_prod_qty.setText(qty);
                        // Updating the Current Quantity of the Product in Product class, as when the Item
                        // gets recycled it was fetching the old value of Current Qty of the Product.
                        product.setCurrent_qty(String.valueOf(holder.p_qty));
                    } else {
                        flag = false;
                        Toast.makeText(v.getContext(), "Some Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                    }
                } else if(holder.p_qty == 1){
                    holder.delete_product = true;
                }
                if(mListener != null && flag){
                    if(position != RecyclerView.NO_POSITION){
                        final int id = product.getId();
                        final double our_price = Double.parseDouble(product.getOur_price());
                        final double mrp = Double.parseDouble(product.getMrp());
                        final double retail_price = Double.parseDouble(product.getRetailers_price());
                        final double discount = Double.parseDouble(product.getTotal_discount());
                        mListener.onDecreaseQuantity(position, id, mrp, our_price, retail_price, discount, holder.delete_product);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        int p_qty = 0, quantity_available = 0;
        boolean delete_product = false;
        ImageView im, im_add, im_delete, im_delete_item;
        TextView tv_prod_name, tv_prod_price, tv_prod_qty;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            im = itemView.findViewById(R.id.c_im);
            im_add = itemView.findViewById(R.id.ca_add);
            im_delete = itemView.findViewById(R.id.ca_delete);
            tv_prod_qty = itemView.findViewById(R.id.ca_qty);
            tv_prod_name = itemView.findViewById(R.id.ca_prod_name);
            tv_prod_price = itemView.findViewById(R.id.ca_prod_price);
            im_delete_item = itemView.findViewById(R.id.ci_delete);

            im_delete_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            Product product = ProductList.get(position);
                            final int id = product.getId();
                            final double our_price = Double.parseDouble(product.getOur_price());
                            final double mrp = Double.parseDouble(product.getMrp());
                            final double retail_price = Double.parseDouble(product.getRetailers_price());
                            final double discount = Double.parseDouble(product.getTotal_discount());
                            mListener.onDeleteClick(position, id, our_price, mrp, p_qty, retail_price, discount, true);
                        }
                    }
                }
            });

        }
    }

}
