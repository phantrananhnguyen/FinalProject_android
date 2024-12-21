package com.example.finalproject_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//Only used for timer
public class BaseActivity extends AppCompatActivity {
    protected UsageTimer usageTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usageTimer = new UsageTimer();
        usageTimer.loadTotalUsageTime(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        usageTimer.startSession();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usageTimer.stopSession();
        usageTimer.saveTotalUsageTime(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usageTimer.saveTotalUsageTime(this);
    }
}


/* Usage:
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hiển thị tổng thời gian sử dụng
        long totalUsageTimeInSeconds = usageTimer.getTotalUsageTime() / 1000;
        Toast.makeText(this, "Tổng thời gian: " + totalUsageTimeInSeconds + " giây", Toast.LENGTH_SHORT).show();
    }
}

 */