package com.example.finalproject_android.models;

public class UserUpdateResponse {

    private String id;
    private String message;
    private String email;
    private String name;
    private String profilePicture;
    private String token;
    private boolean isVerified;
    private String role;
    private String createdAt;
    private String updatedAt;
    private String sex;
    private String bio;
    private String birthday;
    private String phoneNumber;
    private int score;


    public UserUpdateResponse() {

    }

    public UserUpdateResponse(String id, String message, String email, String name,
                              String profilePicture, String token, boolean isVerified,
                              String role, String createdAt, String updatedAt,
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