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
import java.io.Reader;
import java.util.List;

public class GDriveRestore extends GDriveBase implements GetFilesListener {
    File costBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.COST_BACKUP_NAME).build();
    File fuelBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.FUEL_BACKUP_NAME).build();
    File oilBackup = FileBuilder.createNewFile().setTitle(CarCostGDriveConst.OIL_BACKUP_NAME).build();
    private Activity activity;
    private FragmentOperator fragmentOperator;

    public GDriveRestore(DriveService driveService, Activity activity, int reconnectRequestCode, FragmentOperator fragmentOperator) {
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
                getProgressDialog().hide();
                getNotificator().showInfo("Restored");
                fragmentOperator.reload();
            }
        };

        final Runnable oilRunnable = new Runnable() {
            @Override
            public void run() {
                downloadFile(oilBackup, new ImporterFactory() {
                    @Override
                    public ImporterBase create(DbHandler dbHandler, Reader reader) {
                        return new OilImporter(reader, dbHandler, getNotificator());
                    }
                }, CarCostGDriveConst.OIL_BACKUP_NAME, reload);
            }
        };

        final Runnable fuelRunnable = new Runnable() {
            @Override
            public void run() {
                downloadFile(fuelBackup, new ImporterFactory() {
                    @Override
                    public ImporterBase create(DbHandler dbHandler, Reader reader) {
                        return new FuelImporter(reader, dbHandler, getNotificator());
                    }
                }, CarCostGDriveConst.FUEL_BACKUP_NAME, oilRunnable);
            }
        };

        downloadFile(costBackup, new ImporterFactory() {
            @Override
            public ImporterBase create(DbHandler dbHandler, Reader reader) {
                return new CostImporter(reader, dbHandler, getNotificator());
            }
        }, CarCostGDriveConst.COST_BACKUP_NAME, fuelRunnable);
    }

    public void restore(String accountName) {
        getDriveService()
                .setAccountName(accountName)
                .setApplicationName(CarCostGDriveConst.APPLICATION_NAME);
        restoreFromGDrive();
    }

    private void downloadFile(File file, final ImporterFactory importerFactory, final String backupName, final Runnable next) {
        if (file == null) {
            if (next != null) {
                next.run();
            }
            return;
        }

        getProgressDialog().show();
        getProgressDialog().setMessage("Downloading " + file.getTitle());
        getDriveService().downloadFile(file, new DownloadFileListener() {
            @Override
            public void onDownloadedFile(File file, final byte[] bytes) {
                getProgressDialog().hide();
                restoreFromGDrive(importerFactory.create(new DbHandler(activity), new InputStreamReader(new ByteArrayInputStream(bytes))), backupName, next);
            }

            @Override
            public void onProgress(File file, double v) {
            }
        }, this, this);
    }

    private void restoreFromGDrive(final ImporterBase importer, final String backupName, final Runnable next) {
        final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        importer.importFromCsv(pointer);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotificator().showInfo("restored from " + backupName);
                        if (next != null) {
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
