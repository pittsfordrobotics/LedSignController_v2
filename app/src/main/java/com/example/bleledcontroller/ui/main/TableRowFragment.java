package com.example.bleledcontroller.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bleledcontroller.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableRowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableRowFragment extends Fragment {

    private static final String ARG_PARAMNAME = "paramName";
    private String parameterName;
    private SeekBar parameterValue;
    private boolean enabled;

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
        TextView parameterTextView = view.findViewById(R.id.txtParameterName);
        parameterTextView.setText(parameterName + ": ");
        parameterValue = view.findViewById(R.id.seekBarParameterValue);
        refresh();
        return view;
    }

    public int getParameterValue() {
        return parameterValue.getProgress();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        refresh();
    }

    private void refresh() {
        if (parameterValue == null) {
            // We haven't initialized yet.
            return;
        }

        parameterValue.setEnabled(enabled);
    }
}