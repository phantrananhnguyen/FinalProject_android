package com.example.finalproject_android.models;

public class Pothole {
    private String id;
    private String user_id;
    private String location;
    private String coordinates;
    private String potholeId;
    private String date;
    private String type; // "Caution", "Warning", "Danger"
    private String state; // "Accept", "Pending", "Reject"

    // Constructor
    public Pothole(String id, String user_id, String location, String coordinates, String potholeId, String date, String type, String state) {
        this.id = id;
        this.user_id = user_id;
        this.location = location;
        this.coordinates = coordinates;
        this.potholeId = potholeId;
        this.date = date;
        this.type = type;
        this.state = state;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return user_id; }
    public String getLocation() { return location; }
    public String getCoordinates() { return coordinates; }
    public String getPotholeId() { return potholeId; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getState() { return state; }
}
