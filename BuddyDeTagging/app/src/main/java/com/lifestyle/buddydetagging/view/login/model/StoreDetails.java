package com.lifestyle.buddydetagging.view.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class StoreDetails implements Serializable, Comparable<StoreDetails> {

    @SerializedName("storeId")
    @Expose
    public String storeId;

    @SerializedName("storeName")
    @Expose
    public String storeName;

    @SerializedName("storeLocation")
    @Expose
    public String storeLocation;
    @SerializedName("defaultWhName")
    @Expose
    public String defaultWhName;
    @SerializedName("defaultWh")
    @Expose
    public String defaultWh;


    @Override
    public int compareTo(@NonNull  StoreDetails o) {
        return this.storeName.compareTo(o.storeName);
    }
}
