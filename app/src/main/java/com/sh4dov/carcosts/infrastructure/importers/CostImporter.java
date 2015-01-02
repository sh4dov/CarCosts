package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;

import java.io.File;

/**
 * Created by sh4dov on 2014-12-31.
 */
public class CostImporter extends ImporterBase {

    public CostImporter(File file, DbHandler dbHandler, Notificator notificator) {
        super(file, dbHandler, notificator);
    }

    @Override
    protected ContentValues createContentValues(String[] values) {
        ContentValues cv = new ContentValues();
        cv.put(DbHandler.Tables.Costs.date, values[0]);
        cv.put(DbHandler.Tables.Costs.cost, values[1]);
        cv.put(DbHandler.Tables.Costs.comment, values[2]);
        return cv;
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.costs;
    }
}

