package com.lifestyle.buddydetagging.view.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequestData {
    @SerializedName("userId")
    @Expose
    public String userId;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("imei")
    @Expose
    public String imeiNumber;

    @SerializedName("uniqueAndroidId")
    @Expose
    public String uniqueAndroidId;

    @SerializedName("versionNumber")
    @Expose
    public String versionNumber;

    @SerializedName("macId")
    @Expose
    public String macId;


}
