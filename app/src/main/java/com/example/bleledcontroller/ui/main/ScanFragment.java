package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bleledcontroller.R;
import com.example.bleledcontroller.ViewModel;
import com.example.bleledcontroller.bluetooth.DiscoveredDevice;
import com.example.bleledcontroller.views.ScanView;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

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
    private ArrayAdapter<DiscoveredDevice> discoveredDeviceListAdapter;
    private ListView deviceList = null;

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
    public void addDiscoveredDevice(DiscoveredDevice device) {
        discoveredDeviceListAdapter.add(device);
    }

    public void resetToInitialState() {
        discoveredDeviceListAdapter.clear();
        scanButton.setVisibility(View.VISIBLE);
        stopScanButton.setVisibility(View.GONE);
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
        connectionStatus.setText("Press SCAN to look for connections.");
        discoveredDevicesText.setVisibility(View.GONE);
    }

    private void initialize(View view) {
        scanButton = view.findViewById(R.id.scan);
        stopScanButton = view.findViewById(R.id.stopScan);
        connectButton = view.findViewById(R.id.connect);
        disconnectButton = view.findViewById(R.id.disconnect);
        connectionStatus = view.findViewById(R.id.connectionStatus);
        scanButton.setOnClickListener(this::startScan);
        stopScanButton.setOnClickListener(this::stopScan);
        discoveredDevicesText = view.findViewById(R.id.discoveredDevicesText);
        deviceList = view.findViewById(R.id.deviceList);
        discoveredDeviceListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice);
        deviceList.setAdapter(discoveredDeviceListAdapter);
    }

    private void startScan(View v) {
        // Clear any prior selection, since they carry over when re-scanning
        deviceList.setItemChecked(-1, true);
        discoveredDeviceListAdapter.clear();
        scanButton.setVisibility(View.GONE);
        stopScanButton.setVisibility(View.VISIBLE);
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
        connectionStatus.setText("Scanning...");
        discoveredDevicesText.setVisibility(View.VISIBLE);
        viewModel.beginScan();
    }

    private void stopScan(View v) {
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
}