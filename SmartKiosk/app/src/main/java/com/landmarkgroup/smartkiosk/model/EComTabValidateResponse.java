package com.landmarkgroup.smartkiosk.model;


public class EComTabValidateResponse extends StatusResponse {

	private String tabNo;
	private String tabName;
	private String tabId;
	private String serverErrormsg;
	//private String statusMessage;

	public String getTabNo() {
		return tabNo;
	}

	public void setTabNo(String tabNo) {
		this.tabNo = tabNo;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	public String getServerErrormsg() {
		return serverErrormsg;
	}

	public void setServerErrormsg(String serverErrormsg) {
		this.serverErrormsg = serverErrormsg;
	}

	/*public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}*/
}
