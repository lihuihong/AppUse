package com.hc.baselibrary.ioc;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 那个谁 on 2018/3/4.
 * 奥特曼打小怪兽
 * 作用：
 */
public class ViewUtils {

    // 目前
    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    // 后期
    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }

    // 后期
    public static void inject(View view, Object object) {
        inject(new ViewFinder(view), object);
    }

    // 兼容 上面三个方法  object --> 反射需要执行的类
    private static void inject(ViewFinder finder, Object object) {
        injectFiled(finder, object);
        injectEvent(finder, object);
    }

    /**
     * 事件注入
     */
    private static void injectEvent(ViewFinder finder, Object object) {
        // 1. 获取类里面所有的方法
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        // 2. 获取Onclick的里面的value值
        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                int[] viewIds = onClick.value();
                for (int viewId : viewIds) {
                    // 3. findViewById 找到View
                    View view = finder.findViewById(viewId);


                    // 扩展功能 检测网络
                    boolean isCheckNet = method.getAnnotation(CheckNet.class) != null;

                    if (view != null) {
                        // 4. view.setOnClickListener
                        view.setOnClickListener(new DeclaredOnClickListener(method, object, isCheckNet));
                    }
                }
            }
        }
    }


    private static class DeclaredOnClickListener implements View.OnClickListener {
        private Object mObject;
        private Method mMethod;
        private boolean mIsCheckNet;

        public DeclaredOnClickListener(Method method, Object object, boolean isCheckNet) {
            this.mObject = object;
            this.mMethod = method;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        public void onClick(View v) {
            // 需不需要检测网络
            if (mIsCheckNet) {
                // 需要
                if (!networkAvailable(v.getContext())) {
                    // 打印Toast   "亲，您的网络不太给力"  写死有点问题  需要配置
                    Toast.makeText(v.getContext(), "亲，您的网络不太给力", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            // 点击会调用该方法
            try {
                // 所有方法都可以 包括私有共有
                mMethod.setAccessible(true);
                // 5. 反射执行方法
                mMethod.invoke(mObject, v);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mMethod.invoke(mObject, null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 注入属性
     */
    private static void injectFiled(ViewFinder finder, Object object) {
        // 1. 获取类里面所有的属性
        Class<?> clazz = object.getClass();
        // 获取所有属性包括私有和共有
        Field[] fields = clazz.getDeclaredFields();

        // 2. 获取ViewById的里面的value值
        for (Field field : fields) {
            ViewById viewById = field.getAnnotation(ViewById.class);
            if (viewById != null) {
                // 获取注解里面的id值  --> R.id.test_tv
                int viewId = viewById.value();
                // 3. findViewById 找到View
                View view = finder.findViewById(viewId);
                if (view != null) {
                    // 能够注入所有修饰符  private public
                    field.setAccessible(true);
                    // 4. 动态的注入找到的View
                    try {
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 判断当前网络是否可用
     */
    private static boolean networkAvailable(Context context) {
        // 得到连接管理器对象
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
