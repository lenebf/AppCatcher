package com.lenebf.ac.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ACPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def library = project.extensions.getByType(AppExtension)
        library.registerTransform(new ACTransform())
    }
}