package com.lifestyle.buddydetagging.view.detagging.model;

import java.util.List;

public class OrderLS {

    public String orderId;
    public String orderDate;
  //  public String cardNumber;
    public String customerPhoneNo;
    public String storeCode;
   // public String storeKeyId;
   // public boolean hasPaid;
    public boolean hasDetagged;
    public String userId;
    public String userName;
    public List<ItemListLS> itemList;
    public CarryBagItemLS carryBagItem;
   // public boolean isCompleted;




    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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



    public boolean isHasDetagged() {
        return hasDetagged;
    }

    public void setHasDetagged(boolean hasDetagged) {
        this.hasDetagged = hasDetagged;
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

    public List<ItemListLS> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemListLS> itemList) {
        this.itemList = itemList;
    }

    public CarryBagItemLS getCarryBagItem() {
        return carryBagItem;
    }

    public void setCarryBagItem(CarryBagItemLS carryBagItem) {
        this.carryBagItem = carryBagItem;
    }




}
