package com.sh4dov.carcosts.controllers;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.AccountPicker;
import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.infrastructure.FragmentFactory;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.SectionsPagerAdapter;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.infrastructure.exporters.CostExporter;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterBase;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterFactory;
import com.sh4dov.carcosts.infrastructure.exporters.FuelExporter;
import com.sh4dov.carcosts.infrastructure.exporters.OilExporter;
import com.sh4dov.carcosts.infrastructure.importers.CostImporter;
import com.sh4dov.carcosts.infrastructure.importers.FuelImporter;
import com.sh4dov.carcosts.infrastructure.importers.ImporterBase;
import com.sh4dov.carcosts.infrastructure.importers.OilImporter;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.FileDialogBase;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.OpenFileDialog;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.SaveFileDialog;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.common.gdrive.GDriveBackup;
import com.sh4dov.common.gdrive.GDriveBase;
import com.sh4dov.common.gdrive.GDriveRestore;
import com.sh4dov.google.DriveService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity implements FragmentOperator, FuelListFragment.EditFuelListener, CostListFragment.EditCostListener, OilListFragment.EditOilListener {
    private String costBackupName = "cost.backup.csv";
    private String fuelBackupName = "fuel.backup.csv";
    private GDriveBackup gDriveBackup;
    private GDriveRestore gDriveRestore;
    private String oilBackupName = "oil.backup.csv";
    private SectionsPagerAdapter pagerAdapter;
    private File path = null;
    private ViewPager viewPager;

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
    public void goToFragment(int fragmentId) {
        viewPager.setCurrentItem(fragmentId);
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
                                try {
                                    return new CostImporter(new FileReader(file), dbHandler, notificator);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    notificator.showInfo(e.getMessage());
                                }
                                return null;
                            }
                        },
                        FragmentFactory.FragmentPosition.CostsList);
                return true;

            case R.id.action_import_oil:
                importFromCsv(
                        new ImporterFactory() {
                            @Override
                            public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator) {
                                try {
                                    return new OilImporter(new FileReader(file), dbHandler, notificator);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    notificator.showInfo(e.getMessage());
                                }
                                return null;
                            }
                        },
                        FragmentFactory.FragmentPosition.OilList);
                return true;

            case R.id.action_import_refueling:
                importFromCsv(
                        new ImporterFactory() {
                            @Override
                            public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator) {
                                try {
                                    return new FuelImporter(new FileReader(file), dbHandler, notificator);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    notificator.showInfo(e.getMessage());
                                }
                                return null;
                            }
                        },
                        FragmentFactory.FragmentPosition.RefuelingList);
                return true;

            case R.id.action_backup:
                choseAccount(RequestCodes.Backup);
                return true;

            case R.id.action_restore:
                choseAccount(RequestCodes.Restore);
                return true;

            case R.id.action_export_costs:
                exportToCsv(costBackupName, new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new CostExporter(dbHandler);
                    }
                });
                return true;

            case R.id.action_export_oil:
                exportToCsv(oilBackupName, new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new OilExporter(dbHandler);
                    }
                });
                return true;

            case R.id.action_export_refueling:
                exportToCsv(fuelBackupName, new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new FuelExporter(dbHandler);
                    }
                });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void reload() {
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCodes.EditFuel:
            case RequestCodes.EditCost:
            case RequestCodes.EditOil:
                reload();
                break;

            case RequestCodes.Backup:
                if (resultCode == RESULT_OK) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    gDriveBackup.backup(accountName);
                }
                break;

            case RequestCodes.Restore:
                if (resultCode == RESULT_OK) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    gDriveRestore.restore(accountName);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(FragmentFactory.FragmentPosition.AddRefueling);
        DriveService driveService = GDriveBase.createService(this);
        gDriveBackup = new GDriveBackup(driveService, this, RequestCodes.Backup);
        gDriveRestore = new GDriveRestore(driveService, this, RequestCodes.Restore, this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        pagerAdapter.saveState();
    }

    protected void onStop() {
        super.onStop();
        gDriveBackup.close();
        gDriveRestore.close();
    }

    private void choseAccount(int requestCode) {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, true, null, null, null, null);
        startActivityForResult(intent, requestCode);
    }

    private void exportToCsv(String fileName, final ExporterFactory exporterFactory) {
        final Activity activity = this;
        new SaveFileDialog()
                .addFileName(fileName)
                .addListeners(new FileDialogBase.DialogListener() {
                    @Override
                    public void selected(final File file) {
                        final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
                        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new Runnable() {
                            @Override
                            public void run() {
                                savePath(file);
                                String fileContent = exporterFactory.create(new DbHandler(activity)).exportToString(pointer);
                                saveFile(file, fileContent);
                            }
                        });
                        pointer.setProgressPointer(progressIndicator);
                        progressIndicator.execute();
                    }
                })
                .setPath(path)
                .show(getFragmentManager(), "");
    }

    private void importFromCsv(final ImporterFactory importerFactory, final int fragmentId) {
        final Activity activity = this;
        final DbHandler dbHandler = new DbHandler(activity);
        final Notificator notificator = new ToastNotificator(activity);

        new OpenFileDialog()
                .addListeners(new OpenFileDialog.DialogListener() {
                    @Override
                    public void selected(final File file) {
                        final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
                        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                                .willExecute(new Runnable() {
                                    @Override
                                    public void run() {
                                        savePath(file);
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
                })
                .setPath(path)
                .show(getFragmentManager(), "");
    }

    private void saveFile(File file, String fileContent) {
        Notificator notificator = new ToastNotificator(this);

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    notificator.showInfo(R.string.cannot_create_file);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                notificator.showInfo(e.getMessage());
                return;
            }
        }

        try {
            FileOutputStream stream = new FileOutputStream(file, false);
            stream.write(fileContent.getBytes());
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            notificator.showInfo(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            notificator.showInfo(e.getMessage());
        }

        notificator.showInfo(R.string.file_saved);
    }

    private void savePath(File file) {
        if (file == null) {
            return;
        }

        if (file.isFile() || file.getParentFile().isDirectory()) {
            path = file.getParentFile();
            return;
        }

        if (file.isDirectory()) {
            path = file;
        }
    }


    private interface ImporterFactory {
        public ImporterBase create(File file, DbHandler dbHandler, Notificator notificator);
    }

    private static class RequestCodes {
        public static final int EditFuel = 1;
        public static final int EditCost = 2;
        public static final int EditOil = 3;
        public static final int Backup = 4;
        public static final int Restore = 5;
    }
}
