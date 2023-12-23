package com.example.bleledcontroller.bluetooth;

import androidx.annotation.NonNull;

public abstract class BleDevice {
    public abstract String getName();

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
