package com.example.bleledcontroller.bluetooth;

import com.example.bleledcontroller.signdata.PatternData;
import com.example.bleledcontroller.signdata.PatternOptionData;

public abstract class ConnectedDevice extends BleDevice {
    // Dummy placeholder methods

    // Gets the ColorPattern and DisplayPattern information so we can
    // initialize the "configure" pane in the UI.
    public abstract PatternOptionData getPatternOptionData();

    // Gets the current color/display pattern data that the device is using.
    public abstract PatternData getCurrentPatternData();

    // Sets the color/display pattern data for the device.
    public abstract void setPatternData(PatternData patternData);

    public abstract byte getBrightness();
    public abstract void setBrightness(byte brightness);

    public abstract byte getSpeed();
    public abstract void setSpeed(byte speed);
}
