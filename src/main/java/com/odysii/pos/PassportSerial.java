package com.odysii.pos;

import com.odysii.general.PropertyLoader;

import java.util.Properties;

public class PassportSerial extends Customer {
    public String getStartTransaction() {
        return startTransaction;
    }

    public String getAddItem() {
        return addItem;
    }

    public String getAddItem(String plu) {
        return addItem.replace("(12)","("+plu+")");
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
        this.startTransaction = properties.getProperty("passport_start_transaction");
        this.addItem = properties.getProperty("passport_add_item");
        this.endTransaction = properties.getProperty("passport_end_transaction");
    }
}
