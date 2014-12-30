package com.sh4dov.carcosts.repositories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sh4dov on 2014-12-25.
 */
public class DbHandler extends SQLiteOpenHelper {

    public DbHandler(Context context) {
        super(context, "carcosts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Fuel (Id INTEGER PRIMARY KEY, Date DATE NOT NULL, Mileage INT NOT NULL, Liters FLOAT NOT NULL, Cost FLOAT NOT NULL, LiterCost FLOAT NOT NULL, AverageFuel FLOAT, Distance FLOAT, FuelType VARCHAR(100) NOT NULL, IsDeleted BOOLEAN DEFAULT FALSE)");
        db.execSQL("CREATE TABLE Oil (Id INTEGER PRIMARY KEY, Date DATE NOT NULL, Liters FLOAT NOT NULL, Comment VARCHAR(255), IsDeleted BOOLEAN DEFAULT FALSE)");
        db.execSQL("CREATE TABLE Costs (Id INTEGER PRIMARY KEY, Date DATE NOT NULL, Cost FLOAT NOT NULL, Comment VARCHAR(255), IsDeleted BOOLEAN DEFAULT FALSE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public static class Tables {
        public static final String fuel = "Fuel";
        public static final String oil = "Oil";
        public static final String costs = "Costs";

        public static class Base {
            public static final String id = "Id";
            public static final String date = "Date";
            public static final String isDeleted = "IsDeleted";
        }

        public static class Fuel extends Base {
            public static final String mileage = "Mileage";
            public static final String liters = "Liters";
            public static final String cost = "Cost";
            public static final String literCost = "LiterCost";
            public static final String averageFuel = "AverageFuel";
            public static final String distance = "Distance";
            public static final String fuelType = "FuelType";
        }

        public static class Costs extends Base {
            public static final String cost = "Cost";
            public static final String comment = "Comment";
        }
    }
}
