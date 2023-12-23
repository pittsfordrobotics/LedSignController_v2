package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;

public class BleDiscoveredDevice extends DiscoveredDevice {
    private BluetoothDevice device;

    BleDiscoveredDevice(BluetoothDevice device)
    {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public String getName() {
        return device.getName();
    }
}
