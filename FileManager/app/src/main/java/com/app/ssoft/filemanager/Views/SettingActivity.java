package com.app.ssoft.filemanager.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.ssoft.filemanager.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
