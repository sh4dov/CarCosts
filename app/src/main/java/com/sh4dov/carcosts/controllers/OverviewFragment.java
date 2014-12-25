package com.sh4dov.carcosts.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh4dov.carcosts.R;

/**
 * Created by sh4dov on 2014-12-25.
 */
public class OverviewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        return rootView;
    }
}
