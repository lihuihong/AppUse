package com.example.testdemo.server;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.testdemo.MainActivity;
import com.example.testdemo.utils.ApplicationInfoUtil;

/**
 * Created by 那个谁 on 2018/3/8.
 * 奥特曼打小怪兽
 * 作用：
 */

public class AppService extends Service {

    public static final String SERVICE_ACTION = "service_action";
    public static final String SERVICE_ACTION_CHECK = "service_action_check";
    static final long CHECK_INTERVAL = 400;

    private ApplicationInfoUtil mManager;
    private Context mContext;
    private Handler mHandler = new Handler();
    private Runnable mRepeatCheckTask = new Runnable() {
        @Override
        public void run() {
            if (!mManager.hasPermission(mContext)) {
                mHandler.postDelayed(mRepeatCheckTask, CHECK_INTERVAL);
            } else {
                mHandler.removeCallbacks(mRepeatCheckTask);
                startActivity(new Intent(mContext, MainActivity.class));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mManager = new ApplicationInfoUtil();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getStringExtra(SERVICE_ACTION);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case SERVICE_ACTION_CHECK:
                        startIntervalCheck();
                        break;
                }
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            startService(new Intent(mContext, AppService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startIntervalCheck() {
        boolean valid = true;
        try {
            mManager.requestPermission(mContext);
        } catch (ActivityNotFoundException e) {
            valid = false;
        }
        if (valid) {
            Toast.makeText(mContext, "已经授权", Toast.LENGTH_LONG).show();
            mHandler.post(mRepeatCheckTask);
        } else {
            Toast.makeText(mContext, "不支持", Toast.LENGTH_LONG).show();
        }
    }
}