package com.example.bleledcontroller;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.bleledcontroller.ui.main.SectionsPagerAdapter;
import com.example.bleledcontroller.databinding.ActivityTabDemoBinding;

public class TabDemoActivity extends AppCompatActivity {

    private ActivityTabDemoBinding binding;
    private ViewPager pager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_demo);

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setText("Scan");
        TabLayout.Tab tab2 = tabLayout.newTab();
        tab2.setText("Configure");
        TabLayout.Tab tab3 = tabLayout.newTab();
        tab3.setText("Debug");
        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);
    }
}