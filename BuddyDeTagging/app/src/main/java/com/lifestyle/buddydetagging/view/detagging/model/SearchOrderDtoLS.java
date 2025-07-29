package com.lifestyle.buddydetagging.view.detagging.model;

import java.util.List;

public class SearchOrderDtoLS {

    public String statusCode;
    public String statusMessage;
    public String orderId;
    public String accessToken;
    public List<OrderLS> orders;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<OrderLS> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderLS> orders) {
        this.orders = orders;
    }
}


