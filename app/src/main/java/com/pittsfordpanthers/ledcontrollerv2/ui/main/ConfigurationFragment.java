package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.pittsfordpanthers.ledcontrollerv2.R;
import com.pittsfordpanthers.ledcontrollerv2.ViewModel;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.signdata.ColorPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.DisplayPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternData;
import com.pittsfordpanthers.ledcontrollerv2.views.ConfigurationView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigurationFragment extends ConfigurationView {

    private View view;
    private ConnectedDevice connectedDevice = null;
    private ViewModel viewModel = null;
    private TextView statusText;
    private Button btnUpdate;
    private Button btnReload;
    private Spinner displayPatternList;
    private Spinner colorPatternList;
    private LinearLayout colorChooser;
    private TableRowFragment brightnessFragment;
    private TableRowFragment speedFragment;
    private TableRowFragment[] parameterFragments;
    private View[] colorBars;
    private TableRowFragment redFragment;
    private TableRowFragment greenFragment;
    private TableRowFragment blueFragment;
    private Button[] btnPresets;

    public ConfigurationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConfigurationFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        TableRowFragment.setValueDisplay(true);
        statusText = view.findViewById(R.id.configStatus);
        btnReload = view.findViewById(R.id.btnReload);
        btnReload.setOnClickListener(this::onReload);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this::onUpdate);
        colorChooser = view.findViewById(R.id.colorChooser);
        colorPatternList = view.findViewById(R.id.colorPattern);
        ArrayAdapter<ColorPatternOptionData> colorPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        colorPatternList.setAdapter(colorPatternListAdapter);
        colorPatternList.setOnItemSelectedListener(this.onColorPatternSelected);
        displayPatternList = view.findViewById(R.id.displayPattern);
        ArrayAdapter<DisplayPatternOptionData> displayPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        displayPatternList.setAdapter(displayPatternListAdapter);
        displayPatternList.setOnItemSelectedListener(this.onDisplayPatternSelected);
        brightnessFragment = TableRowFragment.newInstance("Brightness");
        speedFragment = TableRowFragment.newInstance("Speed");
        redFragment = TableRowFragment.newInstance("Red");
        redFragment.setOnValueChangeListener(onColorChangeListener);
        greenFragment = TableRowFragment.newInstance("Green");
        greenFragment.setOnValueChangeListener(onColorChangeListener);
        blueFragment = TableRowFragment.newInstance("Blue");
        blueFragment.setOnValueChangeListener(onColorChangeListener);
        Button btnCancelColor = view.findViewById(R.id.cancelColor);
        btnCancelColor.setOnClickListener(this::onColorSelectionCanceled);
        Button btnSelectColor = view.findViewById(R.id.selectColor);
        btnSelectColor.setOnClickListener(this::onColorSelectionCompleted);

        btnPresets = new Button[] {
                view.findViewById(R.id.preset1),
                view.findViewById(R.id.preset2),
                view.findViewById(R.id.preset3),
                view.findViewById(R.id.preset4),
                view.findViewById(R.id.preset5)
        };

        for (int i = 0; i < btnPresets.length; i++) {
            btnPresets[i].setOnClickListener(readPreferenceListener(i));
            btnPresets[i].setOnLongClickListener(writePreferenceListener(i));
        }

        colorBars = new View[]{
            view.findViewById(R.id.color1),
            view.findViewById(R.id.color2),
            view.findViewById(R.id.color3),
            view.findViewById(R.id.color4)
        };

        for (View colorBar:colorBars) {
            colorBar.setOnClickListener(this::onColorSelectionStart);
        }

        parameterFragments = new TableRowFragment[] {
                TableRowFragment.newInstance("Parameter 1"),
                TableRowFragment.newInstance("Parameter 2"),
                TableRowFragment.newInstance("Parameter 3"),
                TableRowFragment.newInstance("Parameter 4"),
                TableRowFragment.newInstance("Parameter 5"),
                TableRowFragment.newInstance("Parameter 6")
        };

        int[] presetColorButtonIds = new int[] {
                R.id.presetColor1,
                R.id.presetColor2,
                R.id.presetColor3
        };

        for (int id:presetColorButtonIds) {
            Button b = view.findViewById(id);
            b.setOnClickListener(this::onPresetColorButtonClicked);
        }

        // Dynamically add the parameter sliders
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.configTable, brightnessFragment);
        ft.add(R.id.configTable, speedFragment);
        for (TableRowFragment parameter: parameterFragments) {
            ft.add(R.id.configTable, parameter);
        }
        ft.add(R.id.colorParameterTable, redFragment);
        ft.add(R.id.colorParameterTable, greenFragment);
        ft.add(R.id.colorParameterTable, blueFragment);
        ft.commit();
    }

    private void refreshDisplay() {
        if (statusText == null) {
            // We haven't been initialized yet.
            return;
        }

        getActivity().runOnUiThread(() -> {
            if (connectedDevice == null) {
                setDisplayForDisconnectedDevice();
            } else {
                setDisplayForConnectedDevice();
            }

            // Make sure the color chooser panel isn't showing, regardless of connection state.
            view.findViewById(R.id.colorChooser).setVisibility(View.GONE);
        });
    }

    private void setDisplayForDisconnectedDevice() {
        statusText.setText("Not connected.");
        btnReload.setEnabled(false);
        btnUpdate.setEnabled(false);
        displayPatternList.setEnabled(false);
        ((ArrayAdapter<DisplayPatternOptionData>) displayPatternList.getAdapter()).clear();
        colorPatternList.setEnabled(false);
        ((ArrayAdapter<ColorPatternOptionData>) colorPatternList.getAdapter()).clear();
        brightnessFragment.setEnabled(false);
        speedFragment.setEnabled(false);
        clearParameterList();
        clearColorList();
    }

    private void setDisplayForConnectedDevice() {
        statusText.setText("Connected to: " + connectedDevice.getName());
        btnReload.setEnabled(true);
        btnUpdate.setEnabled(true);
        displayPatternList.setEnabled(true);
        colorPatternList.setEnabled(true);
        brightnessFragment.setEnabled(true);
        speedFragment.setEnabled(true);

        // Populate dropdowns
        List<ColorPatternOptionData> colorOptions = connectedDevice.getPatternOptionData().getColorPatternOptions();
        ArrayAdapter<ColorPatternOptionData> colorPatternAdapter = (ArrayAdapter<ColorPatternOptionData>) colorPatternList.getAdapter();
        colorPatternAdapter.clear();
        colorPatternAdapter.addAll(colorOptions);
        List<DisplayPatternOptionData> displayOptions = connectedDevice.getPatternOptionData().getDisplayPatternOptions();
        ArrayAdapter<DisplayPatternOptionData> displayPatternAdapter = (ArrayAdapter<DisplayPatternOptionData>) displayPatternList.getAdapter();
        displayPatternAdapter.clear();
        displayPatternAdapter.addAll(displayOptions);

        // Set current values
        brightnessFragment.setParameterValue(connectedDevice.getBrightness());
        speedFragment.setParameterValue(connectedDevice.getSpeed());
        PatternData patternData = connectedDevice.getCurrentPatternData();
        selectColorPatternById(patternData.getColorPatternId());
        selectDisplayPatternById(patternData.getDisplayPatternId());

        for (int i = 0; i < parameterFragments.length; i++) {
            parameterFragments[i].setParameterValue(patternData.getParameterValue(i));
        }

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setBackgroundColor(patternData.getColorValue(i));
        }

        resetColorList();
        resetParameterList();
    }

    private void clearParameterList() {
        for (TableRowFragment parameter: parameterFragments) {
            parameter.setVisibility(View.GONE);
        }
    }

    private void clearColorList() {
        for (View colorBar:colorBars) {
            ((TableRow)colorBar.getParent()).setVisibility(View.GONE);
        }
    }

    private void resetColorList() {
        clearColorList();

        if (colorPatternList.getSelectedItemId() < 0) {
            return;
        }

        ColorPatternOptionData colorData = (ColorPatternOptionData)colorPatternList.getSelectedItem();
        for (int i = 0; i < colorData.getNumberOfColors(); i++) {
            ((TableRow)colorBars[i].getParent()).setVisibility(View.VISIBLE);
            colorBars[i].setEnabled(true);
        }
    }

    private void resetParameterList() {
        clearParameterList();

        if (colorPatternList.getSelectedItemId() < 0 || displayPatternList.getSelectedItemId() < 0) {
            return;
        }

        ColorPatternOptionData colorData = (ColorPatternOptionData)colorPatternList.getSelectedItem();
        int paramNumber = 0;
        for (String parameterName:colorData.getParameterNames()) {
            parameterFragments[paramNumber].setParameterName(parameterName);
            parameterFragments[paramNumber].setVisibility(View.VISIBLE);
            parameterFragments[paramNumber].setEnabled(true);
            paramNumber++;
        }

        DisplayPatternOptionData displayData = (DisplayPatternOptionData)displayPatternList.getSelectedItem();
        for (String parameterName:displayData.getParameterNames()) {
            parameterFragments[paramNumber].setParameterName(parameterName);
            parameterFragments[paramNumber].setVisibility(View.VISIBLE);
            parameterFragments[paramNumber].setEnabled(true);
            paramNumber++;
        }
    }

    private void disableAll() {
        // Disable all elements in the UI.
        colorChooser.setVisibility(View.GONE);
        colorPatternList.setEnabled(false);
        displayPatternList.setEnabled(false);
        brightnessFragment.setEnabled(false);
        speedFragment.setEnabled(false);
        for (View colorBar:colorBars) {
            colorBar.setEnabled(false);
        }
        for (TableRowFragment parameter: parameterFragments) {
            parameter.setEnabled(false);
        }
        btnReload.setEnabled(false);
        btnUpdate.setEnabled(false);
    }

    private void onReload(View v) {
        statusText.setText("Refreshing device...");
        disableAll();
        viewModel.reloadConfiguration(connectedDevice);
    }

    private void onUpdate(View v) {
        statusText.setText("Updating device...");
        // Update the properties and send it to the ViewModel to be updated.
        disableAll();
        connectedDevice.setBrightness((byte) brightnessFragment.getParameterValue());
        connectedDevice.setSpeed((byte) speedFragment.getParameterValue());
        PatternData newPatternData = new PatternData();
        newPatternData.setColorPatternId((byte)((ColorPatternOptionData)colorPatternList.getSelectedItem()).getId());
        newPatternData.setDisplayPatternId((byte)((DisplayPatternOptionData)displayPatternList.getSelectedItem()).getId());
        for (int i = 0; i < colorBars.length; i++) {
            newPatternData.setColorValue(i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
        }

        for (int i = 0; i < parameterFragments.length; i++) {
            newPatternData.setParameterValue(i, (byte) parameterFragments[i].getParameterValue());
        }

        connectedDevice.setPatternData(newPatternData);
        viewModel.updateConfiguration(connectedDevice);
    }

    private AdapterView.OnItemSelectedListener onColorPatternSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            resetColorList();
            resetParameterList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private AdapterView.OnItemSelectedListener onDisplayPatternSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            resetParameterList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private TableRowFragment.OnValueChangeListener onColorChangeListener = new TableRowFragment.OnValueChangeListener() {
        @Override
        public void onValueChanged(TableRowFragment tableRowFragment, int i, boolean b) {
            int color = Color.rgb(redFragment.getParameterValue(), greenFragment.getParameterValue(), blueFragment.getParameterValue());
            View colorPreview = view.findViewById(R.id.colorPreview);
            colorPreview.setBackgroundColor(color);
        }
    };

    private View colorBeingSelected = null;

    private void onColorSelectionCompleted(View v) {
        if (colorBeingSelected != null) {
            View colorPreview = view.findViewById(R.id.colorPreview);
            ColorDrawable drawable = (ColorDrawable) colorPreview.getBackground();
            colorBeingSelected.setBackgroundColor(drawable.getColor());
        }

        colorBeingSelected = null;
        colorChooser.setVisibility(View.GONE);
    }

    private void onColorSelectionCanceled(View v) {
        colorBeingSelected = null;
        colorChooser.setVisibility(View.GONE);
    }

    private void onColorSelectionStart(View sourceColorBar) {
        ColorDrawable drawable = (ColorDrawable) sourceColorBar.getBackground();
        int color = drawable.getColor();
        redFragment.setParameterValue((byte)Color.red(color));
        greenFragment.setParameterValue((byte)Color.green(color));
        blueFragment.setParameterValue((byte)Color.blue(color));
        colorBeingSelected = sourceColorBar;
        colorChooser.setVisibility(View.VISIBLE);
    }

    private void onPresetColorButtonClicked(View presetColorButton) {
        Button b = (Button)presetColorButton;
        int color = Color.parseColor(b.getTag().toString());
        redFragment.setParameterValue(Color.red(color));
        greenFragment.setParameterValue(Color.green(color));
        blueFragment.setParameterValue(Color.blue(color));
    }

    private void selectColorPatternById(int colorPatternId) {
        for (int i = 0; i < colorPatternList.getCount(); i++) {
            ColorPatternOptionData colorPattern = (ColorPatternOptionData) colorPatternList.getItemAtPosition(i);
            if (colorPattern.getId() == colorPatternId) {
                colorPatternList.setSelection(i);
                break;
            }
        }
    }

    private void selectDisplayPatternById(int displayPatternId) {
        for (int i = 0; i < displayPatternList.getCount(); i++) {
            DisplayPatternOptionData displayPattern = (DisplayPatternOptionData) displayPatternList.getItemAtPosition(i);
            if (displayPattern.getId() == displayPatternId) {
                displayPatternList.setSelection(i);
                break;
            }
        }
    }

    private View.OnClickListener readPreferenceListener(int buttonNumber) {
        return view -> {
            int speed = getPreferenceIntValue("Pref_Speed" + buttonNumber);
            int brightness = getPreferenceIntValue("Pref_Brightness" + buttonNumber);
            int colorId = getPreferenceIntValue("Pref_ColorId" + buttonNumber);
            int displayId = getPreferenceIntValue("Pref_DisplayId" + buttonNumber);

            if (speed < 0 || brightness < 0 || colorId < 0 || displayId < 0) {
                viewModel.logMessage("At least one preference value could not be read for button " + (buttonNumber + 1));
                return;
            }

            brightnessFragment.setParameterValue(brightness);
            speedFragment.setParameterValue(speed);
            selectColorPatternById(colorId);
            selectDisplayPatternById(displayId);
            for (int i = 0; i < parameterFragments.length; i++) {
                parameterFragments[i].setParameterValue(getPreferenceIntValue("Pref_Param" + buttonNumber + "_" + i));
            }
            for (int i = 0; i < colorBars.length; i++) {
                colorBars[i].setBackgroundColor(getPreferenceIntValue("Pref_Color" + buttonNumber + "_" + i));
            }
        };
    }

    private int getPreferenceIntValue(String prefName) {
        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
        if (!pref.contains(prefName)) {
            return -1;
        }

        return pref.getInt(prefName, -1);
    }

    private View.OnLongClickListener writePreferenceListener(int buttonNumber) {
        return view -> {
            SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("Pref_Speed" + buttonNumber, speedFragment.getParameterValue());
            editor.putInt("Pref_Brightness" + buttonNumber, brightnessFragment.getParameterValue());
            editor.putInt("Pref_ColorId" + buttonNumber, ((ColorPatternOptionData) colorPatternList.getSelectedItem()).getId());
            editor.putInt("Pref_DisplayId" + buttonNumber, ((DisplayPatternOptionData) displayPatternList.getSelectedItem()).getId());
            for (int i = 0; i < parameterFragments.length; i++) {
                editor.putInt("Pref_Param" + buttonNumber + "_" + i, parameterFragments[i].getParameterValue());
            }
            for (int i = 0; i < colorBars.length; i++) {
                editor.putInt("Pref_Color" + buttonNumber + "_" + i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
            }
            editor.apply();
            Toast.makeText(getContext(), "Values set for preset number " + (buttonNumber + 1) + ".", Toast.LENGTH_SHORT).show();
            return true;
        };
    }

}