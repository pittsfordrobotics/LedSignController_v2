package com.pittsfordpanthers.ledcontrollerv2.views;

import androidx.fragment.app.Fragment;

import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;

public abstract class AdvancedView extends Fragment {
    public abstract void setConnectedDevice(ConnectedDevice device);
    public abstract void setDisconnectedState();
}
