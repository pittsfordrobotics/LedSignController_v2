package com.example.bleledcontroller.bluetooth;

import java.util.function.Consumer;

public interface BluetoothProvider {
    void startScan(Consumer<BleDevice> discoveredDeviceCallback);

    void stopScan();

    void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback);

    void disconnect();

    void readDeviceSettings(ConnectedDevice device, Consumer<ConnectedDevice> deviceReadCallback);

    void updateDevice(ConnectedDevice device, Consumer<ConnectedDevice> deviceUpdatedCallback);
}
