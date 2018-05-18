package com.example.testdemo.bean;

import java.util.List;
import java.util.Locale;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public class AppInfo {
    public String appName;
    public String mPackageName;
    public long mEventTime;
    public long mUsageTime;
    public int mEventType;
    public int mCount;
    private boolean mIsSystem;

    private String st_userId;

    public String getSt_userId() {
        return st_userId;
    }

    public void setSt_userId(String st_userId) {
        this.st_userId = st_userId;
    }

    private  List<String> mAppMessageInfo;

    public  List<String> getmAppMessageInfo() {
        return mAppMessageInfo;
    }

    public  void setmAppMessageInfo(List<String> mAppMessageInfo) {
        this.mAppMessageInfo = mAppMessageInfo;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "name:%s package_name:%s time:%d total:%d type:%d system:%b count:%d",
                appName, mPackageName, mEventTime, mUsageTime, mEventType, mIsSystem, mCount);
    }

    public AppInfo copy() {
        AppInfo newItem = new AppInfo();
        newItem.appName = this.appName;
        newItem.mPackageName = this.mPackageName;
        newItem.mEventTime = this.mEventTime;
        newItem.mUsageTime = this.mUsageTime;
        newItem.mEventType = this.mEventType;
        newItem.mIsSystem = this.mIsSystem;
        newItem.mCount = this.mCount;
        newItem.st_userId = this.st_userId;
        return newItem;
    }



}
