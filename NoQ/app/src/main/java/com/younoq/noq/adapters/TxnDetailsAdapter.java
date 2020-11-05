package com.younoq.noq.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq.classes.Product;
import com.younoq.noq.R;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class TxnDetailsAdapter extends RecyclerView.Adapter<TxnDetailsAdapter.TxnDetailViewHolder> {

    Context context;
    List<Product> productList;

    public TxnDetailsAdapter(Context ctx, List<Product> prodList) {

        this.context = ctx;
        productList = prodList;

    }


    @NonNull
    @Override
    public TxnDetailsAdapter.TxnDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.txn_details_card, parent, false);
        return new TxnDetailsAdapter.TxnDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TxnDetailsAdapter.TxnDetailViewHolder holder, int position) {

        Product product = productList.get(position);

        boolean isExpanded = product.isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        final String no_of_items = product.getCurrent_qty();
        holder.tv_total_orders.setText(no_of_items);

        final String p_name = product.getProduct_name();
        holder.tv_p_name.setText(p_name);

        String tot_amt = "₹" + product.getTot_amt();

        if (isExpanded) {
            holder.separator.setVisibility(View.VISIBLE);
        } else {
            holder.separator.setVisibility(View.GONE);
        }

        holder.tv_tot_amt.setText(tot_amt);

        final String barcode = product.getBarcode();
        holder.tv_barcode.setText(barcode);

        final String retailer_price = "₹" + product.getOur_price();
        holder.tv_product_price.setText(retailer_price);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class TxnDetailViewHolder extends RecyclerView.ViewHolder {

        final String TAG = "TxnDetails Activity";

        TextView tv_p_name, tv_tot_amt, tv_barcode, tv_product_price, tv_total_orders;
        ConstraintLayout expandableLayout;
        LinearLayout linearLayout;
        View separator;

        public TxnDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.tdc_expandable_constraint_layout);
            linearLayout = itemView.findViewById(R.id.tdc_linear0);
            separator = itemView.findViewById(R.id.tcd_separator);

            tv_p_name = itemView.findViewById(R.id.tdc_p_name);
            tv_tot_amt = itemView.findViewById(R.id.tdc_tot_amt);
            tv_barcode = itemView.findViewById(R.id.tdc_bcode);
            tv_product_price = itemView.findViewById(R.id.tdc_product_price);
            tv_total_orders = itemView.findViewById(R.id.tdc_p_qty);
//            tv_our_price = itemView.findViewById(R.id.tdc_our_price);
//            tv_total_discount = itemView.findViewById(R.id.tdc_total_discount);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = productList.get(getAdapterPosition());
                    Log.d(TAG, product.getProduct_name()+" is Expanded : "+product.isExpanded());
                    product.setExpanded(!product.isExpanded());
                    Log.d(TAG, product.getProduct_name()+" is Expanded : "+product.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

    }
}
