package com.example.testdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 那个谁 on 2018/5/2.
 * 奥特曼打小怪兽
 * 作用：
 */

public class PrefUtils {

    public static String getString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("st_name",Context.MODE_PRIVATE);
        return sp.getString(key,value);
    }
    public static void setString(Context context,String key,String value){
        SharedPreferences sp = context.getSharedPreferences("st_name",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
}
