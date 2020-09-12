package com.lenebf.ac.component_test1;

import android.util.Log;

import com.lenebf.ac.component_application.ComponentApplication;

/**
 * @author lenebf@126.com
 * @since 2020/9/13
 */
public class Test1Application extends ComponentApplication {
    @Override
    public void init() {
        Log.d("Test1Application", "I'm component test1, function -init- be invoked.");
    }

    @Override
    public void initBackground() {
        Log.d("Test1Application", "I'm component test1, function -initBackground- be invoked.");
    }
}
