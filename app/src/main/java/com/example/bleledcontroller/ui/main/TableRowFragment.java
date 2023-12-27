package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.bleledcontroller.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableRowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableRowFragment extends Fragment {

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

        sbParameterValue.setOnSeekBarChangeListener(seekBarChangeListener);
        txtParameterValue = view.findViewById(R.id.txtValue);
        isInitialized = true;
        refresh();
        return view;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
        refresh();
    }

    public int getParameterValue() {
        return sbParameterValue.getProgress();
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

        if (!TableRowFragment.showValue) {
            txtParameterValue.setVisibility(View.GONE);
        }

        String expectedName = parameterName + ":";
        if (!txtParameterName.getText().equals(expectedName)) {
            txtParameterName.setText(expectedName);
        }

        sbParameterValue.setEnabled(enabled);
        rowContainer.setVisibility(visibility);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            txtParameterValue.setText(String.valueOf(i));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
}