package com.example.finalproject_android;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalproject_android.afterlogin.Dashboard;
import com.example.finalproject_android.afterlogin.Map;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomNavigation extends AppCompatActivity {
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;

    private Fragment dashboardFragment;
    private Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Xóa background cho BottomNavigationView
        bottomNavigationView.setBackground(null);

        dashboardFragment = new Dashboard();
        mapFragment = new Map();

        // Display the Dashboard fragment by default
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, dashboardFragment, "DASHBOARD")
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Hide both fragments first
            if (dashboardFragment.isAdded()) fragmentTransaction.hide(dashboardFragment);
            if (mapFragment.isAdded()) fragmentTransaction.hide(mapFragment);

            // Show the selected fragment
            if (item.getItemId() == R.id.dashboard) {
                if (dashboardFragment.isAdded()) {
                    fragmentTransaction.show(dashboardFragment);
                } else {
                    fragmentTransaction.add(R.id.fragment_container, dashboardFragment, "DASHBOARD");
                }
            } else if (item.getItemId() == R.id.map) {
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
