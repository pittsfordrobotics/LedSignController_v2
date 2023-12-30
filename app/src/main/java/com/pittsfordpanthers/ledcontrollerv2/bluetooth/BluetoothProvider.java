package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

import java.util.function.Consumer;

public interface BluetoothProvider {
    void startScan(Consumer<BleDevice> discoveredDeviceCallback);

    void stopScan();

    void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback);

    void disconnect(ConnectedDevice device);

    void readDeviceSettings(ConnectedDevice device, Consumer<ConnectedDevice> deviceReadCallback);

    void updateDevice(ConnectedDevice device, Consumer<ConnectedDevice> deviceUpdatedCallback);
}
