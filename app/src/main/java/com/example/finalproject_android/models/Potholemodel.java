package com.example.finalproject_android.models;

import com.google.gson.annotations.SerializedName;

public class Potholemodel {
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    private String type;
    private String author;
    private String date;

    private String state;
    private String img;
    @SerializedName("_id") // Ánh xạ từ "_id" trong JSON
    private String id;

    public String getDate() {
        return date;
    }
    public Potholemodel() {

    }

    public  Potholemodel(double lat, double lon, String type, String author, String date){
        this.author = author;
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.date = date;
    }

    public  Potholemodel(double lat, double lon, String type, String author, String date, String state, String img){
        this.author = author;
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.date = date;
        this.state = state;
        this.img = img;
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

    public String getState() {
        return state;
    }
    public String getImg() {
        return img;
    }
    public String getId() {
        return id;
    }


}