package com.lenebf.ac.main_application;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.lenebf.ac.component_application.ComponentApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenebf@126.com
 * @since 2020/9/12
 */
public abstract class MainApplication extends Application {

    private List<ComponentApplication> componentApps;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }

    public void init(@NonNull Application application) {
        // 获取ComponentApplication列表
        final List<String> componentAppNames = getComponentApplications(application);
        if (componentAppNames.isEmpty()) {
            return;
        }
        componentApps = new ArrayList<>(componentAppNames.size());
        Context baseContext = getBaseContext();
        // 初始化ComponentApplication
        for (String componentAppName : componentAppNames) {
            try {
                componentAppName = componentAppName.replace("/", ".");
                Class<?> appClass = Class.forName(componentAppName);
                Constructor<?> constructor = appClass.getConstructor();
                final ComponentApplication moduleApp = (ComponentApplication) constructor.newInstance();
                moduleApp.attachBaseContext(baseContext);
                componentApps.add(moduleApp);
            } catch (Throwable throwable) {
                onCatchThrowable(throwable);
            }
        }
        // 同步初始化
        for (ComponentApplication componentApp : componentApps) {
            // 调用onCreate方法
            componentApp.onCreate(true);
            // 调用init方法
            componentApp.init();
        }
        // 异步初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ComponentApplication moduleApp : componentApps) {
                    // 调用initBackground方法
                    moduleApp.initBackground();
                }
                onComponentAppsInitFinish();
            }
        }).start();
    }

    private List<String> getComponentApplications(@NonNull Context context) {
        String packageName = context.getPackageName();
        String caughtAppsClassName = packageName + ".CaughtApps";
        try {
            Class<?> helpClass = Class.forName(caughtAppsClassName);
            Field subAppsField = helpClass.getField("componentApps");
            Object helperObject = helpClass.newInstance();
            return (List<String>) subAppsField.get(helperObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 所有组件Application初始化完成
     */
    public abstract void onComponentAppsInitFinish();

    /**
     * 组件初始化过程中出现异常
     */
    public abstract void onCatchThrowable(Throwable throwable);
}
