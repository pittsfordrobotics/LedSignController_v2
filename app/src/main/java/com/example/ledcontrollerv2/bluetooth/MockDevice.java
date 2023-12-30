package com.example.ledcontrollerv2.bluetooth;

import com.example.ledcontrollerv2.signdata.PatternOptionData;

public class MockDevice extends ConnectedDevice {
    MockDevice(String name) {
        super(name);
    }

    public void setPatternOptionData(PatternOptionData data) {
        super.setPatternOptionData(data);
    }
}
