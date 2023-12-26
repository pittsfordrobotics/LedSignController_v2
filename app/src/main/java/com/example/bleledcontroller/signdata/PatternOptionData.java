package com.example.bleledcontroller.signdata;

import java.util.ArrayList;
import java.util.List;

public class PatternOptionData {
    private ArrayList<ColorPatternOptionData> colorPatternOptions = new ArrayList<>();
    private ArrayList<DisplayPatternOptionData> displayPatternOptionData = new ArrayList<>();

    public void addColorPatternOption(ColorPatternOptionData colorPatternOptionData) {
        this.colorPatternOptions.add(colorPatternOptionData);
    }

    public void addDisplayPatternOption(DisplayPatternOptionData displayPatternOptionData) {
        this.displayPatternOptionData.add(displayPatternOptionData);
    }

    public List<ColorPatternOptionData> getColorPatternOptions() {
        return colorPatternOptions;
    }

    public List<DisplayPatternOptionData> getDisplayPatternOptions() {
        return displayPatternOptionData;
    }
}
