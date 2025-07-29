package com.validateemp;

import com.google.gson.annotations.SerializedName;

public class ValidEmployeeRequest {
    @SerializedName("employeeId")
    public  String employeeId;

    @SerializedName("deviceType")
    public String deviceType;

    @SerializedName("macId")
    public String macId;

    @SerializedName("imei")
    public String imei;

    @SerializedName("uniqueAndroidId")
    public String uniqueAndroidId;

    @SerializedName("currentVersion")
    public String currentVersion;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUniqueAndroidId() {
        return uniqueAndroidId;
    }

    public void setUniqueAndroidId(String uniqueAndroidId) {
        this.uniqueAndroidId = uniqueAndroidId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
