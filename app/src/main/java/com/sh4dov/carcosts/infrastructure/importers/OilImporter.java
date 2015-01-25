package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;

import java.io.Reader;

public class OilImporter extends ImporterBase {

    public OilImporter(Reader reader, DbHandler dbHandler, Notificator notificator) {
        super(reader, dbHandler, notificator);
    }

    @Override
    protected ContentValues createContentValues(String[] values) {
        ContentValues cv = new ContentValues();
        putIfCan(DbHandler.Tables.Oil.date, values, cv, 0);
        putIfCan(DbHandler.Tables.Oil.liters, values, cv, 1);
        putIfCan(DbHandler.Tables.Oil.comment, values, cv, 2);
        putIfCan(DbHandler.Tables.Oil.isDeleted, values, cv, 3);

        return cv;
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.oil;
    }
}

