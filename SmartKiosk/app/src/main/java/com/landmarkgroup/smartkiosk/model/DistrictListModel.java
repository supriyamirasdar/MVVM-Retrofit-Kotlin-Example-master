package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistrictListModel {
    @SerializedName("district")
    String district;

    @SerializedName("districtName")
    String districtName;

    @SerializedName("storeList")
    List<StoreListModel> storeList;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<StoreListModel> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<StoreListModel> storeList) {
        this.storeList = storeList;
    }
}
