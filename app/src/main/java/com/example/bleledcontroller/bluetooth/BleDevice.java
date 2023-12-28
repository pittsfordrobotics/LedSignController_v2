package com.example.bleledcontroller.bluetooth;

import androidx.annotation.NonNull;

public abstract class BleDevice {
    private String name;

    protected BleDevice(String name) {
        this.name = name;
    }

    public String getName() { return name; };

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
