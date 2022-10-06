package com.mohamed_mosabeh.cookaholics_capstone.origin_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mohamed_mosabeh.cookaholics_capstone.R;

public class HottestFragment extends Fragment {

    public HottestFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hottest, container, false);
    }
}