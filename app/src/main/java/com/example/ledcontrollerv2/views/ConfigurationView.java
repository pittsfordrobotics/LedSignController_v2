package com.example.ledcontrollerv2.views;

import androidx.fragment.app.Fragment;

import com.example.ledcontrollerv2.bluetooth.ConnectedDevice;

public abstract class ConfigurationView extends Fragment {
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
}
