package com.example.bleledcontroller;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bleledcontroller.bluetooth.BleDevice;
import com.example.bleledcontroller.bluetooth.BluetoothProvider;
import com.example.bleledcontroller.bluetooth.ConnectedDevice;
import com.example.bleledcontroller.bluetooth.MockBluetoothProvider;
import com.example.bleledcontroller.ui.main.ConfigurationFragment;
import com.example.bleledcontroller.ui.main.DebugLogFragment;
import com.example.bleledcontroller.ui.main.ScanFragment;
import com.example.bleledcontroller.views.ConfigurationView;
import com.example.bleledcontroller.views.DebugView;
import com.example.bleledcontroller.views.ScanView;

public class ViewModel {
    private DebugView debugView = null;
    private ScanView scanView = null;
    private ConfigurationView configurationView = null;
    private Fragment[] uiViews = new Fragment[] {};
    private final BluetoothProvider btProvider = new MockBluetoothProvider();

    public ViewModel() {
        init(DebugLogFragment.newInstance(this), ScanFragment.newInstance(this), ConfigurationFragment.newInstance(this));
    }

    public ViewModel(DebugView debugView, ScanView scanView, ConfigurationView configurationView) {
        init(debugView, scanView, configurationView);
    }

    private void init(DebugView debugView, ScanView scanView, ConfigurationView configurationView) {
        this.debugView = debugView;
        this.scanView = scanView;
        this.configurationView = configurationView;

        uiViews = new Fragment[] {
                scanView,
                configurationView,
                debugView
        };
    }

    public void beginScan() {
        logMessage("Starting scan.");
        btProvider.startScan(this::onDeviceDiscovered);
    }

    public void stopScan() {
        btProvider.stopScan();
        logMessage("Scan stopped.");
    }

    public void connect(BleDevice device)
    {
        logMessage("Connecting to device: " + device.getName());
        btProvider.connectToDevice(device, this::onDeviceConnected, this::onConnectionFailed);
    }

    public void reloadConfiguration(ConnectedDevice device) {
        // Notify UI elements that a reload is taking place?
        logMessage("Reloading configuration for device.");
        btProvider.readDeviceSettings(device, this::onDeviceConnected);
    }

    public void updateConfiguration(ConnectedDevice device) {
        // Notify UI elements that an update is taking place?
        logMessage("Updating settings for device.");
        btProvider.updateDevice(device, this::onDeviceConnected);
    }

    public void disconnect() {
        logMessage("Disconnecting...");
        btProvider.disconnect();
        logMessage("Disconnected.");
        scanView.setDisconnectedState();
        configurationView.setDisconnectedState();
    }

    public void logMessage(String message) {
        debugView.addText(message);
    }

    public FragmentStateAdapter getFragmentStateAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        return new FragmentStateAdapter(fragmentManager, lifecycle) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Check position for range
                return uiViews[position];
            }

            @Override
            public int getItemCount() {
                return uiViews.length;
            }
        };
    }

    private void onDeviceDiscovered(BleDevice device) {
        logMessage("Discovered device: " + device.getName());
        scanView.addDiscoveredDevice(device);
    }

    private void onDeviceConnected(ConnectedDevice device) {
        logMessage("Connected to device: " + device.getName());
        scanView.setConnectedDevice(device);
        configurationView.setConnectedDevice(device);
    }

    private void onConnectionFailed(BleDevice device) {
        logMessage("Failed to connect to device: " + device.getName());
        scanView.setConnectionFailed();
    }
}
