package com.example.finalproject_android.models;

import android.net.Uri;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public class UserInfo {
    private String name;
    private String birthday;
    private String address;
    private String bio;
    private String membertype;
    private String since;
    private String email;
    private String sex;
    private String phone;

    public String getPhone() {
        return phone;
    }

    // Constructor with all fields
    public UserInfo(String name, String birthday,String phone, String address, String bio, String membertype, String since, String email, String sex) {
        this.name = name;
        this.birthday = birthday;
        this.address = address;
        this.bio = bio;
        this.membertype = membertype;
        this.since = since;
        this.email = email;
        this.sex = sex;
        this.phone = phone;
    }


    public void updateUserInfo(String name, String birthday, String address, String bio, String membertype,String phone, String since, String sex) {
        this.name = name != null ? name : this.name;
        this.birthday = birthday != null ? birthday : this.birthday;
        this.address = address != null ? address : this.address;
        this.bio = bio != null ? bio : this.bio;
        this.membertype = membertype != null ? membertype : this.membertype;
        this.phone = phone != null ? phone : this.phone;
        this.since = since != null ? since : this.since;
        this.sex = sex != null ? sex : this.sex;
    }
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return membertype;
    }

    public void setProfilePicture(String membertype) {
        this.membertype = membertype;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}

