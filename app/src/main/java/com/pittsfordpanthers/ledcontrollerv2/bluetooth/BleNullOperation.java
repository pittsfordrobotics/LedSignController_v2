package com.pittsfordpanthers.ledcontrollerv2.bluetooth;

public class BleNullOperation extends BleOperation {
    private Runnable callback;

    public BleNullOperation(Runnable callback) {
        super(null, null);
        this.callback = callback;
    }

    public Runnable getCallback() {
        return callback;
    }
}
