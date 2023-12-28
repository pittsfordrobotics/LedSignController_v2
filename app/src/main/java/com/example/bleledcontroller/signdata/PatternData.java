package com.example.bleledcontroller.signdata;

import android.graphics.Color;

import java.util.regex.Pattern;

public class PatternData {
    byte colorPattern;
    byte displayPattern;
    byte parameter1;
    byte parameter2;
    byte parameter3;
    byte parameter4;
    byte parameter5;
    byte parameter6;
    int color1;
    int color2;
    int color3;
    int color4;

    public static PatternData ParseBinaryData(byte[] binaryData) {
        if (binaryData.length != 20) {
            // Invalid data
            return null;
        }

        PatternData data = new PatternData();
        data.setColorPattern(binaryData[0]);
        data.setDisplayPattern(binaryData[1]);
        data.setParameter1(binaryData[2]);
        data.setParameter2(binaryData[3]);
        data.setParameter3(binaryData[4]);
        data.setParameter4(binaryData[8]);
        data.setParameter5(binaryData[12]);
        data.setParameter6(binaryData[16]);
        data.setColor1(Color.rgb(binaryData[5], binaryData[6], binaryData[7]));
        data.setColor2(Color.rgb(binaryData[9], binaryData[10], binaryData[11]));
        data.setColor3(Color.rgb(binaryData[13], binaryData[14], binaryData[15]));
        data.setColor4(Color.rgb(binaryData[17], binaryData[18], binaryData[19]));
        return data;
    }

    public byte[] ToBinaryData() {
        byte[] binaryData = new byte[20];
        binaryData[0] = getColorPattern();
        binaryData[1] = getDisplayPattern();
        binaryData[2] = getParameter1();
        binaryData[3] = getParameter2();
        binaryData[4] = getParameter3();
        binaryData[5] = (byte)Color.red(getColor1());
        binaryData[6] = (byte)Color.green(getColor1());
        binaryData[7] = (byte)Color.blue(getColor1());
        binaryData[8] = getParameter4();
        binaryData[9] = (byte)Color.red(getColor2());
        binaryData[10] = (byte)Color.green(getColor2());
        binaryData[11] = (byte)Color.blue(getColor2());
        binaryData[12] = getParameter5();
        binaryData[13] = (byte)Color.red(getColor3());
        binaryData[14] = (byte)Color.green(getColor3());
        binaryData[15] = (byte)Color.blue(getColor3());
        binaryData[16] = getParameter6();
        binaryData[17] = (byte)Color.red(getColor4());
        binaryData[18] = (byte)Color.green(getColor4());
        binaryData[19] = (byte)Color.blue(getColor4());

        return binaryData;
    }

    public byte getColorPattern() {
        return colorPattern;
    }

    public void setColorPattern(byte colorPattern) {
        this.colorPattern = colorPattern;
    }

    public byte getDisplayPattern() {
        return displayPattern;
    }

    public void setDisplayPattern(byte displayPattern) {
        this.displayPattern = displayPattern;
    }

    public byte getParameter1() {
        return parameter1;
    }

    public void setParameter1(byte parameter1) {
        this.parameter1 = parameter1;
    }

    public byte getParameter2() {
        return parameter2;
    }

    public void setParameter2(byte parameter2) {
        this.parameter2 = parameter2;
    }

    public byte getParameter3() {
        return parameter3;
    }

    public void setParameter3(byte parameter3) {
        this.parameter3 = parameter3;
    }

    public byte getParameter4() {
        return parameter4;
    }

    public void setParameter4(byte parameter4) {
        this.parameter4 = parameter4;
    }

    public byte getParameter5() {
        return parameter5;
    }

    public void setParameter5(byte parameter5) {
        this.parameter5 = parameter5;
    }

    public byte getParameter6() {
        return parameter6;
    }

    public void setParameter6(byte parameter6) {
        this.parameter6 = parameter6;
    }

    public int getColor1() {
        return color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public int getColor3() {
        return color3;
    }

    public void setColor3(int color3) {
        this.color3 = color3;
    }

    public int getColor4() {
        return color4;
    }

    public void setColor4(int color4) {
        this.color4 = color4;
    }
}
