package com.lifestyle.retail;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.lifestyle.retail.networking.NetworkModule;
import com.lifestyle.retail.utils.CustomizedExceptionHandler;


public class StylusApp extends Application {
    public static StylusApp mInstance;
    public NetworkModule apiClient;

    public static StylusApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler());

        StrictMode.setVmPolicy(builder.build());
        mInstance = this;
        //Hawk.init(getApplicationContext()).build();
        apiClient = NetworkModule.getInstance();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
