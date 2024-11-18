package com.example.finalproject_android.models;

public class UserResponse {

    private String id;          // ID người dùng
    private String message;     // Thông điệp phản hồi
    private String email;       // Email người dùng
    private String name;        // Tên người dùng
    private String profilePicture; // URL ảnh đại diện (nếu có)
    private String token;       // Token xác thực
    private boolean isVerified; // Trạng thái xác minh email
    private String role;        // Vai trò người dùng (admin, user, v.v.)
    private String createdAt;   // Thời gian tạo tài khoản
    private String updatedAt;   // Thời gian cập nhật tài khoản gần nhất
    private String sex;
    private String bio;
    private String birthday;



    public UserResponse(String id, String message, String email, String name, String profilePicture, String token, boolean isVerified, String role, String createdAt, String updatedAt) {
        this.id = id;
        this.message = message;
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
        this.token = token;
        this.isVerified = isVerified;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getToken() {
        return token;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getRole() {
        return role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
