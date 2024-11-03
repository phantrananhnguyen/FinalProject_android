package com.example.finalproject_android.models;

public class UserResponse {

    private String message;

    private boolean isVerify = false;
    private String token;

    public boolean isVerify() {
        return isVerify;
    }

    public UserResponse(String message, String token, boolean isVerify) {
        this.message = message;
        this.token = token;
        this.isVerify = isVerify;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token; // Trả về token
    }
}
