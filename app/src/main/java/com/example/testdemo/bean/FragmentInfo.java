package com.example.testdemo.bean;

/**
 * Created by 那个谁 on 2018/3/6.
 * 奥特曼打小怪兽
 * 作用：
 */

public class FragmentInfo {
    private  String title;
    private Class fragment;

    public FragmentInfo(String title, Class fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }
}
