package com.landmarkgroup.smartkiosk.ui.pdppage.model;

import java.util.Comparator;

public class SizeDto {
    private  String id;
    private String size;
    private String count;
    private String itemBarCode;
    private String skuCode;
    private String styleCode;
    private String itemDesc;
    private String mrp ;
    private String lastSold;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getItemBarCode() {
        return itemBarCode;
    }

    public void setItemBarCode(String itemBarCode) {
        this.itemBarCode = itemBarCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getLastSold() {
        return lastSold;
    }

    public void setLastSold(String lastSold) {
        this.lastSold = lastSold;
    }


    public static Comparator<SizeDto> sizeComparatorDesc = new Comparator<SizeDto>() {
        @Override
        public int compare(SizeDto jc1, SizeDto jc2) {
            return (Integer.parseInt(jc1.getSize()) < Integer.parseInt(jc2.getSize()) ? -1 :
                    (Integer.parseInt(jc1.getSize())  == Integer.parseInt(jc2.getSize()) ? 0 : 1));
        }
    };

}
