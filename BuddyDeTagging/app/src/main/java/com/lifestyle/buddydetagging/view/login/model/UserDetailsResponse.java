package com.lifestyle.buddydetagging.view.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetailsResponse {
    @SerializedName("userId")
    @Expose
    public String userId;

    @SerializedName("actorName")
    @Expose
    public String actorName;

    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("middleName")
    @Expose
    public String middleName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("emailId")
    @Expose
    public String emailId;
    @SerializedName("mobileNumber")
    @Expose
    public String mobileNumber;

    @SerializedName("designation")
    @Expose
    public String designation;
    @SerializedName("address2")
    @Expose
    public String address2;
    @SerializedName("location2")
    @Expose
    public String location2;
    @SerializedName("storeId")
    @Expose
    public String storeId;
    @SerializedName("storeName")
    @Expose
    public String storeName;
    @SerializedName("state")
    @Expose
    public String state;

    @SerializedName("region")
    @Expose
    public String region;

    @SerializedName("city")
    @Expose
    public String city;

    @SerializedName("ouCode")
    @Expose
    public String ouCode;
    @SerializedName("actorId")
    @Expose
    public String actorId;

    @SerializedName("deviceTyp")
    @Expose
    public String deviceTyp;

}
