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
import com.example.bleledcontroller.views.ScanView;

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
    private Button scanButton = null;
    private Button stopScanButton = null;
    private Button connectButton = null;
    private Button disconnectButton = null;

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

    public void resetToInitialState() {
        scanButton.setVisibility(View.VISIBLE);
        stopScanButton.setVisibility(View.GONE);
        connectButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
        connectionStatus.setText("Press SCAN to begin scanning.");
    }

    private void initialize(View view) {
        scanButton = view.findViewById(R.id.scan);
        stopScanButton = view.findViewById(R.id.stopScan);
        connectButton = view.findViewById(R.id.connect);
        disconnectButton = view.findViewById(R.id.disconnect);
        connectionStatus = view.findViewById(R.id.connectionStatus);
        scanButton.setOnClickListener(this::startScan);
        stopScanButton.setOnClickListener(this::stopScan);
    }

    private void startScan(View v) {
        scanButton.setVisibility(View.GONE);
        stopScanButton.setVisibility(View.VISIBLE);
        connectionStatus.setText("Scanning...");
        viewModel.beginScan();
    }

    private void stopScan(View v) {
        // Just reset everything.
        resetToInitialState();
        viewModel.stopScan();
    }
}