package com.example.bleledcontroller.bluetooth;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static android.bluetooth.le.ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;

import com.example.bleledcontroller.bluetooth.BleConstants;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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
                .setServiceUuid(new ParcelUuid(BleConstants.PrimaryLedServiceUuid))
                .build());

        filters.add(new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BleConstants.SecondaryLedServiceUuid))
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
        BluetoothGattCallback gattCallback = createGattCallback(androidBleDevice, onConnectedCallback, onConnectionFailedCallback);
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

    private BluetoothGattCallback createGattCallback(AndroidBleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback) {
        return new BluetoothGattCallback() {
            AndroidBleDevice bleDevice = device;

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                logger.accept("BLE connect state changed. Status: " + status + ", state: " + newState);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        logger.accept("Connected to device - discovering services");
                        // Start discovering services -- this will call back to 'onServicesDiscovered'.
                        gatt.discoverServices();
                    } else {
                        logger.accept("Unexpected GATT state encountered: " + newState);
                        gatt.disconnect();
                        gatt.close();
                        onConnectionFailedCallback.accept(bleDevice);
                    }
                } else {
                    logger.accept("Unexpected GATT status encountered. Status: " + status + ", state: " + newState);
                    gatt.disconnect();
                    gatt.close();
                    onConnectionFailedCallback.accept(bleDevice);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattService ledService = null;
                ArrayList<UUID> targetServices = new ArrayList<>();
                targetServices.add(BleConstants.PrimaryLedServiceUuid);
                targetServices.add(BleConstants.SecondaryLedServiceUuid);

                logger.accept("Looking for LED service...");
                logger.accept("Found " + services.size() + " services:");

                for (BluetoothGattService service : services) {
                    logger.accept("Found service: " + service.getUuid());
                    if (targetServices.contains(service.getUuid())) {
                        ledService = service;
                    }
                }

                if (ledService == null) {
                    logger.accept("LED service not found!");
                    onConnectionFailedCallback.accept(bleDevice);
                    return;
                }

                logger.accept("Found LED service.");
                bindCharacteristics(ledService);
                if (!bleDevice.areAllCharacteristicsValid()) {
                    logger.accept("At least one characteristic was not found in the service.");
                    onConnectionFailedCallback.accept(bleDevice);
                    return;
                }

                logger.accept("Services bound successfully.");

//                InitializeCharacteristicOperations();
//
//                // We can only read one characteristic at a time, so add all the initial
//                // characteristic read operations to the queue.
//                addOperation(readOperations.get(BrightnessCharacteristicId));
//                addOperation(readOperations.get(StyleCharacteristicId));
//                addOperation(readOperations.get(NamesCharacteristicId));
//                addOperation(readOperations.get(SpeedCharacteristicId));
//                addOperation(readOperations.get(StepCharacteristicId));
//                addOperation(readOperations.get(PatternCharacteristicId));
//                addOperation(readOperations.get(PatternNamesCharacteristicId));
//                addOperation(readOperations.get(BatteryVoltageCharacteristicId));
                onConnectedCallback.accept(bleDevice);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
//
//                if (!(pendingOperation instanceof BleReadCharacteristicOperation)) {
//                    // Something unexpected happened!
//                    callback.acceptStatus("ERROR: In the 'read' callback, but the pending operation is not a read operation.");
//                    completeOperation();
//                    return;
//                }
//
//                BleReadCharacteristicOperation op = (BleReadCharacteristicOperation) pendingOperation;
//                op.getCallback().ProcessCharacteristic(characteristic);
//                completeOperation();
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                completeOperation();
            }

            private void bindCharacteristics(BluetoothGattService service) {
                bleDevice.setBrightnessCharacteristic(findCharacteristic(service, BleConstants.BrightnessCharacteristicId, "Brightness"));
                bleDevice.setSpeedCharacteristic(findCharacteristic(service, BleConstants.SpeedCharacteristicId, "Speed"));
                bleDevice.setColorPatternDataCharacteristic(findCharacteristic(service, BleConstants.ColorPatternListCharacteristicId, "Color Pattern List"));
                bleDevice.setDisplayPatternDataCharacteristic(findCharacteristic(service, BleConstants.DisplayPatternListCharacteristicId, "Color Pattern List"));
                bleDevice.setPatternDataCharacteristic(findCharacteristic(service, BleConstants.PatternDataCharacteristicId, "Pattern Data"));
            }

            private BluetoothGattCharacteristic findCharacteristic(BluetoothGattService service, UUID id, String name) {
                BluetoothGattCharacteristic gattChar = service.getCharacteristic(id);
                if (gattChar == null) {
                    logger.accept("Characteristic '" + name + "' not found!");
                }
                return gattChar;
            }
        };
    }
}
