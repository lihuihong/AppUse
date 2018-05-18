package com.example.testdemo;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.hc.baselibrary.base.BaseActivity;
import com.hc.baselibrary.ioc.OnClick;
import com.hc.baselibrary.ioc.ViewById;

public class ChooseActivity extends BaseActivity {

    @ViewById(R.id.app_time)
    private LinearLayout mAppTime;
    @ViewById(R.id.app_locahost)
    private LinearLayout mAppLocahost;
    @ViewById(R.id.app_seat)
    private LinearLayout mAppSeat;



    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_choose);
    }

    @OnClick(R.id.app_time)
    private void appTimeClick(LinearLayout appTime) {
        startActivity(MainActivity.class);
    }

    @OnClick(R.id.app_locahost)
    private void appLocahostClick(LinearLayout appLocahost) {
        Toast.makeText(this, "待开发......", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.app_seat)
    private void appSeatClick(LinearLayout appSeat) {
        Toast.makeText(this, "待开发......", Toast.LENGTH_SHORT).show();
    }
}
