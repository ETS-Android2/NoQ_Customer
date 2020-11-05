package com.younoq.noq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class MyPageFragmentAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;
    int[] layouts = {R.layout.activity_slide_first_fragment, R.layout.activity_slide_second_fragment, R.layout.activity_slide_third_fragment};

    public MyPageFragmentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view==(ConstraintLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View one = layoutInflater.inflate(R.layout.activity_slide_first_fragment, container, false);
        View two = layoutInflater.inflate(R.layout.activity_slide_second_fragment, container, false);
        View three = layoutInflater.inflate(R.layout.activity_slide_third_fragment, container, false);
        View[] viewarr = {one, two, three};
        container.addView(viewarr[position]);
        return viewarr[position];
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
