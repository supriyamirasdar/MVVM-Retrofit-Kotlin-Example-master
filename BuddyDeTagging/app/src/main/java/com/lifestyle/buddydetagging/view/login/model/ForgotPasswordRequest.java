package com.lifestyle.buddydetagging.view.login.model;

public class ForgotPasswordRequest {
    public String userId;
    public String email;

    private int role;
    // Getter Methods

    public String getUserId() {
        return userId;
    }


    // Setter Methods

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
