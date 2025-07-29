package com.landmarkgroup.smartkiosk.ui.pdppage.model;

import com.google.gson.annotations.SerializedName;
import com.landmarkgroup.smartkiosk.model.StatusResponse;

import java.util.ArrayList;

public class GetItemDetailsAtOptionLevelResponse extends StatusResponse {
   /* private String serverErrormsg ;
    private String itemNotFound ;
    private String optionNotFound;
    private String itemEanErrorMsg;
    @SerializedName("colourSizeList")
    private ArrayList<ColourSizeListModel> colourSizeListModelArrayList;*/



    private String serverErrormsg;

     private String itemNotFound ;
    private String optionNotFound;
    private String itemEanErrorMsg;
    @SerializedName("colourSizeList")
    private ArrayList<ColourSizeListModel> colourSizeListModelArrayList;

    //private ItemDetails itemDetails;
    private String itemDesc;

    public ArrayList<ColourSizeListModel> getColourSizeListModelArrayList() {
        return colourSizeListModelArrayList;
    }

    public void setColourSizeListModelArrayList(ArrayList<ColourSizeListModel> colourSizeListModelArrayList) {
        this.colourSizeListModelArrayList = colourSizeListModelArrayList;
    }
// Getter Methods

    public String getServerErrormsg() {
        return serverErrormsg;
    }

    public String getItemNotFound() {
        return itemNotFound;
    }

    public String getOptionNotFound() {
        return optionNotFound;
    }

    public String getItemEanErrorMsg() {
        return itemEanErrorMsg;
    }

    // Setter Methods

    public void setServerErrormsg(String serverErrormsg) {
        this.serverErrormsg = serverErrormsg;
    }

    public void setItemNotFound(String itemNotFound) {
        this.itemNotFound = itemNotFound;
    }

    public void setOptionNotFound(String optionNotFound) {
        this.optionNotFound = optionNotFound;
    }

    public void setItemEanErrorMsg(String itemEanErrorMsg) {
        this.itemEanErrorMsg = itemEanErrorMsg;
    }

   /* public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }*/

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
}
