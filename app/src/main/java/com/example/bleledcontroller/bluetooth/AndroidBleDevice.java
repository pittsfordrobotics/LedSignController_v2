package com.example.bleledcontroller.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.example.bleledcontroller.signdata.ColorPatternOptionData;
import com.example.bleledcontroller.signdata.DisplayPatternOptionData;
import com.example.bleledcontroller.signdata.PatternData;
import com.example.bleledcontroller.signdata.PatternOptionData;

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
    HashMap<UUID, BleReadCharacteristicOperation> readOperations = new HashMap<>();


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
            bluetoothGatt.disconnect();
        }
    }

    public boolean bindCharacteristics(BluetoothGattService service) {
        brightnessCharacteristic = findCharacteristic(service, BleConstants.BrightnessCharacteristicId, "Brightness");
        speedCharacteristic = findCharacteristic(service, BleConstants.SpeedCharacteristicId, "Speed");
        colorPatternDataCharacteristic = findCharacteristic(service, BleConstants.ColorPatternListCharacteristicId, "Color Pattern List");
        displayPatternDataCharacteristic = findCharacteristic(service, BleConstants.DisplayPatternListCharacteristicId, "Color Pattern List");
        patternDataCharacteristic = findCharacteristic(service, BleConstants.PatternDataCharacteristicId, "Pattern Data");

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
        btProvider.queueOperation(new BleNullOperation(() -> {
            logger.accept("All characteristics refreshed.");
            refreshCompletedCallback.accept(this);
        }));
    }

    private BluetoothGattCharacteristic findCharacteristic(BluetoothGattService service, UUID id, String name) {
        BluetoothGattCharacteristic gattChar = service.getCharacteristic(id);
        if (gattChar == null) {
            logger.accept("Characteristic '" + name + "' not found!");
        }
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
