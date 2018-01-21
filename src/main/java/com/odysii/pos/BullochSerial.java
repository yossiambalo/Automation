package com.odysii.pos;

import com.odysii.general.PropertyLoader;

import java.util.Properties;

public class BullochSerial extends Customer {
    public String getStartTransaction() {
        return startTransaction;
    }

    public String getAddItem() {
        return addItem;
    }

    public String getEndTransaction() {
        return endTransaction;
    }
    private String startTransaction;
    private String addItem;
    private String endTransaction;

    @Override
    public void init() {
        PropertyLoader loader = new PropertyLoader();
        Properties properties = loader.loadPropFile("pos_serial.properties");
        this.startTransaction = properties.getProperty("bulloch_start_transaction");
        this.addItem = properties.getProperty("bulloch_add_item");
        this.endTransaction = properties.getProperty("bulloch_end_transaction");
    }
}
