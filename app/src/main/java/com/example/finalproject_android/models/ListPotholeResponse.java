package com.example.finalproject_android.models;

import java.util.List;

// Định nghĩa Response chứa mảng potholes
public class ListPotholeResponse {
    private List<Potholemodel> potholes;

    public List<Potholemodel> getPotholes() {
        return potholes;
    }

    public void setPotholes(List<Potholemodel> potholes) {
        this.potholes = potholes;
    }
}