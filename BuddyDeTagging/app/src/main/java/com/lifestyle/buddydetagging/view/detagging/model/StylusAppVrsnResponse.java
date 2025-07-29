package com.lifestyle.buddydetagging.view.detagging.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lifestyle.buddydetagging.model.StatusResponse;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;

public class StylusAppVrsnResponse  extends ResponseDTO {


    @SerializedName("mobAppVersion")
    @Expose
    public String mobAppsVersion;

    @SerializedName("ftpHostName")
    @Expose
    public String ftpHostName;

    @SerializedName("ftpPortNum")
    @Expose
    public String ftpPortNum;

    @SerializedName("ftpUsr")
    @Expose
    public String ftpUsr;

    @SerializedName("ftpPwd")
    @Expose
    public String ftpPwd;




    public String getMobAppsVersion() {
        return mobAppsVersion;
    }

    public void setMobAppsVersion(String mobAppsVersion) {
        this.mobAppsVersion = mobAppsVersion;
    }

    public String getFtpHostName() {
        return ftpHostName;
    }

    public void setFtpHostName(String ftpHostName) {
        this.ftpHostName = ftpHostName;
    }

    public String getFtpPortNum() {
        return ftpPortNum;
    }

    public void setFtpPortNum(String ftpPortNum) {
        this.ftpPortNum = ftpPortNum;
    }

    public String getFtpUsr() {
        return ftpUsr;
    }

    public void setFtpUsr(String ftpUsr) {
        this.ftpUsr = ftpUsr;
    }

    public String getFtpPwd() {
        return ftpPwd;
    }

    public void setFtpPwd(String ftpPwd) {
        this.ftpPwd = ftpPwd;
    }
}
