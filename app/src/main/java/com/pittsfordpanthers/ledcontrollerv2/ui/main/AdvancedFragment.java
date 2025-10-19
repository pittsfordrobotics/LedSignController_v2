package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pittsfordpanthers.ledcontrollerv2.R;
import com.pittsfordpanthers.ledcontrollerv2.ViewModel;
import com.pittsfordpanthers.ledcontrollerv2.bluetooth.ConnectedDevice;
import com.pittsfordpanthers.ledcontrollerv2.signdata.PatternData;
import com.pittsfordpanthers.ledcontrollerv2.views.AdvancedView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

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
    private View[] colorBars;
    private EditText[] colorValues;
    private TextView colorPatternView;
    private TextView displayPatternView;
    private TextView statusView;
    private Button reloadButton;
    private Button updateButton;

    private final InputFilter hexInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            StringBuilder sb = new StringBuilder();

            for (int i = start; i < end; i++) {
                if (Character.isDigit(source.charAt(i))
                        || source.charAt(i)=='A'
                        || source.charAt(i)=='B'
                        || source.charAt(i)=='C'
                        || source.charAt(i)=='D'
                        || source.charAt(i)=='E'
                        || source.charAt(i)=='F'
                        || source.charAt(i)=='a'
                        || source.charAt(i)=='b'
                        || source.charAt(i)=='c'
                        || source.charAt(i)=='d'
                        || source.charAt(i)=='e'
                        || source.charAt(i)=='f'
                ) {
                    sb.append(source.charAt(i));
                    if (sb.length() >= 6) {
                        break;
                    }
                }
            }
            return sb.toString().toUpperCase();
        }
    };

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
        connectedDevice = null;
        refreshDisplay();
    }

    private void initialize() {
        statusView = view.findViewById(R.id.connectionStatus);
        reloadButton = view.findViewById(R.id.btnReload);
        reloadButton.setOnClickListener(this::onReload);
        updateButton = view.findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(this::onUpdate);
        displayPatternView = view.findViewById(R.id.txtDisplayPattern);
        displayPatternView.setOnEditorActionListener(this::setByteBounds);
        colorPatternView = view.findViewById(R.id.txtColorPattern);
        colorPatternView.setOnEditorActionListener(this::setByteBounds);
        brightnessSlider = view.findViewById(R.id.brightness);
        speedSlider = view.findViewById(R.id.speed);

        colorBars = new View[] {
                view.findViewById(R.id.color1),
                view.findViewById(R.id.color2),
                view.findViewById(R.id.color3),
                view.findViewById(R.id.color4)
        };

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setTag(i);
            colorBars[i].setOnClickListener(this::onColorTapped);
        }

        colorValues = new EditText[] {
                view.findViewById(R.id.colorCode1),
                view.findViewById(R.id.colorCode2),
                view.findViewById(R.id.colorCode3),
                view.findViewById(R.id.colorCode4)
        };

        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i].setFilters(new InputFilter[]{hexInputFilter});
            colorValues[i].setTag(i);
            colorValues[i].setOnEditorActionListener(this::setColorValue);
            colorValues[i].setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

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
        disableAll();
    }

    private void setDisplayForConnectedDevice() {
        statusView.setText("Connected to: " + connectedDevice.getName());

        // Populate settings
        brightnessSlider.setValue(connectedDevice.getBrightness());
        speedSlider.setValue(connectedDevice.getSpeed());
        PatternData patternData = connectedDevice.getCurrentPatternData();
        colorPatternView.setText(String.valueOf(patternData.getColorPatternId()));
        displayPatternView.setText(String.valueOf(patternData.getDisplayPatternId()));

        for (int i = 0; i < parameterSliders.length; i++) {
            parameterSliders[i].setValue(patternData.getParameterValue(i));
        }

        for (int i = 0; i < colorBars.length; i++) {
            colorBars[i].setBackgroundColor(patternData.getColorValue(i));
            colorValues[i].setText(colorIntToString(patternData.getColorValue(i)));
        }

        // Enable UI elements
        reloadButton.setEnabled(true);
        updateButton.setEnabled(true);
        displayPatternView.setEnabled(true);
        colorPatternView.setEnabled(true);
        brightnessSlider.setEnabled(true);
        speedSlider.setEnabled(true);

        for (NumberSliderView p : parameterSliders) {
            p.setEnabled(true);
        }

        for (View v : colorBars) {
            v.setEnabled(true);
        }
    }

    private String colorIntToString(int color) {
        String colorString = Integer.toHexString(color);

        // String of the leading FF.
        // Sanity check - it should always be 8 characters long.
        if (colorString.length() == 8) {
            colorString = colorString.substring(2);
        } else {
            colorString = "0";
        }

        return colorString;
    }

    private void onColorTapped(View sourceColorBar) {
        int barId = (int)sourceColorBar.getTag();
        ColorDrawable drawable = (ColorDrawable) sourceColorBar.getBackground();
        int initialColor = drawable.getColor();

        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(getContext())
                .setTitle("Color " + (barId + 1))
                .setPositiveButton("OK",
                        new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                sourceColorBar.setBackgroundColor(envelope.getColor());
                                String colorString = colorIntToString(envelope.getColor());
                                colorValues[barId].setText(colorString);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .attachAlphaSlideBar(false) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(10); // set a bottom space between the last slidebar and buttons.

        ColorPickerView cpView = builder.getColorPickerView();
        cpView.setInitialColor(initialColor);
        builder.show();
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
        textView.clearFocus();
        return handled;
    }

    private boolean setColorValue(TextView textView, int i, KeyEvent keyEvent) {
        // Format the input string:
        //  - if it's less than 6 characters, pad zeros in front.
        //  - Add "FF" to the front to indicate the alpha value of the color.
        CharSequence inputValue = textView.getText();
        StringBuilder sb = new StringBuilder();
        sb.append("FF");
        for (int c = 6; c > inputValue.length(); c--) {
            sb.append("0");
        }

        sb.append(inputValue);

        int newColor = Integer.parseUnsignedInt(sb.toString(), 16);
        int barId = (int)textView.getTag();
        colorBars[barId].setBackgroundColor(newColor);
        textView.clearFocus();
        return false;
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

            for (int i = 0; i < colorBars.length; i++) {
                colorBars[i].setBackgroundColor(getPreferenceIntValue("Adv_Color" + buttonNumber + "_" + i));
            }
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

            for (int i = 0; i < colorBars.length; i++) {
                editor.putInt("Adv_Color" + buttonNumber + "_" + i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
            }

            editor.apply();
            Toast.makeText(getContext(), "Values set for preset number " + (buttonNumber + 1) + ".", Toast.LENGTH_SHORT).show();
            return true;
        };
    }

    private void disableAll() {
        reloadButton.setEnabled(false);
        updateButton.setEnabled(false);
        displayPatternView.setEnabled(false);
        colorPatternView.setEnabled(false);
        brightnessSlider.setEnabled(false);
        speedSlider.setEnabled(false);

        for (NumberSliderView p : parameterSliders) {
            p.setEnabled(false);
        }

        for (View v : colorBars) {
            v.setEnabled(false);
        }
    }

    private void onReload(View v) {
        statusView.setText("Refreshing device...");
        disableAll();
        viewModel.reloadConfiguration(connectedDevice);
    }

    private void onUpdate(View v) {
        statusView.setText("Updating device...");
        disableAll();
        connectedDevice.setBrightness((byte) brightnessSlider.getValue());
        connectedDevice.setSpeed((byte) speedSlider.getValue());
        PatternData patternData = new PatternData();
        patternData.setColorPatternId((byte) Integer.parseInt(colorPatternView.getText().toString()));
        patternData.setDisplayPatternId((byte) Integer.parseInt(displayPatternView.getText().toString()));
        for (int i = 0; i < colorBars.length; i++) {
            patternData.setColorValue(i, ((ColorDrawable)colorBars[i].getBackground()).getColor());
        }

        for (int i = 0; i < parameterSliders.length; i++) {
            patternData.setParameterValue(i, (byte) parameterSliders[i].getValue());
        }

        connectedDevice.setPatternData(patternData);
        viewModel.updateConfiguration(connectedDevice);
    }
}