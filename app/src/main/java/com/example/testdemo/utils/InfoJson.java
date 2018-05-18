package com.example.testdemo.utils;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.util.Log;

import com.example.testdemo.Application.ApplicationConfig;
import com.example.testdemo.bean.AppInfo;
import com.example.testdemo.bean.TopAppInfo;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by 那个谁 on 2018/3/23.
 * 奥特曼打小怪兽
 * 作用：
 */

public class InfoJson {


    public static void AppInfoMessage(Context context, List<AppInfo> items, int mDay, List<TopAppInfo> mTopInfo) {

        //使用应用信息
        List<AppInfo> mAppTimeline;
        //使用详细情况信息
        List<AppInfo> mNewList = new ArrayList<>();

        List<String> mAppMessage;
        //将信息转换为json
        String mJson = null;

        //获取信息
        String mFormat;

        Gson gson = new Gson();
        List<Object> list = new ArrayList<>();
        for (AppInfo info : items) {
            mAppTimeline = ApplicationInfoUtil.getInstance().getTargetAppTimeline(context, info.mPackageName, mDay);
            long duration = 0;
            mNewList.clear();
            for (AppInfo item : mAppTimeline) {
                if (item.mEventType == UsageEvents.Event.USER_INTERACTION || item.mEventType == UsageEvents.Event.NONE) {
                    continue;
                }
                duration += item.mUsageTime;
                if (item.mEventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    AppInfo newItem = item.copy();
                    newItem.mEventType = -1;
                    mNewList.add(newItem);
                }
                mNewList.add(item);
            }

            mAppMessage = new ArrayList<>();
            mAppMessage.clear();
            for (AppInfo info1 : mNewList) {
                String desc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(info1.mEventTime));
                if (info1.mEventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                } else if (info1.mEventType == -1) {
                    desc = AppUtil.formatMilliSeconds(info1.mUsageTime);
                } else if (info1.mEventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                }
                mFormat = String.format("%s %s", getPrefix(info1.mEventType), desc);
                mAppMessage.add(mFormat);
            }

            info.setmAppMessageInfo(mAppMessage);

            list.add(info);

            list.add(mTopInfo);

            mJson = gson.toJson(list);
            Log.e("==========>", "AppInfoMessage: " + mJson);

        }

        sendJsonMessage(mJson);

    }

    private static String getPrefix(int event) {
        switch (event) {
            case 1:
                return "开始使用时间： ";
            case 2:
                return "结束使用时间：";
            default:
                return "使 用 时 间 ：";
        }
    }

    public static void sendJsonMessage(String result) {

        Map<String, String> params = new HashMap<>();
        params.put("params", result);
        HttpUtils.postAsync(ApplicationConfig.INFO_URL, params, new HttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e) {

            }

            @Override
            public void requestSuccess(String result) {

            }
        });
    }
}
