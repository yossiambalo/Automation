package com.odysii.pos;

public abstract class Customer {
    public abstract void init();
    public abstract String getStartTransaction();
    public abstract String getAddItem();
    public abstract String getEndTransaction();

}
