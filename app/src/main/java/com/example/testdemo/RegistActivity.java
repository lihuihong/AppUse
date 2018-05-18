package com.example.testdemo;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.testdemo.Application.ApplicationConfig;
import com.example.testdemo.utils.CircleProgress;
import com.example.testdemo.utils.HttpUtils;
import com.hc.baselibrary.base.BaseActivity;
import com.hc.baselibrary.ioc.OnClick;
import com.hc.baselibrary.ioc.ViewById;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistActivity extends BaseActivity {

    /****用户名****/
    @ViewById(R.id.regist_username)
    private EditText mRegistUsername;
    /****学号****/
    @ViewById(R.id.regist_id)
    private EditText mRegistId;
    /****密码****/
    @ViewById(R.id.regist_password)
    private EditText mRegistPassword;
    /****注册****/
    @ViewById(R.id.regist_post)
    private Button mRegistPost;

    private String mName;
    private String mPassword;
    private String mId;
    private Map<String, String> mParam;
    /****班级****/
    @ViewById(R.id.regist_class)
    private EditText mRegistClass;
    private String mClass;
    @ViewById(R.id.login_pb)
    private LinearLayout mLoginPb;
    @ViewById(R.id.ll_login_post)
    private LinearLayout mLlLoginPost;
    @ViewById(R.id.linearLayout)
    private LinearLayout mLinearLayout;

    //加载进度条
    @ViewById(R.id.progress)
    private CircleProgress mProgress;


    @ViewById(R.id.rl_progress)
    private RelativeLayout mRlProgress;




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
        setContentView(R.layout.activity_regist);
    }

    @OnClick(R.id.regist_post)
    private void registPostClick(Button registPost) {

        mRlProgress.setVisibility(View.VISIBLE);
        mProgress.startAnim();

        registPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = mRegistUsername.getText().toString().trim();
                mPassword = mRegistPassword.getText().toString().trim();
                mId = mRegistId.getText().toString().trim();
                mClass = mRegistClass.getText().toString().trim();

                postData();
                mRlProgress.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 注册逻辑
     */
    private void postData() {
        if (mName == null && mName == null && mPassword == null) {
            Toast.makeText(RegistActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            mParam = new HashMap<>();
            mParam.put("stUserId", mId);
            mParam.put("stUsername", mName);
            mParam.put("stPassword", mPassword);
            mParam.put("stMyclass", mClass);
            HttpUtils.postAsync(ApplicationConfig.REGIST_URL, mParam, new HttpUtils.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e) {
                    mProgress.stopAnim();
                    Toast.makeText(RegistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws JSONException {
                    JSONObject jsonObject=new JSONObject(result);
                    String info = jsonObject.getString("info");
                    if (info.equals("注册成功")){
                        mProgress.stopAnim();
                        Toast.makeText(RegistActivity.this, info, Toast.LENGTH_SHORT).show();
                        startActivity(LoginActivity.class);
                        finish();
                    }else {
                        Toast.makeText(RegistActivity.this, info, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
