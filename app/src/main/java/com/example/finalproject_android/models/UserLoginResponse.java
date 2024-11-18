package com.example.finalproject_android.models;

public class UserLoginResponse {
    private String message;
    private String id;
    private String isFirstLogin;
    private String token;

    public String getMessage() {
        return message;
    }

    public boolean isFirstLogin() {
        return "true".equals(isFirstLogin);
    }

    public String getToken() {
        return token;
    }
}
