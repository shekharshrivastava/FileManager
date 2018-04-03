
package com.app.ssoft.filemanager.Views;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;

public class ChangePasswordActivity extends AppCompatActivity {

    private SharedPreferences pwdSharedPrefs;
    private EditText etOldPwd,etNewPwd,etCnfrmPwd;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd = findViewById(R.id.etNewPwd);
        etCnfrmPwd = findViewById(R.id.etCnfrmPwd);
        btnSave = findViewById(R.id.btnSave);
        pwdSharedPrefs = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etOldPwd.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etNewPwd.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etCnfrmPwd.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Enter confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwdSharedPrefs.getString(Constants.password_input, null) != null) {
                    if (etOldPwd.getText().toString().equals(pwdSharedPrefs.getString(Constants.password_input, null))) {
                        if (etNewPwd.getText().toString().equals(etCnfrmPwd.getText().toString())) {
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE).edit();
                            editor.putString(Constants.password_input, etNewPwd.getText().toString());
                            editor.commit();
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Password does'nt match", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ChangePasswordActivity.this, "Incorrect old password", Toast.LENGTH_SHORT).show();
                    }

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
