package com.landmarkgroup.smartkiosk.model;

public class PDPPageLoadRequest {
    private String storeId;
    private String itemId;
    private String reqType;
    private String reqFrom;
    private String entName;
    private String usrId;
    private String custMobileNum;
    private String ordConfirmUrl;
    private String kioskId;
    private String reqSource ;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getReqFrom() {
        return reqFrom;
    }

    public void setReqFrom(String reqFrom) {
        this.reqFrom = reqFrom;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getCustMobileNum() {
        return custMobileNum;
    }

    public void setCustMobileNum(String custMobileNum) {
        this.custMobileNum = custMobileNum;
    }

    public String getOrdConfirmUrl() {
        return ordConfirmUrl;
    }

    public void setOrdConfirmUrl(String ordConfirmUrl) {
        this.ordConfirmUrl = ordConfirmUrl;
    }

    public String getKioskId() {
        return kioskId;
    }

    public void setKioskId(String kioskId) {
        this.kioskId = kioskId;
    }

    public String getReqSource() {
        return reqSource;
    }

    public void setReqSource(String reqSource) {
        this.reqSource = reqSource;
    }
}
