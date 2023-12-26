package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private ViewModel viewModel = null;

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
        return inflater.inflate(R.layout.fragment_configuration, container, false);
    }

    @Override
    public void setConnectedDevice(ConnectedDevice device) {

    }

    @Override
    public void setDisconnectedState() {

    }
}