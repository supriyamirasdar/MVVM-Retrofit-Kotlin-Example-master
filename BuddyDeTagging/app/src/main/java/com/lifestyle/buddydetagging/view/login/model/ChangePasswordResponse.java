package com.lifestyle.buddydetagging.view.login.model;


import com.lifestyle.buddydetagging.model.StatusResponse;

public class ChangePasswordResponse extends StatusResponse {
    private String successIndi;
    private String serverErrormsg;

    public String getServerErrormsg() {
        return serverErrormsg;
    }

    public void setServerErrormsg(String serverErrormsg) {
        this.serverErrormsg = serverErrormsg;
    }

    public String getSuccessIndi() {
        return successIndi;
    }

    public void setSuccessIndi(String successIndi) {
        this.successIndi = successIndi;
    }
}
