package com.example.finalproject_android.models;

public class UserLoginResponse {
    private String message;
    private String userId;
    private Boolean isFirstLogin;
    private String token;
    private String email;
    private String profilePicture;

    // Constructor từ nhánh AnhNguyen
    public UserLoginResponse(String token, String message, String email) {
        this.message = message;
        this.token = token;
        this.email = email;
    }

    // Constructor không tham số (từ nhánh HEAD)
    public UserLoginResponse() {
        // Giữ lại nếu cần khởi tạo đối tượng trống
    }

    // Các getter
    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean getIsFirstLogin() {
        return isFirstLogin;
    }

    public boolean isFirstLogin() {
        return "true".equals(isFirstLogin);
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
