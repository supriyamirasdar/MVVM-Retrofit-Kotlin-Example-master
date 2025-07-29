package com.landmarkgroup.smartkiosk.model;

public class PDPPageResponse extends StatusResponse {
    //private String statusErrMessage;
    private String validateMessage;
    private String itemLongUrl;
    //private String statusMessage;
    private String serverErrormsg;

    // added dt: 17-03-2022
    private int onlineSoh;
    /*public String getStatusErrMessage() {
        return statusErrMessage;
    }

    public void setStatusErrMessage(String statusErrMessage) {
        this.statusErrMessage = statusErrMessage;
    }*/

    public String getValidateMessage() {
        return validateMessage;
    }

    public void setValidateMessage(String validateMessage) {
        this.validateMessage = validateMessage;
    }

    public String getItemLongUrl() {
        return itemLongUrl;
    }

    public void setItemLongUrl(String itemLongUrl) {
        this.itemLongUrl = itemLongUrl;
    }

    /*public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }*/

    public String getServerErrormsg() {
        return serverErrormsg;
    }

    public void setServerErrormsg(String serverErrormsg) {
        this.serverErrormsg = serverErrormsg;
    }


    public int getOnlineSoh() {
        return onlineSoh;
    }

    public void setOnlineSoh(int onlineSoh) {
        this.onlineSoh = onlineSoh;
    }
}
