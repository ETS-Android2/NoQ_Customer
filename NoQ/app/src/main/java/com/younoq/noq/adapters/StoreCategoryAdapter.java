package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.younoq.noq.R;
import com.younoq.noq.classes.StoreCategory;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.networkhandler.NetworkApi;
import com.younoq.noq.views.StoresNoq;

import java.util.List;

public class StoreCategoryAdapter extends RecyclerView.Adapter<StoreCategoryAdapter.StoreCategoryViewHolder>{

    Context context;
    List<StoreCategory> categoryList;
    final static String TAG = "StoreCategoryAdapter";

    public StoreCategoryAdapter(Context ctx, List<StoreCategory> cList){
        this.context = ctx;
        categoryList = cList;
    }


    @NonNull
    @Override
    public StoreCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.store_category_item_card, parent, false);
        return new StoreCategoryAdapter.StoreCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCategoryViewHolder holder, int position) {

        StoreCategory storeCategory = categoryList.get(holder.getAdapterPosition());

        final String category_name = storeCategory.getCategory_name();
        holder.tv_store_category_name.setText(category_name);

        final String img_name = storeCategory.getImage_name();
        Log.d(TAG, category_name + " - img_name -> "+img_name);

        final String url = NetworkApi.API_URL + "/images/store_category_images/" + img_name;

        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_store_category_image);

        final boolean available = storeCategory.isAvailable();
        if (available) {
            holder.tv_store_category_availability.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_store_category_availability.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() { return categoryList.size(); }

    class StoreCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView im_store_category_image;
        TextView tv_store_category_name, tv_store_category_availability;
        SaveInfoLocally saveInfoLocally;

        public StoreCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            im_store_category_image = itemView.findViewById(R.id.scic_store_category_image);
            tv_store_category_name = itemView.findViewById(R.id.scic_store_category_name);
            tv_store_category_availability = itemView.findViewById(R.id.scic_availability);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){

                        StoreCategory storeCategory = categoryList.get(position);

                        final boolean available = storeCategory.isAvailable();
                        if(available){

                            /* Saving the CategoryStores in SharedPreferences */
                            saveInfoLocally = new SaveInfoLocally(v.getContext());
                            saveInfoLocally.setCategoryStores(storeCategory.getStoreList().toString());

                            Intent in = new Intent(v.getContext(), StoresNoq.class);
                            in.putExtra("storesList", storeCategory.getStoreList().toString());
                            v.getContext().startActivity(in);

                        } else {

                            Toast.makeText(v.getContext(), "This category isn't available yet", Toast.LENGTH_SHORT).show();

                        }


                    }



                }
            });

        }

    }

}
