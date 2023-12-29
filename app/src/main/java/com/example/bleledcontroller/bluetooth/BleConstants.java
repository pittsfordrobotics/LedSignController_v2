package com.example.bleledcontroller.bluetooth;

import java.util.UUID;

public class BleConstants {
    public static final UUID PrimaryLedServiceUuid = UUID.fromString("99be4fac-c708-41e5-a149-74047f554cc1");
    public static final UUID SecondaryLedServiceUuid = UUID.fromString("1221ca8d-4172-4946-bcd1-f9e4b40ba6b0");
    public static final UUID BrightnessCharacteristicId = UUID.fromString("5eccb54e-465f-47f4-ac50-6735bfc0e730");
    public static final UUID SpeedCharacteristicId = UUID.fromString("b975e425-62e4-4b08-a652-d64ad5097815");
    public static final UUID BatteryVoltageCharacteristicId = UUID.fromString("ea0a95bc-7561-4b1e-8925-7973b3ad7b9a");
    public static final UUID PatternDataCharacteristicId = UUID.fromString("20450d4f-d882-4fee-8be0-bbf82707dc79");
    public static final UUID ColorPatternListCharacteristicId = UUID.fromString("504850ed-f4e7-497a-a1e3-cc70afa56901");
    public static final UUID DisplayPatternListCharacteristicId = UUID.fromString("c999ee9b-294e-4889-8cba-1eb2f515b054");
    public static final UUID SyncDataCharacteristidId = UUID.fromString("2541ad55-ea7e-4afd-9810-06731a76d8dc");
}
