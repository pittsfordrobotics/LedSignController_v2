package com.example.bleledcontroller.signdata;

import android.graphics.Color;

import java.util.regex.Pattern;

public class PatternData {
    byte colorPatternId;
    byte displayPatternId;
    byte[] parameterValues = new byte[6];
    int[] colorValues = new int[4];

    public static PatternData ParseBinaryData(byte[] binaryData) {
        if (binaryData.length != 20) {
            // Invalid data
            return null;
        }

        PatternData data = new PatternData();
        data.setColorPatternId(binaryData[0]);
        data.setDisplayPatternId(binaryData[1]);
        data.setParameterValue(0, binaryData[2]);
        data.setParameterValue(1, binaryData[3]);
        data.setParameterValue(2, binaryData[4]);
        data.setParameterValue(3, binaryData[8]);
        data.setParameterValue(4, binaryData[12]);
        data.setParameterValue(5, binaryData[16]);
        data.setColorValue(0, Color.rgb(binaryData[5], binaryData[6], binaryData[7]));
        data.setColorValue(1, Color.rgb(binaryData[9], binaryData[10], binaryData[11]));
        data.setColorValue(2, Color.rgb(binaryData[13], binaryData[14], binaryData[15]));
        data.setColorValue(3, Color.rgb(binaryData[17], binaryData[18], binaryData[19]));
        return data;
    }

    public byte[] ToBinaryData() {
        byte[] binaryData = new byte[20];
        binaryData[0] = getColorPatternId();
        binaryData[1] = getDisplayPatternId();
        binaryData[2] = getParameterValue(0);
        binaryData[3] = getParameterValue(1);
        binaryData[4] = getParameterValue(2);
        binaryData[5] = (byte)Color.red(getColorValue(0));
        binaryData[6] = (byte)Color.green(getColorValue(0));
        binaryData[7] = (byte)Color.blue(getColorValue(0));
        binaryData[8] = getParameterValue(3);
        binaryData[9] = (byte)Color.red(getColorValue(1));
        binaryData[10] = (byte)Color.green(getColorValue(1));
        binaryData[11] = (byte)Color.blue(getColorValue(1));
        binaryData[12] = getParameterValue(4);
        binaryData[13] = (byte)Color.red(getColorValue(2));
        binaryData[14] = (byte)Color.green(getColorValue(2));
        binaryData[15] = (byte)Color.blue(getColorValue(2));
        binaryData[16] = getParameterValue(5);
        binaryData[17] = (byte)Color.red(getColorValue(3));
        binaryData[18] = (byte)Color.green(getColorValue(3));
        binaryData[19] = (byte)Color.blue(getColorValue(3));

        return binaryData;
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

        return colorValues[colorIndex];
    }

    public void setColorValue(int colorIndex, int value) {
        if (colorIndex < 0 || colorIndex > colorValues.length) {
            return;
        }

        colorValues[colorIndex] = value;
    }
}
