package com.landmarkgroup.smartkiosk.ui.pdppage.model;

public class GetItemDetailsAtOptionLevelRequest {
    public String itemCode;
    public String storeId;
   /*
    public String usecaseId;
    public String userId;*/


    // Getter Methods

    public String getItemCode() {
        return itemCode;
    }
    public String getStoreId() {
        return storeId;
    }
    /*

    public String getUsecaseId() {
        return usecaseId;
    }

    public String getUserId() {
        return userId;
    }*/

    // Setter Methods

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
   /*

    public void setUsecaseId(String usecaseId) {
        this.usecaseId = usecaseId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }*/
}
