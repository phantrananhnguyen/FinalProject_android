package com.example.finalproject_android.models;

public class EmailResponse {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EmailResponse(String token, boolean isVerified) {
        this.token = token;
        this.isVerified= isVerified;
    }

    private String token;

    // Trạng thái xác nhận của email
    private boolean isVerified = false ;



    public boolean isVerified() {
        return isVerified; // Trả về trạng thái xác nhận
    }


}

