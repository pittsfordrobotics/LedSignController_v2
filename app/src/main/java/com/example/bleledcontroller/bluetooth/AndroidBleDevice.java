package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

public class AndroidBleDevice extends ConnectedDevice {
    private BluetoothDevice device;
    private BluetoothGattCharacteristic brightnessCharacteristic;
    private BluetoothGattCharacteristic speedCharacteristic;
    private BluetoothGattCharacteristic patternDataCharacteristic;
    private BluetoothGattCharacteristic colorPatternDataCharacteristic;
    private BluetoothGattCharacteristic displayPatternDataCharacteristic;

    AndroidBleDevice(BluetoothDevice device)
    {
        super(device.getName());
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setBrightnessCharacteristic(BluetoothGattCharacteristic characteristic) {
        brightnessCharacteristic = characteristic;
    }

    public void setSpeedCharacteristic(BluetoothGattCharacteristic characteristic) {
        speedCharacteristic = characteristic;
    }

    public void setPatternDataCharacteristic(BluetoothGattCharacteristic patternDataCharacteristic) {
        this.patternDataCharacteristic = patternDataCharacteristic;
    }

    public void setColorPatternDataCharacteristic(BluetoothGattCharacteristic colorPatternDataCharacteristic) {
        this.colorPatternDataCharacteristic = colorPatternDataCharacteristic;
    }

    public void setDisplayPatternDataCharacteristic(BluetoothGattCharacteristic displayPatternDataCharacteristic) {
        this.displayPatternDataCharacteristic = displayPatternDataCharacteristic;
    }

    public boolean areAllCharacteristicsValid() {
        return brightnessCharacteristic != null
                && speedCharacteristic != null
                && patternDataCharacteristic != null
                && colorPatternDataCharacteristic != null
                && displayPatternDataCharacteristic != null;
    }
}
