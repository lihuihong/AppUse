package com.example.testdemo.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by 那个谁 on 2018/3/28.
 * 奥特曼打小怪兽
 * 作用：
 */

public class HttpUtils {

    private static HttpUtils mHttpUtils;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    private static HttpUtils getIntance(){
        if (mHttpUtils == null){
            mHttpUtils = new HttpUtils();
        }
        return mHttpUtils;
    }

    private HttpUtils(){
        mOkHttpClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void get_Async(final String url, final DataCallBack dataCallBack){
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                delivFailure(request,e,dataCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {

                delivSuccess(response.body().string(),dataCallBack);
            }
        });
    }

    private void post_Async(String url, Map<String,String> params, final DataCallBack dataCallBack){
        RequestBody requestBody = null;
        if (params == null){
            params = new HashMap<>();
        }
        FormEncodingBuilder builder=new FormEncodingBuilder();
        for (Map.Entry<String,String> entry:params.entrySet()) {
            String key=entry.getKey().toString();
            String value=null;
            if(entry.getValue()==null){
                value="";
            }else {
                value=entry.getValue();
            }
            builder.add(key,value);
        }
        requestBody=builder.build();

        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                delivFailure(request,e,dataCallBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                delivSuccess(response.body().string(),dataCallBack);
            }
        });
    }

    public static void getAsync(String url,DataCallBack dataCallBack){
        getIntance().get_Async(url,dataCallBack);
    }

    public static void postAsync(String url,Map<String,String> params,DataCallBack dataCallBack){
        getIntance().post_Async(url,params,dataCallBack);
    }



    private void delivFailure(final Request request, final Exception e, final DataCallBack dataCallBack){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dataCallBack != null){
                    dataCallBack.requestFailure(request,e);
                }
            }
        });
    }
    private void delivSuccess(final String result, final DataCallBack dataCallBack){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dataCallBack != null){
                    try {
                        dataCallBack.requestSuccess(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    //回调方法
    public interface DataCallBack{
        void requestFailure(Request request,Exception e);
        void requestSuccess(String result) throws JSONException;
    }

}
