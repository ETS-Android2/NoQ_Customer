package com.younoq.noq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq.classes.Product;
import com.younoq.noq.R;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptAdapterViewHolder> {

    Context context;
    List<Product> productList;

    public ReceiptAdapter(Context ctx, List<Product> prodList) {

        this.context = ctx;
        productList = prodList;

    }

    @NonNull
    @Override
    public ReceiptAdapter.ReceiptAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pay_succ_card, null);
        return new ReceiptAdapter.ReceiptAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptAdapter.ReceiptAdapterViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.tv_prod_name.setText(product.getProduct_name());

        final String amt_paid = "₹" + product.getOur_price() + " x " + product.getCurrent_qty();
        holder.tv_amt_paid.setText(amt_paid);

        final String tot_price = "₹" + product.getTot_amt();
        holder.tv_total_price.setText(tot_price);

        final String tot_retail_price = "₹" + product.getRetailers_price();
        holder.tv_retail_price.setText(tot_retail_price);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ReceiptAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_amt_paid, tv_retail_price, tv_total_price, tv_prod_name;

        public ReceiptAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_amt_paid = itemView.findViewById(R.id.psc_amt_paid);
            tv_retail_price = itemView.findViewById(R.id.psc_retail_price);
            tv_total_price = itemView.findViewById(R.id.psc_total_price);
            tv_prod_name = itemView.findViewById(R.id.ps_item_name);

        }
    }
}
