package com.example.bleledcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final int RUNTIME_PERMISSION_REQUEST_CODE = 1;

    private TextView txtStatus = null;
    private NanoConnector connector = null;
    private Spinner stylePicker = null;
    private Spinner patternPicker = null;
    private SeekBar brightnessBar = null;
    private SeekBar speedBar = null;
    private SeekBar stepBar = null;
    private Button[] preferenceButtons = null;
    private boolean showDebug = false;

    //
    // Main entry point
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Bind the common UI elements
            txtStatus = findViewById(R.id.txtStatus);
            brightnessBar = findViewById(R.id.seekBarBrightness);
            speedBar = findViewById(R.id.seekBarSpeed);
            stepBar = findViewById(R.id.seekBarStep);
            stylePicker = findViewById(R.id.spStyle);
            patternPicker = findViewById(R.id.spPattern);
            preferenceButtons = new Button[] {
                    findViewById(R.id.btnPreset1),
                    findViewById(R.id.btnPreset2),
                    findViewById(R.id.btnPreset3),
                    findViewById(R.id.btnPreset4)
            };

            // Disable UI elements by default
            setUIEnabledState(false);

            // Bind any initial event handlers
            Button showDebugButton = findViewById(R.id.btnShowHideDebug);
            showDebugButton.setOnClickListener(showHideDebugListener);
            Button refreshVoltage = findViewById(R.id.btnRefreshVoltage);
            refreshVoltage.setOnClickListener(beginReadVoltage);

            for (int i = 0; i < preferenceButtons.length; i++) {
                preferenceButtons[i].setOnClickListener(readPreference(i));
                preferenceButtons[i].setOnLongClickListener(writePreference(i));
            }

            // Set the initial UI state
            txtStatus.setText("");
            showStatus("Initializing");
            showDebug = false;
            updateDebugStateInUI();

            // Request permissions if needed
            if (!hasRequiredRuntimePermissions()) {
                requestRelevantRuntimePermissions();
            } else {
                Toast.makeText(this, "Permissions already granted.", Toast.LENGTH_SHORT).show();
            }

            // Start the BLE connection
            connector = createConnector();
            connector.connect();
        } catch (Exception e) {
            txtStatus.setText(e.toString());
        }
    }

    private void updateDebugStateInUI() {
        // Update the UI to reflect the current state of the 'showDebug' flag
        ScrollView scrollView = findViewById(R.id.scrollview);
        scrollView.setVisibility(showDebug ? View.VISIBLE : View.GONE);
        Button showDebugButton = findViewById(R.id.btnShowHideDebug);
        showDebugButton.setText(showDebug ? "Hide Debug Info" : "Show Debug Info");
    }

    private NanoConnector createConnector() {
        Runnable onConnected = this::onConnected;
        Runnable onDisconnected = this::onDisconnected;

        NanoConnectorCallback callback = new NanoConnectorCallback() {
            @Override
            public void acceptStatus(String status) {
                runOnUiThread(() -> {
                    showStatus(status);
                });
            }

            @Override
            public void connected() {
                runOnUiThread(onConnected);
            }

            @Override
            public void disconnected() {
                runOnUiThread(onDisconnected);
            }

            @Override
            public void acceptBatteryVoltage(float voltage) {
                runOnUiThread(() -> {
                    String message = "Battery: " + String.format("%.2f", voltage) + "v";
                    TextView txtVoltage = findViewById(R.id.txtBatteryVoltage);
                    txtVoltage.setText(message);
                });
            }
        };

        showStatus("Starting scan");
        return new NanoConnector(this, callback);
    }

    private void onConnected() {
        TextView txt = findViewById(R.id.txtConnectStatus);
        txt.setText("Connected");

        // Populate UI with current values
        ArrayAdapter<String> styleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, connector.getKnownStyles());
        stylePicker.setAdapter(styleAdapter);
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, connector.getKnownPatterns());
        patternPicker.setAdapter(patternAdapter);
        brightnessBar.setProgress(connector.getInitialBrightness());
        stylePicker.setSelection(connector.getInitialStyle());
        speedBar.setProgress(connector.getInitialSpeed());
        stepBar.setProgress(connector.getInitialStep());
        patternPicker.setSelection(connector.getInitialPattern());

        // Enable updates
        brightnessBar.setOnSeekBarChangeListener(createGenericSeekBarListener("brightness", connector::setBrightness));
        stylePicker.setOnItemSelectedListener(createGenericPickListener("style", connector::setStyle));
        speedBar.setOnSeekBarChangeListener(createGenericSeekBarListener("speed", connector::setSpeed));
        stepBar.setOnSeekBarChangeListener(createGenericSeekBarListener("step", connector::setStep));
        patternPicker.setOnItemSelectedListener(createGenericPickListener("pattern", connector::setPattern));
        setUIEnabledState(true);
    }

    private void onDisconnected() {
        showStatus("Disconnected.");
        TextView txt = findViewById(R.id.txtConnectStatus);
        txt.setText("Disconnected");

        setUIEnabledState(false);
    }

    private void showStatus(String status) {
        String newText = txtStatus.getText().toString() + '\n' + status;
        txtStatus.setText(newText);
    }

    private void setUIEnabledState(boolean enabled) {
        brightnessBar.setEnabled(enabled);
        stylePicker.setEnabled(enabled);
        speedBar.setEnabled(enabled);
        stepBar.setEnabled(enabled);
        patternPicker.setEnabled(enabled);

        for (Button b : preferenceButtons) {
            b.setEnabled(enabled);
        }
    }

    //
    // Permission handling helpers
    //
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasRequiredRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestLocationPermission();
        } else {
            requestBluetoothPermissions();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                RUNTIME_PERMISSION_REQUEST_CODE
        );
    }

    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                },
                RUNTIME_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if ((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showStatus("Permission granted.");
            } else {
                showStatus("Permission denied.");
            }
        }
    }

    //
    // Various event handlers
    //
    private AdapterView.OnItemSelectedListener createGenericPickListener(String pickerName, Consumer<Integer> methodToInvoke) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String item = (String) adapterView.getItemAtPosition(i);
                    showStatus("Selected " + pickerName + ": " + i + "(" + item + ")");
                    methodToInvoke.accept(i);
                }
                catch (Exception e) {
                    showStatus("Error: " + e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }

    private SeekBar.OnSeekBarChangeListener createGenericSeekBarListener(String seekbarName, Consumer<Integer> methodToInvoke) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (!fromUser) {
                    // Only emit the value if it was set programmatically.
                    // If the user was changing the value, it will be handled by 'onStopTrackingTouch'.
                    emitValue(seekBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                emitValue(seekBar);
            }

            private void emitValue(SeekBar seekBar) {
                try {
                    int value = seekBar.getProgress();
                    showStatus("Setting " + seekbarName + " to " + value);
                    methodToInvoke.accept(value);
                }
                catch (Exception e) {
                    showStatus("Error: " + e);
                }
            }
        };
    }

    private View.OnClickListener showHideDebugListener = view -> {
        showDebug = !showDebug;
        updateDebugStateInUI();
    };

    private View.OnClickListener beginReadVoltage = view -> {
        connector.refreshVoltage();
    };

    private View.OnClickListener readPreference(int buttonNumber) {
        return view -> {
            int style = getPreferenceIntValue("Pref_Style" + buttonNumber);
            int pattern = getPreferenceIntValue("Pref_Pattern" + buttonNumber);
            int speed = getPreferenceIntValue("Pref_Speed" + buttonNumber);
            int brightness = getPreferenceIntValue("Pref_Brightness" + buttonNumber);
            int step = getPreferenceIntValue("Pref_Step" + buttonNumber);

            if (style < 0 || pattern < 0 || speed < 0 || brightness < 0 || step < 0) {
                showStatus("At least one preference value could not be read for button " + (buttonNumber + 1));
                return;
            }

            stylePicker.setSelection(style);
            patternPicker.setSelection(pattern);
            brightnessBar.setProgress(brightness);
            speedBar.setProgress(speed);
            stepBar.setProgress(step);
        };
    }

    private int getPreferenceIntValue(String prefName) {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        if (!pref.contains(prefName)) {
            return -1;
        }

        return pref.getInt(prefName, -1);
    }

    private View.OnLongClickListener writePreference(int buttonNumber) {
        return view -> {
            SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("Pref_Style" + buttonNumber, stylePicker.getSelectedItemPosition());
            editor.putInt("Pref_Pattern" + buttonNumber, patternPicker.getSelectedItemPosition());
            editor.putInt("Pref_Speed" + buttonNumber, speedBar.getProgress());
            editor.putInt("Pref_Brightness" + buttonNumber, brightnessBar.getProgress());
            editor.putInt("Pref_Step" + buttonNumber, stepBar.getProgress());
            editor.apply();
            Toast.makeText(this, "Values set for preset number " + (buttonNumber + 1) + ".", Toast.LENGTH_SHORT).show();
            return true;
        };
    }
}