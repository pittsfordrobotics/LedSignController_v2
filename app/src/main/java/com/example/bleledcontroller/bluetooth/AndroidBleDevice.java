package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;

public class AndroidBleDevice extends BleDevice {
    private BluetoothDevice device;

    AndroidBleDevice(BluetoothDevice device)
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
