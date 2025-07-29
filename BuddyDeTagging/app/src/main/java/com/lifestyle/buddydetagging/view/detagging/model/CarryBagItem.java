package com.lifestyle.buddydetagging.view.detagging.model;

import java.util.List;

public class CarryBagItem {

    public String itemIdentifier;
    public int discountAmount;
    public int extSellPrice;
    public String itemID;
    public Object giftCouponList;
    public List<PromoList> promoList;
    public int quantity;
    public int sellPrice;
    public String itemEAN;
    public String itemDesc;
    public String itemDept;
    public String itemBrand;
    public String itemColour;
    public String itemSize;
    public String mmrpFlag;


    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getExtSellPrice() {
        return extSellPrice;
    }

    public void setExtSellPrice(int extSellPrice) {
        this.extSellPrice = extSellPrice;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public Object getGiftCouponList() {
        return giftCouponList;
    }

    public void setGiftCouponList(Object giftCouponList) {
        this.giftCouponList = giftCouponList;
    }

    public List<PromoList> getPromoList() {
        return promoList;
    }

    public void setPromoList(List<PromoList> promoList) {
        this.promoList = promoList;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getItemDept() {
        return itemDept;
    }

    public void setItemDept(String itemDept) {
        this.itemDept = itemDept;
    }

    public String getItemBrand() {
        return itemBrand;
    }

    public void setItemBrand(String itemBrand) {
        this.itemBrand = itemBrand;
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

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
}
