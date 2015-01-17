package com.sh4dov.carcosts.infrastructure.exporters;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.sh4dov.carcosts.repositories.DbHandler;
import com.sh4dov.common.ProgressPointer;
import com.sh4dov.common.ProgressPointerOperator;

public abstract class ExporterBase {
    private final DbHandler dbHandler;

    public ExporterBase(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public String exportToString(ProgressPointer progressPointer) {
        ProgressPointerOperator pointerOperator = new ProgressPointerOperator(progressPointer);
        StringBuilder buffer = new StringBuilder();
        String newLine = "\r\n";
        String[] columns = getColumns();
        String table = getTable();

        buffer.append(TextUtils.join(";", columns) + newLine);

        SQLiteDatabase db = dbHandler.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT " + TextUtils.join(", ", columns) + " FROM " + table, null);
        pointerOperator.setMax(c.getCount());
        if (!c.moveToFirst()) {
            c.close();
            db.close();
            return buffer.toString();
        }

        do {
            for (int i = 0; i < c.getColumnCount(); i++) {
                buffer.append(c.getString(i));
                buffer.append(";");
            }
            buffer.append(newLine);
            pointerOperator.addProgress();
        }
        while (c.moveToNext());
        c.close();
        db.close();

        return buffer.toString();
    }

    protected abstract String getTable();

    protected abstract String[] getColumns();
}
