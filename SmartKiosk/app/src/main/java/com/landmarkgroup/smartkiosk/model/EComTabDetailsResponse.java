package com.landmarkgroup.smartkiosk.model;

import java.util.List;

public class EComTabDetailsResponse extends StatusResponse {

    private String serverErrormsg;
    private String storeEnable;
    private List<EComTabDetails> tabList;

    public String getServerErrormsg() {
        return serverErrormsg;
    }

    public void setServerErrormsg(String serverErrormsg) {
        this.serverErrormsg = serverErrormsg;
    }

    public String getStoreEnable() {
        return storeEnable;
    }

    public void setStoreEnable(String storeEnable) {
        this.storeEnable = storeEnable;
    }

    public List<EComTabDetails> getTabList() {
        return tabList;
    }

    public void setTabList(List<EComTabDetails> tabList) {
        this.tabList = tabList;
    }
}