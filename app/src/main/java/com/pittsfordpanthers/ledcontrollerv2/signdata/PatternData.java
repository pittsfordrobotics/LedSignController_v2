package com.pittsfordpanthers.ledcontrollerv2.signdata;

import android.graphics.Color;

public class PatternData {
    byte colorPatternId;
    byte displayPatternId;
    byte[] parameterValues = new byte[6];
    int[] colorValues = new int[4];

    public static PatternData fromBinaryData(byte[] binaryData) {
        if (binaryData.length != 20) {
            // Invalid data
            return null;
        }

        PatternData data = new PatternData();
        data.setColorPatternId(binaryData[0]);
        data.setDisplayPatternId(binaryData[1]);
        data.setParameterValue(0, binaryData[2]);
        data.setParameterValue(1, binaryData[3]);
        data.setParameterValue(2, binaryData[7]);
        data.setParameterValue(3, binaryData[11]);
        data.setParameterValue(4, binaryData[15]);
        data.setParameterValue(5, binaryData[19]);
        data.setColorValue(0, Color.rgb(Byte.toUnsignedInt(binaryData[6]), Byte.toUnsignedInt(binaryData[5]), Byte.toUnsignedInt(binaryData[4])));
        data.setColorValue(1, Color.rgb(Byte.toUnsignedInt(binaryData[10]), Byte.toUnsignedInt(binaryData[9]), Byte.toUnsignedInt(binaryData[8])));
        data.setColorValue(2, Color.rgb(Byte.toUnsignedInt(binaryData[14]), Byte.toUnsignedInt(binaryData[13]), Byte.toUnsignedInt(binaryData[12])));
        data.setColorValue(3, Color.rgb(Byte.toUnsignedInt(binaryData[18]), Byte.toUnsignedInt(binaryData[17]), Byte.toUnsignedInt(binaryData[16])));
        return data;
    }

    public byte[] toBinaryData() {
        byte[] binaryData = new byte[20];
        binaryData[0] = getColorPatternId();
        binaryData[1] = getDisplayPatternId();
        binaryData[2] = getParameterValue(0);
        binaryData[3] = getParameterValue(1);
        binaryData[4] = (byte)Color.blue(getColorValue(0));
        binaryData[5] = (byte)Color.green(getColorValue(0));
        binaryData[6] = (byte)Color.red(getColorValue(0));
        binaryData[7] = getParameterValue(2);
        binaryData[8] = (byte)Color.blue(getColorValue(1));
        binaryData[9] = (byte)Color.green(getColorValue(1));
        binaryData[10] = (byte)Color.red(getColorValue(1));
        binaryData[11] = getParameterValue(3);
        binaryData[12] = (byte)Color.blue(getColorValue(2));
        binaryData[13] = (byte)Color.green(getColorValue(2));
        binaryData[14] = (byte)Color.red(getColorValue(2));
        binaryData[15] = getParameterValue(4);
        binaryData[16] = (byte)Color.blue(getColorValue(3));
        binaryData[17] = (byte)Color.green(getColorValue(3));
        binaryData[18] = (byte)Color.red(getColorValue(3));
        binaryData[19] = getParameterValue(5);

        return binaryData;
    }

    public String toBinaryDataAsString() {
        byte[] data = toBinaryData();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (byte b: data) {
            sb.append(Integer.toString(Byte.toUnsignedInt(b), 16));
            sb.append(", ");
        }

        sb.replace(sb.length()-2, sb.length()-1, "]");
        return sb.toString().toUpperCase();
    }

    public byte getColorPatternId() {
        return colorPatternId;
    }

    public void setColorPatternId(byte colorPatternId) {
        this.colorPatternId = colorPatternId;
    }

    public byte getDisplayPatternId() {
        return displayPatternId;
    }

    public void setDisplayPatternId(byte displayPatternId) {
        this.displayPatternId = displayPatternId;
    }

    public byte getParameterValue(int parameterIndex) {
        if (parameterIndex < 0 || parameterIndex >= parameterValues.length) {
            return 0;
        }

        return parameterValues[parameterIndex];
    }

    public void setParameterValue(int parameterIndex, byte value) {
        if (parameterIndex < 0 || parameterIndex >= parameterValues.length) {
            return;
        }

        parameterValues[parameterIndex] = value;
    }

    public int getColorValue(int colorIndex) {
        if (colorIndex < 0 || colorIndex > colorValues.length) {
            return 0;
        }

        int color = colorValues[colorIndex];
        return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    public void setColorValue(int colorIndex, int value) {
        if (colorIndex < 0 || colorIndex > colorValues.length) {
            return;
        }

        colorValues[colorIndex] = value;
    }
}
