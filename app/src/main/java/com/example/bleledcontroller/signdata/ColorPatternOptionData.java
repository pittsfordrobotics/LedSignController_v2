package com.example.bleledcontroller.signdata;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ColorPatternOptionData {
    private String name;
    private int id;
    private int numberOfColors;
    private ArrayList<String> parameterNames = new ArrayList<>();

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
