package com.example.bleledcontroller.bluetooth;

import android.os.Handler;

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
    public ConnectedDevice connectToDevice(BleDevice device) {
        String name = device.getName();
        return new ConnectedDevice() {
            @Override
            public Object getPatternOptionData() {
                return null;
            }

            @Override
            public Object getCurrentPatternData() {
                return null;
            }

            @Override
            public void setPatternData(Object patternData) {

            }

            @Override
            public String getName() {
                return name;
            }
        };
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
