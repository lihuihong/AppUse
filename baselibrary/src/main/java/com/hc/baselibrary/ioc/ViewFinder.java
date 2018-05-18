package com.hc.baselibrary.ioc;

import android.app.Activity;
import android.view.View;

/**
 * Created by 那个谁 on 2018/3/4.
 * 奥特曼打小怪兽
 * 作用： View的findViewById的辅助类
 */

public class ViewFinder {
    private Activity mActivity;
    private View mView;


    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }

    public ViewFinder(View view) {
        this.mView = view;
    }

    public View findViewById(int viewId) {
        return mActivity != null ? mActivity.findViewById(viewId) : mView.findViewById(viewId);
    }
}
