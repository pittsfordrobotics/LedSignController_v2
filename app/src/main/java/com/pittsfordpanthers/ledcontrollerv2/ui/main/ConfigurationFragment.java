package com.pittsfordpanthers.ledcontrollerv2.ui.main;

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
    private TableRowFragment brightness;
    private TableRowFragment speed;
    private TableRowFragment[] parameters;
    private View[] colorBars;
    private TableRowFragment redFragment;
    private TableRowFragment greenFragment;
    private TableRowFragment blueFragment;

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
        brightness = TableRowFragment.newInstance("Brightness");
        speed = TableRowFragment.newInstance("Speed");
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

        colorBars = new View[]{
            view.findViewById(R.id.color1),
            view.findViewById(R.id.color2),
            view.findViewById(R.id.color3),
            view.findViewById(R.id.color4)
        };

        for (View colorBar:colorBars) {
            colorBar.setOnClickListener(this::onColorSelectionStart);
        }

        parameters = new TableRowFragment[] {
                TableRowFragment.newInstance("Parameter 1"),
                TableRowFragment.newInstance("Parameter 2"),
                TableRowFragment.newInstance("Parameter 3"),
                TableRowFragment.newInstance("Parameter 4"),
                TableRowFragment.newInstance("Parameter 5"),
                TableRowFragment.newInstance("Parameter 6")
        };

        // Dynamically add the parameter sliders
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.configTable, brightness);
        ft.add(R.id.configTable, speed);
        for (TableRowFragment parameter:parameters) {
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
        brightness.setEnabled(false);
        speed.setEnabled(false);
        clearParameterList();
        clearColorList();
    }

    private void setDisplayForConnectedDevice() {
        statusText.setText("Connected to: " + connectedDevice.getName());
        btnReload.setEnabled(true);
        btnUpdate.setEnabled(true);
        displayPatternList.setEnabled(true);
        colorPatternList.setEnabled(true);
        brightness.setEnabled(true);
        speed.setEnabled(true);

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
        brightness.setParameterValue(connectedDevice.getBrightness());
        speed.setParameterValue(connectedDevice.getSpeed());
        PatternData patternData = connectedDevice.getCurrentPatternData();
        // Find the index of the current color/display pattern
        int index = 0;
        for (ColorPatternOptionData option:connectedDevice.getPatternOptionData().getColorPatternOptions()) {
            if (option.getId() == patternData.getColorPatternId())
            {
                colorPatternList.setSelection(index);
                break;
            }
            index++;
        }

        index = 0;
        for (DisplayPatternOptionData option:connectedDevice.getPatternOptionData().getDisplayPatternOptions()) {
            if (option.getId() == patternData.getDisplayPatternId())
            {
                displayPatternList.setSelection(index);
                break;
            }
            index++;
        }

        for (int i = 0; i < parameters.length; i++) {
            parameters[i].setParameterValue(patternData.getParameterValue(i));
        }

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setBackgroundColor(patternData.getColorValue(i));
        }

        resetColorList();
        resetParameterList();
    }

    private void clearParameterList() {
        for (TableRowFragment parameter:parameters) {
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
            parameters[paramNumber].setParameterName(parameterName);
            parameters[paramNumber].setVisibility(View.VISIBLE);
            parameters[paramNumber].setEnabled(true);
            paramNumber++;
        }

        DisplayPatternOptionData displayData = (DisplayPatternOptionData)displayPatternList.getSelectedItem();
        for (String parameterName:displayData.getParameterNames()) {
            parameters[paramNumber].setParameterName(parameterName);
            parameters[paramNumber].setVisibility(View.VISIBLE);
            parameters[paramNumber].setEnabled(true);
            paramNumber++;
        }
    }

    private void disableAll() {
        // Disable all elements in the UI.
        colorChooser.setVisibility(View.GONE);
        colorPatternList.setEnabled(false);
        displayPatternList.setEnabled(false);
        brightness.setEnabled(false);
        speed.setEnabled(false);
        for (View colorBar:colorBars) {
            colorBar.setEnabled(false);
        }
        for (TableRowFragment parameter:parameters) {
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
        connectedDevice.setBrightness((byte)brightness.getParameterValue());
        connectedDevice.setSpeed((byte)speed.getParameterValue());
        PatternData newPatternData = new PatternData();
        newPatternData.setColorPatternId((byte)((ColorPatternOptionData)colorPatternList.getSelectedItem()).getId());
        newPatternData.setDisplayPatternId((byte)((DisplayPatternOptionData)displayPatternList.getSelectedItem()).getId());
        for (int i = 0; i < colorBars.length; i++) {
            newPatternData.setColorValue(i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
        }

        for (int i = 0; i < parameters.length; i++) {
            newPatternData.setParameterValue(i, (byte)parameters[i].getParameterValue());
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
}