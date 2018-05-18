package com.example.testdemo.server;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.testdemo.ProcessConnection;

/**
 * Created by 那个谁 on 2018/3/8.
 * 奥特曼打小怪兽
 * 作用：守护进程，双进程通信
 */

public class GuardService extends Service{

    private static final int NOTIFICATION_ID = 1234;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //提高进程优先级
        if (Build.VERSION.SDK_INT < 18) {
            //18以前空通知栏即可
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            Intent innerIntent = new Intent(this, GuardInnerService.class);
            startService(innerIntent);
            startForeground(NOTIFICATION_ID, new Notification());
        }

        //绑定建立连接
        bindService(new Intent(this,MessageService.class),mServiceConnection, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new ProcessConnection.Stub() {};
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接上
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //断开连接 重新启动 重新绑定
            startService(new Intent(GuardService.this,MessageService.class));
            bindService(new Intent(GuardService.this,MessageService.class),mServiceConnection, Context.BIND_IMPORTANT);

        }
    };


    private static class GuardInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(NOTIFICATION_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return START_STICKY;
        }
    }
}
