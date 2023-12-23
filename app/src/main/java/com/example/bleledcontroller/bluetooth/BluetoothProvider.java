package com.example.bleledcontroller.bluetooth;

import java.util.function.Consumer;

public interface BluetoothProvider {
    void startScan(Consumer<BleDevice> discoveredDeviceCallback);

    void stopScan();

    ConnectedDevice connectToDevice(BleDevice device);

    void disconnect();
}
