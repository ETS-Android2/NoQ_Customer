package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.younoq.noq.classes.Category;
import com.younoq.noq.networkhandler.NetworkApi;
import com.younoq.noq.views.ProductsList;
import com.younoq.noq.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class BottomSheetCategoryAdapter extends RecyclerView.Adapter<BottomSheetCategoryAdapter.BottomSheetCategoryAdapterViewHolder> {

    List<Category> categoriesList;
    Context context;
    private String shoppingMethod;

    public BottomSheetCategoryAdapter(Context ctx, List<Category> cList, String shoppingMethod) {
        this.context = ctx;
        categoriesList = cList;
        this.shoppingMethod = shoppingMethod;
    }

    @NonNull
    @Override
    public BottomSheetCategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_card_item, parent, false);
        return new BottomSheetCategoryAdapter.BottomSheetCategoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetCategoryAdapterViewHolder holder, int position) {

        Category category = categoriesList.get(position);

        final String category_name = category.getCategory_name();
        Log.d("BottomSheetCategory", "Category Name : "+category_name);
        holder.tv_category_name.setText(category_name);

        final String img_name = category.getImg_name();

        final String url = NetworkApi.API_URL +  "/DB/images/product_category_images/" + img_name;

        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_category_image);

    }

    @Override
    public int getItemCount() { return categoriesList.size(); }

    class BottomSheetCategoryAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;
        ImageView im_category_image;

        public BottomSheetCategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_category_name = itemView.findViewById(R.id.cc_category_name);
            im_category_image = itemView.findViewById(R.id.cci_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Category category = categoriesList.get(getAdapterPosition());
                    final String category_name = category.getCategory_name();

                    Intent in = new Intent(v.getContext(), ProductsList.class);
                    in.putExtra("coming_from", "ProductsList");
                    in.putExtra("category_name", category_name);
                    in.putExtra("shoppingMethod", shoppingMethod);
                    v.getContext().startActivity(in);

                }
            });

        }
    }
}
