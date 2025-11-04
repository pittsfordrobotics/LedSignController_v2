package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pittsfordpanthers.ledcontrollerv2.R;
import com.pittsfordpanthers.ledcontrollerv2.ViewModel;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.signdata.ColorPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.DisplayPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternData;
import com.pittsfordpanthers.ledcontrollerv2.views.AdvancedView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigurationFragment extends AdvancedView {
    private static final int unknownId = 255;
    private final ColorPatternOptionData unknownColorPatterOptionData = new ColorPatternOptionData("<Unknown>", unknownId, 4);
    private final DisplayPatternOptionData unkownDisplayPatternOptionData = new DisplayPatternOptionData("<Unknown>", unknownId);

    private boolean isAdvancedMode = false;
    private ViewModel viewModel = null;
    private View view;
    private ConnectedDevice connectedDevice = null;
    private NumberSliderView[] parameterSliders;
    private NumberSliderView brightnessSlider;
    private NumberSliderView speedSlider;
    private View[] colorBars;
    private EditText[] colorValues;
    private TextView colorPatternTextBox;
    private TextView displayPatternTextBox;
    private TextView statusView;
    private Button reloadButton;
    private Button updateButton;
    private Spinner displayPatternList;
    private Spinner colorPatternList;

    private final InputFilter hexInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            StringBuilder sb = new StringBuilder();

            for (int i = start; i < end; i++) {
                if (Character.isDigit(source.charAt(i))
                        || source.charAt(i)=='A'
                        || source.charAt(i)=='B'
                        || source.charAt(i)=='C'
                        || source.charAt(i)=='D'
                        || source.charAt(i)=='E'
                        || source.charAt(i)=='F'
                        || source.charAt(i)=='a'
                        || source.charAt(i)=='b'
                        || source.charAt(i)=='c'
                        || source.charAt(i)=='d'
                        || source.charAt(i)=='e'
                        || source.charAt(i)=='f'
                ) {
                    sb.append(source.charAt(i));
                    if (sb.length() >= 6) {
                        break;
                    }
                }
            }
            return sb.toString().toUpperCase();
        }
    };

    public ConfigurationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvancedFragment.
     */
    public static ConfigurationFragment newInstance(ViewModel viewModel) {
        ConfigurationFragment fragment = new ConfigurationFragment();
        fragment.viewModel = viewModel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_configuration, container, false);
        initialize();
        refreshDisplay();
        return view;
    }

    @Override
    public void setConnectedDevice(ConnectedDevice device) {
        connectedDevice = device;
        refreshDisplay();
    }

    @Override
    public void setDisconnectedState() {
        connectedDevice = null;
        refreshDisplay();
    }

    private void initialize() {
        statusView = view.findViewById(R.id.connectionStatus);
        reloadButton = view.findViewById(R.id.btnReload);
        reloadButton.setOnClickListener(this::onReload);
        updateButton = view.findViewById(R.id.btnSend);
        updateButton.setOnClickListener(this::onSend);
        displayPatternTextBox = view.findViewById(R.id.txtDisplayPattern);
        displayPatternTextBox.setOnEditorActionListener(this::updateDisplayPatternView);
        colorPatternTextBox = view.findViewById(R.id.txtColorPattern);
        colorPatternTextBox.setOnEditorActionListener(this::updateColorPatternView);
        brightnessSlider = view.findViewById(R.id.brightness);
        speedSlider = view.findViewById(R.id.speed);
        colorPatternList = view.findViewById(R.id.spnColorPattern);
        ArrayAdapter<ColorPatternOptionData> colorPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        colorPatternList.setAdapter(colorPatternListAdapter);
        colorPatternList.setOnItemSelectedListener(this.onColorPatternSelected);
        displayPatternList = view.findViewById(R.id.spnDisplayPattern);
        ArrayAdapter<DisplayPatternOptionData> displayPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        displayPatternList.setAdapter(displayPatternListAdapter);
        displayPatternList.setOnItemSelectedListener(this.onDisplayPatternSelected);

        colorBars = new View[] {
                view.findViewById(R.id.color1),
                view.findViewById(R.id.color2),
                view.findViewById(R.id.color3),
                view.findViewById(R.id.color4)
        };

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setTag(i);
            colorBars[i].setOnClickListener(this::onColorTapped);
        }

        colorValues = new EditText[] {
                view.findViewById(R.id.colorCode1),
                view.findViewById(R.id.colorCode2),
                view.findViewById(R.id.colorCode3),
                view.findViewById(R.id.colorCode4)
        };

        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i].setFilters(new InputFilter[]{hexInputFilter});
            colorValues[i].setTag(i);
            colorValues[i].setOnEditorActionListener(this::setColorValue);
            colorValues[i].setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        parameterSliders = new NumberSliderView[] {
                view.findViewById(R.id.parameter1),
                view.findViewById(R.id.parameter2),
                view.findViewById(R.id.parameter3),
                view.findViewById(R.id.parameter4),
                view.findViewById(R.id.parameter5),
                view.findViewById(R.id.parameter6)
        };

        int[] presetIds = {
                R.id.preset1,
                R.id.preset2,
                R.id.preset3,
                R.id.preset4,
                R.id.preset5,
                R.id.preset6,
                R.id.preset7,
                R.id.preset8,
                R.id.preset9,
                R.id.preset10
        };

        for (int i = 0; i < presetIds.length; i++) {
            Button b = view.findViewById(presetIds[i]);
            b.setOnClickListener(readPreferenceListener(i));
            b.setOnLongClickListener(writePreferenceListener(i));
        }

        view.findViewById(R.id.showAdvanced).setOnClickListener( v -> {
            isAdvancedMode = ((Switch)v).isChecked();
            refreshDisplay();
        });
    }

    private void refreshDisplay() {
        if (view == null) {
            // We haven't been initialized yet.
            return;
        }

        getActivity().runOnUiThread(() -> {
            colorPatternTextBox.setVisibility(isAdvancedMode ? VISIBLE : GONE);
            displayPatternTextBox.setVisibility(isAdvancedMode ? VISIBLE : GONE);
            colorPatternList.setVisibility(isAdvancedMode ? GONE : VISIBLE);
            displayPatternList.setVisibility(isAdvancedMode ? GONE : VISIBLE);

            if (connectedDevice == null) {
                setDisplayForDisconnectedDevice();
            } else {
                setDisplayForConnectedDevice();
            }
        });
    }

    private void setDisplayForDisconnectedDevice() {
        statusView.setText("Not connected.");
        ((ArrayAdapter<DisplayPatternOptionData>) displayPatternList.getAdapter()).clear();
        ((ArrayAdapter<ColorPatternOptionData>) colorPatternList.getAdapter()).clear();
        setParameterListToDefault();
        setColorBarsToDefault();
        disableAll();
    }

    private void setColorBarsToDefault() {
        for (View colorBar:colorBars) {
            ((TableRow)colorBar.getParent()).setVisibility(isAdvancedMode ? VISIBLE : GONE);
            colorBar.setEnabled(true);
        }
    }

    private void showColorBarsForPattern() {
        setColorBarsToDefault();
        if (isAdvancedMode) {
            // If in advanced mode, leave all color bars showing.
            return;
        }

        if (colorPatternList.getSelectedItemId() < 0) {
            // Shouldn't happen?
            return;
        }

        ColorPatternOptionData colorData = (ColorPatternOptionData)colorPatternList.getSelectedItem();
        for (int i = 0; i < colorData.getNumberOfColors(); i++) {
            ((TableRow)colorBars[i].getParent()).setVisibility(View.VISIBLE);
        }
    }

    private void setParameterListToDefault() {
        for (int i = 0; i < parameterSliders.length; i++) {
            parameterSliders[i].setVisibility(VISIBLE);
            parameterSliders[i].setLabel("Parameter " + String.valueOf(i+1) +":");
            parameterSliders[i].setEnabled(true);
        }
    }

    private void showParameterListForPattern() {
        setParameterListToDefault();

        if (isAdvancedMode) {
            // If in advanced mode, leave all parameters showing with default labels.
            return;
        }

        if (colorPatternList.getSelectedItemId() < 0 || displayPatternList.getSelectedItemId() < 0) {
            // Nothing selected?  Shouldn't get here.
            return;
        }

        if (((ColorPatternOptionData) colorPatternList.getSelectedItem()).getId() == unknownId
                || ((DisplayPatternOptionData) displayPatternList.getSelectedItem()).getId() == unknownId) {
            // We won't be able to determine the number of parameters or their labels.
            return;
        }

        ColorPatternOptionData colorData = (ColorPatternOptionData)colorPatternList.getSelectedItem();
        int paramNumber = 0;
        for (String parameterName:colorData.getParameterNames()) {
            parameterSliders[paramNumber].setLabel(parameterName + ":");
            paramNumber++;
        }

        DisplayPatternOptionData displayData = (DisplayPatternOptionData)displayPatternList.getSelectedItem();
        for (String parameterName:displayData.getParameterNames()) {
            parameterSliders[paramNumber].setLabel(parameterName + ":");
            paramNumber++;
        }

        for (int i = paramNumber; i < parameterSliders.length; i++) {
            parameterSliders[i].setVisibility(GONE);
        }
    }

    private void setDisplayForConnectedDevice() {
        statusView.setText("Connected to: " + connectedDevice.getName());

        // Populate settings
        brightnessSlider.setValue(Byte.toUnsignedInt(connectedDevice.getBrightness()));
        speedSlider.setValue(Byte.toUnsignedInt(connectedDevice.getSpeed()));
        PatternData patternData = connectedDevice.getCurrentPatternData();
        colorPatternTextBox.setText(String.valueOf(Byte.toUnsignedInt(patternData.getColorPatternId())));
        displayPatternTextBox.setText(String.valueOf(Byte.toUnsignedInt(patternData.getDisplayPatternId())));

        for (int i = 0; i < parameterSliders.length; i++) {
            parameterSliders[i].setValue(Byte.toUnsignedInt(patternData.getParameterValue(i)));
        }

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setBackgroundColor(patternData.getColorValue(i));
            colorValues[i].setText(colorIntToString(patternData.getColorValue(i)));
        }

        // Populate dropdowns
        List<ColorPatternOptionData> colorOptions = connectedDevice.getPatternOptionData().getColorPatternOptions();
        ArrayAdapter<ColorPatternOptionData> colorPatternAdapter = (ArrayAdapter<ColorPatternOptionData>) colorPatternList.getAdapter();
        colorPatternAdapter.clear();
        colorPatternAdapter.addAll(colorOptions);
        colorPatternAdapter.add(unknownColorPatterOptionData);
        List<DisplayPatternOptionData> displayOptions = connectedDevice.getPatternOptionData().getDisplayPatternOptions();
        ArrayAdapter<DisplayPatternOptionData> displayPatternAdapter = (ArrayAdapter<DisplayPatternOptionData>) displayPatternList.getAdapter();
        displayPatternAdapter.clear();
        displayPatternAdapter.addAll(displayOptions);
        displayPatternAdapter.add(unkownDisplayPatternOptionData);
        selectColorPatternById(patternData.getColorPatternId());
        colorPatternTextBox.setText(String.valueOf(patternData.getColorPatternId()));
        selectDisplayPatternById(patternData.getDisplayPatternId());
        displayPatternTextBox.setText(String.valueOf(patternData.getDisplayPatternId()));
        colorPatternList.setEnabled(true);
        displayPatternList.setEnabled(true);

        // Enable UI elements
        reloadButton.setEnabled(true);
        updateButton.setEnabled(true);
        displayPatternTextBox.setEnabled(true);
        colorPatternTextBox.setEnabled(true);
        brightnessSlider.setEnabled(true);
        speedSlider.setEnabled(true);

        for (View v : colorValues) {
            v.setEnabled(true);
        }

        showColorBarsForPattern();
        showParameterListForPattern();
    }

    private String colorIntToString(int color) {
        String colorString = Integer.toHexString(color);

        // String of the leading FF.
        // Sanity check - it should always be 8 characters long.
        if (colorString.length() == 8) {
            colorString = colorString.substring(2);
        } else {
            colorString = "0";
        }

        return colorString;
    }

    private boolean updateColorPatternView(TextView textView, int i, KeyEvent keyEvent) {
        setByteBounds(textView);
        textView.clearFocus();
        return false;
    }

    private boolean updateDisplayPatternView(TextView textView, int i, KeyEvent keyEvent) {
        setByteBounds(textView);
        textView.clearFocus();
        return false;
    }

    private void setByteBounds(TextView textView) {
        Integer value = Integer.parseInt(textView.getText().toString());
        if (value < 0) {
            value = 0;
        }
        if (value > 255) {
            value = 255;
        }
        textView.setText(String.valueOf(value));
    }

    private void selectColorPatternById(int colorPatternId) {
        for (int i = 0; i < colorPatternList.getCount(); i++) {
            ColorPatternOptionData colorPattern = (ColorPatternOptionData) colorPatternList.getItemAtPosition(i);
            if (colorPattern.getId() == colorPatternId) {
                colorPatternList.setSelection(i);
                return;
            }
        }

        // Didn't find it.
        colorPatternList.setSelection(colorPatternList.getCount()-1);
    }

    private void selectDisplayPatternById(int displayPatternId) {
        for (int i = 0; i < displayPatternList.getCount(); i++) {
            DisplayPatternOptionData displayPattern = (DisplayPatternOptionData) displayPatternList.getItemAtPosition(i);
            if (displayPattern.getId() == displayPatternId) {
                displayPatternList.setSelection(i);
                return;
            }
        }

        // Didn't find it
        displayPatternList.setSelection(displayPatternList.getCount()-1);
    }

    private int getPreferenceIntValue(String prefName) {
        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
        if (!pref.contains(prefName)) {
            return -1;
        }

        return pref.getInt(prefName, -1);
    }

    private void disableAll() {
        reloadButton.setEnabled(false);
        updateButton.setEnabled(false);
        displayPatternTextBox.setEnabled(false);
        colorPatternTextBox.setEnabled(false);
        brightnessSlider.setEnabled(false);
        speedSlider.setEnabled(false);
        colorPatternList.setEnabled(false);
        displayPatternList.setEnabled(false);

        for (NumberSliderView p : parameterSliders) {
            p.setEnabled(false);
        }

        for (View v : colorBars) {
            v.setEnabled(false);
        }

        for (View v : colorValues) {
            v.setEnabled(false);
        }
    }

    // *****
    // Event callbacks
    // *****

    // Reload button - refresh connected device settings
    private void onReload(View v) {
        statusView.setText("Refreshing device...");
        disableAll();
        viewModel.reloadConfiguration(connectedDevice);
    }

    // Send button - send new values to the connected device
    private void onSend(View v) {
        statusView.setText("Updating device...");
        disableAll();
        connectedDevice.setBrightness((byte) brightnessSlider.getValue());
        connectedDevice.setSpeed((byte) speedSlider.getValue());
        PatternData patternData = new PatternData();
        patternData.setColorPatternId((byte) Integer.parseInt(colorPatternTextBox.getText().toString()));
        patternData.setDisplayPatternId((byte) Integer.parseInt(displayPatternTextBox.getText().toString()));
        for (int i = 0; i < colorBars.length; i++) {
            patternData.setColorValue(i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
        }

        for (int i = 0; i < parameterSliders.length; i++) {
            patternData.setParameterValue(i, (byte) parameterSliders[i].getValue());
        }

        connectedDevice.setPatternData(patternData);
        viewModel.updateConfiguration(connectedDevice);
    }

    // Called when the color value text field has been updated - formats the text and updates the color bar.
    private boolean setColorValue(TextView textView, int i, KeyEvent keyEvent) {
        // Format the input string:
        //  - if it's less than 6 characters, pad zeros in front.
        //  - Add "FF" to the front to indicate the alpha value of the color.
        CharSequence inputValue = textView.getText();
        StringBuilder sb = new StringBuilder();
        for (int c = 6; c > inputValue.length(); c--) {
            sb.append("0");
        }

        sb.append(inputValue);
        textView.setText(sb.toString());
        String fullColorString = "FF" + sb.toString();
        int newColor = Integer.parseUnsignedInt(fullColorString, 16);
        int barId = (int)textView.getTag();
        colorBars[barId].setBackgroundColor(newColor);
        textView.clearFocus();
        return false;
    }

    // Called when the color bar has been tapped - opens the color picker.
    private void onColorTapped(View sourceColorBar) {
        int barId = (int)sourceColorBar.getTag();
        ColorDrawable drawable = (ColorDrawable) sourceColorBar.getBackground();
        int initialColor = drawable.getColor();
        // odd bug: if the color is black, the color picker doesn't send a new color
        // unless the brightness slider has been moved
        if (initialColor == Color.BLACK) {
            initialColor = Color.argb(255, 255, 255, 255);
        }

        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(getContext())
                .setTitle("Color " + (barId + 1))
                .setPositiveButton("OK",
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                sourceColorBar.setBackgroundColor(envelope.getColor());
                                String colorString = colorIntToString(envelope.getColor());
                                colorValues[barId].setText(colorString);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(false) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(10); // set a bottom space between the last slidebar and buttons.

        ColorPickerView cpView = builder.getColorPickerView();
        cpView.setInitialColor(initialColor);
        builder.show();
    }

    private final AdapterView.OnItemSelectedListener onColorPatternSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ColorPatternOptionData colorData = (ColorPatternOptionData)colorPatternList.getSelectedItem();
            if (colorData.getId() != unknownId) {
                colorPatternTextBox.setText(String.valueOf(colorData.getId()));
            }
            showColorBarsForPattern();
            showParameterListForPattern();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private final AdapterView.OnItemSelectedListener onDisplayPatternSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            DisplayPatternOptionData displayData = (DisplayPatternOptionData)displayPatternList.getSelectedItem();
            if (displayData.getId() != unknownId) {
                displayPatternTextBox.setText(String.valueOf(displayData.getId()));
            }
            showParameterListForPattern();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private View.OnClickListener readPreferenceListener(int buttonNumber) {
        return view -> {
            int speed = getPreferenceIntValue("Adv_Speed" + buttonNumber);
            int brightness = getPreferenceIntValue("Adv_Brightness" + buttonNumber);
            int colorPattern = getPreferenceIntValue("Adv_ColorPattern" + buttonNumber);
            int displayPattern = getPreferenceIntValue("Adv_DisplayPattern" + buttonNumber);

            if (speed < 0 || brightness < 0 || colorPattern < 0 || displayPattern < 0) {
                viewModel.logMessage("At least one preference value could not be read for button " + (buttonNumber + 1));
                return;
            }

            brightnessSlider.setValue(brightness);
            speedSlider.setValue(speed);
            colorPatternTextBox.setText(String.valueOf(colorPattern));
            selectColorPatternById(colorPattern);
            displayPatternTextBox.setText(String.valueOf(displayPattern));
            selectDisplayPatternById(displayPattern);
            for (int i = 0; i < parameterSliders.length; i++) {
                parameterSliders[i].setValue(getPreferenceIntValue("Adv_Param" + buttonNumber + "_" + i));
            }

            for (int i = 0; i < colorBars.length; i++) {
                int color = getPreferenceIntValue("Adv_Color" + buttonNumber + "_" + i);
                colorBars[i].setBackgroundColor(color);
                colorValues[i].setText(colorIntToString(color));
            }
        };
    }

    private View.OnLongClickListener writePreferenceListener(int buttonNumber) {
        return view -> {
            SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("Adv_Speed" + buttonNumber, speedSlider.getValue());
            editor.putInt("Adv_Brightness" + buttonNumber, brightnessSlider.getValue());
            editor.putInt("Adv_ColorPattern" + buttonNumber, Integer.parseInt(colorPatternTextBox.getText().toString()));
            editor.putInt("Adv_DisplayPattern" + buttonNumber, Integer.parseInt(displayPatternTextBox.getText().toString()));
            for (int i = 0; i < parameterSliders.length; i++) {
                editor.putInt("Adv_Param" + buttonNumber + "_" + i, parameterSliders[i].getValue());
            }

            for (int i = 0; i < colorBars.length; i++) {
                editor.putInt("Adv_Color" + buttonNumber + "_" + i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
            }

            editor.apply();
            Toast.makeText(getContext(), "Values set for preset number " + (buttonNumber + 1) + ".", Toast.LENGTH_SHORT).show();
            return true;
        };
    }
}