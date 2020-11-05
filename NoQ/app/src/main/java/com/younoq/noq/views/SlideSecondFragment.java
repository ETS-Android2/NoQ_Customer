package com.younoq.noq.views;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.younoq.noq.R;

/**
 * Created by Harsh Chaurasia(Phantom Boy).
 */

public class SlideSecondFragment extends Fragment {

    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.activity_slide_second_fragment, container, false);

        return v;
    }

    public static SlideSecondFragment newInstance() {

        SlideSecondFragment f = new SlideSecondFragment();
        return f;

    }
}
