package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pittsfordpanthers.ledcontrollerv2.R;

public class NumberSliderView extends LinearLayout {
    public interface OnValueChangeListener {
        public void onValueChange(NumberSliderView numberSliderView, int i, boolean b);
    }

    private LinearLayout parentLayout;
    private TextView label;
    private SeekBar seekBar;
    private EditText textBox;

    private OnValueChangeListener onValueChangeListener = null;

    private int lastValue = 0;
    private String labelValue;
    private boolean enabled = true;
    private int visibility;
    private int minValue;
    private int maxValue;

    public NumberSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
        Add attributes...
        https://www.vogella.com/tutorials/AndroidCustomViews/article.html#exercise-create-a-compound-view

        Ex:
        TypedArray a = context.obtainStyledAttributes(attrs,
        R.styleable.ColorOptionsView, 0, 0);
        String titleText = a.getString(R.styleable.ColorOptionsView_titleText);
        @SuppressWarnings("ResourceAsColor")
        int valueColor = a.getColor(R.styleable.ColorOptionsView_valueColor,
                android.R.color.holo_blue_light);
        a.recycle();
         */

        // read attributes
        minValue = 0;
        maxValue = 255;
        lastValue = 0;
        labelValue = "Parameter1:";

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.number_slider, this, true);

        parentLayout = findViewById(R.id.parentLayout);
        label = findViewById(R.id.txtLabel);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMin(minValue);
        seekBar.setMax(maxValue);
        seekBar.setOnSeekBarChangeListener(createSeekBarChangeListener());
        textBox = findViewById(R.id.textValue);
        textBox.setOnEditorActionListener(this::editorActionListener);
        refresh();
    }

    public NumberSliderView(Context context) {
        super(context, null);
    }

    public void setLabel(String label) {
        labelValue = label;
        refresh();
    }

    public int getValue() {
        return seekBar.getProgress();
    }

    public void setValue(int value) {
        lastValue = value;
        refresh();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        refresh();
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        refresh();
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    private void refresh() {
        if (!label.getText().equals(labelValue)) {
            label.setText(labelValue);
        }

        seekBar.setEnabled(enabled);
        seekBar.setProgress(lastValue);
        textBox.setText(String.valueOf(lastValue));
        textBox.clearFocus();
        parentLayout.setVisibility(visibility);
    }

    private NumberSliderView getNumberSliderViewRef() {
        return this;
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lastValue = i;
                textBox.setText(String.valueOf(i));
                if (onValueChangeListener != null) {
                    onValueChangeListener.onValueChange(getNumberSliderViewRef(), i, b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private boolean editorActionListener(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        int value = Integer.parseInt(textView.getText().toString());
        if (value < minValue) {
            value = minValue;
        }
        if (value > maxValue) {
            value = maxValue;
        }
        setValue(value);
        return handled;
    }
}
