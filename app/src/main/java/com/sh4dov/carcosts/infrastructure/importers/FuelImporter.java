package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;

import java.io.File;

public class FuelImporter extends ImporterBase{
    public FuelImporter(File file, DbHandler dbHandler, Notificator notificator) {
        super(file, dbHandler, notificator);
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.fuel;
    }

    @Override
    protected ContentValues createContentValues(String[] values) {
        ContentValues cv = new ContentValues();
        cv.put(DbHandler.Tables.Fuel.date, values[0]);
        cv.put(DbHandler.Tables.Fuel.mileage, values[1]);
        cv.put(DbHandler.Tables.Fuel.cost, values[2]);
        cv.put(DbHandler.Tables.Fuel.literCost, values[3]);
        cv.put(DbHandler.Tables.Fuel.liters, values[4]);
        cv.put(DbHandler.Tables.Fuel.averageFuel, values[5]);
        cv.put(DbHandler.Tables.Fuel.distance, values[6]);
        cv.put(DbHandler.Tables.Fuel.fuelType, values[7]);
        return cv;
    }
}
