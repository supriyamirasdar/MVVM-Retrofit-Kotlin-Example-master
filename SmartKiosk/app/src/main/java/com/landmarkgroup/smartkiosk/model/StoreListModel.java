package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreListModel {
    @Expose
    @SerializedName("storeId")
    private String storeId;

    @Expose
    @SerializedName("storeName")
    private String storeName;

    @Expose
    @SerializedName("storeLocation")
    private String storeLocation;

    @Expose
    @SerializedName("defaultWhName")
    private String defaultWhName;

    @Expose
    @SerializedName("defaultWh")
    private String defaultWh;




    public String getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public String getDefaultWhName() {
        return defaultWhName;
    }

    public String getDefaultWh() {
        return defaultWh;
    }



    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public void setDefaultWhName(String defaultWhName) {
        this.defaultWhName = defaultWhName;
    }

    public void setDefaultWh(String defaultWh) {
        this.defaultWh = defaultWh;
    }
}
