package com.lenebf.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author lenebf@126.com
 * @since 2020/9/19
 */
class AppCatcherPlugin implements Plugin<Project> {

    public void apply(Project project) {
        // register app catcher transform
        def app = project.extensions.getByType(AppExtension)
        app.registerTransform(new AppCatcherTransform())
        System.out.println("App catcher transform has register to $app")
    }
}
