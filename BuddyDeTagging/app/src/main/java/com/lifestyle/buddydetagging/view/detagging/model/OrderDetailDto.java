package com.lifestyle.buddydetagging.view.detagging.model;

public class OrderDetailDto {

    private int orderItemStatus;
    private int tagStatus;
    private String tagStatusVal;

    ItemList itemList;

    //public Order order;

    public OrderDetailDto( ) {

    }

    public OrderDetailDto(int orderItemStatus, int tagStatus) {
        this.orderItemStatus = orderItemStatus;
        this.tagStatus = tagStatus;
    }

    public int getOrderItemStatus() {
        return orderItemStatus;
    }

    public void setOrderItemStatus(int orderItemStatus) {
        this.orderItemStatus = orderItemStatus;
    }

    public int getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(int tagStatus) {
        this.tagStatus = tagStatus;
    }

    public OrderDetailDto(int orderItemStatus, int tagStatus, String tagStatusVal) {
        this.orderItemStatus = orderItemStatus;
        this.tagStatus = tagStatus;
        this.tagStatusVal = tagStatusVal;
    }

    public String getTagStatusVal() {
        return tagStatusVal;
    }

    public void setTagStatusVal(String tagStatusVal) {
        this.tagStatusVal = tagStatusVal;
    }


    /*public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }*/

    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }
}
