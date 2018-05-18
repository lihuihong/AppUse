package com.example.testdemo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by 那个谁 on 2018/3/20.
 * 奥特曼打小怪兽
 * 作用：通知权限
 */

public class CommonUtils {

    public static boolean checkNotificationReadPermission(Activity activity) {
        String notiStr = Settings.Secure.getString(activity.getContentResolver(), "enabled_notification_listeners");
        if (notiStr != null && !TextUtils.isEmpty(notiStr)) {
            final String[] names = notiStr.split(":");
            for (String name : names) {
                ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (activity.getPackageName().equals(cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
