package com.pittsfordpanthers.ledcontrollerv2.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.pittsfordpanthers.ledcontrollerv2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableRowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableRowFragment extends Fragment {
    public interface OnValueChangeListener {
        public void onValueChanged(TableRowFragment tableRowFragment, int i, boolean b);
    }

    private OnValueChangeListener onValueChangeListener = null;
    private static final String ARG_PARAMNAME = "paramName";
    private static boolean showValue = false;
    private boolean isInitialized = false;
    private String parameterName;
    private TextView txtParameterName;
    private TextView txtParameterValue;
    private SeekBar sbParameterValue;
    private TableRow rowContainer;
    private boolean enabled = true;
    private int visibility = View.VISIBLE;
    private int lastSetValue = 0;

    public static void setValueDisplay(boolean showValue) {
        TableRowFragment.showValue = showValue;
    }

    public TableRowFragment() {
        // Required empty public constructor
    }

    public static TableRowFragment newInstance(String parameterName) {
        TableRowFragment fragment = new TableRowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAMNAME, parameterName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parameterName = getArguments().getString(ARG_PARAMNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_row, container, false);
        rowContainer = view.findViewById(R.id.tableRow);
        txtParameterName = view.findViewById(R.id.txtParameterName);
        sbParameterValue = view.findViewById(R.id.seekBarParameterValue);
        sbParameterValue.setOnSeekBarChangeListener(createSeekBarChangeListener());
        txtParameterValue = view.findViewById(R.id.txtValue);
        txtParameterValue.setOnEditorActionListener(this::editorActionListener);
        isInitialized = true;
        refresh();
        return view;
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
        refresh();
    }

    public int getParameterValue() {
        return sbParameterValue.getProgress();
    }

    public void setParameterValue(byte value) {
        int intValue = Byte.toUnsignedInt(value);
        setParameterValue(intValue);
    }

    public void setParameterValue(int value) {
        lastSetValue = value;
        refresh();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        refresh();
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        refresh();
    }

    private void refresh() {
        if (!isInitialized) {
            // We haven't initialized yet.
            return;
        }

        if (TableRowFragment.showValue) {
            txtParameterValue.setVisibility(View.VISIBLE);
        } else {
            txtParameterValue.setVisibility(View.GONE);
        }

        String expectedName = parameterName + ":";
        if (!txtParameterName.getText().equals(expectedName)) {
            txtParameterName.setText(expectedName);
        }

        sbParameterValue.setEnabled(enabled);
        txtParameterValue.setEnabled(enabled);
        rowContainer.setVisibility(visibility);
        sbParameterValue.setProgress(lastSetValue);
        txtParameterValue.clearFocus();
    }

    private TableRowFragment getOwningRowFragment() {
        return this;
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lastSetValue = i;
                txtParameterValue.setText(String.valueOf(i));
                if (onValueChangeListener != null) {
                    onValueChangeListener.onValueChanged(getOwningRowFragment(), i, b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    private boolean editorActionListener(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        Integer value = Integer.parseInt(textView.getText().toString());
        if (value < 0) {
            value = 0;
        }
        if (value > 255) {
            value = 255;
        }
        setParameterValue(value);
        return handled;
    }
}