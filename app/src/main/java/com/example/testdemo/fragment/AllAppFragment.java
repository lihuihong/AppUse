package com.example.testdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.testdemo.DetailActivity;
import com.example.testdemo.MainActivity;
import com.example.testdemo.R;
import com.example.testdemo.adapter.AppInfoAdapter;
import com.example.testdemo.bean.AppInfo;
import com.example.testdemo.bean.TopAppInfo;
import com.example.testdemo.utils.InfoJson;

import java.util.List;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public class AllAppFragment extends BaseFragment {


    private ListView mListView;
    private List<AppInfo> items;
    private int mDay = 0;
    List<TopAppInfo> mTopInfo;
    private AppInfoAdapter mAdapter;


    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_all;
    }

    @Override
    protected void setUpView(View mContentView) {
        mListView = mContentView.findViewById(R.id.lv_all);

    }

    @Override
    protected void setUpData() {
        //默认获取当天
        mAdapter = new AppInfoAdapter(getActivity(), mDay);
        mListView.setAdapter(mAdapter);
        mAdapter.setCallback(new AppInfoAdapter.CallBack() {
            @Override
            public void work(List<AppInfo> appInfos) {
                items = appInfos;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InfoJson.AppInfoMessage(getContext(), items, mDay, mTopInfo);
                    }
                }).start();
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(DetailActivity.EXTRA_PACKAGE_NAME, items.get(position).mPackageName);
                        //Log.e("TAG--------", items.get(position).mPackageName.toString());
                        bundle.putInt(DetailActivity.EXTRA_DAY, mDay);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    }
                });

            }
        });
        //得到位于栈顶的App信息
        mTopInfo =((MainActivity)getActivity()).getTopAppInfo();
    }
}
