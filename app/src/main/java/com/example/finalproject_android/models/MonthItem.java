package com.example.finalproject_android.models;

import java.util.List;

public class MonthItem<T> {  // Sử dụng Generics để có thể chứa bất kỳ loại dữ liệu nào
    private String monthName;
    private List<T> items;  // T có thể là Pothole hoặc PointPlus

    // Constructor nhận tên tháng và danh sách items
    public MonthItem(String monthName, List<T> items) {
        this.monthName = monthName;
        this.items = items;
    }

    // Getter cho tháng
    public String getMonthName() { 
        return monthName; 
    }

    // Getter cho danh sách items
    public List<T> getItems() { 
        return items; 
    }

    // Setters nếu cần
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
