package com.sh4dov.carcosts.infrastructure.exporters;

import com.sh4dov.carcosts.repositories.DbHandler;

public class CostExporter extends ExporterBase {
    public CostExporter(DbHandler dbHandler) {
        super(dbHandler);
    }

    @Override
    protected String getTable() {
        return DbHandler.Tables.costs;
    }

    @Override
    protected String[] getColumns() {
        return new String[] {
                DbHandler.Tables.Costs.date,
                DbHandler.Tables.Costs.cost,
                DbHandler.Tables.Costs.comment,
                DbHandler.Tables.Costs.isDeleted
        };
    }
}

