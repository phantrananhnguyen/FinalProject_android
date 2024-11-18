package com.example.finalproject_android.models;

import android.net.Uri;

public class UserUpdateRequest {

    private String name;
    private String profilePicture;
    private String sex;
    private String bio;
    private String birthday;
    private String phoneNumber;
    private String address;

    public UserUpdateRequest(String name, String address, String sex, String bio, String birthday, String phoneNumber) {
        this.name = name;
        this.sex = sex;
        this.bio = bio;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicture() {
        return profilePicture;
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
