package com.odysii.selenium.cloudMI;

public enum PriceBookLoaderType {
    FULL("NACSFull"),INC("NACSInc");
    private String type;
     PriceBookLoaderType(String type){
        this.type = type;
    }
    public String getType(){
         return type;
    }
}
