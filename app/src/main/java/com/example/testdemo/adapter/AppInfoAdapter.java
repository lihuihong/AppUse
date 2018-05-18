package com.example.testdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.testdemo.R;
import com.example.testdemo.bean.AppInfo;
import com.example.testdemo.utils.AppUtil;
import com.example.testdemo.utils.ApplicationInfoUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：数据适配器
 */

public class AppInfoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private List<AppInfo> mAppInfos;
    private long mTotal;

    private static CallBack mCallBack;

    private int mDay;

    public AppInfoAdapter(Context context,int day) {
        mContext = context;
        mDay = day;
        mAppInfos = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        new MyAsyncTask().execute();
        notifyDataSetChanged();
    }
    void updateData(List<AppInfo> data) {
        mAppInfos = data;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (mAppInfos.size() > position) {
            return mAppInfos.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        AppInfo bean = mAppInfos.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.info_item, null);

            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.app_image);
            viewHolder.mUsage = (TextView) convertView.findViewById(R.id.app_usetime);
            viewHolder.mProgress = (ProgressBar) convertView.findViewById(R.id.progressBar);
            viewHolder.mTime = (TextView) convertView.findViewById(R.id.app_time);
            viewHolder.mName = convertView.findViewById(R.id.app_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            if (mTotal > 0) {
                viewHolder.mProgress.setProgress((int) (bean.mUsageTime * 100 / mTotal));
            } else {
                viewHolder.mProgress.setProgress(0);
            }
            viewHolder.mUsage.setText(AppUtil.formatMilliSeconds(bean.mUsageTime));
            viewHolder.mIcon.setImageDrawable(AppUtil.getPackageIcon(mContext, bean.mPackageName));
            viewHolder.mName.setText(bean.appName);
            viewHolder.mTime.setText(String.format(Locale.getDefault(), "%s · %d %s ",
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(bean.mEventTime)),
                    bean.mCount, "次"));
        return convertView;
    }

    class ViewHolder {

        private TextView mName;
        private TextView mUsage;
        private TextView mTime;
        private ImageView mIcon;
        private ProgressBar mProgress;
    }



    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends AsyncTask<Integer, Void, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mSwipe.setRefreshing(true);
        }

        @Override
        protected List<AppInfo> doInBackground(Integer... integers) {
            return ApplicationInfoUtil.getInstance().getApps(mContext, mDay);
        }

        @Override
        protected void onPostExecute(List<AppInfo> appItems) {
            mTotal = 0;
            for (AppInfo item : appItems) {
                if (item.mUsageTime <= 0) continue;
                mTotal += item.mUsageTime;
            }
            updateData(appItems);
            doWork(appItems);
        }
    }

    /*
     * 设置回调接口对象成员变量
     */
    public void setCallback(CallBack callback) {
        this.mCallBack = callback;
    }
    /*
     * 调用回调接口对象中的方法
     */
    public void doWork(List<AppInfo> appInfos) {
        mCallBack.work(appInfos);
    }

    /*
    * 声明回调接口
    */
    public interface CallBack{
        public abstract void work(List<AppInfo> appInfos);
    }

}
