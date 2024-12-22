package com.example.finalproject_android.models;

import org.mapsforge.core.model.LatLong;

import java.io.Serializable;

public class Feature implements Serializable {
    private String name;
    private String amenity;
    private double latitude;
    private double longitude;
    private String operation;

    public String getName() {
        return name;
    }

    public String getAmenity() {
        return amenity;
    }

    public double getLat() {
        return latitude;
    }

    public double getLon() {
        return longitude;
    }

    public String getOperation() {
        return operation;
    }

    public Feature(String name, String amenity, double lat, double lon) {
        this.name = name;
        this.amenity = amenity;
        this.latitude = lat;
        this.longitude = lon;
    }
}
