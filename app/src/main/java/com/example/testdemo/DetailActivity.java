package com.example.testdemo;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testdemo.adapter.MyAdapter;
import com.example.testdemo.bean.AppInfo;
import com.example.testdemo.utils.AppUtil;
import com.example.testdemo.utils.ApplicationInfoUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_DAY = "day";

    private MyAdapter mAdapter;
    private TextView mTime;
    private String mPackageName;
    private int mDay;
    private List<AppInfo> mNewList = new ArrayList<>();
    private RecyclerView mList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        if (intent != null){
            Bundle extras = intent.getExtras();
            mPackageName = extras.getString(EXTRA_PACKAGE_NAME);
            mDay = extras.getInt(EXTRA_DAY);
            //包名
            TextView mPackage = (TextView) findViewById(R.id.pkg_name);
            //Log.e("TAG--------", mPackageName);
            mPackage.setText(mPackageName);
            // 图标
            ImageView imageView = (ImageView) findViewById(R.id.icon);
            Drawable icon = AppUtil.getPackageIcon(this, mPackageName);
            imageView.setImageDrawable(icon);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetail();
                }
            });

            // 应用名
            TextView name = (TextView) findViewById(R.id.name);

            name.setText(AppUtil.parsePackageName(getPackageManager(), mPackageName));

            mList = (RecyclerView) findViewById(R.id.list);
            mList.setLayoutManager(new LinearLayoutManager(this));

            new MyAsyncTask(this).execute(mPackageName);

            Log.e("------->", "onCreate: "+mNewList );

            // 时间 次数
            mTime = (TextView) findViewById(R.id.time);

            // 打开应用
            final Button mOpenButton = (Button) findViewById(R.id.open);
            final Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(mPackageName);
            if (LaunchIntent == null) {
                mOpenButton.setClickable(false);
                mOpenButton.setAlpha(0.5f);
            } else {
                mOpenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(LaunchIntent);
                    }
                });
            }
        }
    }

    //打开详情
    private void openDetail() {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mPackageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyAsyncTask extends AsyncTask<String, Void, List<AppInfo>> {

        private WeakReference<Context> mContext;

        MyAsyncTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected List<AppInfo> doInBackground(String... strings) {
            return ApplicationInfoUtil.getInstance().getTargetAppTimeline(mContext.get(), strings[0], mDay);
        }

        @Override
        protected void onPostExecute(List<AppInfo> appItems) {
            if (mContext.get() != null) {
                long duration = 0;
                for (AppInfo item : appItems) {
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
                mAdapter = new MyAdapter(DetailActivity.this,mNewList,new MyAdapter.getFormat(){
                    @Override
                    public void getData(String data) {

                    }
                });
                mList.setAdapter(mAdapter);
                mTime.setText(String.format(getResources().getString(R.string.times), AppUtil.formatMilliSeconds(duration), appItems.get(appItems.size() - 1).mCount));

            }
        }
    }
}
