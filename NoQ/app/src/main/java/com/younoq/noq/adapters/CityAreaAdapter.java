package com.younoq.noq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.younoq.noq.R;
import com.younoq.noq.classes.City;
import com.younoq.noq.classes.CityArea;
import com.younoq.noq.models.SaveInfoLocally;
import com.younoq.noq.views.MyProfile;

import java.util.List;

public class CityAreaAdapter extends RecyclerView.Adapter<CityAreaAdapter.CityAreaViewHolder> {

    private String city_name, phone_no;
    private boolean isDirectLogin;
    private SaveInfoLocally saveInfoLocally;
    private List<CityArea> cityAreaList;
    private Context context;

    public CityAreaAdapter(Context ctx, List<CityArea> caList, String city_name, String p_no, boolean isDL) {
        this.context = ctx;
        this.saveInfoLocally = new SaveInfoLocally(ctx);
        this.city_name = city_name;
        phone_no = p_no;
        cityAreaList = caList;
        isDirectLogin = isDL;
    }

    @NonNull
    @Override
    public CityAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.city_area_card_item, parent, false);
        return new CityAreaAdapter.CityAreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAreaViewHolder holder, int position) {

        final CityArea cityArea = cityAreaList.get(holder.getAdapterPosition());

        final String cityarea = cityArea.getCityArea();

        if (!cityarea.equals(""))
            holder.tv_city_area.setText(cityarea);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfoLocally.setStoreCity(city_name);
                saveInfoLocally.setStoreCityArea(cityArea.getCityArea());

                Intent in = new Intent(v.getContext(), MyProfile.class);
                in.putExtra("Phone", phone_no);
                in.putExtra("isDirectLogin", isDirectLogin);
                v.getContext().startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() { return cityAreaList.size(); }

    public class CityAreaViewHolder extends RecyclerView.ViewHolder {

        TextView tv_city_area;

        public CityAreaViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_city_area = itemView.findViewById(R.id.caci_city_area);

        }
    }

}
