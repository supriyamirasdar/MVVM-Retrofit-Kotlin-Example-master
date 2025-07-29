package com.lifestyle.buddydetagging.view.login.dto;

public class UserDTO {
	private String userId;
	private String fName;
	private String lName;
	private String email;
	private String contactNo;
	private String stroreId;
	private String stroreName;
	private int role;
	private String pwdChgReq;
	private String status;
	private String ouCode;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}



	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getStroreId() {
		return stroreId;
	}
	public void setStroreId(String stroreId) {
		this.stroreId = stroreId;
	}
	public String getStroreName() {
		return stroreName;
	}
	public void setStroreName(String stroreName) {
		this.stroreName = stroreName;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getPwdChgReq() {
		return pwdChgReq;
	}
	public void setPwdChgReq(String pwdChgReq) {
		this.pwdChgReq = pwdChgReq;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOuCode() {
		return ouCode;
	}
	public void setOuCode(String ouCode) {
		this.ouCode = ouCode;
	}
	
}
