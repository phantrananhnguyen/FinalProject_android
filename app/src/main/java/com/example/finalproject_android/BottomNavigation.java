package com.example.finalproject_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalproject_android.afterlogin.Dashboard;
import com.example.finalproject_android.afterlogin.Map;
import com.example.finalproject_android.afterlogin.Profile;
import com.example.finalproject_android.models.Feature;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomNavigation extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    private Fragment dashboardFragment;
    private Fragment mapFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Remove background for BottomNavigationView
        bottomNavigationView.setBackground(null);

        // Initialize fragments
        dashboardFragment = new Dashboard();
        mapFragment = new Map();
        profileFragment = new Profile();
        // Set the default fragment to Dashboard
        setFragment(dashboardFragment);

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                setFragment(dashboardFragment);
                return true;
            } else if (item.getItemId() == R.id.map) {
                setFragment(mapFragment);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                setFragment(profileFragment);
                return true;
            }
            return false;
        });
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent); // Handle any new intent received
    }

    private void handleIntent(Intent intent) {
        Feature feature = (Feature) intent.getSerializableExtra("feature");

        if (feature != null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currentFragment instanceof Map) {
                ((Map) currentFragment).updateFeature(feature); // Implement an updateFeature method in Map to handle feature update.
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("feature", feature);
                mapFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mapFragment)
                        .addToBackStack(null)
                        .commit();
                bottomNavigationView.setSelectedItemId(R.id.map);
            }
        } else {
            setFragment(dashboardFragment);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
