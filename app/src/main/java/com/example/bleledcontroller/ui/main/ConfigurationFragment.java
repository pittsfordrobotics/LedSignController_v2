package com.example.bleledcontroller.ui.main;

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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.bleledcontroller.R;
import com.example.bleledcontroller.ViewModel;
import com.example.bleledcontroller.bluetooth.ConnectedDevice;
import com.example.bleledcontroller.signdata.ColorPatternOptionData;
import com.example.bleledcontroller.signdata.DisplayPatternOptionData;
import com.example.bleledcontroller.views.ConfigurationView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigurationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigurationFragment extends ConfigurationView {

    private ConnectedDevice connectedDevice = null;
    private ViewModel viewModel = null;
    private TextView statusText;
    private Button btnUpdate;
    private Button btnReload;
    private Spinner displayPatternList;
    private Spinner colorPatternList;
    private TextView[] parameterNames;
    private SeekBar[] parameterValues;
    private SeekBar brightness;
    private SeekBar speed;
    private ArrayAdapter<ColorPatternOptionData> colorPatternListAdapter;
    private ArrayAdapter<DisplayPatternOptionData> displayPatternListAdapter;
    private TableRowFragment testRow;

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
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);
        initialize(view);
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

    private void initialize(View view) {
        statusText = view.findViewById(R.id.configStatus);
        btnReload = view.findViewById(R.id.btnReload);
        btnReload.setOnClickListener(this::onReload);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this::onUpdate);
        colorPatternList = view.findViewById(R.id.colorPattern);
        colorPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        colorPatternList.setAdapter(colorPatternListAdapter);
        colorPatternList.setOnItemSelectedListener(this.onColorPatternSelected);
        displayPatternList = view.findViewById(R.id.displayPattern);
        displayPatternListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        displayPatternList.setAdapter(displayPatternListAdapter);
        displayPatternList.setOnItemSelectedListener(this.onDisplayPatternSelected);
        brightness = view.findViewById(R.id.seekBarBrightness);
        speed = view.findViewById(R.id.seekBarSpeed);

        parameterNames = new TextView[] {
                view.findViewById(R.id.txtParam1),
                view.findViewById(R.id.txtParam2),
                view.findViewById(R.id.txtParam3),
                view.findViewById(R.id.txtParam4),
                view.findViewById(R.id.txtParam5),
                view.findViewById(R.id.txtParam6)
        };

        parameterValues = new SeekBar[] {
                view.findViewById(R.id.seekBarParam1),
                view.findViewById(R.id.seekBarParam2),
                view.findViewById(R.id.seekBarParam3),
                view.findViewById(R.id.seekBarParam4),
                view.findViewById(R.id.seekBarParam5),
                view.findViewById(R.id.seekBarParam6)
        };

        testRow = TableRowFragment.newInstance("Dummy Param1");
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.configTable, testRow);
        ft.commit();
    }

    private void refreshDisplay() {
        if (statusText == null) {
            // We haven't been initialized yet.
            return;
        }

        if (connectedDevice == null) {
            statusText.setText("Not connected.");
            btnReload.setEnabled(false);
            btnUpdate.setEnabled(false);
            displayPatternList.setEnabled(false);
            colorPatternList.setEnabled(false);
            brightness.setEnabled(false);
            speed.setEnabled(false);
            clearParameterList();
            testRow.setEnabled(false);
        } else {
            statusText.setText("Connected to: " + connectedDevice.getName());
            btnReload.setEnabled(true);
            btnUpdate.setEnabled(true);
            displayPatternList.setEnabled(true);
            colorPatternList.setEnabled(true);
            brightness.setEnabled(true);
            speed.setEnabled(true);
            testRow.setEnabled(true);

            // Populate dropdowns
            List<ColorPatternOptionData> colorOptions = connectedDevice.getPatternOptionData().getColorPatternOptions();
            colorPatternListAdapter.clear();
            colorPatternListAdapter.addAll(colorOptions);
            List<DisplayPatternOptionData> displayOptions = connectedDevice.getPatternOptionData().getDisplayPatternOptions();
            displayPatternListAdapter.clear();
            displayPatternListAdapter.addAll(displayOptions);
        }
    }

    private void clearParameterList() {
        for (TextView parameterName:parameterNames) {
            parameterName.setVisibility(View.GONE);
        }

        for (SeekBar parameterValue:parameterValues) {
            parameterValue.setVisibility(View.GONE);
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
            parameterNames[paramNumber].setVisibility(View.VISIBLE);
            parameterValues[paramNumber].setVisibility(View.VISIBLE);
            parameterNames[paramNumber].setText(parameterName);
            paramNumber++;
        }

        DisplayPatternOptionData displayData = (DisplayPatternOptionData)displayPatternList.getSelectedItem();
        for (String parameterName:displayData.getParameterNames()) {
            parameterNames[paramNumber].setVisibility(View.VISIBLE);
            parameterValues[paramNumber].setVisibility(View.VISIBLE);
            parameterNames[paramNumber].setText(parameterName);
            paramNumber++;
        }
    }

    private void onReload(View v) {
        viewModel.reloadConfiguration(connectedDevice);
    }

    private void onUpdate(View v) {
        viewModel.updateConfiguration(connectedDevice);
    }

    private AdapterView.OnItemSelectedListener onColorPatternSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
}