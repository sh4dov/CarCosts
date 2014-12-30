package com.sh4dov.carcosts.repositories;

import android.content.ContentValues;
import android.database.Cursor;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Oil;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressPointerIndicator;
import com.sh4dov.common.ProgressPointerOperator;

import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-30.
 */
public class OilRepository extends BaseRepository {
    public OilRepository(DbHandler dbHandler, Notificator notificator) {
        super(dbHandler, notificator);
    }

    public void add(Oil oil) {
        ContentValues values = getContentValues(oil);
        add(DbHandler.Tables.oil, values);
        getNotificator().showInfo(R.string.added_oil);
    }

    public void update(Oil oil) {
        ContentValues values = getContentValues(oil);
        update(DbHandler.Tables.oil, values, oil);
        getNotificator().showInfo(R.id.updated_oil);
    }

    public void delete(Oil oil) {
        oil.isDeleted = true;
        ContentValues values = createContentValues(oil);
        update(DbHandler.Tables.oil, values, oil);
        getNotificator().showInfo(R.id.deleted_oil);
    }

    private ContentValues getContentValues(Oil oil) {
        ContentValues values = createContentValues(oil);
        values.put(DbHandler.Tables.Oil.liters, oil.liters);
        values.put(DbHandler.Tables.Oil.comment, oil.comment);
        return values;
    }

    public ArrayList<Oil> getOil(ProgressPointerIndicator progressPointer) {
        final ArrayList<Oil> result = new ArrayList<Oil>();
        final ProgressPointerOperator pointer = new ProgressPointerOperator(progressPointer);

        getNotDeleted(
                DbHandler.Tables.oil,
                new GetItem() {
                    @Override
                    public void nextItem(Cursor cursor) {
                        Oil oil = getOil(cursor);
                        result.add(oil);
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

    private Oil getOil(Cursor cursor) {
        Oil oil = new Oil();
        for (String column : cursor.getColumnNames()) {
            if (fillBase(oil, cursor, column)) {
            } else if (column.equals(DbHandler.Tables.Oil.liters)) {
                oil.liters = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Oil.comment)) {
                oil.comment = getString(cursor, column);
            }
        }

        return oil;
    }
}
