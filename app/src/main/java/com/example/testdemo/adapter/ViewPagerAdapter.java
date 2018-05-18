package com.example.testdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.testdemo.bean.FragmentInfo;
import com.example.testdemo.fragment.AllAppFragment;
import com.example.testdemo.fragment.SystemAppFragment;
import com.example.testdemo.fragment.UesAppFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentInfo> mFragments = new ArrayList<>(3);
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments(){

        mFragments.add(new FragmentInfo("今天应用信息",AllAppFragment.class));
        mFragments.add(new FragmentInfo ("昨天应用信息",SystemAppFragment.class));
        mFragments.add(new FragmentInfo ("最近一周应用信息", UesAppFragment.class));
    }
    @Override
    public Fragment getItem(int position) {
        try {
            return (Fragment) mFragments.get(position).getFragment().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).getTitle();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    //super.destroyItem(container, position, object);
    }
}