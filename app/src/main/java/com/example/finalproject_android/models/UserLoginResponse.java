package com.example.finalproject_android.models;

public class UserLoginResponse {
    private String message;
    private String token;
    private String email;
    public UserLoginResponse(String token,String message,String email) {
        this.message = message;
        this.token = token;
        this.email = email;
    }
    public String getEmail(){
        return email;
    }
    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
