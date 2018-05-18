package com.example.testdemo;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.example.testdemo.adapter.ViewPagerAdapter;
import com.example.testdemo.bean.TopAppInfo;
import com.example.testdemo.server.GuardService;
import com.example.testdemo.server.JobWakeUpService;
import com.example.testdemo.server.MessageService;
import com.example.testdemo.utils.CommonUtils;
import com.hc.baselibrary.base.BaseActivity;
import com.hc.baselibrary.ioc.ViewById;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity{

    @ViewById(R.id.tab_layout)
    private TabLayout mTabLayout;
    @ViewById(R.id.view_pager)
    private ViewPager mViewPager;

    private MyReceiver receiver = null;
    TopAppInfo mTopAppInfo;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {

        startService(new Intent(this, GuardService.class));
        startService(new Intent(this, MessageService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //必需大于5.0
            startService(new Intent(this, JobWakeUpService.class));
            //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

            if (!isNoSwitch()) {
                //跳转到此设置界面的方法
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }

        }
        if (!isEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        //初始化适配器及TabLayout
        initTablayout();
    }

    @Override
    protected void initView() {
        //注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.testdemo.MyService");
        MainActivity.this.registerReceiver(receiver, filter);
    }

    @Override
    protected void initTitle() {

    }
    private void initTablayout() {

        PagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    //获取值
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            mTopAppInfo = new TopAppInfo();
            Bundle bundle = intent.getExtras();
            String count = bundle.getString("name");
            mTopAppInfo.setTopAppPackageName(count);
        }
    }

    public List<TopAppInfo> getTopAppInfo() {
        List<TopAppInfo> list = new ArrayList<>();
        list.add(mTopAppInfo);
        return list;
    }

    /**
     * Activity创建或者从被覆盖、后台重新回到前台时被调用
     */
    @Override
    protected void onResume() {
        super.onResume();

        startService(new Intent(this, GuardService.class));
        startService(new Intent(this, MessageService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //必需大于5.0
            startService(new Intent(this, JobWakeUpService.class));
        }
        if (!CommonUtils.checkNotificationReadPermission(this)) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

    }

    //判断这个选项的打开状态
    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        @SuppressLint("WrongConstant") UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    //开启NotificationMonitor的监听功能
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 退出当前Activity时被调用,调用之后Activity就结束了
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

