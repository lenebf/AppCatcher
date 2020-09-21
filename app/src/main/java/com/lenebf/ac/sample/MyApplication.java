package com.lenebf.ac.sample;

import android.util.Log;

import com.lenebf.ac.main_application.MainApplication;

/**
 * @author lenebf@126.com
 * @since 2020/9/21
 */
public class MyApplication extends MainApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onComponentAppsInitFinish() {

    }

    @Override
    public void onCatchThrowable(Throwable throwable) {
        Log.e("MyApplication", "onCatchThrowable", throwable);
    }
}
