package com.example.finalproject_android.models;

public class UserUpdateResponse {

    private String id;                // ID người dùng
    private String message;           // Thông điệp phản hồi
    private String email;             // Email người dùng
    private String name;              // Tên người dùng
    private String address;           // Địa chỉ (nếu có)
    private String profilePicture;    // URL ảnh đại diện
    private String token;             // Token xác thực
    private boolean isVerified;       // Trạng thái xác minh email
    private String role;              // Vai trò người dùng
    private String createdAt;         // Thời gian tạo tài khoản
    private String updatedAt;         // Thời gian cập nhật tài khoản gần nhất
    private String sex;               // Giới tính
    private String bio;               // Tiểu sử cá nhân
    private String birthday;          // Ngày sinh
    private String phoneNumber;       // Số điện thoại
    private int score;                // Điểm người dùng

    // Constructor đầy đủ từ nhánh HEAD
    public UserUpdateResponse(String id, String message, String email, String name, String profilePicture,
                              String token, boolean isVerified, String role, String createdAt, String updatedAt,
                              String sex, String bio, String birthday, String phoneNumber, int score) {
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
        this.sex = sex;
        this.bio = bio;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.score = score;
    }

    // Constructor đơn giản hơn từ nhánh `7b33d18`
    public UserUpdateResponse(String email, String name, String profilePicture, boolean isVerified, String role,
                              String createdAt, String sex, String bio, String birthday, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.profilePicture = profilePicture;
        this.isVerified = isVerified;
        this.role = role;
        this.createdAt = createdAt;
        this.sex = sex;
        this.bio = bio;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    // Getter cho tất cả thuộc tính
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

    public String getAddress() {
        return address;
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

    public String getSex() {
        return sex;
    }

    public String getBio() {
        return bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getScore() {
        return score;
    }
}
