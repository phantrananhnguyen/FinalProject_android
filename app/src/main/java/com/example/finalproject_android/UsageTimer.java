package com.example.finalproject_android;

import android.content.Context;
import android.content.SharedPreferences;

public class UsageTimer {
    private long sessionStartTime = 0; // Thời gian bắt đầu phiên
    private long totalUsageTime = 0;  // Tổng thời gian sử dụng (ms)

    public void startSession() {
        sessionStartTime = System.currentTimeMillis();
    }

    public void stopSession() {
        if (sessionStartTime != 0) {
            long sessionEndTime = System.currentTimeMillis();
            totalUsageTime += (sessionEndTime - sessionStartTime);
            sessionStartTime = 0;
        }
    }

    public long getTotalUsageTime() {
        return totalUsageTime;
    }

    public void reset() {
        totalUsageTime = 0;
    }

    // Lưu và tải thời gian sử dụng với SharedPreferences
    public void saveTotalUsageTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UsagePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("totalUsageTime", totalUsageTime);
        editor.apply();
    }

    public void loadTotalUsageTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UsagePrefs", Context.MODE_PRIVATE);
        totalUsageTime = prefs.getLong("totalUsageTime", 0);
    }
}
