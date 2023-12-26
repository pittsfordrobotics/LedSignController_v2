package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            // hide / mark invisible other stuff...
        } else {
            statusText.setText("Connected to: " + connectedDevice.getName());
            btnReload.setEnabled(true);
            btnUpdate.setEnabled(true);
            // unhide other stuff...
        }
    }

    private void onReload(View v) {
        viewModel.reloadConfiguration(connectedDevice);
    }

    private void onUpdate(View v) {
        viewModel.updateConfiguration(connectedDevice);
    }
}