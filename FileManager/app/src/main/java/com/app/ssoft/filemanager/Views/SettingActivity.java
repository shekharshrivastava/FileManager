package com.app.ssoft.filemanager.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;

public class SettingActivity extends AppCompatActivity {

    private Switch appLockSwitch;
    private Typeface typeface;
    private TextView tvChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE);
        final SharedPreferences pwdSharedPrefs = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE);
        appLockSwitch = findViewById(R.id.appLockSwitch);
        tvChangePwd = findViewById(R.id.tvChangePwd);
        appLockSwitch.setTypeface(typeface);
        tvChangePwd.setTypeface(typeface);
        appLockSwitch.setChecked(sharedPrefs.getBoolean(Constants.is_locked_enabled, false));
        appLockSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appLockSwitch.isChecked()) {
                    if (pwdSharedPrefs.getString(Constants.password_input, null) != null) {
                        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.is_locked_enabled, true);
                        editor.commit();
                    } else {
// get prompts.xml view
                        LayoutInflater li = LayoutInflater.from(SettingActivity.this);
                        View promptsView = li.inflate(R.layout.set_pwd_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                SettingActivity.this);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userNewPassword = (EditText) promptsView
                                .findViewById(R.id.etNewPwd);
                        final EditText userConfirmPassword = (EditText) promptsView
                                .findViewById(R.id.etCnfrmPwd);
                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // get user input and set it to result
                                                // edit text
                                                if (userNewPassword.getText().toString().length() == 4) {
                                                    if (userNewPassword.getText().toString().equals(userConfirmPassword.getText().toString())) {
                                                        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE).edit();
                                                        editor.putString(Constants.password_input, userNewPassword.getText().toString());
                                                        editor.commit();

                                                        SharedPreferences.Editor prefLockEditor = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE).edit();
                                                        prefLockEditor.putBoolean(Constants.is_locked_enabled, true);
                                                        prefLockEditor.commit();
                                                    } else {
                                                        appLockSwitch.setChecked(false);
                                                        Toast.makeText(SettingActivity.this, "Password does'nt match", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    appLockSwitch.setChecked(false);
                                                    Toast.makeText(SettingActivity.this, "Password must be of 4 numbers", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                appLockSwitch.setChecked(false);
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.is_locked_enabled, false);
                    editor.commit();
                }
            }
        });

        tvChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
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
