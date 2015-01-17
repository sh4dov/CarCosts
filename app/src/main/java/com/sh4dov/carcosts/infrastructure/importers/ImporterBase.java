package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.NullNotificator;
import com.sh4dov.common.ProgressPointer;
import com.sh4dov.common.ProgressPointerOperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ImporterBase {
    private DbHandler dbHandler;
    private File file;
    private Notificator notificator;

    public ImporterBase(File file, DbHandler dbHandler, Notificator notificator) {
        this.dbHandler = dbHandler;
        this.file = file;
        this.notificator = notificator != null ? notificator : new NullNotificator();
    }

    protected void insert(String table, ArrayList<ContentValues> items, ProgressPointer progressPointer) {
        ProgressPointerOperator pointer = new ProgressPointerOperator(progressPointer);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        pointer.setMax(items.size());
        for (ContentValues item : items) {
            db.insert(table, null, item);
            pointer.addProgress();
        }

        db.close();
    }

    public void importFromCsv(ProgressPointer progressPointer) {
        ArrayList<ContentValues> items = new ArrayList<ContentValues>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                ContentValues cv = createContentValues(values);
                items.add(cv);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            notificator.showInfo(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            notificator.showInfo(e.getMessage());
        }

        dbHandler.clear(getTable());
        insert(getTable(), items, progressPointer);
    }

    protected void putIfCan(String column, String[] values, ContentValues contentValues, int valueSelector){
        if(valueSelector < values.length){
            contentValues.put(column, values[valueSelector]);
        }
    }

    protected abstract String getTable();

    protected abstract ContentValues createContentValues(String[] values);
}
