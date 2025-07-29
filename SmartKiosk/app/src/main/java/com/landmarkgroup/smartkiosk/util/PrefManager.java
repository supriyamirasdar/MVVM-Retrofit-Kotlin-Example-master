package com.landmarkgroup.smartkiosk.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "Smart_Kiosk";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String STORE_ID = "storeId";
    private static final String STORE_NAME = "storeName";
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String OU_CODE = "oa_code";
    private static final String OU_NAME = "oa_name";
    private static final String OU_URL = "oa_url";
    private static final String CALL_TYPE = "call_type"; // WEB or PDP
    private static final String TAB_SELECTED = "tab_selected";

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setStoreId(String storeId) {
        editor.putString(STORE_ID, storeId);
        editor.apply();
    }

    public String getStoreId() {
        return pref.getString(STORE_ID, "");
    }


    public void setStoreName(String storeName) {
        editor.putString(STORE_NAME, storeName);
        editor.apply();
    }

    public String getStoreName() {
        return pref.getString(STORE_NAME, "");
    }



    public void setOuName(String ouName) {
        editor.putString(OU_NAME, ouName);
        editor.apply();
    }

    public String getOuName() {
        return pref.getString(OU_NAME, "");
    }

    public void setOuCode(int oaCode) {
        editor.putInt(OU_CODE, oaCode);
        editor.apply();
    }

    public int getOuCode() {
        return pref.getInt(OU_CODE, 0);
    }

    public String getOuUrl() {
        return pref.getString(OU_URL, "");
    }

    public void setOuUrl(String ouUrl) {
        editor.putString(OU_URL, ouUrl);
        editor.apply();
    }


    public String getCallType() {
        return pref.getString(CALL_TYPE, "");
    }

    public void setCallType(String callType) {
        editor.putString(CALL_TYPE, callType);
        editor.apply();
    }

    public void setTabSelected(String tabId) {
        editor.putString(TAB_SELECTED, tabId);
        editor.apply();
    }

    public String getTabSelected() {
        return pref.getString(TAB_SELECTED, "");
    }

    public void setEmployeeId(String employeeId) {
        editor.putString(EMPLOYEE_ID, employeeId);
        editor.apply();
    }

    public String getEmployeeId() {
        return pref.getString(EMPLOYEE_ID, null);
    }
}