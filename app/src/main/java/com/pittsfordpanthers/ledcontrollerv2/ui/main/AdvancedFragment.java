package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pittsfordpanthers.ledcontrollerv2.R;
import com.pittsfordpanthers.ledcontrollerv2.ViewModel;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.signdata.ColorPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.signdata.DisplayPatternOptionData;
import com.pittsfordpanthers.ledcontrollerv2.views.AdvancedView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdvancedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvancedFragment extends AdvancedView {

    private ViewModel viewModel = null;
    private View view;
    private ConnectedDevice connectedDevice = null;

    private NumberSliderView[] parameterSliders;

    private NumberSliderView brightnessSlider;
    private NumberSliderView speedSlider;
    private TextView colorPatternView;
    private TextView displayPatternView;
    private TextView statusView;
    private Button reloadButton;
    private Button updateButton;

    public AdvancedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvancedFragment.
     */
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
        connectedDevice = device;
        refreshDisplay();
    }

    @Override
    public void setDisconnectedState() {

    }

    private void initialize() {
        statusView = view.findViewById(R.id.connectionStatus);
        reloadButton = view.findViewById(R.id.btnReload);
        updateButton = view.findViewById(R.id.btnUpdate);
        displayPatternView = view.findViewById(R.id.txtDisplayPattern);
        displayPatternView.setOnEditorActionListener(this::setByteBounds);
        colorPatternView = view.findViewById(R.id.txtColorPattern);
        colorPatternView.setOnEditorActionListener(this::setByteBounds);
        brightnessSlider = view.findViewById(R.id.brightness);
        speedSlider = view.findViewById(R.id.speed);

        parameterSliders = new NumberSliderView[] {
                view.findViewById(R.id.parameter1),
                view.findViewById(R.id.parameter2),
                view.findViewById(R.id.parameter3),
                view.findViewById(R.id.parameter4),
                view.findViewById(R.id.parameter5),
                view.findViewById(R.id.parameter6)
        };

        int[] presetIds = {
                R.id.preset1,
                R.id.preset2,
                R.id.preset3,
                R.id.preset4,
                R.id.preset5,
                R.id.preset6,
                R.id.preset7,
                R.id.preset8,
                R.id.preset9,
                R.id.preset10
        };

        for (int i = 0; i < presetIds.length; i++) {
            Button b = view.findViewById(presetIds[i]);
            b.setOnClickListener(readPreferenceListener(i));
            b.setOnLongClickListener(writePreferenceListener(i));
        }
    }

    private void refreshDisplay() {
        if (view == null) {
            // We haven't been initialized yet.
            return;
        }

        if (connectedDevice == null) {
            setDisplayForDisconnectedDevice();
        } else {
            setDisplayForConnectedDevice();
        }
    }

    private void setDisplayForDisconnectedDevice() {
        statusView.setText("Not connected.");
    }

    private void setDisplayForConnectedDevice() {
        statusView.setText("Connected to: " + connectedDevice.getName());
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

    private int getPreferenceIntValue(String prefName) {
        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
        if (!pref.contains(prefName)) {
            return -1;
        }

        return pref.getInt(prefName, -1);
    }

    private View.OnClickListener readPreferenceListener(int buttonNumber) {
        return view -> {
            int speed = getPreferenceIntValue("Adv_Speed" + buttonNumber);
            int brightness = getPreferenceIntValue("Adv_Brightness" + buttonNumber);
            int colorPattern = getPreferenceIntValue("Adv_ColorPattern" + buttonNumber);
            int displayPattern = getPreferenceIntValue("Adv_DisplayPattern" + buttonNumber);

            if (speed < 0 || brightness < 0 || colorPattern < 0 || displayPattern < 0) {
                viewModel.logMessage("At least one preference value could not be read for button " + (buttonNumber + 1));
                return;
            }

            brightnessSlider.setValue(brightness);
            speedSlider.setValue(speed);
            colorPatternView.setText(String.valueOf(colorPattern));
            displayPatternView.setText(String.valueOf(displayPattern));
            for (int i = 0; i < parameterSliders.length; i++) {
                parameterSliders[i].setValue(getPreferenceIntValue("Adv_Param" + buttonNumber + "_" + i));
            }
//            for (int i = 0; i < colorBars.length; i++) {
//                colorBars[i].setBackgroundColor(getPreferenceIntValue("Adv_Color" + buttonNumber + "_" + i));
//            }
        };
    }

    private View.OnLongClickListener writePreferenceListener(int buttonNumber) {
        return view -> {
            SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("Adv_Speed" + buttonNumber, speedSlider.getValue());
            editor.putInt("Adv_Brightness" + buttonNumber, brightnessSlider.getValue());
            editor.putInt("Adv_ColorPattern" + buttonNumber, Integer.parseInt(colorPatternView.getText().toString()));
            editor.putInt("Adv_DisplayPattern" + buttonNumber, Integer.parseInt(displayPatternView.getText().toString()));
            for (int i = 0; i < parameterSliders.length; i++) {
                editor.putInt("Adv_Param" + buttonNumber + "_" + i, parameterSliders[i].getValue());
            }
//            for (int i = 0; i < colorBars.length; i++) {
//                editor.putInt("Adv_Color" + buttonNumber + "_" + i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
//            }
            editor.apply();
            Toast.makeText(getContext(), "Values set for preset number " + (buttonNumber + 1) + ".", Toast.LENGTH_SHORT).show();
            return true;
        };
    }

}