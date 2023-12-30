package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

import android.graphics.Color;
import android.os.Handler;

import com.pittsfordpanthers.ledcontrollerv2.signdata.ColorPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.DisplayPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternOptionData;

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
        PatternOptionData optionData = new PatternOptionData();
        optionData.addColorPatternOption(colorData1);
        optionData.addColorPatternOption(colorData2);
        optionData.addColorPatternOption(colorData3);
        optionData.addDisplayPatternOption(displayData1);
        optionData.addDisplayPatternOption(displayData2);
        optionData.addDisplayPatternOption(displayData3);
        optionData.addDisplayPatternOption(displayData4);
        d.setPatternOptionData(optionData);

        d.setBrightness((byte)123);
        d.setSpeed((byte)55);

        PatternData data = new PatternData();
        data.setColorValue(0, Color.RED);
        data.setColorValue(1, Color.GREEN);
        data.setColorPatternId((byte)2);
        data.setDisplayPatternId((byte)4);
        data.setParameterValue(0, (byte)34);
        data.setParameterValue(1, (byte)45);
        d.setPatternData(data);

        handler.postDelayed(() -> onConnectedCallback.accept(d), 1000);
    }

    @Override
    public void disconnect(ConnectedDevice device) {
    }

    @Override
    public void readDeviceSettings(ConnectedDevice device, Consumer<ConnectedDevice> deviceReadCallback) {
        handler.postDelayed(() -> deviceReadCallback.accept(device), 1000);
    }

    @Override
    public void updateDevice(ConnectedDevice device, Consumer<ConnectedDevice> deviceUpdatedCallback) {
        handler.postDelayed(() -> deviceUpdatedCallback.accept(device), 1000);
    }

    private void doFakeDeviceDiscovery(Consumer<BleDevice> discoveredDeviceCallback) {
        int deviceNumber = fakeDevicesDiscovered++;

        if (deviceNumber > maxDevicesToDiscover || !inScan) {
            return;
        }

        String name = "Dummy LED sign " + deviceNumber;
        discoveredDeviceCallback.accept(new BleDevice(name) {});

        handler.postDelayed(() -> doFakeDeviceDiscovery(discoveredDeviceCallback), 1500);
    }
}
