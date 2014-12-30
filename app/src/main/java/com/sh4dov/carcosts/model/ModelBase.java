package com.sh4dov.carcosts.model;

import java.io.Serializable;
import java.util.Date;

public abstract class ModelBase implements Serializable {
    public int id;
    public Date date;
    public boolean isDeleted;
}
