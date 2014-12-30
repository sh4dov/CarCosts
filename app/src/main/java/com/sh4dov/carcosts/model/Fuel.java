package com.sh4dov.carcosts.model;

/**
 * Created by sh4dov on 2014-12-26.
 */
public class Fuel extends ModelBase {
    public final  static int MAX_MILEAGE = 1000000;

    public int mileage;
    public double liters;
    public double cost;
    public double literCost;
    public double averageFuel;
    public double distance;
    public String fuelType;

    public boolean isValid(){
        return date != null && mileage > 0 && liters > 0 && cost > 0 && literCost > 0 && averageFuel > 0 && distance > 0 && !fuelType.isEmpty();
    }
}

