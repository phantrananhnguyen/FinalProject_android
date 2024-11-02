package com.example.finalproject_android.models;

public class ResetRequest {
    public ResetRequest(String newPassword, String email) {
        this.newPassword = newPassword;
        this.email = email;
    }

    private String newPassword;
    private String email;
}
