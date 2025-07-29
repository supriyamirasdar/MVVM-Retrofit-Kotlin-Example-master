package com.landmarkgroup.smartkiosk.ui.pdppage.model;

public class SizeCountListmodel {
    private String size;
    private String count;
    private String itemBarCode;
    private String skuCode;
    private String styleCode;
    private String itemDesc;
    private String mrp ;
    private String lastSold;


    // Getter Methods

    public String getSize() {
        return size;
    }

    public String getCount() {
        return count;
    }

    public String getItemBarCode() {
        return itemBarCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getMrp() {
        return mrp;
    }

    public String getLastSold() {
        return lastSold;
    }

    // Setter Methods

    public void setSize(String size) {
        this.size = size;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setItemBarCode(String itemBarCode) {
        this.itemBarCode = itemBarCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public void setLastSold(String lastSold) {
        this.lastSold = lastSold;
    }
}
