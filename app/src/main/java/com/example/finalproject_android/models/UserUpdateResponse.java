package com.example.finalproject_android.models;

public class UserUpdateResponse {

    private String email;
    private String name;
    private String address;
    private String profilePicture;
    private boolean isVerified;
    private String role;
    private String createdAt;
    private String sex;
    private String bio;
    private String birthday;
    private String phoneNumber;


    public UserUpdateResponse() {

    }

    public UserUpdateResponse(String email, String name,
                              String profilePicture, boolean isVerified,
                              String role, String createdAt,
                              String sex, String bio, String birthday, String phoneNumber) {

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


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicture() {
        return profilePicture;
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

}