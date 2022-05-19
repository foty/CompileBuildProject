package com.example.aop;

import android.util.Log;
import android.view.View;

public class ClickHelper {

    private static long mLastClickTime;
    private static int mLastClickViewId;

    public static boolean isFastClick(View view, long intervalMillis) {
        int viewId = view.getId();
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - mLastClickTime);

        if (viewId == mLastClickViewId && timeInterval < intervalMillis) {
            Log.e("lxx", "拦截点击事件");
            return true;
        }
        mLastClickTime = time;
        mLastClickViewId = viewId;
        return false;
    }

}