package com.example.testdemo.server;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.testdemo.ProcessConnection;
import com.example.testdemo.utils.AppUtil;
import com.example.testdemo.utils.ApplicationInfoUtil;

/**
 * Created by 那个谁 on 2018/3/8.
 * 奥特曼打小怪兽
 * 作用：守护进程，双进程通信
 */

public class MessageService extends Service {

    private String packageName = "";

    public static final int NOTIFICATION_ID = 1234;


    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //已在主线程中，可以更新UI
                            //获取当前使用应用名
                            String name = AppUtil.parsePackageName(getApplicationContext().getPackageManager(),
                                    ApplicationInfoUtil.getTopAppPackageName(MessageService.this));

                            if (!packageName.equals(name) && !name.equals("")) {

                                Toast.makeText(MessageService.this, name, Toast.LENGTH_SHORT).show();
                                packageName = name;

                                //发送广播
                                Intent intent=new Intent();
                                intent.putExtra("name", name);
                                intent.setAction("com.example.testdemo.MyService");
                                sendBroadcast(intent);
                            }

                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //提高进程优先级
        if (Build.VERSION.SDK_INT < 18) {
            //18以前空通知栏即可
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            startForeground(NOTIFICATION_ID, new Notification());
        }

        //绑定建立连接
        bindService(new Intent(this, GuardService.class), mServiceConnection, Context.BIND_IMPORTANT);

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
            startService(new Intent(MessageService.this, NLService.class));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //断开连接 重新启动 重新绑定
            startService(new Intent(MessageService.this, NLService.class));
            startService(new Intent(MessageService.this, GuardService.class));
            bindService(new Intent(MessageService.this, GuardService.class), mServiceConnection, Context.BIND_IMPORTANT);

        }
    };

}
