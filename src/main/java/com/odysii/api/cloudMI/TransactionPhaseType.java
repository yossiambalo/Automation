package com.odysii.api.cloudMI;

public enum TransactionPhaseType {
    TACTICAL("tactical"),TARGETED("targeted"),TENDER("tender");

    public String getPhase() {
        return phase;
    }

    private String phase;

    TransactionPhaseType(String phase) {
        this.phase = phase;
    }
}
