package com.lifestyle.buddydetagging.view.login.dto;


public class LoginResponseDTO extends ResponseDTO{
	private UserDTO data;

	public UserDTO getData() {
		return data;
	}

	public void setData(UserDTO data) {
		this.data = data;
	}
	
}
