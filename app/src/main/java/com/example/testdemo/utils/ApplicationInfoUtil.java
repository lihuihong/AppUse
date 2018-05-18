package com.example.testdemo.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.example.testdemo.bean.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public class ApplicationInfoUtil {

    PackageManager pm;

    private static ApplicationInfoUtil mInstance;
    private UsageStatsManager manager;

    public static void init() {
        mInstance = new ApplicationInfoUtil();
    }

    public static ApplicationInfoUtil getInstance() {
        return mInstance;
    }

    /**
     * 请求授权
     *
     * @param context
     */
    public void requestPermission(Context context) {
        Intent intent = new Intent(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断授权
     *
     * @param context
     * @return
     */
    public boolean hasPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOps != null) {
            int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }


    public List<AppInfo> getTargetAppTimeline(Context context, String target, int offset) {
        List<AppInfo> items = new ArrayList<>();
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (manager != null) {
            long[] range = AppUtil.getTimeRange(SortEnum.getSortEnum(offset));
            UsageEvents events = manager.queryEvents(range[0], range[1]);
            UsageEvents.Event event = new UsageEvents.Event();

            AppInfo item = new AppInfo();
            item.mPackageName = target;
            item.appName = AppUtil.parsePackageName(context.getPackageManager(), target);

            // 缓存
            ClonedEvent prevEndEvent = null;
            long start = 0;

            while (events.hasNextEvent()) {
                events.getNextEvent(event);
                String currentPackage = event.getPackageName();
                int eventType = event.getEventType();
                long eventTime = event.getTimeStamp();
                if (currentPackage.equals(target)) { // 本次交互开始
                    // 记录第一次开始时间
                    if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        if (start == 0) {
                            start = eventTime;
                            item.mEventTime = eventTime;
                            item.mEventType = eventType;
                            item.mUsageTime = 0;
                            items.add(item.copy());
                        }
                    } else if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) { // 结束事件
                        if (start > 0) {
                            prevEndEvent = new ClonedEvent(event);
                        }
                    }
                } else {
                    // 记录最后一次结束事件
                    if (prevEndEvent != null && start > 0) {
                        item.mEventTime = prevEndEvent.timeStamp;
                        item.mEventType = prevEndEvent.eventType;
                        item.mUsageTime = prevEndEvent.timeStamp - start;
                        if (item.mUsageTime <= 0) item.mUsageTime = 0;
                        if (item.mUsageTime > 5000) item.mCount++;
                        items.add(item.copy());
                        start = 0;
                        prevEndEvent = null;
                    }
                }
            }
        }
        return items;
    }

    /**
     * 获得应用程序
     *
     * @param context 上下文
     * @param offset  查询类型
     * @return
     */
    public List<AppInfo> getApps(Context context, int offset) {
        pm = context.getPackageManager();
        List<AppInfo> items = new ArrayList<>();
        List<AppInfo> newList = new ArrayList<>();
        //判断API大于等于21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (manager != null) {
            // 缓存变量
            String prevPackage = "";
            Map<String, Long> startPoints = new HashMap<>();
            Map<String, ClonedEvent> endPoints = new HashMap<>();
            // 传入类型获取时间差数组
            long[] range = AppUtil.getTimeRange(SortEnum.getSortEnum(offset));
            //获取栈顶元素
            UsageEvents events = manager.queryEvents(range[0], range[1]);
            UsageEvents.Event event = new UsageEvents.Event();
            while (events.hasNextEvent()) {
                // 解析时间
                events.getNextEvent(event);
                //事件类型
                int eventType = event.getEventType();
                //事件时间
                long eventTime = event.getTimeStamp();
                String eventPackage = event.getPackageName();
                // 传入时间类型 获取此事件及时间，包括Activity名字  起始点
                if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    //判断包含项目
                    AppInfo item = containItem(items, eventPackage);
                    if (item == null) {
                        item = new AppInfo();

                        //判断学号是否存在
                        String st_id = PrefUtils.getString(context, "st_id", "");
                        if (!st_id.isEmpty()) {
                            item.setSt_userId(st_id);
                        }

                        item.mPackageName = eventPackage;
                        items.add(item.copy());
                    }
                    if (!startPoints.containsKey(eventPackage)) {
                        startPoints.put(eventPackage, eventTime);
                    }
                }
                // 记录结束时间点 当Activity被置于后台时，会记录此事件及时间，包括Activity名字
                if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    if (startPoints.size() > 0 && startPoints.containsKey(eventPackage)) {

                        endPoints.put(eventPackage, new ClonedEvent(event));
                    }
                }
                // 计算时间和次数 事件应该是连续的
                if (TextUtils.isEmpty(prevPackage)) prevPackage = eventPackage;
                // 包名有变化
                if (!prevPackage.equals(eventPackage)) {

                    if (startPoints.containsKey(prevPackage) && endPoints.containsKey(prevPackage)) {

                        ClonedEvent lastEndEvent = endPoints.get(prevPackage);

                        AppInfo listItem = containItem(items, prevPackage);

                        if (listItem != null) {

                            //获取时间戳
                            listItem.mEventTime = lastEndEvent.timeStamp;

                            long duration = lastEndEvent.timeStamp - startPoints.get(prevPackage);

                            if (duration <= 0) duration = 0;

                            listItem.mUsageTime += duration;

                            if (duration > 5000) {
                                listItem.mCount++;
                            }
                        }
                        startPoints.remove(prevPackage);
                        endPoints.remove(prevPackage);
                    }
                    prevPackage = eventPackage;
                }
            }
        }

            for (AppInfo item : items) {
                if (AppUtil.isSystemApp(pm, item.mPackageName)) {
                    continue;
                }
                if (!AppUtil.isInstalled(pm, item.mPackageName)) {
                    continue;
                }
                item.appName = AppUtil.parsePackageName(pm, item.mPackageName);

                newList.add(item.copy());
            }

        return newList;
    }

    //获取当前正在使用的应用
    public static String getTopAppPackageName(Context context) {
        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                List<ActivityManager.RunningTaskInfo> rti = activityManager.getRunningTasks(1);
                packageName = rti.get(0).topActivity.getPackageName();
            } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
                if (processes.size() == 0) {
                    return packageName;
                }
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            } else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                final long end = System.currentTimeMillis();
                final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE);
                if (null == usageStatsManager) {
                    return packageName;
                }
                final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
                if (null == events) {
                    return packageName;
                }
                UsageEvents.Event usageEvent = new UsageEvents.Event();
                UsageEvents.Event lastMoveToFGEvent = null;
                while (events.hasNextEvent()) {
                    events.getNextEvent(usageEvent);
                    if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastMoveToFGEvent = usageEvent;
                    }
                }
                if (lastMoveToFGEvent != null) {
                    packageName = lastMoveToFGEvent.getPackageName();
                }
            }
        }catch (Exception ignored){
        }
        return packageName;
    }


    //获取包含项目
    private AppInfo containItem(List<AppInfo> items, String packageName) {
        for (AppInfo item : items) {
            if (item.mPackageName.equals(packageName))
                return item;
        }
        return null;
    }


    class ClonedEvent {

        String packageName;
        String eventClass;
        long timeStamp;
        int eventType;

        ClonedEvent(UsageEvents.Event event) {
            packageName = event.getPackageName();
            eventClass = event.getClassName();
            timeStamp = event.getTimeStamp();
            eventType = event.getEventType();
        }
    }

}
