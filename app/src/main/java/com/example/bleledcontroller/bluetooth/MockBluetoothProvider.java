package com.example.bleledcontroller.bluetooth;

import android.os.Handler;

import com.example.bleledcontroller.signdata.ColorPatternOptionData;
import com.example.bleledcontroller.signdata.DisplayPatternOptionData;
import com.example.bleledcontroller.signdata.PatternOptionData;

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

        // configure the set of default pattern options
        ColorPatternOptionData colorData1 = new ColorPatternOptionData("Single color", 1, 1);
        ColorPatternOptionData colorData2 = new ColorPatternOptionData("Two color", 2, 2);
        colorData2.addParameterName("Color 1 duration");
        colorData2.addParameterName("Color 2 duration");
        ColorPatternOptionData colorData3 = new ColorPatternOptionData("Rainbow", 3, 0);
        colorData3.addParameterName("Hue increment");
        DisplayPatternOptionData displayData1 = new DisplayPatternOptionData("Solid", 0);
        DisplayPatternOptionData displayData2 = new DisplayPatternOptionData("Right", 1);
        DisplayPatternOptionData displayData3 = new DisplayPatternOptionData("Down", 4);
        DisplayPatternOptionData displayData4 = new DisplayPatternOptionData("Random", 6);
        displayData4.addParameterName("Update amount");
        PatternOptionData data = new PatternOptionData();
        data.addColorPatternOption(colorData1);
        data.addColorPatternOption(colorData2);
        data.addColorPatternOption(colorData3);
        data.addDisplayPatternOption(displayData1);
        data.addDisplayPatternOption(displayData2);
        data.addDisplayPatternOption(displayData3);
        data.addDisplayPatternOption(displayData4);

        d.setPatternOptionData(data);

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
