package com.app.ssoft.filemanager.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by shekharshrivastava on 26/01/18.
 */

public  class Constants {

    public static String AppVersion;


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
