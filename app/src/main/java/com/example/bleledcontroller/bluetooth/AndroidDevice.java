package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;

public class AndroidDevice extends ConnectedDevice {
    private BluetoothDevice device;

    AndroidDevice(BluetoothDevice device)
    {
        super(device.getName());
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
