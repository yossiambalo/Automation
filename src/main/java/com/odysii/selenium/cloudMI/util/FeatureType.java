package com.odysii.selenium.cloudMI.util;

public enum FeatureType {
    MANUAL_PROMOTION("Manual Promotions"),
    BLACK_LISTING("Blacklisting"),
    COMMAND_AND_CONTROL("Command And Control"),
    CS4_APPLICATION_MANAGER("CS4 Application Manager"),
    CS4_BRAND("CS4 Brand");
    private String featureType;

    FeatureType(String feature) {
        this.featureType = feature;
    }

    public String getFeatureType() {
        return featureType;
    }
}
