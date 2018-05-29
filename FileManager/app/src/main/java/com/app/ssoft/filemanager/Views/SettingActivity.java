package com.app.ssoft.filemanager.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SettingActivity extends AppCompatActivity {

    private Switch appLockSwitch;
    private Typeface typeface;
    private TextView tvChangePwd;
    private TextView update_check;
    private String currentVersion;

    private ProgressBar loading_indicator;
    private ImageView icon_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_LOCK_MODE, MODE_PRIVATE);
        final SharedPreferences pwdSharedPrefs = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE);
        appLockSwitch = findViewById(R.id.appLockSwitch);
        tvChangePwd = findViewById(R.id.tvChangePwd);
        update_check = findViewById(R.id.update_check);
        icon_image = findViewById(R.id.icon_image);
        loading_indicator = findViewById(R.id.loading_indicator);
        update_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetVersionCode().execute();
//                getPlayStoreAppVersion("https://play.google.com/store/apps/details?id=" + SettingActivity.this.getPackageName() + "&hl=it");
            }
        });
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
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(SettingActivity.this, R.style.myDialogTheme));


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
                                                } else {
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
                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

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

    private class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            return getPlayStoreAppVersion();

            /*} catch (Exception e) {

                return newVersion;

            }*/

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            icon_image.setVisibility(View.INVISIBLE);
            loading_indicator.setVisibility(View.VISIBLE);

        }

        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);
            icon_image.setVisibility(View.VISIBLE);
            loading_indicator.setVisibility(View.INVISIBLE);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (onlineVersion.equalsIgnoreCase(currentVersion)) {

                    Toast.makeText(SettingActivity.this, "You are using the latest version", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialogUpdate = new AlertDialog.Builder(new ContextThemeWrapper(SettingActivity.this, R.style.myDialogTheme));
              /*  final AlertDialog.Builder alertDialogDelete = new AlertDialog.Builder(
                        InternalExplorerActivity.this);*/
                    alertDialogUpdate.setTitle("New version available");
                    alertDialogUpdate.setMessage("New version of Secure Explorer "+onlineVersion+" is available do you want to update ?");
                    alertDialogUpdate.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                            }


                    });
                    alertDialogUpdate.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogUpdate.show();


                }

            }else{
                Toast.makeText(SettingActivity.this, "Error occurred while checking for updates please check your network connection", Toast.LENGTH_LONG).show();

            }

            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

        }
    }



    private String getPlayStoreAppVersion() {
        String latestVersion = null;
        try {

            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en").timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get();
            if (document != null) {
                if (document.select(".hAyfc .htlgb").get(5) != null) {
                    latestVersion = document.select(".hAyfc .htlgb").get(5).ownText();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return latestVersion;
    }
}
