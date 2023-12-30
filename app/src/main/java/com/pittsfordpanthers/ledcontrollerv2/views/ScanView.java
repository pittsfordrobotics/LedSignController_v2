package com.pittsfordpanthers.ledcontrollerv2.views;

import androidx.fragment.app.Fragment;

import com.pittsfordpanthers.ledcontrollerv2.bluetooth.BleDevice;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;

public abstract class ScanView extends Fragment {
    public abstract void addDiscoveredDevice(BleDevice device);
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
    public abstract void setConnectionFailed();
    public abstract void setBluetoothEnabled();
}
