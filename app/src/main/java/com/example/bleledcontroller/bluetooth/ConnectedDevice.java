package com.example.bleledcontroller.bluetooth;

public abstract class ConnectedDevice extends BleDevice {
    // Dummy placeholder methods

    // Gets the ColorPattern and DisplayPattern information so we can
    // initialize the "configure" pane in the UI.
    public abstract Object getPatternOptionData();

    // Gets the current color/display pattern data that the device is using.
    public abstract Object getCurrentPatternData();

    // Sets the color/display pattern data for the device.
    public abstract void setPatternData(Object patternData);
}
