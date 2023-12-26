package com.example.bleledcontroller;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private final ViewModel viewModel = new ViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 pager = findViewById(R.id.pager);

        configureTabs(tabLayout, pager);
        configurePager(pager, tabLayout, viewModel.getFragmentStateAdapter(getSupportFragmentManager(), getLifecycle()));

        viewModel.logMessage("Initialized.");
    }

    private void configureTabs(TabLayout tabLayout, ViewPager2 pager) {
        tabLayout.addTab(tabLayout.newTab().setText("Connection"));
        tabLayout.addTab(tabLayout.newTab().setText("Configure"));
        tabLayout.addTab(tabLayout.newTab().setText("Debug"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void configurePager(ViewPager2 pager, TabLayout tabLayout, FragmentStateAdapter adapter) {
        pager.setAdapter(adapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}