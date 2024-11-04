package com.example.finalproject_android.models;

public class UserResponse {

    private String message;

    private String email;

    private String token;

    private boolean isVerified;


    public String getEmail() {
        return email;
    }

    public UserResponse(String message, String email, String token, boolean isVerified) {
        this.message = message;
        this.email = email;
        this.token = token;
        this.isVerified =isVerified;
    }

    public String getMessage() {
        return message;
    }

    public boolean getStatus(){
        return isVerified;
    }

    public String getToken() {
        return token; // Trả về token
    }
}

