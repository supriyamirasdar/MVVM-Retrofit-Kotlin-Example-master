package com.lifestyle.buddydetagging.view.detagging.model;

import java.util.List;

public class Order {

    public String orderId;
    public String orderDate;
    public String cardNumber;
    public String customerPhoneNo;
    public String storeCode;
    public String storeKeyId;
    public String createdAt;
    public boolean hasPaid;
    public boolean hasDetagged;
    public String userId;
    public String userName;
    public List<ItemList> itemList;
    public CarryBagItem carryBagItem;
    public boolean isCompleted;


    public String paymentVerifiedAt;
    public boolean hasExpired;



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCustomerPhoneNo() {
        return customerPhoneNo;
    }

    public void setCustomerPhoneNo(String customerPhoneNo) {
        this.customerPhoneNo = customerPhoneNo;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreKeyId() {
        return storeKeyId;
    }

    public void setStoreKeyId(String storeKeyId) {
        this.storeKeyId = storeKeyId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<ItemList> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }

    public boolean isHasDetagged() {
        return hasDetagged;
    }

    public void setHasDetagged(boolean hasDetagged) {
        this.hasDetagged = hasDetagged;
    }

    public String getPaymentVerifiedAt() {
        return paymentVerifiedAt;
    }

    public void setPaymentVerifiedAt(String paymentVerifiedAt) {
        this.paymentVerifiedAt = paymentVerifiedAt;
    }

    public boolean isHasExpired() {
        return hasExpired;
    }

    public void setHasExpired(boolean hasExpired) {
        this.hasExpired = hasExpired;
    }

    public CarryBagItem getCarryBagItem() {
        return carryBagItem;
    }

    public void setCarryBagItem(CarryBagItem carryBagItem) {
        this.carryBagItem = carryBagItem;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
