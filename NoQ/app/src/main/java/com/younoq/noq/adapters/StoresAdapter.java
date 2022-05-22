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
import com.younoq.noq.networkhandler.NetworkApi;
import com.younoq.noq.views.ChooseShopType;
import com.younoq.noq.classes.Store;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.R;

import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresViewHolder> {

    private Context ctx;
    private List<Store> StoreList;
    public static final String TAG = "ProductAdapter";

    public StoresAdapter(Context ctx, List<Store> stList) {
        this.ctx = ctx;
        this.StoreList = stList;
    }

    @NonNull
    @Override
    public StoresAdapter.StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.noq_stores_card, parent, false);
        return new StoresAdapter.StoresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresAdapter.StoresViewHolder holder, int position) {
        Store store = StoreList.get(position);

        final String img_name = store.getImg_name();

        final String url = NetworkApi.API_URL +  "/images/store_images/" + img_name;

        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.ic_city_pin)
                .into(holder.im_store_image);

        final String s_name = store.getStore_name();
        holder.tv_store_name.setText(s_name);
        final String s_addr = store.getStore_address() + ", " + store.getStore_city();
        holder.tv_store_addr.setText(s_addr);
        /* final String s_loc = store.getStore_state() + ", " + store.getStore_country();
        holder.tv_store_loc.setText(s_loc);
        final String pin = store.getPincode();
        holder.tv_pin.setText(pin); */


    }

    @Override
    public int getItemCount() {
        return StoreList.size();
    }

    class StoresViewHolder extends RecyclerView.ViewHolder {

        TextView tv_store_name, tv_store_addr, tv_store_loc, tv_pin;
        ImageView im_store_image;

        public StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    Store store = StoreList.get(position);

                    /* final String s_id = store.getStore_id();
                    if (s_id.equals("1")) {

                        Bundle txnData = new Bundle();
                        ArrayList<String> TxnDetailList = new ArrayList<>();

                        TxnDetailList.add(store.getStore_name());
                        TxnDetailList.add(store.getStore_address());
                        TxnDetailList.add(store.getStore_city());
                        TxnDetailList.add(store.getStore_state());
                        TxnDetailList.add(store.getStore_country());

                        txnData.putStringArrayList("storeData", TxnDetailList);

                        Intent in = new Intent(v.getContext(), ShopDetails.class); */
                        SaveInfoLocally saveInfoLocally = new SaveInfoLocally(v.getContext());
                        /* Storing the required Info in SharedPreferences. */
                        saveInfoLocally.set_store_id(store.getStore_id());
                        saveInfoLocally.setStoreName(store.getStore_name());
                        saveInfoLocally.setStoreAddress(store.getStore_address());
                        saveInfoLocally.setRetailer_Phone_No(store.getRetailer_phone_no());
                        saveInfoLocally.setDeliveryCharge(store.getDelivery_charge());
                        saveInfoLocally.setMinCharge(store.getMin_charge());
                        saveInfoLocally.setMaxCharge(store.getMax_charge());
                        saveInfoLocally.setStoreDeliveryDuration(store.getDelivery_duration());
                        Log.d(TAG, "Retailer's Phone No. : "+store.getRetailer_phone_no());

                        final boolean in_store = store.isIn_store();
                        final boolean takeaway = store.isTakeaway();
                        final boolean home_delivery = store.isHome_delivery();
                        /* Storing ShoppingType related Info, in SharedPreferences. */
                        saveInfoLocally.setIs_InStore(in_store);
                        saveInfoLocally.setIs_Takeaway(takeaway);
                        saveInfoLocally.setIs_Home_Delivery(home_delivery);

                        Intent in = new Intent(v.getContext(), ChooseShopType.class);
                        in.putExtra("in_store", in_store);
                        in.putExtra("takeaway", takeaway);
                        in.putExtra("home_delivery", home_delivery);
                        v.getContext().startActivity(in);
                    /* } else {
                        Toast.makeText(v.getContext(), "Service is Temporarily Unavailable in this Store", Toast.LENGTH_SHORT).show();
                    } */

                }
            });

            tv_store_name = itemView.findViewById(R.id.ns_store_name);
            tv_store_addr = itemView.findViewById(R.id.ns_store_addr);
            /* tv_store_loc = itemView.findViewById(R.id.ns_store_loc);
            tv_pin = itemView.findViewById(R.id.ns_store_pin); */
            im_store_image = itemView.findViewById(R.id.ns_store_img);

        }

    }

}
