package com.sh4dov.carcosts.infrastructure.exporters;

import com.sh4dov.carcosts.repositories.DbHandler;

public class OilExporter extends ExporterBase {
    public OilExporter(DbHandler dbHandler) {
        super(dbHandler);
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.oil;
    }

    @Override
    protected String[] getColumns() {
        return new String[]{
                DbHandler.Tables.Oil.date,
                DbHandler.Tables.Oil.liters,
                DbHandler.Tables.Oil.comment,
                DbHandler.Tables.Oil.isDeleted
        };
    }
}

