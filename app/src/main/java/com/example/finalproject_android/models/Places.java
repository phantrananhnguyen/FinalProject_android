package com.example.finalproject_android.models;

import org.mapsforge.core.model.LatLong;

import java.util.List;

public class Places {
    String message;
    List<Feature> places;
    List<LatLong> coordinates;
    Double distance;
    Double duration;
    public List<LatLong> getCoordinates() {
        return coordinates;
    }

    public Places(List<LatLong> coordinates) {
        this.coordinates = coordinates;
    }

    public String getMessage() {
        return message;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getDuration() {
        return duration;
    }

    public Places(List<Feature> places, String message, double distance, double duration) {
        this.places = places;
        this.message=message;
        this.distance = distance;
        this.duration = duration;
    }
    public List<Feature> getPlaces() {
        return places;
    }
}
