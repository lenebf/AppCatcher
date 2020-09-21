package com.lenebf.ac.component_test1;

import android.util.Log;
import android.widget.Toast;

import com.lenebf.ac.component_application.ComponentApplication;

/**
 * @author lenebf@126.com
 * @since 2020/9/21
 */
public class TestApplication1 extends ComponentApplication {

    @Override
    public void init() {
        Log.d("TestApplication1", "Component init");
        Toast.makeText(this, "Toast from component 1", Toast.LENGTH_LONG).show();
    }

    @Override
    public void initBackground() {
        Log.d("TestApplication1", "Component init");
    }
}
