package com.example.finalproject_android.models;

public class Achievement {
    private String name;
    private int thresholdPoint;
    private String icon;
    private int backgroundColor;
    private int titleTextColor;
    private int subtitleTextColor;
    private int quoteTextColor;
    private String titleText;      // Nội dung tiêu đề
    private String subtitleText;   // Nội dung phụ đề
    private String quoteText;      // Nội dung trích dẫn

    // Constructor
    public Achievement(String name, int thresholdPoint, String icon,
                       int backgroundColor, int titleTextColor, int subtitleTextColor, int quoteTextColor,
                       String titleText, String subtitleText, String quoteText) {
        this.name = name;
        this.thresholdPoint = thresholdPoint;
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.titleTextColor = titleTextColor;
        this.subtitleTextColor = subtitleTextColor;
        this.quoteTextColor = quoteTextColor;
        this.titleText = titleText;
        this.subtitleText = subtitleText;
        this.quoteText = quoteText;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThresholdPoint() {
        return thresholdPoint;
    }

    public void setThresholdPoint(int thresholdPoint) {
        this.thresholdPoint = thresholdPoint;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getSubtitleTextColor() {
        return subtitleTextColor;
    }

    public void setSubtitleTextColor(int subtitleTextColor) {
        this.subtitleTextColor = subtitleTextColor;
    }

    public int getQuoteTextColor() {
        return quoteTextColor;
    }

    public void setQuoteTextColor(int quoteTextColor) {
        this.quoteTextColor = quoteTextColor;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getSubtitleText() {
        return subtitleText;
    }

    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }
}
