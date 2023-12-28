package com.example.bleledcontroller.bluetooth;

import com.example.bleledcontroller.signdata.PatternData;
import com.example.bleledcontroller.signdata.PatternOptionData;

public abstract class ConnectedDevice extends BleDevice {
    protected PatternOptionData patternOptionData;
    private byte brightness;
    private byte speed;
    private PatternData patternData;

    protected ConnectedDevice(String name) {
        super(name);
    }

    // Gets the ColorPattern and DisplayPattern information so we can
    // initialize the "configure" pane in the UI.
    public PatternOptionData getPatternOptionData() { return patternOptionData; }

    // Gets the current color/display pattern data that the device is using.
    public PatternData getCurrentPatternData() { return patternData; }

    // Sets the color/display pattern data for the device.
    public void setPatternData(PatternData patternData) { this.patternData = patternData; }

    public byte getBrightness() { return brightness; }

    public void setBrightness(byte brightness) { this.brightness = brightness; }

    public byte getSpeed() { return speed; }

    public void setSpeed(byte speed) { this.speed = speed; }
}
