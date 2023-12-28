package com.example.bleledcontroller.bluetooth;

import java.util.function.Consumer;

public class AndroidBluetoothProvider implements BluetoothProvider {
    private Consumer<String> logger;

    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
    }

    @Override
    public void startScan(Consumer<BleDevice> discoveredDeviceCallback) {

    }

    @Override
    public void stopScan() {

    }

    @Override
    public void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void readDeviceSettings(ConnectedDevice device, Consumer<ConnectedDevice> deviceReadCallback) {

    }

    @Override
    public void updateDevice(ConnectedDevice device, Consumer<ConnectedDevice> deviceUpdatedCallback) {

    }

    private void logMessage(String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }
}
