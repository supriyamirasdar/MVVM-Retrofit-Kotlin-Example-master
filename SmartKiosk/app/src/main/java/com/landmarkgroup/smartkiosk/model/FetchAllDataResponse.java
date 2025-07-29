package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchAllDataResponse extends StatusResponse {
    @SerializedName("regionList")
    List<RegionModel> regionModelList;

    public List<RegionModel> getRegionModelList() {
        return regionModelList;
    }

    public void setRegionModelList(List<RegionModel> regionModelList) {
        this.regionModelList = regionModelList;
    }

}
