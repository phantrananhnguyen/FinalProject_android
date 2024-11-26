package com.example.finalproject_android.models;

import org.mapsforge.core.model.LatLong;

import java.util.List;

public class Places {
    String message;
    List<Feature> places;
    List<LatLong> coordinates;

    public List<LatLong> getCoordinates() {
        return coordinates;
    }

    public Places(List<LatLong> coordinates) {
        this.coordinates = coordinates;
    }

    public String getMessage() {
        return message;
    }

    public Places(List<Feature> places, String message) {
        this.places = places;
        this.message=message;
    }
    public List<Feature> getPlaces() {
        return places;
    }
}
