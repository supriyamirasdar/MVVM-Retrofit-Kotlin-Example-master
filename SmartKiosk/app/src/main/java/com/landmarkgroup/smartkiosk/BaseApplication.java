package com.landmarkgroup.smartkiosk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static BaseApplication baseApplication;
   // public NetworkModule apiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        baseApplication= this;
        //apiClient = NetworkModule.getInstance();
    }

    public static Context getInstance() {
        return context;
    }
    public static BaseApplication getBaseInstance() {
        return baseApplication;
    }
}
