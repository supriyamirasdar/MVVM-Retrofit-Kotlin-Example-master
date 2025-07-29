package com.lifestyle.buddydetagging.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.lifestyle.buddydetagging.CustomizedExceptionHandler;
import com.lifestyle.buddydetagging.network.NetworkModule;
import com.orhanobut.hawk.Hawk;

public class BaseApplication extends Application {
    public static BaseApplication mInstance;
    public NetworkModule apiClient;
    public static Context context;
    public static BaseApplication getBaseAppInstance() {
        return mInstance;
    }

    public NetworkModule getApiClient() {
        return apiClient;
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();


        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler());

        StrictMode.setVmPolicy(builder.build());
        context = this;
        mInstance = this;
        Hawk.init(getApplicationContext()).build();
        apiClient = NetworkModule.getInstance();
       // setupActivityListener();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);            }
            @Override
            public void onActivityStarted(Activity activity) {
            }
            @Override
            public void onActivityResumed(Activity activity) {

            }
            @Override
            public void onActivityPaused(Activity activity) {

            }
            @Override
            public void onActivityStopped(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
