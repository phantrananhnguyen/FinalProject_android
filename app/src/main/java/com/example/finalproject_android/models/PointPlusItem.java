package com.example.finalproject_android.models;
public class PointPlusItem {
    private String pointText;
    private String congratulationText;
    private String acceptedAnnouncementText;
    private String potholeId;
    private String potholeType;
    private String potholeDate;
    private int point;

    public PointPlusItem(String pointText, String congratulationText, String acceptedAnnouncementText,
                         String potholeId, String potholeType, String potholeDate, int point) {
        this.pointText = pointText;
        this.congratulationText = congratulationText;
        this.acceptedAnnouncementText = acceptedAnnouncementText;
        this.potholeId = potholeId;
        this.potholeType = potholeType;
        this.potholeDate = potholeDate;
        this.point = point;
    }

    public String getPointText() { return pointText; }
    public String getCongratulationText() { return congratulationText; }
    public String getAcceptedAnnouncementText() { return acceptedAnnouncementText; }
    public String getPotholeId() { return potholeId; }
    public String getPotholeType() { return potholeType; }
    public String getPotholeDate() { return potholeDate; }
    public int getPoint() { return point; }
}
