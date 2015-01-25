package com.sh4dov.common.gdrive;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.api.services.drive.model.File;
import com.sh4dov.carcosts.infrastructure.FragmentOperator;
import com.sh4dov.carcosts.infrastructure.importers.CostImporter;
import com.sh4dov.carcosts.infrastructure.importers.FuelImporter;
import com.sh4dov.carcosts.infrastructure.importers.ImporterBase;
import com.sh4dov.carcosts.infrastructure.importers.ImporterFactory;
import com.sh4dov.carcosts.infrastructure.importers.OilImporter;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.google.DriveService;
import com.sh4dov.google.builders.FileBuilder;
import com.sh4dov.google.listeners.DownloadFileListener;
import com.sh4dov.google.listeners.GetFilesListener;
import com.sh4dov.google.utils.FileHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class GDriveResore extends GDriveBase implements GetFilesListener {
    File costBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.COST_BACKUP_NAME).build();
    File fuelBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.FUEL_BACKUP_NAME).build();
    File oilBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.OIL_BACKUP_NAME).build();
    private Activity activity;
    private FragmentOperator fragmentOperator;

    public GDriveResore(DriveService driveService, Activity activity, int reconnectRequestCode, FragmentOperator fragmentOperator) {
        super(driveService, activity, reconnectRequestCode);
        this.activity = activity;
        this.fragmentOperator = fragmentOperator;
    }

    @Override
    public void onGetFiles(List<File> files) {
        File backupRoot = FileHelper.firstOrDefault(files, CarCostGDriveConst.BACKUP_ROOT_FOLDER_NAME, FileHelper.ROOT_ID);
        if (backupRoot == null) {
            showThereIsNoBackup();
            return;
        }

        File backup = FileHelper.firstOrDefault(files, CarCostGDriveConst.BACKUP_APP_FOLDER_NAME, backupRoot.getId());
        if (backup == null) {
            showThereIsNoBackup();
            return;
        }

        costBackup = FileHelper.firstOrDefault(files, CarCostGDriveConst.COST_BACKUP_NAME, backup.getId());
        fuelBackup = FileHelper.firstOrDefault(files, CarCostGDriveConst.FUEL_BACKUP_NAME, backup.getId());
        oilBackup = FileHelper.firstOrDefault(files, CarCostGDriveConst.OIL_BACKUP_NAME, backup.getId());

        getProgressDialog().hide();

        final Runnable reload = new Runnable() {
            @Override
            public void run() {
                getNotificator().showInfo("Restored");
                fragmentOperator.reload();
            }
        };

        final Runnable oilRunnable = new Runnable() {
            @Override
            public void run() {
                if (oilBackup != null) {
                    getProgressDialog().show();
                    getProgressDialog().setMessage("Downloading " + oilBackup.getTitle());
                    getDriveService().downloadFile(oilBackup, new DownloadFileListener() {
                        @Override
                        public void onDownloadedFile(File file, final byte[] bytes) {
                            getProgressDialog().hide();
                            restoreFromGDrive(new ImporterFactory() {
                                @Override
                                public ImporterBase create(DbHandler dbHandler) {
                                    return new OilImporter(new InputStreamReader(new ByteArrayInputStream(bytes)), dbHandler, getNotificator());
                                }
                            }, CarCostGDriveConst.OIL_BACKUP_NAME, reload);
                        }

                        @Override
                        public void onProgress(File file, double v) {

                        }
                    }, GDriveResore.this, GDriveResore.this);
                }
                else {
                    reload.run();
                }
            }
        };

        final Runnable fuelRunnable = new Runnable() {
            @Override
            public void run() {
                if (fuelBackup != null) {
                    getProgressDialog().show();
                    getProgressDialog().setMessage("Downloading " + fuelBackup.getTitle());
                    getDriveService().downloadFile(fuelBackup, new DownloadFileListener() {
                        @Override
                        public void onDownloadedFile(File file, final byte[] bytes) {
                            getProgressDialog().hide();
                            restoreFromGDrive(new ImporterFactory() {
                                @Override
                                public ImporterBase create(DbHandler dbHandler) {
                                    return new FuelImporter(new InputStreamReader(new ByteArrayInputStream(bytes)), dbHandler, getNotificator());
                                }
                            }, CarCostGDriveConst.FUEL_BACKUP_NAME, oilRunnable);
                        }

                        @Override
                        public void onProgress(File file, double v) {

                        }
                    }, GDriveResore.this, GDriveResore.this);
                } else {
                    oilRunnable.run();
                }
            }
        };

        if (costBackup != null) {
            getProgressDialog().show();
            getProgressDialog().setMessage("Downloading " + costBackup.getTitle());
            getDriveService().downloadFile(costBackup, new DownloadFileListener() {
                @Override
                public void onDownloadedFile(File file, final byte[] bytes) {
                    getProgressDialog().hide();
                    restoreFromGDrive(new ImporterFactory() {
                        @Override
                        public ImporterBase create(DbHandler dbHandler) {
                            return new CostImporter(new InputStreamReader(new ByteArrayInputStream(bytes)), dbHandler, getNotificator());
                        }
                    }, CarCostGDriveConst.COST_BACKUP_NAME, fuelRunnable);
                }

                @Override
                public void onProgress(File file, double v) {

                }
            }, this, this);
        } else {
            fuelRunnable.run();
        }
    }

    public void restore(String accountName) {
        getDriveService()
                .setAccountName(accountName)
                .setApplicationName(CarCostGDriveConst.APPLICATION_NAME);
        restoreFromGDrive();
    }

    private void restoreFromGDrive(final ImporterFactory importerFactory, final String backupName, final Runnable next) {
        final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        importerFactory.create(new DbHandler(activity)).importFromCsv(pointer);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificator().showInfo("restored from " + backupName);
                        if(next != null){
                            next.run();
                        }
                    }
                })
        );
        pointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
    }

    private void restoreFromGDrive() {
        getProgressDialog().show();
        getProgressDialog().setMessage("connecting...");
        getDriveService().getFiles("title contains 'backup'", this, this, this);
    }

    private void showThereIsNoBackup() {
        getNotificator().showInfo("There is no backup");
    }


}
