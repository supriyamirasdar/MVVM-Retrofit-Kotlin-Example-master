package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionModel {
    @SerializedName("region")
    String region;

    @SerializedName("regionName")
    String regionName;

    @SerializedName("districtList")
    private List<DistrictListModel> districtListModels;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<DistrictListModel> getDistrictListModels() {
        return districtListModels;
    }

    public void setDistrictListModels(List<DistrictListModel> districtListModels) {
        this.districtListModels = districtListModels;
    }
}
