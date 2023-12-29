package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.function.Consumer;

public class BleNullOperation extends BleOperation {
    private Runnable callback;

    public BleNullOperation(Runnable callback) {
        super(null, null);
        this.callback = callback;
    }

    public Runnable getCallback() {
        return callback;
    }
}
