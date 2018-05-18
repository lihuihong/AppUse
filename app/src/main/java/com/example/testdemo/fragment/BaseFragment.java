package com.example.testdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public abstract class BaseFragment extends Fragment {

    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";

    private View mContentView;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(setLayoutResourceID(), container, false);
        //ButterKnife.bind(this,mContentView);
        mContext = getContext();
        setUpView(mContentView);
        isInit = true;
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return mContentView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null){
            this.getView().setVisibility(menuVisible ? View.VISIBLE:View.GONE);
        }
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {

        if (!isInit) {
            return;
        }
        if (getUserVisibleHint()) {
            setUpData();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }

    }

    /**
     * 此方法用于返回Fragment设置ContentView的布局文件资源ID
     *
     * @return 布局文件资源ID
     */
    protected abstract int setLayoutResourceID();

    /**
     * 一些View的相关操作
     */
    protected abstract void setUpView(View mContentView);

    /**
     * 一些Data的相关操作
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void setUpData();


    public View getContentView() {
        return mContentView;
    }

    public Context getMContext() {
        return mContext;
    }

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected void stopLoad() {
    }

    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;

    }


}
