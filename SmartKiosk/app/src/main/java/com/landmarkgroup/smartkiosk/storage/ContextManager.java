package com.landmarkgroup.smartkiosk.storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GURPREET SINGH on 01/03/2018..
 */

public class ContextManager {

    private static  ContextManager i_Self = null;
    private static Context AppContext;
    private static Context mainContext;
    private static ArrayList<Activity> activities = new ArrayList<Activity>();

    private static Map<Context, Intent> activityIntent = new HashMap<>();
    private Activity currentActivity;

    public static  ContextManager getInstance() {
        if (i_Self == null) {
            i_Self = new  ContextManager();
            return i_Self;
        } else {
            return i_Self;
        }
    }

    public static Context getAppContext() {
        return AppContext;
    }

    public static void setAppContext(Context appContext) {
        AppContext = appContext;
        // add the existing context into arralist
        activities.add((Activity) AppContext);
    }

    public static void setActivityIntent(Context activity, Intent intent) {
        activityIntent.put(activity, intent);
    }

    public static Intent getActivityIntent(Context activity) {
        Intent intent = null;

        if (activityIntent.containsKey(activity)) {
            intent = activityIntent.get(activity);
        }

        return intent;
    }

    /**
     * set the context of mainscreen
     */
    public static void setContextHomeScreen(Context mContext) {
        mainContext = mContext;
    }

    /**
     * return the context of mainscreen
     */
    public static Context getContextHomeScreen() {
        return mainContext;
    }

    /**
     * remove destroyed context from arraylist
     */
    public static void removeContext(Context rContext) {
        activities.remove((Activity) rContext);
    }

    /**
     * close all the activities in the app (ClOSE THE APP)
     */
    public static void finishAll() {
        for (Activity activity : activities)
            activity.finish();
    }

    public void setCurrentActivity(Activity currentActivity) {
        currentActivity = currentActivity;
    }

    public void isInstanceOfCurrentActivity(Activity currentActivity) {
        //Activity activity =  (Activity) currentActivity;

        /*if (AppContext instanceof MainActivity) {

        }*/
    }
}
