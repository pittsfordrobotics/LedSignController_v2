package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bleledcontroller.R;
import com.example.bleledcontroller.ViewModel;
import com.example.bleledcontroller.bluetooth.BleDevice;
import com.example.bleledcontroller.bluetooth.ConnectedDevice;
import com.example.bleledcontroller.views.ScanView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends ScanView {

    private ViewModel viewModel = null;
    private TextView connectionStatus = null;
    private TextView discoveredDevicesText = null;
    private Button scanButton = null;
    private Button stopScanButton = null;
    private Button connectButton = null;
    private Button disconnectButton = null;
    private ArrayAdapter<BleDevice> discoveredDeviceListAdapter;
    private ListView deviceList = null;
    private boolean bluetoothEnabled = false;
    private ConnectedDevice currentDevice = null;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(ViewModel viewModel) {
        ScanFragment fragment = new ScanFragment();
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
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        initialize(view);
        resetToInitialState();
        return view;
    }

    @Override
    public void addDiscoveredDevice(BleDevice device) {
        discoveredDeviceListAdapter.add(device);
    }

    @Override
    public void setConnectedDevice(ConnectedDevice device) {
        currentDevice = device;
        getActivity().runOnUiThread(() -> {
            discoveredDeviceListAdapter.clear();
            scanButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.VISIBLE);
            connectionStatus.setText("Connected to: " + device.getName());
        });
    }

    @Override
    public void setConnectionFailed() {
        currentDevice = null;
        connectionStatus.setText("Connection failed!");
        scanButton.setEnabled(true);
        connectButton.setEnabled(true);
        deviceList.setEnabled(true);
    }

    @Override
    public void setDisconnectedState() {
        currentDevice = null;
        resetToInitialState();
    }

    @Override
    public void setBluetoothEnabled() {
        bluetoothEnabled = true;
        if (scanButton != null) {
            scanButton.setEnabled(true);
        }
    }

    public void resetToInitialState() {
        discoveredDeviceListAdapter.clear();
        scanButton.setEnabled(bluetoothEnabled);
        scanButton.setVisibility(View.VISIBLE);
        stopScanButton.setVisibility(View.GONE);
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
        connectionStatus.setText("Press SCAN to look for connections.");
        discoveredDevicesText.setVisibility(View.GONE);
        scanButton.setEnabled(true);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(true);
        deviceList.setEnabled(true);
    }

    private void initialize(View view) {
        scanButton = view.findViewById(R.id.scan);
        stopScanButton = view.findViewById(R.id.stopScan);
        connectButton = view.findViewById(R.id.connect);
        connectButton.setOnClickListener(this::onConnect);
        disconnectButton = view.findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(this::onDisconnect);
        connectionStatus = view.findViewById(R.id.connectionStatus);
        scanButton.setOnClickListener(this::onStartScan);
        stopScanButton.setOnClickListener(this::onStopScan);
        discoveredDevicesText = view.findViewById(R.id.discoveredDevicesText);
        deviceList = view.findViewById(R.id.deviceList);
        discoveredDeviceListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice);
        deviceList.setAdapter(discoveredDeviceListAdapter);
    }

    private void onStartScan(View v) {
        // Clear any prior selection, since they carry over when re-scanning
        deviceList.setItemChecked(-1, true);
        discoveredDeviceListAdapter.clear();
        scanButton.setVisibility(View.GONE);
        stopScanButton.setVisibility(View.VISIBLE);
        connectButton.setEnabled(true);
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
        connectionStatus.setText("Scanning...");
        discoveredDevicesText.setVisibility(View.VISIBLE);
        deviceList.setEnabled(true);
        viewModel.beginScan();
    }

    private void onStopScan(View v) {
        connectionStatus.setText("");
        stopScanButton.setVisibility(View.GONE);
        scanButton.setVisibility(View.VISIBLE);

        if (discoveredDeviceListAdapter.getCount() > 0) {
            connectionStatus.setText("Select a device to connect to.");
            connectButton.setVisibility(View.VISIBLE);
        } else {
            discoveredDevicesText.setVisibility(View.GONE);
        }

        viewModel.stopScan();
    }

    private void onConnect(View v) {
        int itemPosition = deviceList.getCheckedItemPosition();
        if (itemPosition < 0) {
            // Nothing selected.
            return;
        }

        connectionStatus.setText("Connecting to device...");
        discoveredDevicesText.setVisibility(View.GONE);
        scanButton.setEnabled(false);
        connectButton.setEnabled(false);
        deviceList.setEnabled(false);
        BleDevice device = (BleDevice) deviceList.getItemAtPosition(itemPosition);
        viewModel.connect(device);
    }

    private void onDisconnect(View v) {
        if (currentDevice != null) {
            connectionStatus.setText("Disconnecting from device...");
            disconnectButton.setEnabled(false);
            viewModel.disconnect(currentDevice);
        }
    }
}