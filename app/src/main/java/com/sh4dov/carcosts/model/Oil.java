package com.sh4dov.carcosts.model;

public class Oil extends ModelBase {
    public double liters;
    public String comment;

    public boolean isValid() {
        return date != null && liters > 0;
    }
}
