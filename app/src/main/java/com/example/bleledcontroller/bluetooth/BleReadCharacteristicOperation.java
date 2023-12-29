package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.function.Consumer;

public class BleReadCharacteristicOperation extends BleOperation {
    private Consumer<BluetoothGattCharacteristic> callback;

    public BleReadCharacteristicOperation(
            BluetoothGatt bluetoothGatt,
            BluetoothGattCharacteristic characteristic,
            Consumer<BluetoothGattCharacteristic> callback) {
        super(bluetoothGatt, characteristic);
        this.callback = callback;
    }

    public Consumer<BluetoothGattCharacteristic> getCallback() {
        return callback;
    }
}
