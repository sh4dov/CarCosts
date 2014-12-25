package com.sh4dov.carcosts.infrastructure;

import android.app.Fragment;
import android.os.Bundle;

import com.sh4dov.carcosts.controllers.OverviewFragment;

/**
 * Created by sh4dov on 2014-12-25.
 */
public class FragmentFactory {

    private int[] positions = new int[] {
            FragmentPosition.Overview
    };

    public Fragment create(int position){
        Bundle args = new Bundle();

        switch (position){
            case FragmentPosition.Overview:
            default:
                OverviewFragment fragment = new OverviewFragment();
                fragment.setArguments(args);
                return fragment;
        }

    }

    public int getCount() {
        return positions.length;
    }

    private class FragmentPosition {
        public static final int Overview = 1;
    }
}
