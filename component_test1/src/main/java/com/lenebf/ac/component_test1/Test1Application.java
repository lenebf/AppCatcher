package com.lenebf.ac.component_test1;

import android.util.Log;
import android.widget.Toast;

import com.lenebf.ac.component_application.ComponentApplication;

/**
 * @author lenebf@126.com
 * @since 2020/9/13
 */
public class Test1Application extends ComponentApplication {
    @Override
    public void init() {
        String packageName = getPackageName();
        Toast.makeText(this, "Toast from component1 application.\n" + packageName, Toast.LENGTH_SHORT).show();
        Log.d("Test1Application", "I'm component test1, function -init- be invoked.");
    }

    @Override
    public void initBackground() {
        Log.d("Test1Application", "I'm component test1, function -initBackground- be invoked.");
    }
}
