package com.example.bleledcontroller;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bleledcontroller.bluetooth.DiscoveredDevice;
import com.example.bleledcontroller.ui.main.ConfigurationFragment;
import com.example.bleledcontroller.ui.main.DebugLogFragment;
import com.example.bleledcontroller.ui.main.ScanFragment;
import com.example.bleledcontroller.views.ConfigurationView;
import com.example.bleledcontroller.views.DebugView;
import com.example.bleledcontroller.views.ScanView;

public class ViewModel {
    private DebugView debugView = null;
    private ScanView scanView = null;
    private ConfigurationView configurationView = null;

    private Fragment[] uiFragments = new Fragment[] {};

    public ViewModel() {
        init(DebugLogFragment.newInstance(this), ScanFragment.newInstance(this), ConfigurationFragment.newInstance());
    }

    public ViewModel(DebugView debugView, ScanView scanView, ConfigurationView configurationView) {
        init(debugView, scanView, configurationView);
    }

    private void init(DebugView debugView, ScanView scanView, ConfigurationView configurationView) {
        this.debugView = debugView;
        this.scanView = scanView;
        this.configurationView = configurationView;

        uiFragments = new Fragment[] {
                scanView,
                configurationView,
                debugView
        };
    }

    private int dummyDeviceNumber = 1;

    public void beginScan() {
        logMessage("Starting scan.");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            private String name = "Dummy " + dummyDeviceNumber++;

            @Override
            public void run() {
                scanView.addDiscoveredDevice(new DiscoveredDevice() {

                    @Override
                    public String getName() {
                        return name;
                    }
                });
            }
        }, 1000);

        handler.postDelayed(new Runnable() {
            private String name = "Dummy " + dummyDeviceNumber++;

            @Override
            public void run() {
                scanView.addDiscoveredDevice(new DiscoveredDevice() {

                    @Override
                    public String getName() {
                        return name;
                    }
                });
            }
        }, 2000);

    }

    public void stopScan() {
        logMessage("Scan stopped.");
    }

    public void connect(DiscoveredDevice device)
    {
        logMessage("Pretending to connect to device: " + device.getName());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanView.setConnectedState(device);
            }
        }, 1000);
    }

    public void disconnect() {
        logMessage("Disconnecting.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanView.setDisconnectedState();
            }
        }, 1000);
    }

    public void logMessage(String message) {
        debugView.addText(message);
    }

    public FragmentStateAdapter getFragmentStateAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        return new FragmentStateAdapter(fragmentManager, lifecycle) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Check position for range
                return uiFragments[position];
            }

            @Override
            public int getItemCount() {
                return uiFragments.length;
            }
        };
    }
}
