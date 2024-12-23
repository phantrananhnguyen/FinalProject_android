package com.example.finalproject_android.models;

import java.io.Serializable;

public class Potholemodel implements Serializable {
    private double latitude;
    private double longitude;
    private String type;
    private String author;
    private String date;

    public String getDate() {
        return date;
    }

    public  Potholemodel(double lat, double lon, String type, String author, String date){
        this.author = author;
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.date = date;
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