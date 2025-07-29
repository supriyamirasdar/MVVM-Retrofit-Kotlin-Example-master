package com.landmarkgroup.smartkiosk.model;

public class PDPOrderConfirmResponse extends StatusResponse {
    //private String statusErrMessage;
    private String validateMessage;
    private String itemLongUrl;
    //private String statusMessage;
    private String serverErrormsg;

    private String  businessInfoFault;
    //private String serverErrormsg;//": "Kiosk order details updated successfully",
    private String status;//": null

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

    public String getBusinessInfoFault() {
        return businessInfoFault;
    }

    public void setBusinessInfoFault(String businessInfoFault) {
        this.businessInfoFault = businessInfoFault;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
