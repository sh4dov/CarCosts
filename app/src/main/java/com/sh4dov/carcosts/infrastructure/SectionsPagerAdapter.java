package com.sh4dov.carcosts.infrastructure;

/**
 * Created by sh4dov on 2014-12-25.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private final FragmentFactory fragmentFactory;

    public SectionsPagerAdapter(FragmentManager fm, Context context, FragmentOperator fragmentOperator) {
        super(fm);
        fragmentFactory = new FragmentFactory(context, fragmentOperator);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentFactory.create(position);
    }

    @Override
    public int getCount() {
        return fragmentFactory.getCount();
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }
}
