package com.example.testdemo;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testdemo.Application.ApplicationConfig;
import com.example.testdemo.utils.CircleProgress;
import com.example.testdemo.utils.HttpUtils;
import com.example.testdemo.utils.PrefUtils;
import com.hc.baselibrary.base.BaseActivity;
import com.hc.baselibrary.ioc.OnClick;
import com.hc.baselibrary.ioc.ViewById;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    /****学号****/
    @ViewById(R.id.login_username)
    private EditText mLoginUsername;
    /****密码****/
    @ViewById(R.id.login_password)
    private EditText mLoginPassword;
    /****立即登录****/
    @ViewById(R.id.login_post)
    private Button mLoginPost;
    /****注册？****/
    @ViewById(R.id.login_regist)
    private TextView mLoginRegist;
    /****| 跳过登录****/
    @ViewById(R.id.login_jump)
    private TextView mLoginJump;
    private String mId;
    private String mPassword;
    private Map<String, String> mParam;

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
        setContentView(R.layout.activity_login);

    }

    @OnClick(R.id.login_post)
    private void loginPostClick(Button loginPost) {
        mRlProgress.setVisibility(View.VISIBLE);
        mProgress.startAnim();
        mId = mLoginUsername.getText().toString().trim();
        mPassword = mLoginPassword.getText().toString().trim();
        //登陆
        postLogin();
        mRlProgress.setVisibility(View.GONE);
    }

    @OnClick(R.id.login_regist)
    private void loginRegistClick() {
        startActivity(RegistActivity.class);
    }

    @OnClick(R.id.login_jump)
    private void loginJumpClick() {
        startActivity(ChooseActivity.class);
        finish();
    }

    /**
     * 登陆逻辑
     */
    private void postLogin() {
        if (mId.isEmpty() && mPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
            mProgress.stopAnim();
        } else {
            mParam = new HashMap<>();
            mParam.put("stUserId", mId);
            mParam.put("stPassword", mPassword);
            PrefUtils.setString(getApplication(),"st_id",mId);
            HttpUtils.postAsync(ApplicationConfig.LOGIN_URL, mParam, new HttpUtils.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e) {
                    Toast.makeText(LoginActivity.this, "学号或密码错误", Toast.LENGTH_SHORT).show();
                    mProgress.stopAnim();
                }

                @Override
                public void requestSuccess(String result) throws JSONException {
                    JSONObject jsonObject=new JSONObject(result);
                    String info = jsonObject.getString("info");
                    if (info.equals("登陆成功")) {
                        startActivity(ChooseActivity.class);
                        mProgress.stopAnim();
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, info, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
