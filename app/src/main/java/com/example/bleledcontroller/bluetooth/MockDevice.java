package com.example.bleledcontroller.bluetooth;

import com.example.bleledcontroller.signdata.PatternData;
import com.example.bleledcontroller.signdata.PatternOptionData;

public class MockDevice extends ConnectedDevice {
    private String name;
    private PatternOptionData patternOptionData;
    private byte brightness;
    private byte speed;
    private PatternData patternData;

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
    public PatternData getCurrentPatternData() {
        return patternData;
    }

    @Override
    public void setPatternData(PatternData patternData) { this.patternData = patternData; }

    @Override
    public byte getBrightness() { return brightness; }

    @Override
    public void setBrightness(byte brightness) { this.brightness = brightness; }

    @Override
    public byte getSpeed() { return speed; }

    @Override
    public void setSpeed(byte speed) { this.speed = speed; }
}
