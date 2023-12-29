package com.example.bleledcontroller.bluetooth;

import com.example.bleledcontroller.signdata.PatternOptionData;

public class MockDevice extends ConnectedDevice {
    MockDevice(String name) {
        super(name);
    }

    public void setPatternOptionData(PatternOptionData data) {
        super.setPatternOptionData(data);
    }
}
