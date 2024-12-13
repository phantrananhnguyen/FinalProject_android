package com.example.finalproject_android.models;

import android.net.Uri;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public class UserUpdateRequest {

    private String name;
    private String base64Image;
    private String sex;
    private String bio;
    private String birthday;
    private String phoneNumber;
    private String address;

    public UserUpdateRequest(String name, String address, String base64Image, String sex, String bio, String birthday, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.base64Image = base64Image;
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
        return base64Image;
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