package com.sh4dov.carcosts.controllers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.SectionsPagerAdapter;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.infrastructure.importers.CostImporter;
import com.sh4dov.carcosts.infrastructure.importers.FuelImporter;
import com.sh4dov.carcosts.infrastructure.importers.ImporterBase;
import com.sh4dov.carcosts.infrastructure.importers.OilImporter;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.FileDialog;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;

import java.io.File;

public class MainActivity extends Activity implements FragmentOperator, FuelListFragment.EditFuelListener, CostListFragment.EditCostListener, OilListFragment.EditOilListener {
    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(FragmentFactory.FragmentPosition.AddRefueling);
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

        switch (id) {
            case R.id.action_import_costs:
                importFromCsv(
                        new ImporterFactory() {
                            @Override
                            public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator) {
                                return new CostImporter(file, dbHandler, notificator);
                            }
                        },
                        FragmentFactory.FragmentPosition.CostsList);
                return true;

            case R.id.action_import_oil:
                importFromCsv(
                        new ImporterFactory() {
                            @Override
                            public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator) {
                                return new OilImporter(file, dbHandler, notificator);
                            }
                        },
                        FragmentFactory.FragmentPosition.OilList);
                return true;

            case R.id.action_import_refueling:
                importFromCsv(
                        new ImporterFactory() {
                            @Override
                            public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator) {
                                return new FuelImporter(file, dbHandler, notificator);
                            }
                        },
                        FragmentFactory.FragmentPosition.RefuelingList);

        }

        return super.onOptionsItemSelected(item);
    }

    private void importFromCsv(final ImporterFactory importerFactory, final int fragmentId) {
        final Activity activity = this;
        final DbHandler dbHandler = new DbHandler(activity);
        final Notificator notificator = new ToastNotificator(activity);

        new FileDialog().addListeners(new FileDialog.DialogListener() {
            @Override
            public void selected(final File file) {
                final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
                ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                        .willExecute(new Runnable() {
                            @Override
                            public void run() {
                                importerFactory
                                        .create(file, dbHandler, notificator)
                                        .importFromCsv(pointer);
                            }
                        })
                        .willExecuteOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToFragment(fragmentId);
                                reload();
                            }
                        }));
                pointer.setProgressPointer(progressIndicator);
                progressIndicator.execute();
            }
        }).show(getFragmentManager(), "");
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
        Intent intent = new Intent(this, EditCostActivity.class);
        intent.putExtra(EditCostActivity.EditCostKey, cost);
        startActivityForResult(intent, RequestCodes.EditCost);
    }

    @Override
    public void edit(Oil oil) {
        Intent intent = new Intent(this, EditOilActivity.class);
        intent.putExtra(EditOilActivity.EditOilKey, oil);
        startActivityForResult(intent, RequestCodes.EditCost);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.EditFuel:
            case RequestCodes.EditCost:
            case RequestCodes.EditOil:
                reload();
                break;
        }
    }

    private interface ImporterFactory {
        public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator);
    }

    private static class RequestCodes {
        public static final int EditFuel = 1;
        public static final int EditCost = 2;
        public static final int EditOil = 3;
    }
}
