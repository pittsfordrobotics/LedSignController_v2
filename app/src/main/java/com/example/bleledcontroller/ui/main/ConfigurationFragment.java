package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bleledcontroller.R;
import com.example.bleledcontroller.ViewModel;
import com.example.bleledcontroller.bluetooth.ConnectedDevice;
import com.example.bleledcontroller.views.ConfigurationView;

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
        colorPatternList.setOnItemSelectedListener(this.onColorPatternSelected);
        displayPatternList = view.findViewById(R.id.displayPattern);
        displayPatternList.setOnItemSelectedListener(this.onDisplayPatternSelected);

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

            for (TextView parameterName:parameterNames) {
                parameterName.setVisibility(View.GONE);
            }

            for (SeekBar parameterValue:parameterValues) {
                parameterValue.setVisibility(View.GONE);
            }
        } else {
            statusText.setText("Connected to: " + connectedDevice.getName());
            btnReload.setEnabled(true);
            btnUpdate.setEnabled(true);
            displayPatternList.setEnabled(true);
            colorPatternList.setEnabled(true);

            // unhide other stuff...
        }
    }

    private void onReload(View v) {
        viewModel.reloadConfiguration(connectedDevice);
    }

    private void onUpdate(View v) {
        viewModel.updateConfiguration(connectedDevice);
    }

    private AdapterView.OnItemSelectedListener onColorPatternSelected = new AdapterView.OnItemSelectedListener() {
        // Update the list of parameters
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private AdapterView.OnItemSelectedListener onDisplayPatternSelected = new AdapterView.OnItemSelectedListener() {
        // Update the list of parameters
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
}