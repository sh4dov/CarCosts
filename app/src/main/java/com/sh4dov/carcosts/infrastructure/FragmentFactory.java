package com.sh4dov.carcosts.infrastructure;

import android.app.Fragment;

import com.sh4dov.carcosts.controllers.AddCostFragment;
import com.sh4dov.carcosts.controllers.AddFuelFragment;
import com.sh4dov.carcosts.controllers.AddOilFragment;
import com.sh4dov.carcosts.controllers.CostListFragment;
import com.sh4dov.carcosts.controllers.FuelListFragment;
import com.sh4dov.carcosts.controllers.OilListFragment;
import com.sh4dov.carcosts.controllers.OverviewFragment;

public class FragmentFactory {
    private int[] positions = new int[]{
            FragmentPosition.Overview,
            FragmentPosition.AddRefueling,
            FragmentPosition.RefuelingList,
            FragmentPosition.AddCost,
            FragmentPosition.CostsList,
            FragmentPosition.AddOil,
            FragmentPosition.OilList
    };

    public FragmentFactory() {
    }

    public Fragment create(int position) {
        switch (position) {
            case FragmentPosition.Overview:
            default:
                return new OverviewFragment();

            case FragmentPosition.AddRefueling:
                return new AddFuelFragment();

            case FragmentPosition.RefuelingList:
                return new FuelListFragment();

            case FragmentPosition.AddCost:
                return new AddCostFragment();

            case FragmentPosition.CostsList:
                return new CostListFragment();

            case FragmentPosition.AddOil:
                return new AddOilFragment();

            case FragmentPosition.OilList:
                return new OilListFragment();
        }

    }

    public int getCount() {
        return positions.length;
    }

    public static class FragmentPosition {
        public static final int Overview = 0;
        public static final int AddRefueling = 1;
        public static final int RefuelingList = 2;
        public static final int AddCost = 3;
        public static final int CostsList = 4;
        public static final int AddOil = 5;
        public static final int OilList = 6;
    }
}
