package com.sh4dov.carcosts.model;

public class Cost extends ModelBase {
    public double cost;
    public String comment;

    public boolean isValid() {
        return date != null && cost > 0 && !comment.isEmpty();
    }
}
