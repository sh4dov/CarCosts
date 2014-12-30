package com.sh4dov.carcosts.repositories;

import android.content.ContentValues;
import android.database.Cursor;

import com.sh4dov.carcosts.R;
import com.sh4dov.carcosts.model.Fuel;
import com.sh4dov.common.Notificator;
import com.sh4dov.common.ProgressPointer;
import com.sh4dov.common.ProgressPointerOperator;

import java.util.ArrayList;

/**
 * Created by sh4dov on 2014-12-26.
 */
public class FuelRepository extends BaseRepository {

    public FuelRepository(DbHandler dbHandler, Notificator notificator) {
        super(dbHandler, notificator);
    }

    public void add(Fuel fuel) {
        ContentValues values = getContentValues(fuel);
        add(DbHandler.Tables.fuel, values);
        getNotificator().showInfo(R.string.added_fuel);
    }

    public void update(Fuel fuel) {
        ContentValues values = getContentValues(fuel);
        update(DbHandler.Tables.fuel, values, fuel);
        getNotificator().showInfo(R.string.updated_fuel);
    }

    public void delete(Fuel fuel) {
        fuel.isDeleted = true;
        ContentValues values = getContentValues(fuel);
        update(DbHandler.Tables.fuel, values, fuel);
        getNotificator().showInfo(R.string.deleted_fuel);
    }

    public ArrayList<Fuel> getRefueling(ProgressPointer progressPointer) {
        final ArrayList<Fuel> result = new ArrayList<Fuel>();
        final ProgressPointerOperator pointer = new ProgressPointerOperator(progressPointer);

        getNotDeleted(
                DbHandler.Tables.fuel,
                new GetItem() {
                    @Override
                    public void nextItem(Cursor cursor) {
                        Fuel fuel = getFuel(cursor);
                        result.add(fuel);
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
                null);

        return result;
    }

    public Fuel getPrevious(Fuel fuel) {
        final Fuel[] result = new Fuel[1];
        int mileage = fuel == null ? 0 : fuel.mileage;
        GetItem getItem = new GetItem() {
            @Override
            public void nextItem(Cursor cursor) {
                result[0] = getFuel(cursor);
            }

            @Override
            public void initial(Cursor cursor) {
            }
        };
        getNotDeleted(DbHandler.Tables.fuel, getItem, DbHandler.Tables.Fuel.mileage + " < ?", new String[]{Integer.toString(mileage)}, "1", DbHandler.Tables.Fuel.mileage + " DESC");
        return result[0] != null ? result[0] : new Fuel();
    }

    public Fuel getNext(Fuel fuel) {
        final Fuel[] result = new Fuel[1];
        int mileage = fuel == null ? Fuel.MAX_MILEAGE : fuel.mileage;
        GetItem getItem = new GetItem() {
            @Override
            public void nextItem(Cursor cursor) {
                result[0] = getFuel(cursor);
            }

            @Override
            public void initial(Cursor cursor) {
            }
        };
        getNotDeleted(DbHandler.Tables.fuel, getItem, DbHandler.Tables.Fuel.mileage + " > ?", new String[]{Integer.toString(mileage)}, "1", DbHandler.Tables.Fuel.mileage + " DESC");
        return result[0] != null ? result[0] : new Fuel();
    }

    public Fuel getLastFuel() {
        final Fuel[] fuel = new Fuel[1];
        GetItem getItem = new GetItem() {
            @Override
            public void nextItem(Cursor cursor) {
                fuel[0] = getFuel(cursor);
            }

            @Override
            public void initial(Cursor cursor) {
            }
        };

        getNotDeleted(DbHandler.Tables.fuel, getItem, null, null, "1", null);
        return fuel[0] != null ? fuel[0] : new Fuel();
    }

    private ContentValues getContentValues(Fuel fuel) {
        ContentValues values = createContentValues(fuel);
        values.put(DbHandler.Tables.Fuel.mileage, fuel.mileage);
        values.put(DbHandler.Tables.Fuel.liters, fuel.liters);
        values.put(DbHandler.Tables.Fuel.cost, fuel.cost);
        values.put(DbHandler.Tables.Fuel.literCost, fuel.literCost);
        values.put(DbHandler.Tables.Fuel.averageFuel, fuel.averageFuel);
        values.put(DbHandler.Tables.Fuel.distance, fuel.distance);
        values.put(DbHandler.Tables.Fuel.fuelType, fuel.fuelType);
        return values;
    }

    private Fuel getFuel(Cursor cursor) {
        Fuel fuel = new Fuel();
        for (String column : cursor.getColumnNames()) {
            if (fillBase(fuel, cursor, column)) {
            } else if (column.equals(DbHandler.Tables.Fuel.mileage)) {
                fuel.mileage = getInt(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.liters)) {
                fuel.liters = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.cost)) {
                fuel.cost = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.literCost)) {
                fuel.literCost = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.averageFuel)) {
                fuel.averageFuel = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.distance)) {
                fuel.distance = getDouble(cursor, column);
            } else if (column.equals(DbHandler.Tables.Fuel.fuelType)) {
                fuel.fuelType = getString(cursor, column);
            }
        }

        return fuel;
    }
}
