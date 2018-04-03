package com.app.ssoft.filemanager.Views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;
import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;


public class SplashActivity extends AppCompatActivity {

    private TextView versionCode;
    private Intent myintent;
    private PermissionManager permissionManager;
    private boolean isAppLocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        versionCode = findViewById(R.id.versionCode);
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE);
        isAppLocked = sharedPrefs.getBoolean(Constants.is_locked_enabled, false);
        versionCode.setText("Version - " + Constants.getVersionName(this));
        myintent = new Intent(this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                permissionManager = PermissionManager.getInstance(SplashActivity.this);
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                if (isAppLocked) {
                                    Intent intent = new Intent(SplashActivity.this, LockScreenActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    startActivity(myintent);
                                    finish();
                                }

                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(SplashActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                                if (isAppLocked) {
                                    Intent intent = new Intent(SplashActivity.this, LockScreenActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    startActivity(myintent);
                                    finish();
                                }
                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(SplashActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                        if (isAppLocked) {
                            Intent intent = new Intent(SplashActivity.this, LockScreenActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            startActivity(myintent);
                            finish();
                        }
                    }
                });


            }
        }, 1000);
    }


}
