package com.landmarkgroup.smartkiosk.model;

public class AppVersion extends StatusResponse {
    private String serverErrormsg;
    private String mobAppsVersion;
    private String ftpHostName;
    private String ftpPortNum;
    private String ftpUsr;
    private String ftpPwd;

    public String getServerErrormsg() {
        return serverErrormsg;
    }

    public void setServerErrormsg(String serverErrormsg) {
        this.serverErrormsg = serverErrormsg;
    }

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
