package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.SectionsPagerAdapter;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.model.Fuel;

public class MainActivity extends Activity implements FragmentOperator, FuelListFragment.EditFuelListener, CostListFragment.EditCostListener {
    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this, this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(FragmentFactory.FragmentPosition.AddCost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToFragment(int fragmentId) {
        viewPager.setCurrentItem(fragmentId);
    }

    @Override
    public void reload() {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void edit(Fuel fuel) {
        Intent intent = new Intent(this, EditFuelActivity.class);
        intent.putExtra(EditFuelActivity.EditFuelKey, fuel);
        startActivityForResult(intent, RequestCodes.EditFuel);
    }

    @Override
    public void edit(Cost cost) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.EditFuel:
                reload();
                break;
        }
    }

    private static class RequestCodes {
        public static final int EditFuel = 1;
    }
}
