package com.example.ledcontrollerv2.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ledcontrollerv2.R;
import com.example.ledcontrollerv2.ViewModel;
import com.example.ledcontrollerv2.views.DebugView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebugLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebugLogFragment extends DebugView {

    private ViewModel viewModel = null;
    private String buffer = "";

    private TextView debugText;

    public DebugLogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DebugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebugLogFragment newInstance(ViewModel viewModel) {
        DebugLogFragment fragment = new DebugLogFragment();
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
        View view = inflater.inflate(R.layout.fragment_debuglog, container, false);
        debugText = view.findViewById(R.id.debugText);

        // Add any buffered messages.
        debugText.setText(buffer);
        return view;
    }

    public void addText(String text) {
        if (debugText == null) {
            // We haven't initialized the UI yet, so just add it to the buffer.
            buffer += text + '\n';
        } else {
            getActivity().runOnUiThread(() -> {
                debugText.append(text + '\n');
            });
        }
    }
}