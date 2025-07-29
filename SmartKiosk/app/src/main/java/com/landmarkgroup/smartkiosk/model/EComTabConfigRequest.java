package com.landmarkgroup.smartkiosk.model;

public class EComTabConfigRequest {
    private String storeId;
    private String oldTabId;
    private String newTabId;
    private String status;
    private String usrId;
    private String macId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getOldTabId() {
        return oldTabId;
    }

    public void setOldTabId(String oldTabId) {
        this.oldTabId = oldTabId;
    }

    public String getNewTabId() {
        return newTabId;
    }

    public void setNewTabId(String newTabId) {
        this.newTabId = newTabId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }
}
