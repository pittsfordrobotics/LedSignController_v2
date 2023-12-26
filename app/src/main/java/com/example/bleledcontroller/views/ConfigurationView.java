package com.example.bleledcontroller.views;

import androidx.fragment.app.Fragment;

import com.example.bleledcontroller.bluetooth.ConnectedDevice;

public abstract class ConfigurationView extends Fragment {
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
}
