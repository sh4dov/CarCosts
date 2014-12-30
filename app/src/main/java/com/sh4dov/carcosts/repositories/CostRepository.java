package com.sh4dov.carcosts.repositories;

import android.content.ContentValues;
import android.database.Cursor;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Cost;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.ProgressPointerOperator;

import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-30.
 */
public class CostRepository extends BaseRepository {
    public CostRepository(DbHandler dbHandler, Notificator notificator) {
        super(dbHandler, notificator);
    }

    public void add(Cost cost) {
        ContentValues values = getContentValues(cost);
        add(DbHandler.Tables.costs, values);
        getNotificator().showInfo(R.string.added_cost);
    }

    public void update(Cost cost) {
        ContentValues values = getContentValues(cost);
        update(DbHandler.Tables.costs, values, cost);
        getNotificator().showInfo(R.id.updated_cost);
    }

    public void delete(Cost cost) {
        cost.isDeleted = true;
        ContentValues values = createContentValues(cost);
        update(DbHandler.Tables.costs, values, cost);
        getNotificator().showInfo(R.id.deleted_cost);
    }

    private ContentValues getContentValues(Cost cost) {
        ContentValues values = createContentValues(cost);
        values.put(DbHandler.Tables.Costs.cost, cost.cost);
        values.put(DbHandler.Tables.Costs.comment, cost.comment);
        return values;
    }

    public ArrayList<Cost> getCosts(ProgressPointerIndicator progressPointer) {
        final ArrayList<Cost> result = new ArrayList<Cost>();
        final ProgressPointerOperator pointer = new ProgressPointerOperator(progressPointer);

        getNotDeleted(
                DbHandler.Tables.costs,
                new GetItem() {
                    @Override
                    public void nextItem(Cursor cursor) {
                        Cost cost = getCost(cursor);
                        result.add(cost);
                        pointer.addProgress();
                    }

                    @Override
                    public void initial(Cursor cursor) {
                        pointer.setMax(cursor.getCount());
                    }
                },
                null,
                null,
                null,
                null
        );

        return result;
    }

    private Cost getCost(Cursor cursor) {
        Cost cost = new Cost();
        for (String column : cursor.getColumnNames()) {
            if (fillBase(cost, cursor, column)) {
            } else if (column.equals(DbHandler.Tables.Costs.cost)) {
                cost.cost = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Costs.comment)) {
                cost.comment = getString(cursor, column);
            }
        }

        return cost;
    }
}
