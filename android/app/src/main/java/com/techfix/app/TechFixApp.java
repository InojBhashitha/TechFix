package com.techfix.app;

import android.app.Application;

public class TechFixApp extends Application {

    private static TechFixApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TechFixApp getInstance() {
        return instance;
    }
}
