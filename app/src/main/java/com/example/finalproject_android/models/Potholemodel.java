package com.example.finalproject_android.models;

public class Potholemodel {
    private double latitude;
    private double longitude;
    private String type;
    private String author;
    public  Potholemodel(double lat, double lon, String type, String author){
        this.author = author;
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getType() {
        return type;
    }
    public String getAuthor() {
        return author;
    }
}