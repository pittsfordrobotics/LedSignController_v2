package com.example.bleledcontroller.bluetooth;

import java.util.function.Consumer;

public interface BluetoothProvider {
    void startScan(Consumer<BleDevice> discoveredDeviceCallback);

    void stopScan();

    void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback);

    void disconnect();
}
