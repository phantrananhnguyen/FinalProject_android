package com.example.finalproject_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.finalproject_android.afterlogin.Dashboard;
import com.example.finalproject_android.afterlogin.HistoryFragment;
import com.example.finalproject_android.afterlogin.Map;
import com.example.finalproject_android.afterlogin.HistoryPotholesFragment; // Để sử dụng Fragment HistoryPotholes
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigation extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    private Fragment dashboardFragment;
    private Fragment mapFragment;
    private Fragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        dashboardFragment = new Dashboard();
        mapFragment = new Map();
        historyFragment = new HistoryFragment();  // Sử dụng fragment HistoryPotholes

        // Hiển thị fragment Dashboard mặc định
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, dashboardFragment, "DASHBOARD")
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Handle item clicks
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            // Ẩn tất cả các fragment trước khi hiển thị fragment đã chọn
            if (dashboardFragment.isAdded()) fragmentTransaction.hide(dashboardFragment);
            if (mapFragment.isAdded()) fragmentTransaction.hide(mapFragment);
            if (historyFragment.isAdded()) fragmentTransaction.hide(historyFragment);

            // Kiểm tra xem mục nào đã được chọn
            if (item.getItemId() == R.id.history) {
                // Hiển thị fragment HistoryPotholes
                if (!historyFragment.isAdded()) {
                    fragmentTransaction.add(R.id.fragment_container, historyFragment, "HISTORY");
                } else {
                    fragmentTransaction.show(historyFragment);
                }
            } else if (item.getItemId() == R.id.dashboard) {
                // Hiển thị fragment dashboard
                if (dashboardFragment.isAdded()) {
                    fragmentTransaction.show(dashboardFragment);
                } else {
                    fragmentTransaction.add(R.id.fragment_container, dashboardFragment, "DASHBOARD");
                }
            } else if (item.getItemId() == R.id.map) {
                // Hiển thị fragment map
                if (mapFragment.isAdded()) {
                    fragmentTransaction.show(mapFragment);
                } else {
                    fragmentTransaction.add(R.id.fragment_container, mapFragment, "MAP");
                }
            }

            fragmentTransaction.commit();
            return true;
        });
    }
}
