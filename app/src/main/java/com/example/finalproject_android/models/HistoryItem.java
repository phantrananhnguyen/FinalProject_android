package com.example.finalproject_android.models;

public class HistoryItem {
    private double latitude;
    private double longitude;
    private String type;
    private String author;
    private String date;

    public HistoryItem(double lat, double lon, String type, String author, String date) {
        this.author = author;
        this.latitude = lat;
        this.longitude = lon;
        this.type = type;
        this.date =date;
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

    public String getDate() {
        return date;
    }
}

