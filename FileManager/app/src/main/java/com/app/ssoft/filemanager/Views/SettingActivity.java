package com.app.ssoft.filemanager.Views;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;

public class SettingActivity extends AppCompatActivity {

    private Switch appLockSwitch;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeface =Typeface.createFromAsset(getAssets(),  "fonts/RobotoCondensed-Light.ttf");

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE);
        appLockSwitch = findViewById(R.id.appLockSwitch);
        appLockSwitch.setTypeface(typeface);
        appLockSwitch.setChecked(sharedPrefs.getBoolean(Constants.is_locked_enabled, false));
        appLockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appLockSwitch.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.is_locked_enabled, true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.is_locked_enabled, false);
                    editor.commit();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
