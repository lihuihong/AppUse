package com.example.testdemo.app;

import android.app.Application;
import android.content.Intent;

import com.example.testdemo.server.AppService;
import com.example.testdemo.utils.ApplicationInfoUtil;

/**
 * Created by 那个谁 on 2018/3/8.
 * 奥特曼打小怪兽
 * 作用：基类
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));
        ApplicationInfoUtil.init();
    }

}
