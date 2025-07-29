package com.lifestyle.buddydetagging.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "De-Tagging";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String EMPLOYEE_NAME = "employeeName";

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }
    public void setEmployeeId(String employeeId) {
        editor.putString(EMPLOYEE_ID, employeeId);
        editor.apply();
    }

    public String getEmployeeId() {
        return pref.getString(EMPLOYEE_ID, null);
    }

    public void setEmployeeName(String employeeName) {
        editor.putString(EMPLOYEE_ID, employeeName);
        editor.apply();
    }

    public String getEmployeeName() {
        return pref.getString(EMPLOYEE_NAME, null);
    }
}