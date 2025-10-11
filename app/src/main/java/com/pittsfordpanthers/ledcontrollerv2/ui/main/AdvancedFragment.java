package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pittsfordpanthers.ledcontrollerv2.R;
import com.pittsfordpanthers.ledcontrollerv2.ViewModel;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.views.AdvancedView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdvancedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvancedFragment extends AdvancedView {

    private ViewModel viewModel = null;
    private View view;

    public AdvancedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvancedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdvancedFragment newInstance(ViewModel viewModel) {
        AdvancedFragment fragment = new AdvancedFragment();
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
        view = inflater.inflate(R.layout.fragment_advanced, container, false);
        initialize();
        refreshDisplay();
        return view;
    }

    @Override
    public void setConnectedDevice(ConnectedDevice device) {

    }

    @Override
    public void setDisconnectedState() {

    }

    private void initialize() {

    }

    private void refreshDisplay() {

    }
}