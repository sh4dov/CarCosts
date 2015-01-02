package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;

import java.io.File;

public class OilImporter extends ImporterBase{

    public OilImporter(File file, DbHandler dbHandler, Notificator notificator) {
        super(file, dbHandler, notificator);
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.oil;
    }

    @Override
    protected ContentValues createContentValues(String[] values) {
        ContentValues cv = new ContentValues();
        cv.put(DbHandler.Tables.Oil.date, values[0]);
        cv.put(DbHandler.Tables.Oil.liters, values[1]);
        if(values.length > 2) {
            cv.put(DbHandler.Tables.Oil.comment, values[2]);
        }
        return cv;
    }
}

