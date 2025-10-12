package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView dp = view.findViewById(R.id.txtDisplayPattern);
        dp.setOnEditorActionListener(this::setByteBounds);

        TableRowFragment.setValueDisplay(true);
        TableRowFragment t1 = TableRowFragment.newInstance("Parameter 1");
        TableRowFragment t2 = TableRowFragment.newInstance("Parameter 2");
        // Dynamically add the parameter sliders
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.advConfigTable, t1);
        ft.add(R.id.advConfigTable, t2);
        ft.commit();
    }

    private void refreshDisplay() {

    }

    private boolean setByteBounds(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        Integer value = Integer.parseInt(textView.getText().toString());
        if (value < 0) {
            value = 0;
        }
        if (value > 255) {
            value = 255;
        }
        textView.setText(String.valueOf(value));
        return handled;
    }
}