package com.sh4dov.common.gdrive;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.infrastructure.exporters.CostExporter;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterBase;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterFactory;
import com.sh4dov.carcosts.infrastructure.exporters.FuelExporter;
import com.sh4dov.carcosts.infrastructure.exporters.OilExporter;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.google.DriveService;
import com.sh4dov.google.builders.FileBuilder;
import com.sh4dov.google.listeners.FolderListener;
import com.sh4dov.google.listeners.GetFilesListener;
import com.sh4dov.google.listeners.OnFailedListener;
import com.sh4dov.google.listeners.UploadFileListener;
import com.sh4dov.google.utils.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class GDriveBackup extends GDriveBase implements GetFilesListener {
    private static final String BACKUP_APP_FOLDER_NAME = "Car Costs backups";
    private static final String BACKUP_ROOT_FOLDER_NAME = "backups";
    private static final String COST_BACKUP_NAME = "cost.backup.csv";
    private static final String FUEL_BACKUP_NAME = "fuel.backup.csv";
    private static final String OIL_BACKUP_NAME = "oil.backup.csv";
    File backup;
    File backupRoot;
    File costBackup = FileBuilder.createNewFile().setTitle(COST_BACKUP_NAME).build();
    File fuelBackup = FileBuilder.createNewFile().setTitle(FUEL_BACKUP_NAME).build();
    File oilBackup = FileBuilder.createNewFile().setTitle(OIL_BACKUP_NAME).build();
    private Activity activity;

    public GDriveBackup(DriveService driveService, Activity activity, final int reconnectRequestCode) {
        super(driveService, activity, reconnectRequestCode);
        this.activity = activity;
        getProgressDialog().setCancelable(false);
    }

    public void backup(String accountName) {
        getDriveService()
                .setAccountName(accountName)
                .setApplicationName("Car Costs");
        backupOnGDrive();
    }

    @Override
    public void onGetFiles(List<File> files) {
        backupRoot = FileHelper.firstOrDefault(files, BACKUP_ROOT_FOLDER_NAME, FileHelper.ROOT_ID);
        if (backupRoot == null) {
            createBackupRootFolder();
            return;
        }

        backup = FileHelper.firstOrDefault(files, BACKUP_APP_FOLDER_NAME, backupRoot.getId());
        if (backup == null) {
            createBackupFolder();
            return;
        }

        costBackup = FileHelper.firstOrDefault(files, COST_BACKUP_NAME, backup.getId());
        fuelBackup = FileHelper.firstOrDefault(files, FUEL_BACKUP_NAME, backup.getId());
        oilBackup = FileHelper.firstOrDefault(files, OIL_BACKUP_NAME, backup.getId());
    }

    private void backupOnGDrive() {
        getProgressDialog().show();
        getProgressDialog().setMessage("connecting...");
        getDriveService().getFiles("title contains backup", this, this, this);
    }

    private void createBackup() {
        final Runnable oilJob = new Runnable() {
            @Override
            public void run() {
                exportToGDrive(new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new OilExporter(dbHandler);
                    }
                }, oilBackup, OIL_BACKUP_NAME, null);
            }
        };

        Runnable fuelJob = new Runnable() {
            @Override
            public void run() {
                exportToGDrive(new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new FuelExporter(dbHandler);
                    }
                }, fuelBackup, FUEL_BACKUP_NAME, oilJob);
            }
        };

        exportToGDrive(new ExporterFactory() {
            @Override
            public ExporterBase create(DbHandler dbHandler) {
                return new CostExporter(dbHandler);
            }
        }, costBackup, COST_BACKUP_NAME, fuelJob);
    }

    private void createBackupFolder() {
        if (backup == null) {
            getProgressDialog().setMessage("creating folder " + BACKUP_APP_FOLDER_NAME);
            backup = FileBuilder
                    .createNewFolder()
                    .setTitle(BACKUP_APP_FOLDER_NAME)
                    .build();
            ParentReference parentReference = new ParentReference();
            parentReference.setId(backupRoot.getId());
            ArrayList<ParentReference> parents = new ArrayList<ParentReference>();
            parents.add(parentReference);
            backup.setParents(parents);
            getDriveService().uploadFolder(backup, new FolderListener() {
                @Override
                public void onUpdatedFolder(File file) {
                    backup = file;
                    setBackupParentReference();
                }
            }, this, this);
        } else {
            setBackupParentReference();
        }
    }

    private void createBackupRootFolder() {
        if (backupRoot == null) {
            getProgressDialog().setMessage("creating folder " + BACKUP_ROOT_FOLDER_NAME);
            backupRoot = FileBuilder
                    .createNewFolder()
                    .setTitle(BACKUP_ROOT_FOLDER_NAME)
                    .build();
            getDriveService().uploadFolder(backupRoot, new FolderListener() {
                @Override
                public void onUpdatedFolder(File file) {
                    backupRoot = file;
                    createBackupFolder();
                }
            }, this, this);
        } else {
            createBackupFolder();
        }
    }

    private void exportToGDrive(final ExporterFactory exporterFactory, final File file, final String backupName, final Runnable next) {
        final String[] content = new String[1];
        final ProgressPointerIndicator pointer = new ProgressPointerIndicator();
        ProgressIndicator progressIndicator = new ProgressIndicator(activity, ProgressDialog.STYLE_HORIZONTAL, new TaskScheduler(activity)
                .willExecute(new Runnable() {
                    @Override
                    public void run() {
                        content[0] = exporterFactory.create(new DbHandler(activity)).exportToString(pointer);
                    }
                })
                .willExecuteOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content[0] != null) {
                            getProgressDialog().show();
                            getProgressDialog().setMessage("backup to " + backupName);
                            getDriveService().uploadFile(file, content[0].getBytes(), new UploadFileListener() {
                                @Override
                                public void onUploaded(File file) {
                                    getNotificator().showInfo(activity.getText(R.string.backup_created) + ": " + backupName);
                                    getProgressDialog().hide();

                                    if (next != null) {
                                        next.run();
                                    }
                                }

                                @Override
                                public void onProgress(File file, double v) {

                                }
                            }, new OnFailedListener() {
                                @Override
                                public void onFailed(Exception e) {
                                    getProgressDialog().hide();
                                    getNotificator().showInfo(e.getMessage());
                                }
                            }, null);
                        }
                    }
                }));
        pointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
    }

    private void setBackupParentReference() {
        ParentReference parentReference = new ParentReference();
        parentReference.setId(backup.getId());
        ArrayList<ParentReference> parentReferences = new ArrayList<ParentReference>();
        parentReferences.add(parentReference);
        costBackup.setParents(parentReferences);
        fuelBackup.setParents(parentReferences);
        oilBackup.setParents(parentReferences);

        getProgressDialog().hide();

        createBackup();
    }
}
