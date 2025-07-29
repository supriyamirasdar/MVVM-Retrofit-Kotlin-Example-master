package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ValidEmployeeResponse extends StatusResponse {
    @SerializedName("userDetails")
    private UserDetailModel userDetailModel;

    @SerializedName("storeList")
    private List<StoreListModel> storeListModels;

    @SerializedName("successIndi")
    private String successIndi;

    public String getSuccessIndi() {
        return successIndi;
    }

    public void setSuccessIndi(String successIndi) {
        this.successIndi = successIndi;
    }

    public List<StoreListModel> getStoreListModels() {
        return storeListModels;
        //return  new ArrayList<StoreListModel>();
    }

    public void setStoreListModels(List<StoreListModel> storeListModels) {
        this.storeListModels = storeListModels;
    }

    public UserDetailModel getUserDetailModel() {
        return userDetailModel;
    }

    public void setUserDetailModel(UserDetailModel userDetailModel) {
        this.userDetailModel = userDetailModel;
    }
}
