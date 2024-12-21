package com.example.finalproject_android.models;

import java.util.List;

// Định nghĩa Response chứa mảng potholes
public class ListPotholeResponse {
    private List<Pothole> potholes;

    public List<Pothole> getPotholes() {
        return potholes;
    }

    public void setPotholes(List<Pothole> potholes) {
        this.potholes = potholes;
    }
}