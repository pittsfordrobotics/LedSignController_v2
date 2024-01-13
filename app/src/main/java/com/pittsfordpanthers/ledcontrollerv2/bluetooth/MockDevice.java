package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternOptionData;

public class MockDevice extends ConnectedDevice {
    MockDevice(String name) {
        super(name);
    }

    public void setPatternOptionData(PatternOptionData data) {
        super.setPatternOptionData(data);
    }
}
