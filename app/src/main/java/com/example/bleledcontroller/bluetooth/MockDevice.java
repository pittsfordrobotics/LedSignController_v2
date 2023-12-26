package com.example.bleledcontroller.bluetooth;

public class MockDevice extends ConnectedDevice {
    private String name;
    private Object patternOptionData;

    MockDevice(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setPatternOptionData(Object data) {
        patternOptionData = data;
    }

    @Override
    public Object getPatternOptionData() {
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
