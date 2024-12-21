package com.example.finalproject_android.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Pothole {
    @SerializedName("_id") // Ánh xạ từ "_id" trong JSON
    private String id;
    private String journey_id; // Journey ID được thêm vào đây
    private String author; // user_id
    @SerializedName("latitude") // Ánh xạ từ "latitude" trong JSON
    private String lat;

    @SerializedName("longitude") // Ánh xạ từ "longitude" trong JSON
    private String lon;
    private String type; // "Caution", "Warning", "Danger"
    private String state; // "Accept", "Pending", "Reject"
    private String img; // link ảnh trong database
    private Date created_at;
    private Date updated_at;

    // Constructor
    public Pothole(String id, String journey_id, String author, String lat, String lon, String type, String state, String img, Date created_at, Date updated_at) {
        this.id = id;
        this.journey_id = journey_id; // Khởi tạo journey_id
        this.author = author;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.state = state;
        this.img = img;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Pothole(String id, String type, String state, Date created_at, String lat, String lon) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.img = img;
        this.created_at = created_at;
        this.lat = lat;
        this.lon = lon;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getJourneyId() {  // Getter cho journey_id
        return journey_id;
    }

    public String getAuthor() {
        return author;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public String getImg() {
        return img;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setJourneyId(String journey_id) {  // Setter cho journey_id
        this.journey_id = journey_id;
    }

    public void setAuthor(String user_id) {
        this.author = user_id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }
}
