package com.example.bleledcontroller.signdata;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DisplayPatternOptionData {
    public String name;
    public int id;
    public ArrayList<String> parameterNames = new ArrayList<>();

    public DisplayPatternOptionData(String name, int id) {
        this.name = name;
        this.id = id;
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

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
