package com.sh4dov.carcosts.infrastructure.exporters;

import com.sh4dov.carcosts.repositories.DbHandler;

public class FuelExporter extends ExporterBase {
    public FuelExporter(DbHandler dbHandler) {
        super(dbHandler);
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.fuel;
    }

    @Override
    protected String[] getColumns() {
        return new String[]{
                DbHandler.Tables.Fuel.date,
                DbHandler.Tables.Fuel.mileage,
                DbHandler.Tables.Fuel.cost,
                DbHandler.Tables.Fuel.literCost,
                DbHandler.Tables.Fuel.liters,
                DbHandler.Tables.Fuel.averageFuel,
                DbHandler.Tables.Fuel.distance,
                DbHandler.Tables.Fuel.fuelType,
                DbHandler.Tables.Fuel.isDeleted
        };
    }
}
