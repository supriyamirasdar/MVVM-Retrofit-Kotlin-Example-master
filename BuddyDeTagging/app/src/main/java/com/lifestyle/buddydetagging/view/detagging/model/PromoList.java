package com.lifestyle.buddydetagging.view.detagging.model;

public class PromoList {

    public double discountAmount;
    public String promoDesc;
    public String promoID;
    public int promoType;

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPromoDesc() {
        return promoDesc;
    }

    public void setPromoDesc(String promoDesc) {
        this.promoDesc = promoDesc;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public int getPromoType() {
        return promoType;
    }

    public void setPromoType(int promoType) {
        this.promoType = promoType;
    }
}
