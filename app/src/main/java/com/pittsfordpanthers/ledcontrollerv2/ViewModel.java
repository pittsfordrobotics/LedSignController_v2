package com.pittsfordpanthers.ledcontrollerv2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pittsfordpanthers.ledcontrollerv2.bluetooth.BleDevice;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.BluetoothProvider;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.ui.main.ConfigurationFragment;
import com.pittsfordpanthers.ledcontrollerv2.ui.main.DebugLogFragment;
import com.pittsfordpanthers.ledcontrollerv2.ui.main.ScanFragment;
import com.pittsfordpanthers.ledcontrollerv2.views.ConfigurationView;
import com.pittsfordpanthers.ledcontrollerv2.views.DebugView;
import com.pittsfordpanthers.ledcontrollerv2.views.ScanView;

public class ViewModel {
    private DebugView debugView = null;
    private ScanView scanView = null;
    private ConfigurationView configurationView = null;
    private Fragment[] uiViews = new Fragment[] {};

    private BluetoothProvider btProvider;

    public ViewModel() {
        init(DebugLogFragment.newInstance(this), ScanFragment.newInstance(this), ConfigurationFragment.newInstance(this));
    }

    public ViewModel(DebugView debugView, ScanView scanView, ConfigurationView configurationView) {
        init(debugView, scanView, configurationView);
    }

    public void setBluetoothProvider(BluetoothProvider btProvider) {
        this.btProvider = btProvider;
        scanView.setBluetoothEnabled();
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
        logMessage("Reloading configuration for device.");
        btProvider.readDeviceSettings(device, this::onDeviceConnected);
    }

    public void updateConfiguration(ConnectedDevice device) {
        logMessage("Updating settings for device.");
        btProvider.updateDevice(device, this::onDeviceConnected);
    }

    public void disconnect(ConnectedDevice device) {
        logMessage("Disconnecting...");
        if (device != null) {
            btProvider.disconnect(device);
        }

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
        // Hook into the device's update callback to process Bluetooth notifications.
        // For now, take the simple approach and just use this method, since
        // it will cause the Configuration View to refresh all properties.
        if (device.getOnBluetoothPropertyUpdated() != null) {
            device.setOnBluetoothPropertyUpdated(this::onDeviceConnected);
        }

        scanView.setConnectedDevice(device);
        configurationView.setConnectedDevice(device);
    }

    private void onConnectionFailed(BleDevice device) {
        logMessage("Failed to connect to device: " + device.getName());
        scanView.setConnectionFailed();
        configurationView.setDisconnectedState();
    }
}
