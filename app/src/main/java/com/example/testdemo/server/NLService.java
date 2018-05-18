package com.example.testdemo.server;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

/**
 * Created by 那个谁 on 2018/3/20.
 * 奥特曼打小怪兽
 * 作用：通知栏服务
 */

public class NLService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String name = sbn.getPackageName();

        //判断本应用状态栏 需要动态获取
        if (name.equals("com.example.testdemo")){
            return;
        }else {
            cancelAllNotifications();
            Toast.makeText(this, "onNotificationPosted: 移除成功"+name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
}