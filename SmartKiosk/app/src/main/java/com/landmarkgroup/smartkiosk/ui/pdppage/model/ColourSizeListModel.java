package com.landmarkgroup.smartkiosk.ui.pdppage.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ColourSizeListModel {
    @SerializedName("colour")
    String colour;

    @SerializedName("sizeCountList")
    ArrayList<SizeCountListmodel> sizeCountListmodelArrayList;

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public ArrayList<SizeCountListmodel> getSizeCountListmodelArrayList() {
        return sizeCountListmodelArrayList;
    }

    public void setSizeCountListmodelArrayList(ArrayList<SizeCountListmodel> sizeCountListmodelArrayList) {
        this.sizeCountListmodelArrayList = sizeCountListmodelArrayList;
    }
}
