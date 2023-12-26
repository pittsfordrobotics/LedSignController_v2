package com.example.bleledcontroller.signdata;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ColorPatternOptionData {
    public String name;
    public int id;
    public int numberOfColors;
    public ArrayList<String> parameterNames = new ArrayList<>();

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
