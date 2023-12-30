package com.pittsfordpanthers.ledcontrollerv2.signdata;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ColorPatternOptionData {
    private String name;
    private int id;
    private int numberOfColors;
    private ArrayList<String> parameterNames = new ArrayList<>();

    public static ColorPatternOptionData fromString(String colorPatternString) {
        String[] parts = colorPatternString.split(",");
        if (parts.length < 3) {
            // Invalid length
            return null;
        }

        ColorPatternOptionData optionData = new ColorPatternOptionData(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        for (int i = 3; i < parts.length; i++) {
            optionData.addParameterName(parts[i]);
        }

        return optionData;
    }

    public ColorPatternOptionData(String name, int id, int numberOfColors) {
        this.name = name;
        this.id = id;
        this.numberOfColors = numberOfColors;
    }

    public void addParameterName(String name) {
        parameterNames.add(name);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return id;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
