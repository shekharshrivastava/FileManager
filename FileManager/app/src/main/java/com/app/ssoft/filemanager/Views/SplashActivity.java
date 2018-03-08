package com.app.ssoft.filemanager.Views;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        versionCode = findViewById(R.id.versionCode);
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
                                startActivity(myintent);
                                finish();
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(SplashActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                                startActivity(myintent);
                                finish();
                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(SplashActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                        startActivity(myintent);
                        finish();
                    }
                });



            }
        }, 1000);
    }


}
