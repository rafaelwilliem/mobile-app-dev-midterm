package com.example.dailyplanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dailyplanner.fragments.DashboardFragment;
import com.example.dailyplanner.fragments.HistoryFragment;
import com.example.dailyplanner.fragments.TaskListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    private BottomNavigationView bottomNavigationView;
    private final Fragment dashboardFragment = new DashboardFragment();
    private final Fragment taskListFragment = new TaskListFragment();
    private final Fragment historyFragment = new HistoryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setupBottomNavigation();
        requestNotificationPermissionIfNeeded();

        // Tampilkan fragment default (DashboardFragment) pada startup pertama kali
        if (savedInstanceState == null) {
            loadFragment(dashboardFragment, getString(R.string.nav_dashboard));
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                loadFragment(dashboardFragment, getString(R.string.nav_dashboard));
                return true;
            } else if (itemId == R.id.nav_tasks) {
                loadFragment(taskListFragment, getString(R.string.nav_tasks));
                return true;
            } else if (itemId == R.id.nav_history) {
                loadFragment(historyFragment, getString(R.string.nav_history));
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }
}
