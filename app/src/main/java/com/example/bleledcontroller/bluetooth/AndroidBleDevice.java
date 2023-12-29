package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;

public class AndroidBleDevice extends ConnectedDevice {
    private BluetoothDevice device;

    AndroidBleDevice(BluetoothDevice device)
    {
        super(device.getName());
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
