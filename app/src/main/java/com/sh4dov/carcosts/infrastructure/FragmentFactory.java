package com.sh4dov.carcosts.infrastructure;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.sh4dov.carcosts.controllers.AddFuelFragment;
import com.sh4dov.carcosts.controllers.FuelListFragment;
import com.sh4dov.carcosts.controllers.OverviewFragment;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.carcosts.repositories.FuelRepository;
import com.sh4dov.common.Notificator;

/**
 * Created by sh4dov on 2014-12-25.
 */
public class FragmentFactory {
    private final FuelRepository fuelRepository;
    private int[] positions = new int[]{
            FragmentPosition.Overview,
            FragmentPosition.AddRefueling,
            FragmentPosition.RefuelingList
    };
    private FragmentOperator fragmentOperator;

    public FragmentFactory(Context context, FragmentOperator fragmentOperator) {
        this.fragmentOperator = fragmentOperator;
        Notificator notificator = new ToastNotificator(context);
        fuelRepository = new FuelRepository(new DbHandler(context), notificator);
    }

    public Fragment create(int position) {
        Bundle args = new Bundle();

        switch (position) {
            case FragmentPosition.Overview:
            default:
                OverviewFragment overviewFragment = new OverviewFragment();
                overviewFragment.setArguments(args);
                return overviewFragment;

            case FragmentPosition.AddRefueling:
                AddFuelFragment addRefuelingFragment = new AddFuelFragment();
                addRefuelingFragment.setFuelRepository(fuelRepository);
                addRefuelingFragment.addAddedListener(new AddFuelFragment.AddedListener() {
                    @Override
                    public void added() {
                        fragmentOperator.reload();
                        fragmentOperator.goToFragment(FragmentPosition.RefuelingList);
                    }
                });
                addRefuelingFragment.setArguments(args);
                return addRefuelingFragment;

            case FragmentPosition.RefuelingList:
                FuelListFragment fuelListFragment = new FuelListFragment();
                fuelListFragment.setFuelRepository(fuelRepository);
                fuelListFragment.setArguments(args);
                return fuelListFragment;
        }

    }

    public int getCount() {
        return positions.length;
    }

    public static class FragmentPosition {
        public static final int Overview = 0;
        public static final int AddRefueling = 1;
        public static final int RefuelingList = 2;
    }
}
