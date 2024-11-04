package com.example.finalproject_android.models;

public class VerificationStatusResponse {
    private boolean isVerified;
    private String message;


    public VerificationStatusResponse( boolean isVerified) {

        this.isVerified = isVerified;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
