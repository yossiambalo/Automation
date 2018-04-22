package com.odysii.pos;

public abstract class Customer {
    public abstract void init();
    public abstract String getStartTransaction();
    public abstract String getAddItem();
    public abstract String getAddItem(String s);
    public abstract String getEndTransaction();
    public abstract String getTotal();

}
