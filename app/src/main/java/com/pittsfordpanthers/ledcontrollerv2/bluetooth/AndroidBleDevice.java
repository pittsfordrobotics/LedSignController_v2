package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.pittsfordpanthers.ledcontrollerv2.signdata.ColorPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.DisplayPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternOptionData;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class AndroidBleDevice extends ConnectedDevice {
    private BluetoothDevice device;
    private Consumer<String> logger;
    private BluetoothGatt bluetoothGatt;
    private AndroidBluetoothProvider btProvider;
    private BluetoothGattCharacteristic brightnessCharacteristic;
    private BluetoothGattCharacteristic speedCharacteristic;
    private BluetoothGattCharacteristic patternDataCharacteristic;
    private BluetoothGattCharacteristic colorPatternDataCharacteristic;
    private BluetoothGattCharacteristic displayPatternDataCharacteristic;
    private BluetoothGattCharacteristic syncCharacteristic;
    HashMap<UUID, BleReadCharacteristicOperation> readOperations = new HashMap<>();
    HashMap<UUID, BleWriteCharacteristicOperation> writeOperations = new HashMap<>();
    private int syncData;

    AndroidBleDevice(BluetoothDevice device, AndroidBluetoothProvider btProvider, Consumer<String> logger)
    {
        super(device.getName());
        this.logger = logger;
        this.device = device;
        this.btProvider = btProvider;
        setPatternOptionData(new PatternOptionData());
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setBluetoothGatt(BluetoothGatt gatt) {
        bluetoothGatt = gatt;
    }

    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
    }

    public boolean bindCharacteristics(BluetoothGattService service) {
        brightnessCharacteristic = findCharacteristic(service, BleConstants.BrightnessCharacteristicId, "Brightness");
        speedCharacteristic = findCharacteristic(service, BleConstants.SpeedCharacteristicId, "Speed");
        colorPatternDataCharacteristic = findCharacteristic(service, BleConstants.ColorPatternListCharacteristicId, "Color Pattern List");
        displayPatternDataCharacteristic = findCharacteristic(service, BleConstants.DisplayPatternListCharacteristicId, "Color Pattern List");
        patternDataCharacteristic = findCharacteristic(service, BleConstants.PatternDataCharacteristicId, "Pattern Data");
        syncCharacteristic = findCharacteristic(service, BleConstants.SyncDataCharacteristidId, "Sync Data");

        // Note that the sync characteristic is optional.
        boolean areAllCharacteristicsDefined = brightnessCharacteristic != null
                && speedCharacteristic != null
                && patternDataCharacteristic != null
                && colorPatternDataCharacteristic != null
                && displayPatternDataCharacteristic != null;

        if (areAllCharacteristicsDefined) {
            initializeCharacteristicOperations();
        }

        return areAllCharacteristicsDefined;
    }

    public void refreshCharacteristics(Consumer<ConnectedDevice> refreshCompletedCallback) {
        // Enqueue all the "read" operations,
        // ending with a NullOperation to invoke the final callback.
        logger.accept("Refreshing all characteristics.");
        btProvider.queueOperation(readOperations.get(BleConstants.BrightnessCharacteristicId));
        btProvider.queueOperation(readOperations.get(BleConstants.SpeedCharacteristicId));
        btProvider.queueOperation(readOperations.get(BleConstants.ColorPatternListCharacteristicId));
        btProvider.queueOperation(readOperations.get(BleConstants.DisplayPatternListCharacteristicId));
        btProvider.queueOperation(readOperations.get(BleConstants.PatternDataCharacteristicId));

        if (syncCharacteristic != null) {
            btProvider.queueOperation(readOperations.get(BleConstants.SyncDataCharacteristidId));
        }

        btProvider.queueOperation(new BleNullOperation(() -> {
            logger.accept("All characteristics refreshed.");
            refreshCompletedCallback.accept(this);
        }));
    }

    public void updateCharacteristics(Consumer<ConnectedDevice> updateCompletedCallback) {
        logger.accept("Updating characteristics.");
        btProvider.queueOperation(writeOperations.get(BleConstants.BrightnessCharacteristicId).withValue(new byte[] { getBrightness() }));
        logger.accept("Brightness: " + String.valueOf(Byte.toUnsignedInt(getBrightness())));
        btProvider.queueOperation(writeOperations.get(BleConstants.SpeedCharacteristicId).withValue(new byte[] { getSpeed() }));
        logger.accept("Speed: " + String.valueOf(Byte.toUnsignedInt(getSpeed())));
        btProvider.queueOperation(writeOperations.get(BleConstants.PatternDataCharacteristicId).withValue(getCurrentPatternData().toBinaryData()));
        logger.accept("PatternData: " + getCurrentPatternData().toBinaryDataAsString());

        if (syncCharacteristic != null) {
            syncData++;
            byte[] syncBytes = ByteBuffer.allocate(4).putInt(syncData).array();
            btProvider.queueOperation(writeOperations.get(BleConstants.SyncDataCharacteristidId).withValue(syncBytes));
        }

        btProvider.queueOperation(new BleNullOperation(() -> {
            logger.accept("Characteristics updated.");
            updateCompletedCallback.accept(this);
        }));
    }

    public void processCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        logger.accept("Received a notification callback for characteristic " + characteristic.getUuid());
        // See if we can find a read operation for the characteristic.
        // If found, execute the operation's callback.
        BleReadCharacteristicOperation op = readOperations.get(characteristic.getUuid());
        if (op != null) {
            op.getCallback().accept(characteristic);
        }

        // Let any listener know that something's been updated.
        if (onBluetoothPropertyUpdated != null) {
            onBluetoothPropertyUpdated.accept(this);
        }
    }

    private BluetoothGattCharacteristic findCharacteristic(BluetoothGattService service, UUID id, String name) {
        BluetoothGattCharacteristic gattChar = service.getCharacteristic(id);
        if (gattChar == null) {
            logger.accept("Characteristic '" + name + "' not found!");
        }

        // Note for the future:
        // To enable notifications to be processed, we need to explicitly
        // register for the notifications for each characteristic.
        // Not all characteristics need to be monitored....
        // Probably add a 'enableNotifications' parameter to this method?
        //
        // Ex:
        // bluetoothGatt.setCharacteristicNotification(gattChar, true);
        return gattChar;
    }

    private void initializeCharacteristicOperations() {
        readOperations.clear();
        readOperations.put(BleConstants.BrightnessCharacteristicId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        brightnessCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            setBrightness(c.getValue()[0]);
                        }
                ));
        readOperations.put(BleConstants.SpeedCharacteristicId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        speedCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            setSpeed(c.getValue()[0]);
                        }
                ));
        readOperations.put(BleConstants.ColorPatternListCharacteristicId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        colorPatternDataCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            setColorPatternDataFromString(new String(c.getValue()));
                        }
                ));
        readOperations.put(BleConstants.DisplayPatternListCharacteristicId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        displayPatternDataCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            setDisplayPatternDataFromString(new String(c.getValue()));
                        }
                ));
        readOperations.put(BleConstants.PatternDataCharacteristicId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        patternDataCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            setPatternData(PatternData.fromBinaryData(c.getValue()));
                        }
                ));
        readOperations.put(BleConstants.SyncDataCharacteristidId,
                new BleReadCharacteristicOperation(
                        bluetoothGatt,
                        syncCharacteristic,
                        (BluetoothGattCharacteristic c) -> {
                            syncData = c.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                        }
                ));

        writeOperations.clear();
        writeOperations.put(BleConstants.BrightnessCharacteristicId,
                new BleWriteCharacteristicOperation(bluetoothGatt, brightnessCharacteristic));

        writeOperations.put(BleConstants.SpeedCharacteristicId,
                new BleWriteCharacteristicOperation(bluetoothGatt, speedCharacteristic));

        writeOperations.put(BleConstants.PatternDataCharacteristicId,
                new BleWriteCharacteristicOperation(bluetoothGatt, patternDataCharacteristic));

        writeOperations.put(BleConstants.SyncDataCharacteristidId,
                new BleWriteCharacteristicOperation(bluetoothGatt, syncCharacteristic));
    }

    private void setColorPatternDataFromString(String patternString) {
        String[] patterns = patternString.split(";");
        for (String pattern:patterns) {
            ColorPatternOptionData optionData = ColorPatternOptionData.fromString(pattern);
            if (optionData != null) {
                getPatternOptionData().addColorPatternOption(optionData);
            }
        }
    }

    private void setDisplayPatternDataFromString(String patternString) {
        String[] patterns = patternString.split(";");
        for (String pattern:patterns) {
            DisplayPatternOptionData optionData = DisplayPatternOptionData.fromString(pattern);
            if (optionData != null) {
                getPatternOptionData().addDisplayPatternOption(optionData);
            }
        }
    }
}
