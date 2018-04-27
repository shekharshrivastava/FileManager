package com.app.ssoft.filemanager.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by shekharshrivastava on 26/01/18.
 */

public  class Constants {

    public static String AppVersion;

    //Sharepreference value
    public static String SHARED_PREF_LOCK_MODE = "app_lock_mode";
    public static String is_locked_enabled = "lockEnabled";
    public static String is_folder_locked_enabled = "is_folder_Enabled";
    public static String SHARED_PREF_SET_PASSWORD = "set_password";
    public static String SHARED_PREF_SET_APP_TOUR_ = "set_app_tour";
    public static String password_input = "input_password";
    public static String SHARED_PREF_FOLDER_LOCK_MODE = "app_folder_lock_mode";
    public static String is_home_app_tour_completed = "home_app_tour_completed";
    public static String is_internal_app_tour_completed = "internal_app_tour_completed";




    public static String getVersionName (Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return AppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  AppVersion;
    }

}
