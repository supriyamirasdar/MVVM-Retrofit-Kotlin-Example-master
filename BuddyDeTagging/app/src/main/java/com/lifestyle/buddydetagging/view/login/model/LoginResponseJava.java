package com.lifestyle.buddydetagging.view.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lifestyle.buddydetagging.model.StatusResponse;

import java.util.ArrayList;

public class LoginResponseJava extends StatusResponse {
    @SerializedName("storeNotFound")
    @Expose
    public String storeNotFound;

    @SerializedName("empId")
    @Expose
    public String empId;

    @SerializedName("authKey")
    @Expose
    public String authKey;


    @SerializedName("userDetails")
    @Expose
    public UserDetailsResponse userDetails;

    @SerializedName("storeDetailsList")
    @Expose
    public ArrayList<StoreDetails> storeDetailsList;



}

