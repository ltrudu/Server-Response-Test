package com.ltrudu.serverresponsetest;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ltrudu.serverresponsetest.adapter.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    
    // Notification permission handling
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private boolean hasShownNotificationDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        setupNotificationPermissionLauncher();
        initializeViews();
        setupViewPager();
        setupTabLayout();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermission();
    }
    
    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }
    
    private void setupViewPager() {
        fragmentPagerAdapter = new FragmentPagerAdapter(this);
        viewPager.setAdapter(fragmentPagerAdapter);
    }
    
    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tab_test);
                    break;
                case 1:
                    tab.setText(R.string.tab_server_list);
                    break;
                case 2:
                    tab.setText(R.string.tab_settings);
                    break;
            }
        }).attach();
    }
    
    private void setupNotificationPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, getString(R.string.permission_granted_toast), Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission denied, show explanation and guide to settings
                        showNotificationPermissionDialog();
                    }
                });
    }
    
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires explicit notification permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                
                if (!hasShownNotificationDialog) {
                    requestNotificationPermission();
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8+ - check if notifications are enabled through NotificationManager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
                if (!hasShownNotificationDialog) {
                    showNotificationPermissionDialog();
                }
            }
        }
    }
    
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show explanation before requesting permission
                showNotificationRationaleDialog();
            } else {
                // Request permission directly
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    
    private void showNotificationRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.enable_notifications_title))
                .setMessage(getString(R.string.notification_rationale_message))
                .setIcon(R.drawable.ic_notification_server_test)
                .setPositiveButton(getString(R.string.allow), (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                })
                .setNegativeButton(getString(R.string.not_now), (dialog, which) -> {
                    hasShownNotificationDialog = true;
                    Toast.makeText(this, getString(R.string.permission_later_toast), Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }
    
    private void showNotificationPermissionDialog() {
        hasShownNotificationDialog = true;
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.enable_notifications_title))
                .setMessage(getString(R.string.notification_disabled_message))
                .setIcon(R.drawable.ic_notification_server_test)
                .setPositiveButton(getString(R.string.open_settings), (dialog, which) -> {
                    openAppNotificationSettings();
                })
                .setNegativeButton(getString(R.string.skip), (dialog, which) -> {
                    Toast.makeText(this, getString(R.string.permission_manual_toast), Toast.LENGTH_LONG).show();
                })
                .setCancelable(true)
                .show();
    }
    
    private void openAppNotificationSettings() {
        Intent intent = new Intent();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8+ - Open app-specific notification settings
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            // Android 7 and below - Open general app settings
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Fallback to general app settings if specific intent fails
            Intent fallbackIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            fallbackIntent.setData(Uri.parse("package:" + getPackageName()));
            try {
                startActivity(fallbackIntent);
            } catch (Exception fallbackException) {
                Toast.makeText(this, "Unable to open settings. Please enable notifications manually in Android Settings > Apps", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Public method to allow other components (like TestFragment) to trigger notification permission check
     */
    public void ensureNotificationPermission() {
        hasShownNotificationDialog = false;
        checkNotificationPermission();
    }
    
    /**
     * Check if notifications are currently enabled
     */
    public boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            return notificationManager != null && notificationManager.areNotificationsEnabled();
        }
        return true; // Pre-Android 8 doesn't have notification controls
    }
}