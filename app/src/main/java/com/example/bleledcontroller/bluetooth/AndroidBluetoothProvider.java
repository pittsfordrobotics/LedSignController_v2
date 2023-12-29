package com.example.bleledcontroller.bluetooth;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static android.bluetooth.le.ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;

import static com.example.bleledcontroller.bluetooth.BleConstants.PrimaryLedServiceUuid;
import static com.example.bleledcontroller.bluetooth.BleConstants.SecondaryLedServiceUuid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AndroidBluetoothProvider implements BluetoothProvider {
    private Context context;
    private BluetoothAdapter btAdapter;
    private Consumer<String> logger;

    public AndroidBluetoothProvider(Context context) {
        this.context = context;
        BluetoothManager mgr = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = mgr.getAdapter();
    }

    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
    }

    @Override
    public void startScan(Consumer<BleDevice> discoveredDeviceCallback) {
        if (!btAdapter.isEnabled()) {
            logMessage("Bluetooth adapter disabled!");
            return;
        }

        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(PrimaryLedServiceUuid))
                .build());

        filters.add(new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SecondaryLedServiceUuid))
                .build());

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(SCAN_MODE_BALANCED)
                .setNumOfMatches(MATCH_NUM_MAX_ADVERTISEMENT)
                .setCallbackType(CALLBACK_TYPE_ALL_MATCHES)
                .build();

        btAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, createScanCallback(discoveredDeviceCallback));
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

    private ScanCallback createScanCallback(Consumer<BleDevice> discoveredDeviceCallback) {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                logMessage("Discovered device: " + device.getName());
                // Adding a small sleep.
                // https://medium.com/android-news/lessons-for-first-time-android-bluetooth-le-developers-i-learned-the-hard-way-fee07646624
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
                discoveredDeviceCallback.accept(new AndroidDevice(device));
            }
        };
    }
}
