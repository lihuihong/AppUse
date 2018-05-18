package com.example.testdemo.server;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.List;

/**
 * Created by 那个谁 on 2018/3/8.
 * 奥特曼打小怪兽
 * 作用：5.0以上才有
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobWakeUpService extends JobService {


    private final int jobWakeUpId = 1;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //开启轮寻
        JobInfo.Builder jobBuilder = new JobInfo.Builder(jobWakeUpId
                ,new ComponentName(this,JobWakeUpService.class));
        //循环时间
        jobBuilder.setPeriodic(2000);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobBuilder.build());
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        //开启定时任务 定时轮寻 看服务有没有被杀死
        //如果杀死了启动 轮寻onStartJob

        //判断服务是否正在运行

        boolean serviceAlive = serviceAlive(MessageService.class.getName());
        if (!serviceAlive){
            startService(new Intent(this,MessageService.class));
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    private boolean serviceAlive( String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
