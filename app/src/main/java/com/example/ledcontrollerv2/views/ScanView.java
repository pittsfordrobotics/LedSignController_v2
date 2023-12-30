package com.example.ledcontrollerv2.views;

import androidx.fragment.app.Fragment;

import com.example.ledcontrollerv2.bluetooth.BleDevice;
import com.example.ledcontrollerv2.bluetooth.ConnectedDevice;

public abstract class ScanView extends Fragment {
    public abstract void addDiscoveredDevice(BleDevice device);
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
    public abstract void setConnectionFailed();
    public abstract void setBluetoothEnabled();
}
