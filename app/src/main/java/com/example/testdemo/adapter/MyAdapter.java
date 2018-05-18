package com.example.testdemo.adapter;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testdemo.R;
import com.example.testdemo.bean.AppInfo;
import com.example.testdemo.utils.AppUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 那个谁 on 2018/3/21.
 * 奥特曼打小怪兽
 * 作用：
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private List<AppInfo> mData;
    private String mFormat;
    private Context mContext;

    private getFormat mGetFormat;

    public MyAdapter(Context context,List<AppInfo> mList,getFormat mGetFormat) {
        this.mContext = context;
        this.mData = mList;
        this.mGetFormat = mGetFormat;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AppInfo item = mData.get(position);
        String desc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mEventTime));
        if (item.mEventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
            holder.mLayout.setPadding(dpToPx(16), 0, dpToPx(16), dpToPx(4));
        } else if (item.mEventType == -1) {
            holder.mLayout.setPadding(dpToPx(16), dpToPx(4), dpToPx(16), dpToPx(4));
            desc = AppUtil.formatMilliSeconds(item.mUsageTime);
        } else if (item.mEventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
            holder.mLayout.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), 0);
        }
        mFormat = String.format("%s %s", getPrefix(item.mEventType), desc);
        holder.mEvent.setText(mFormat);
        mGetFormat.getData(mFormat);
    }

    private String getPrefix(int event) {
        switch (event) {
            case 1:
                return "开始使用时间： ";
            case 2:
                return "结束使用时间：";
            default:
                return "使 用 时 间 ：";
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mEvent;
        LinearLayout mLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            mEvent = itemView.findViewById(R.id.event);
            mLayout = itemView.findViewById(R.id.layout);
        }
    }

    public interface getFormat{
        void getData(String data);
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


}

