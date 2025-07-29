package com.lifestyle.buddydetagging.view.detagging.model;

public class OrderDto {
    int orderStatus;
    String orderId;
    String customerPhoneNo;

    String createdAt;
    String extSellPrice;
    String sellPrice;


    String detaggedByST="";
    int orderDetagged=0;

    String detagBy;
    String detagDate;

    public OrderDto(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderDto(int orderStatus, String orderId, String customerPhoneNo, String createdAt, String extSellPrice, String sellPrice) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.customerPhoneNo = customerPhoneNo;
        this.createdAt = createdAt;
        this.extSellPrice = extSellPrice;
        this.sellPrice = sellPrice;
    }

    public OrderDto(int orderStatus, String orderId, String customerPhoneNo, String createdAt, String extSellPrice, String sellPrice, String detaggedByST, int orderDetagged) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.customerPhoneNo = customerPhoneNo;
        this.createdAt = createdAt;
        this.extSellPrice = extSellPrice;
        this.sellPrice = sellPrice;
        this.detaggedByST = detaggedByST;
        this.orderDetagged = orderDetagged;
    }

    public OrderDto(int orderStatus, String orderId, String customerPhoneNo, String createdAt, String extSellPrice, String sellPrice, String detaggedByST, int orderDetagged, String detagBy, String detagDate) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.customerPhoneNo = customerPhoneNo;
        this.createdAt = createdAt;
        this.extSellPrice = extSellPrice;
        this.sellPrice = sellPrice;
        this.detaggedByST = detaggedByST;
        this.orderDetagged = orderDetagged;
        this.detagBy = detagBy;
        this.detagDate = detagDate;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerPhoneNo() {
        return customerPhoneNo;
    }

    public void setCustomerPhoneNo(String customerPhoneNo) {
        this.customerPhoneNo = customerPhoneNo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExtSellPrice() {
        return extSellPrice;
    }

    public void setExtSellPrice(String extSellPrice) {
        this.extSellPrice = extSellPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getDetaggedByST() {
        return detaggedByST;
    }

    public void setDetaggedByST(String detaggedByST) {
        this.detaggedByST = detaggedByST;
    }

    public int getOrderDetagged() {
        return orderDetagged;
    }

    public void setOrderDetagged(int orderDetagged) {
        this.orderDetagged = orderDetagged;
    }

    public String getDetagBy() {
        return detagBy;
    }

    public void setDetagBy(String detagBy) {
        this.detagBy = detagBy;
    }

    public String getDetagDate() {
        return detagDate;
    }

    public void setDetagDate(String detagDate) {
        this.detagDate = detagDate;
    }
}
