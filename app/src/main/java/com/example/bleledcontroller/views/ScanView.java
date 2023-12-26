package com.example.bleledcontroller.views;

import androidx.fragment.app.Fragment;

import com.example.bleledcontroller.bluetooth.BleDevice;
import com.example.bleledcontroller.bluetooth.ConnectedDevice;

public abstract class ScanView extends Fragment {
    public abstract void resetToInitialState();
    public abstract void addDiscoveredDevice(BleDevice device);
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
    public abstract void setConnectionFailed();
}
