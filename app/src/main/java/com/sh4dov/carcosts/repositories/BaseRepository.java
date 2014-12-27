package com.sh4dov.carcosts.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sh4dov.carcosts.model.ModelBase;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.NullNotificator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sh4dov on 2014-12-26.
 */
public abstract class BaseRepository {
    private final DbHandler dbHandler;
    private Notificator notificator;

    public BaseRepository(DbHandler dbHandler, Notificator notificator) {
        this.dbHandler = dbHandler;
        this.notificator = notificator != null ? notificator : new NullNotificator();
    }

    protected Notificator getNotificator() {
        return notificator;
    }

    protected ContentValues createContentValues(ModelBase model) {
        ContentValues values = new ContentValues();
        if (model.id > 0) {
            values.put(DbHandler.Tables.Base.id, model.id);
        }
        values.put(DbHandler.Tables.Base.date, toString(model.date));
        values.put(DbHandler.Tables.Base.isDeleted, model.isDeleted);

        return values;
    }

    protected void add(String table, ContentValues values) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.insert(table, null, values);
        db.close();
    }

    protected void update(String table, ContentValues values, ModelBase model) {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.update(table, values, "Id = ?", new String[]{Integer.toString(model.id)});
        db.close();
    }

    protected void getNotDeleted(String table, GetItem getItem, String selection, String[] selectionArgs, String limit) {
        String notDeleted = "NOT " + DbHandler.Tables.Base.isDeleted;
        get(table, getItem, selection == null ? notDeleted : selection + "AND " + notDeleted, selectionArgs, limit);
    }

    protected void get(String table, GetItem getItem, String selection, String[] selectionArgs, String limit) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.query(true, table, null, selection, selectionArgs, null, null, DbHandler.Tables.Base.date + " DESC", limit);

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        getItem.initial(cursor);

        do {
            getItem.nextItem(cursor);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
    }

    private String toString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    protected boolean fillBase(ModelBase model, Cursor cursor, String column) {
        if (column.equals(DbHandler.Tables.Fuel.id)) {
            model.id = getInt(cursor, column);
            return true;
        } else if (column.equals(DbHandler.Tables.Fuel.date)) {
            model.date = getDate(cursor, column);
            return true;
        } else if (column.equals(DbHandler.Tables.Fuel.isDeleted)) {
            model.isDeleted = getBoolean(cursor, column);
            return true;
        }
        return false;
    }

    protected Date getDate(Cursor cursor, String column) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex(column)));
        } catch (ParseException e) {
            e.printStackTrace();
            getNotificator().showInfo(e.getMessage());
        }
        return null;
    }

    protected double getFloat(Cursor cursor, String column) {
        return cursor.getFloat(cursor.getColumnIndex(column));
    }

    protected String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    protected boolean getBoolean(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column)) > 0;
    }

    protected int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    protected interface GetItem {
        void nextItem(Cursor cursor);

        void initial(Cursor cursor);
    }
}
