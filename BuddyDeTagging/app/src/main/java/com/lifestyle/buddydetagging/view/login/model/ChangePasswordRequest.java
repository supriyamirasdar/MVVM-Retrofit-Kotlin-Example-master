package com.lifestyle.buddydetagging.view.login.model;

public class ChangePasswordRequest {
    public String userId;
    public String emailId;
    public String  password;
    public String  oldPassword ;

    // Getter Methods

    public String getUserId() {
        return userId;
    }

    public String getEmailId() {
        return emailId;
    }

    // Setter Methods

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
