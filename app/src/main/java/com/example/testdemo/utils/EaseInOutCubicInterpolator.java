package com.example.testdemo.utils;

import android.animation.TimeInterpolator;

/**
 * Created by 那个谁 on 2017/9/19.
 * 奥特曼打小怪兽
 * 作用：
 */

public class EaseInOutCubicInterpolator implements TimeInterpolator {
    @Override
    public float getInterpolation(float input) {
        if ((input *= 2) < 1.0f) {
            return 0.5f * input * input * input;
        }
        input -= 2;
        return 0.5f * input * input * input + 1;
    }
}
