package com.lifestyle.buddydetagging.view.detagging.model;

import java.util.List;

public class ItemListLS {

    public double discountAmount;
    public double extSellPrice;
    public String itemID;
    public String itemIdentifier;
    public int quantity;
    public double sellPrice;
    public String itemEAN;
    public String itemDesc;
   // public String itemDept;
   // public String itemBrand;
    public String itemColour;
    public String itemSize;
    public String mmrpFlag;
  //  public double couponDiscountAmount;
  //  public double promoDiscountAmount;
    public boolean isFreebie;
 //   public boolean isMembership;



    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getExtSellPrice() {
        return extSellPrice;
    }

    public void setExtSellPrice(double extSellPrice) {
        this.extSellPrice = extSellPrice;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getItemEAN() {
        return itemEAN;
    }

    public void setItemEAN(String itemEAN) {
        this.itemEAN = itemEAN;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemColour() {
        return itemColour;
    }

    public void setItemColour(String itemColour) {
        this.itemColour = itemColour;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getMmrpFlag() {
        return mmrpFlag;
    }

    public void setMmrpFlag(String mmrpFlag) {
        this.mmrpFlag = mmrpFlag;
    }

    public boolean isFreebie() {
        return isFreebie;
    }

    public void setFreebie(boolean freebie) {
        isFreebie = freebie;
    }
}
