package com.pittsfordpanthers.ledcontrollerv2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.pittsfordpanthers.ledcontrollerv2.bluetooth.AndroidBluetoothProvider;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.BluetoothProvider;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.MockBluetoothProvider;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int RUNTIME_PERMISSION_REQUEST_CODE = 1;

    private ViewModel viewModel = new ViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothProvider btProvider = CreateBluetoothProvider();
        viewModel.setBluetoothProvider(btProvider);

        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 pager = findViewById(R.id.pager);

        configureTabs(tabLayout, pager);
        configurePager(pager, tabLayout, viewModel.getFragmentStateAdapter(getSupportFragmentManager(), getLifecycle()));

        viewModel.logMessage("Initialized.");
    }

    private BluetoothProvider CreateBluetoothProvider() {
        BluetoothAdapter btAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        boolean isBluetoothAvailable = btAdapter != null && btAdapter.isEnabled();

        if (!isBluetoothAvailable) {
            viewModel.logMessage("No BT adapter detected or BT is not enabled. Using a MOCK instead.");
            return new MockBluetoothProvider();
        } else {
            viewModel.logMessage("API version: " + Build.VERSION.SDK_INT);
            // Request permissions if needed
            if (hasRequiredRuntimePermissions()) {
                viewModel.logMessage("Bluetooth permissions are already granted.");
            } else {
                requestRelevantRuntimePermissions();
            }

            AndroidBluetoothProvider provider = new AndroidBluetoothProvider(this);
            provider.setLogger(viewModel::logMessage);
            return provider;
        }
    }

    private void configureTabs(TabLayout tabLayout, ViewPager2 pager) {
        tabLayout.addTab(tabLayout.newTab().setText("Connection"));
        tabLayout.addTab(tabLayout.newTab().setText("Configure"));
        tabLayout.addTab(tabLayout.newTab().setText("Debug"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void configurePager(ViewPager2 pager, TabLayout tabLayout, FragmentStateAdapter adapter) {
        pager.setAdapter(adapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    //
    // Permission handling helpers
    //
    private boolean hasRequiredRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasPermission(Manifest.permission.BLUETOOTH_SCAN)
                    && hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    && hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestLegacyBluetoothPermission();
        } else {
            requestBluetoothPermissions();
        }
    }

    private void requestLegacyBluetoothPermission() {
        // Prior to v31 ("S"), location permissions are required.
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                RUNTIME_PERMISSION_REQUEST_CODE
        );
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                },
                RUNTIME_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                viewModel.logMessage("Bluetooth permission granted.");
            } else {
                viewModel.logMessage("Bluetooth permission denied.");
            }
        }
    }
}