package com.example.bleledcontroller.bluetooth;

import android.os.Handler;

import java.util.Random;
import java.util.function.Consumer;

public class MockBluetoothProvider implements BluetoothProvider {
    private final int maxDevicesToDiscover = 5;
    private int fakeDevicesDiscovered = 0;
    private boolean inScan = false;
    private Handler handler = new Handler();

    @Override
    public void startScan(Consumer<BleDevice> discoveredDeviceCallback) {
        inScan = true;
        fakeDevicesDiscovered = 0;
        handler.postDelayed(() -> doFakeDeviceDiscovery(discoveredDeviceCallback), 2000);
    }

    @Override
    public void stopScan() {
        inScan = false;
    }

    @Override
    public void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback) {
        // Pretend to fail to connect to sign 2.
        boolean shouldFail = device.getName().contains("sign 2");

        if (shouldFail) {
            handler.postDelayed(() -> onConnectionFailedCallback.accept(device), 1000);
            return;
        }

        MockDevice d = new MockDevice(device.getName());
        handler.postDelayed(() -> onConnectedCallback.accept(d), 1000);
    }

    @Override
    public void disconnect() {

    }

    private void doFakeDeviceDiscovery(Consumer<BleDevice> discoveredDeviceCallback) {
        int deviceNumber = fakeDevicesDiscovered++;

        if (deviceNumber > maxDevicesToDiscover || !inScan) {
            return;
        }

        String name = "Dummy LED sign " + deviceNumber;

        discoveredDeviceCallback.accept(new BleDevice() {
            @Override
            public String getName() {
                return name;
            }
        });

        handler.postDelayed(() -> doFakeDeviceDiscovery(discoveredDeviceCallback), 2000);
    }
}
