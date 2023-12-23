package com.example.bleledcontroller.views;

import androidx.fragment.app.Fragment;

import com.example.bleledcontroller.bluetooth.DiscoveredDevice;

import java.util.function.Consumer;

public abstract class ScanView extends Fragment {
    public abstract void resetToInitialState();
    public abstract void addDiscoveredDevice(DiscoveredDevice device);
    public abstract void setConnectedState(DiscoveredDevice device);
    public abstract void setDisconnectedState();
}
