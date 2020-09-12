package com.lenebf.ac.component_application;

import android.app.Application;
import android.content.Context;

/**
 * @author lenebf@126.com
 * @since 2020/9/12
 */
public abstract class ComponentApplication extends Application {

    @Override
    final public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    final public void onCreate() {
        super.onCreate();
        onCreate(false);
    }

    public void onCreate(boolean library) {
        super.onCreate();
        if (!library) {
            init();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initBackground();
                }
            }).start();
        }
    }

    public abstract void init();

    public abstract void initBackground();
}
