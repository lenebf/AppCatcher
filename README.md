# AppCatcher

An Android library to help component/module initialization.

## Feature

1. Automatic initialization of components

2. The component has a fully functional application instance 

## Configuration

**Step 1.** Add it in your root build.gradle at the end of repositories:

```groovy
buildscript {
    repositories {
        ...
        maven { url './repo'}
    }
    dependencies {
        ...
        classpath "com.lenebf.plugin:app_catcher:1.0.0"
    }
}

allprojects {
    repositories {
        ...
        maven { url './repo'}
    }
}
```

**Step 2.** Apply <mark>app_catcher</mark> plugin on your app and add dependencies

```groovy
apply plugin: 'com.lenebf.ac.plugin'

dependencies {
    implementation project(path: ':lib_main_application')
}
```

**Step 3.** Let your main application extend ***<u>MainApplication</u>***

```java
public class MyApplication extends MainApplication {
    @Override
    public void onComponentAppsInitFinish() {

    }

    @Override
    public void onCatchThrowable(Throwable throwable) {

    }
}
```

**Step 4.** Let your componetn/module application extend ***<u>ComponentApplication</u>***

```java
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
```

**3. Add confusing rules (If Proguard is turn on)**

```java
-keep class com.lenebf.ac.demo.CatchedApps {
    *;
}
```

*That's all you need to do, and the <mark>app_catcher</mark> plugins will take care of the rest automatically!*

## License

GNU General Public License v3.0. See the [LICENSE](https://github.com/lenebf/AppCatcher/blob/master/LICENSE) file for details.
