package com.sh4dov.common.gdrive;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.infrastructure.ToastNotificator;
import com.sh4dov.carcosts.infrastructure.exporters.CostExporter;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterBase;
import com.sh4dov.carcosts.infrastructure.exporters.ExporterFactory;
import com.sh4dov.carcosts.infrastructure.exporters.FuelExporter;
import com.sh4dov.carcosts.infrastructure.exporters.OilExporter;
import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressIndicator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.TaskScheduler;
import com.sh4dov.google.DriveService;
import com.sh4dov.google.FileHelper;
import com.sh4dov.google.builders.FileBuilder;
import com.sh4dov.google.listeners.FolderListener;
import com.sh4dov.google.listeners.GetFilesListener;
import com.sh4dov.google.listeners.OnFailedListener;
import com.sh4dov.google.listeners.UploadFileListener;
import com.sh4dov.google.listeners.UserRecoverableRequestCodeProvider;

import java.util.ArrayList;
import java.util.List;

public class GDriveBackup {
    private final DriveService backupGDriveService;
    private final ProgressDialog progressDialog;
    private final Notificator notificator;
    private String costBackupName = "cost.backup.csv";
    private String fuelBackupName = "fuel.backup.csv";
    private String oilBackupName = "oil.backup.csv";
    private String backupRootFolder = "backups";
    private String backupFolder = "Car Costs backups";
    private Activity activity;

    public GDriveBackup(Activity activity, final int reconnectRequestCode) {
        this.activity = activity;
        backupGDriveService = new DriveService(activity)
                .addScope(DriveService.DRIVE)
        .setUserRecoverableRequestCodeProvider(new UserRecoverableRequestCodeProvider() {
            @Override
            public int getRequestCode() {
                return reconnectRequestCode;
            }
        });
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        notificator = new ToastNotificator(activity);
    }

    private void backupOnGDrive() {
        progressDialog.show();
        progressDialog.setMessage("connecting...");
        backupGDriveService.getFiles(new GetFilesListener() {
            File costBackup = FileBuilder.createNewFile().setTitle(costBackupName).build();
            File fuelBackup = FileBuilder.createNewFile().setTitle(fuelBackupName).build();
            File oilBackup = FileBuilder.createNewFile().setTitle(oilBackupName).build();
            File backupRoot
                    ,
                    backup;

            @Override
            public void onGetFiles(List<File> files) {
                for (File file : files) {
                    if (file.getTitle().equalsIgnoreCase(costBackupName)) {
                        costBackup = file;
                    } else if (file.getTitle().equalsIgnoreCase(fuelBackupName)) {
                        fuelBackup = file;
                    } else if (file.getTitle().equalsIgnoreCase(oilBackupName)) {
                        oilBackup = file;
                    } else if (FileHelper.isFolder(file)) {
                        if (file.getTitle().equalsIgnoreCase(backupRootFolder)) {
                            backupRoot = file;
                        } else if (file.getTitle().equalsIgnoreCase(backupFolder)) {
                            backup = file;
                        }
                    }
                }

                if (backupRoot == null) {
                    backupRoot = FileBuilder
                            .createNewFolder()
                            .setTitle(backupRootFolder)
                            .build();
                    createFolder(backupRoot);
                    return;
                }

                if (backup == null) {
                    backup = FileBuilder
                            .createNewFolder()
                            .setTitle(backupFolder)
                            .build();
                    ParentReference parentReference = new ParentReference();
                    parentReference.setId(backupRoot.getId());
                    ArrayList<ParentReference> parents = new ArrayList<ParentReference>();
                    parents.add(parentReference);
                    backup.setParents(parents);
                    createFolder(backup);
                    return;
                }

                ParentReference parentReference = new ParentReference();
                parentReference.setId(backup.getId());
                ArrayList<ParentReference> parentReferences = new ArrayList<ParentReference>();
                parentReferences.add(parentReference);
                costBackup.setParents(parentReferences);
                fuelBackup.setParents(parentReferences);
                oilBackup.setParents(parentReferences);

                progressDialog.hide();

                final Runnable oilJob = new Runnable() {
                    @Override
                    public void run() {
                        exportToGDrive(new ExporterFactory() {
                            @Override
                            public ExporterBase create(DbHandler dbHandler) {
                                return new OilExporter(dbHandler);
                            }
                        }, oilBackup, oilBackupName, null);
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
                        }, fuelBackup, fuelBackupName, oilJob);
                    }
                };

                exportToGDrive(new ExporterFactory() {
                    @Override
                    public ExporterBase create(DbHandler dbHandler) {
                        return new CostExporter(dbHandler);
                    }
                }, costBackup, costBackupName, fuelJob);
            }

            protected void createFolder(File folder) {
                backupGDriveService.uploadFolder(folder, new FolderListener() {
                    @Override
                    public void onUpdatedFolder(File file) {
                        backupOnGDrive();
                    }
                }, new OnFailedListener() {
                    @Override
                    public void onFailed(Exception e) {
                        progressDialog.hide();
                        notificator.showInfo(e.getMessage());
                    }
                }, null);
            }
        }, new OnFailedListener() {
            @Override
            public void onFailed(Exception e) {
                progressDialog.hide();
                notificator.showInfo(e.getMessage());
            }
        }, null);
    }

    public void backup(String accountName) {
        backupGDriveService
                .setAccountName(accountName)
                .setApplicationName("Car Costs");
        backupOnGDrive();
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
                            progressDialog.show();
                            progressDialog.setMessage("backup to " + backupName);
                            backupGDriveService.uploadFile(file, content[0].getBytes(), new UploadFileListener() {
                                @Override
                                public void onUploaded(File file) {
                                    notificator.showInfo(activity.getText(R.string.backup_created) + ": " + backupName);
                                    progressDialog.hide();

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
                                    progressDialog.hide();
                                    notificator.showInfo(e.getMessage());
                                }
                            }, null);
                        }
                    }
                }));
        pointer.setProgressPointer(progressIndicator);
        progressIndicator.execute();
    }


    public void close() {
        progressDialog.hide();
        backupGDriveService.close();
    }
}
