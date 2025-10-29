package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
import static android.bluetooth.le.ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;

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
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

public class AndroidBluetoothProvider implements BluetoothProvider {
    private static final int OperationTimeoutMillis = 5000;
    private static final int OperationMaxRetries = 3;
    private Context context;
    private BluetoothAdapter btAdapter;
    private Consumer<String> logger;
    private HashSet<String> discoveredAddresses = new HashSet<>();
    private ScanCallback currentScanCallback;
    private Queue<BleOperation> operationQueue = new LinkedList<>();
    private BleOperation pendingOperation = null;
    private int operationRetryCount = 0;
    private long operationStartTimeMillis = 0;
    private Handler handler = new Handler();

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
            currentScanCallback = null;
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
        if (currentScanCallback != null) {
            btAdapter.getBluetoothLeScanner().stopScan(currentScanCallback);
        }
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
    public void disconnect(ConnectedDevice device) {
        if (!(device instanceof AndroidBleDevice)) {
            logMessage("Incorrect BLE device type was specified.");
            return;
        }
        AndroidBleDevice androidBleDevice = (AndroidBleDevice) device;
        androidBleDevice.disconnect();
    }

    @Override
    public void readDeviceSettings(ConnectedDevice device, Consumer<ConnectedDevice> deviceReadCallback) {
        if (!(device instanceof AndroidBleDevice)) {
            logMessage("Incorrect BLE device type was specified.");
            return;
        }
        AndroidBleDevice androidBleDevice = (AndroidBleDevice) device;
        androidBleDevice.refreshCharacteristics(deviceReadCallback);
    }

    @Override
    public void updateDevice(ConnectedDevice device, Consumer<ConnectedDevice> deviceUpdatedCallback) {
        if (!(device instanceof AndroidBleDevice)) {
            logMessage("Incorrect BLE device type was specified.");
            return;
        }
        AndroidBleDevice androidBleDevice = (AndroidBleDevice) device;
        androidBleDevice.updateCharacteristics(deviceUpdatedCallback);
    }

    //
    // Operation queuing methods - add / complete / doNext.
    // BLE is notorious for dropping concurrent operations, so
    // this queuing mechanism allows us to "stack up" operations.
    //
    public void queueOperation(BleOperation operation) {
        operationQueue.add(operation);
        if (pendingOperation == null) {
            // No operations are yet processing. Kick off the next one in the queue.
            doNextOperation();
        }
    }

    // Complete the pending operation and start the next one.
    private void completeOperation() {
        pendingOperation = null;
        doNextOperation();
    }

    private void doNextOperation() {
        if (pendingOperation != null) {
            // Already working on an operation.
            return;
        }

        if (operationQueue.isEmpty()) {
            return;
        }

        pendingOperation = operationQueue.remove();
        if (pendingOperation instanceof BleNullOperation) {
            // A do-nothing operation just to provide a callback method
            try {
                BleNullOperation op = (BleNullOperation) pendingOperation;
                op.getCallback().run();
            } catch (Exception e) {
                logMessage("Failed to complete callback: " + e.getMessage());
            }
            completeOperation();
            return;
        }

        operationRetryCount = 0;
        operationStartTimeMillis = Calendar.getInstance().getTimeInMillis();
        submitPendingOperation();
    }

    private void submitPendingOperation() {
        if (pendingOperation instanceof BleReadCharacteristicOperation) {
            BleReadCharacteristicOperation op = (BleReadCharacteristicOperation) pendingOperation;
            op.getBluetoothGatt().readCharacteristic(op.getCharacteristic());
            handler.postDelayed(this::operationTimedOut, OperationTimeoutMillis);
            return;
        }

        if (pendingOperation instanceof BleWriteCharacteristicOperation) {
            BleWriteCharacteristicOperation op = (BleWriteCharacteristicOperation) pendingOperation;
            BluetoothGattCharacteristic characteristic = op.getCharacteristic();
            characteristic.setValue(op.getTargetValue());
            op.getBluetoothGatt().writeCharacteristic(characteristic);
            handler.postDelayed(this::operationTimedOut, OperationTimeoutMillis);
            return;
        }

        logMessage("Unknown operation type encountered. Skipping.");
    }

    private void operationTimedOut() {
        if (pendingOperation == null) {
            // The operation already finished.
            return;
        }

        if (pendingOperation instanceof BleNullOperation) {
            logMessage("BleNullOperation timed out. This is not expected!");
            pendingOperation = null;
            return;
        }

        long timeSinceStartMillis = Calendar.getInstance().getTimeInMillis() - operationStartTimeMillis;
        if (timeSinceStartMillis <= OperationTimeoutMillis) {
            // Not enough time elapsed.
            // This callback was likely started by an earlier operation that already completed.
            return;
        }

        operationRetryCount++;
        if (operationRetryCount < OperationMaxRetries) {
            logMessage("Operation timed out. Retrying...");
            String className = "unknown";
            try {
                className = pendingOperation.getClass().getName();
            } catch (Exception e) {
                className = e.getMessage();
            }
            logMessage("Operation class name: " + className);
            String uuid = "unknown";
            try {
                uuid = pendingOperation.getCharacteristic().getUuid().toString();
            } catch (Exception e) {
                uuid = e.getMessage();
            }
            logMessage("Characteristic: " + uuid);
            operationStartTimeMillis = Calendar.getInstance().getTimeInMillis();
            submitPendingOperation();
            return;
        }

        // If we made it this far, we ran out of retries.
        // Assume that we ran out of retries due to service issues, and disconnect.
        // Remember that pendingOperation can be changed underneath us, so grab a copy.
        BleOperation capturedOperation = pendingOperation;
        if (capturedOperation != null) {
            logMessage("Operation exceeded the retry limit. Closing connection.");
            BluetoothGatt gatt = capturedOperation.getBluetoothGatt();
            gatt.disconnect();
        }
    }

    private void logMessage(String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }

    private ScanCallback createScanCallback(Consumer<BleDevice> discoveredDeviceCallback) {
        AndroidBluetoothProvider btProvider = this;

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
                    discoveredDeviceCallback.accept(new AndroidBleDevice(device, btProvider, (String s) -> logMessage(s)));
                }
            }
        };
    }

    private BluetoothGattCallback createGattCallback(AndroidBleDevice device, Consumer<ConnectedDevice> onConnectedCallback, Consumer<BleDevice> onConnectionFailedCallback) {
        return new BluetoothGattCallback() {
            AndroidBleDevice bleDevice = device;

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                logMessage("BLE connect state changed. Status: " + status + ", state: " + newState);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        logMessage("Connected to device - discovering services");
                        // Start discovering services -- this will call back to 'onServicesDiscovered'.
                        bleDevice.setBluetoothGatt(gatt);
                        gatt.discoverServices();
                    } else {
                        logMessage("Unexpected GATT state encountered: " + newState);
                        gatt.disconnect();
                        gatt.close();
                        onConnectionFailedCallback.accept(bleDevice);
                    }
                } else {
                    logMessage("Unexpected GATT status encountered. Status: " + status + ", state: " + newState);
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

                logMessage("Looking for LED service...");
                logMessage("Found " + services.size() + " services:");

                for (BluetoothGattService service : services) {
                    logMessage("Found service: " + service.getUuid());
                    if (targetServices.contains(service.getUuid())) {
                        ledService = service;
                    }
                }

                if (ledService == null) {
                    logMessage("LED service not found!");
                    onConnectionFailedCallback.accept(bleDevice);
                    return;
                }

                logMessage("Found LED service.");
                if (!bleDevice.bindCharacteristics(ledService)) {
                    logMessage("At least one characteristic was not found in the service.");
                    onConnectionFailedCallback.accept(bleDevice);
                    return;
                }

                // Perform the initial read of the characteristics, call the "connected" callback when done.
                bleDevice.refreshCharacteristics(onConnectedCallback);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {

                if (!(pendingOperation instanceof BleReadCharacteristicOperation)) {
                    // Something unexpected happened!
                    logMessage("ERROR: In the 'read' callback, but the pending operation is not a read operation.");
                    completeOperation();
                    return;
                }

                BleReadCharacteristicOperation op = (BleReadCharacteristicOperation) pendingOperation;
                op.getCallback().accept(characteristic);
                completeOperation();
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                completeOperation();
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                // Received a notification that the characteristic value has changed on the remote device.
                bleDevice.processCharacteristicNotification(characteristic);
            }
        };
    }
}
