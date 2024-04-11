package com.easyengineering;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.easyengineering.Adapters.SectionpagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class LoginRegisterActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginregister_activity);

        ViewPager viewPager = findViewById(R.id.viewpager);
        SectionpagerAdapter sectionpagerAdapter = new SectionpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionpagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Set different text colors for the login and register tabs
        tabLayout.setTabTextColors(getResources().getColor(R.color.loginTabTextColor), getResources().getColor(R.color.registerTabTextColor));

        tabLayout.getTabAt(0).setCustomView(createTabView("login", R.color.loginTabBackgroundColor));
        tabLayout.getTabAt(1).setCustomView(createTabView("register", R.color.registerTabBackgroundColor));

    }

    private TextView createTabView(String title, int backgroundColor) {
        TextView tabView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabView.setText(title);
        tabView.setBackgroundColor(getResources().getColor(backgroundColor));
        return tabView;
    }
}
