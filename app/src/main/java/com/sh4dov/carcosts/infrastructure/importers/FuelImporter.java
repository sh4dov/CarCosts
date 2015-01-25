package com.sh4dov.carcosts.infrastructure.importers;

import android.content.ContentValues;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.Notificator;

import java.io.Reader;

public class FuelImporter extends ImporterBase {
    public FuelImporter(Reader reader, DbHandler dbHandler, Notificator notificator) {
        super(reader, dbHandler, notificator);
    }

    @Override
    protected ContentValues createContentValues(String[] values) {
        ContentValues cv = new ContentValues();
        putIfCan(DbHandler.Tables.Fuel.date, values, cv, 0);
        putIfCan(DbHandler.Tables.Fuel.mileage, values, cv, 1);
        putIfCan(DbHandler.Tables.Fuel.cost, values, cv, 2);
        putIfCan(DbHandler.Tables.Fuel.literCost, values, cv, 3);
        putIfCan(DbHandler.Tables.Fuel.liters, values, cv, 4);
        putIfCan(DbHandler.Tables.Fuel.averageFuel, values, cv, 5);
        putIfCan(DbHandler.Tables.Fuel.distance, values, cv, 6);
        putIfCan(DbHandler.Tables.Fuel.fuelType, values, cv, 7);
        putIfCan(DbHandler.Tables.Fuel.isDeleted, values, cv, 8);
        return cv;
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.fuel;
    }
}
