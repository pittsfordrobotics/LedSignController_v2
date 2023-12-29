package com.example.bleledcontroller.bluetooth;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static android.bluetooth.le.ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;

import static com.example.bleledcontroller.bluetooth.BleConstants.PrimaryLedServiceUuid;
import static com.example.bleledcontroller.bluetooth.BleConstants.SecondaryLedServiceUuid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class AndroidBluetoothProvider implements BluetoothProvider {
    private Context context;
    private BluetoothAdapter btAdapter;
    private Consumer<String> logger;
    private HashSet<String> discoveredAddresses = new HashSet<>();
    private ScanCallback currentScanCallback;

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

        discoveredAddresses.clear();
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

        // Create the callback and retain a reference to it so we can use it to
        // stop the scanning later.
        currentScanCallback = createScanCallback(discoveredDeviceCallback);
        btAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, currentScanCallback);
    }

    @Override
    public void stopScan() {
        btAdapter.getBluetoothLeScanner().stopScan(currentScanCallback);
    }

    @Override
    public void connectToDevice(BleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback) {
        if (!(device instanceof AndroidBleDevice)) {
            logMessage("Incorrect BLE device type was specified.");
            onConnectionFailedCallback.accept(device);
            return;
        }

        AndroidBleDevice androidBleDevice = (AndroidBleDevice) device;
        BluetoothGattCallback gattCallback = null;
        androidBleDevice.getDevice().connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
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
                if (discoveredAddresses.contains(device.getAddress())) {
                    // Already reported this device.
                    return;
                }
                logMessage("Discovered device '" + device.getName() + "' with address " + device.getAddress());
                discoveredAddresses.add(device.getAddress());

                if (discoveredDeviceCallback != null) {
                    discoveredDeviceCallback.accept(new AndroidBleDevice(device));
                }
            }
        };
    }


}
