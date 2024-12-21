package com.example.finalproject_android.models;

import java.util.Date;

public class Journey {
    private String user_id;
    private String start_time;
    private String end_time;
    private double start_latitude;
    private double start_longitude;
    private double end_latitude;
    private double end_longitude;
    private double distance;
    private Date created_at;
    private Date updated_at;

    public Journey() {}

    public Journey(String user_id, String start_time, String end_time, double start_latitude, double start_longitude,
                   double end_latitude, double end_longitude, double distance, Date created_at, Date updated_at ) {
        this.user_id = user_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.distance = distance;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public double getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(double end_latitude) {
        this.end_latitude = end_latitude;
    }

    public double getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(double end_longitude) {
        this.end_longitude = end_longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Date getCreated_at(Date created_at)
        {
        return created_at;
    }
    public void setCreated_at(Date created_at)
        {
        this.created_at = created_at;
    }
    public Date getUpdated_at(Date updated_at)
    {
        return updated_at;
    }
    public void setUpdated_at(Date updated_at)
    {
        this.updated_at = updated_at;
    }

}
