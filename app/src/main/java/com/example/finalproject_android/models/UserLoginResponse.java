package com.example.finalproject_android.models;

public class UserLoginResponse {
    private String message;
    private String userId;
    private Boolean isFirstLogin;
    private String token;
    private String email;
    private String profilePicture;

    public String getMessage() {
        return message;
    }

    public boolean isFirstLogin() {
        return "true".equals(isFirstLogin);
    }

    public String getToken() {
        return token;
    }

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
}
