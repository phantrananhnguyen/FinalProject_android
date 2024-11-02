package com.example.finalproject_android.models;

public class VerifyRequest {
    public VerifyRequest(String code, String email) {
        this.code = code;
        this.email = email;
    }

    private String code;
    private String email;
}
