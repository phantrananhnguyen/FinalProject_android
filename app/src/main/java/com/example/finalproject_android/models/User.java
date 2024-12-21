package com.example.finalproject_android.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private String id;               // ID người dùng
    private String message;          // Thông điệp phản hồi
    private String email;
    @SerializedName("username")// Email người dùng
    private String name;             // Tên người dùng
    private String profilePicture;   // URL ảnh đại diện (nếu có)
    private String token;            // Token xác thực
    private boolean isVerified;      // Trạng thái xác minh email
    private String role;             // Vai trò người dùng (admin, user, v.v.)
    private String createdAt;        // Thời gian tạo tài khoản
    private String updatedAt;        // Thời gian cập nhật tài khoản gần nhất
    private String sex;              // Giới tính người dùng
    private String bio;              // Tiểu sử người dùng
    private String birthday;         // Ngày sinh người dùng
    private String phoneNumber;      // Số điện thoại người dùng
    private int score;               // Điểm người dùng (nếu có)
    private boolean isFirstLogin;

    // Constructor
    public User(String id, String message, String email, String name, String profilePicture,
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

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }
}
