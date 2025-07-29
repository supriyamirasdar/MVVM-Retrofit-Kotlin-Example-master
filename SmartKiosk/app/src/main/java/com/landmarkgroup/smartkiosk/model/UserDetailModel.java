package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

public class UserDetailModel {
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("middleName")
    private String middleName;

    @SerializedName("actorName")
    private String actorName;

    @SerializedName("emailId")
    private String emailId;

    @SerializedName("mobileNumber")
    private String mobileNumber;

    @SerializedName("designation")
    private String designation;

    @SerializedName("address2")
    private String address2;

    @SerializedName("location2")
    private String location2;

    @SerializedName("storeId")
    private String storeId;

    @SerializedName("storeName")
    private String storeName;

    @SerializedName("state")
    private String state;

    @SerializedName("region")
    private String region;

    @SerializedName("city")
    private String city;

    @SerializedName("ouCode")
    private String ouCode;

    @SerializedName("empty")
    private boolean empty;


    // Getter Methods

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getActorName() {
        return actorName;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public String getAddress2() {
        return address2;
    }

    public String getLocation2() {
        return location2;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getState() {
        return state;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getOuCode() {
        return ouCode;
    }

    public boolean getEmpty() {
        return empty;
    }

    // Setter Methods

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
