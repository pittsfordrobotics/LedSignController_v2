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
        init(DebugLogFragment.newInstance(this), ScanFragment.newInstance(this), ConfigurationFragment.newInstance());
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
        logMessage("Pretending to connect to device: " + device.getName());
        ConnectedDevice connectedDevice = btProvider.connectToDevice(device);
        scanView.setConnectedDevice(connectedDevice);
    }

    public void disconnect() {
        btProvider.disconnect();
        logMessage("Disconnected.");
        scanView.setDisconnectedState();
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
}
