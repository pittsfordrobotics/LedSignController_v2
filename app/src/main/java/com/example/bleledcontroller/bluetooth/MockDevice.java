package com.example.bleledcontroller.bluetooth;

import com.example.bleledcontroller.signdata.PatternOptionData;

public class MockDevice extends ConnectedDevice {
    private String name;
    private PatternOptionData patternOptionData;

    MockDevice(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setPatternOptionData(PatternOptionData data) {
        patternOptionData = data;
    }

    @Override
    public PatternOptionData getPatternOptionData() {
        return patternOptionData;
    }

    @Override
    public Object getCurrentPatternData() {
        return null;
    }

    @Override
    public void setPatternData(Object patternData) {

    }
}
