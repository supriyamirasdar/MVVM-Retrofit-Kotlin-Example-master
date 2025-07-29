package com.landmarkgroup.smartkiosk.model;

import com.google.gson.annotations.SerializedName;

public class FetchAllDataRequest {
    @SerializedName("ouCode")
    public String ouCode;

    public String getOuCode() {
        return ouCode;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }
}
