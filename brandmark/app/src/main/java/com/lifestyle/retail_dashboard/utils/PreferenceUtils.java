package com.lifestyle.retail_dashboard.utils;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;

import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeDetail;
import com.lifestyle.retail_dashboard.view.attendance.model.FilterItemSelected;
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail;
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class PreferenceUtils implements Observable {
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();

    public PreferenceUtils() {
    }

    public static void setUserId(String userId) {
        Hawk.put(PreferenceConstants.USER_ID, userId);
    }

    public static String getUserId() {
        return Hawk.get(PreferenceConstants.USER_ID, null);
    }

    public static void setUserPass(String userPass) {
        Hawk.put(PreferenceConstants.USER_PASS, userPass);
    }

    public static String getUserPass() {
        return Hawk.get(PreferenceConstants.USER_PASS, null);
    }

    public static void setMobileNo(String mobileNo) {
        Hawk.put(PreferenceConstants.MOBILE_NO, mobileNo);
    }

    public static String getMobileNo() {
        return Hawk.get(PreferenceConstants.MOBILE_NO, null);
    }


    public static void setEmailId(String emailId) {
        Hawk.put(PreferenceConstants.EMAIL, emailId);
    }

    public static String getEmailId() {
        return Hawk.get(PreferenceConstants.EMAIL, null);
    }


    public static void setAuthKey(String authKey) {
        Hawk.put(PreferenceConstants.AUTH_KEY, authKey);
    }

    public static String getAuthKey() {
        return Hawk.get(PreferenceConstants.AUTH_KEY, null);
    }


    public static void setUserName(String name) {
        Hawk.put(PreferenceConstants.USER_NAME, name);
    }

    public static String getUserName() {
        return Hawk.get(PreferenceConstants.USER_NAME, null);
    }

    public static void setAttendanceBrandId(String name) {
        Hawk.put(PreferenceConstants.ATTENDANCE_BRAND_ID, name);
    }

    public static String getAttendanceBrandId() {
        return Hawk.get(PreferenceConstants.ATTENDANCE_BRAND_ID, null);
    }


    public static void setBrandPerfDetail(List<BrandPerfUserDetail> brandPerfUserDetails) {
        Hawk.put(PreferenceConstants.BRAND_PERFORMANCE_DETAIL, brandPerfUserDetails);
    }

    public static List<BrandPerfUserDetail> getBrandPerfDetail() {
        return Hawk.get(PreferenceConstants.BRAND_PERFORMANCE_DETAIL, null);
    }


    public static void setEmpAttendanceDetail(List<EmployeeDetail> empAttendenceDetail) {
        Hawk.put(PreferenceConstants.EMP_ATTENDANCE_DETAIL, empAttendenceDetail);
    }

    public static List<EmployeeDetail> getEmpAttendanceDetail() {
        return Hawk.get(PreferenceConstants.EMP_ATTENDANCE_DETAIL, null);
    }
    public static void setPlanogramData(List<Planogram> planogramData) {
        Hawk.put(PreferenceConstants.PLANOGRAM_DETAIL, planogramData);
    }

    public static List<Planogram> getPlanogramData() {
        return Hawk.get(PreferenceConstants.PLANOGRAM_DETAIL, null);
    }


    public static void setSavedFilter(List<FilterItemSelected> filterItemSelecteds) {
        Hawk.put(PreferenceConstants.ATTENDANCE_FILTERS, filterItemSelecteds);
    }

    public static List<FilterItemSelected> getSavedFilter() {
        return Hawk.get(PreferenceConstants.ATTENDANCE_FILTERS, null);
    }

    public static boolean setIsStoreListFound(boolean found) {
        return Hawk.put(PreferenceConstants.IS_STORE_LIST_FOUND, found);
    }

    public static boolean isStoreListFound() {
        return Hawk.get(PreferenceConstants.IS_STORE_LIST_FOUND, false);
    }


    public static void setBPCalender(Calendar calendar) {
        Hawk.put(PreferenceConstants.BPCalender, calendar);
    }

    public static Calendar getBPCalender() {
        return Hawk.get(PreferenceConstants.BPCalender, null);
    }


    /*public static void setRACDetail(List<HashMap<Integer,String>> racDetail) {
        Hawk.put(PreferenceConstants.RAC, racDetail);
    }

    public static List<HashMap<Integer,String>> getRACDetail() {
        return Hawk.get(PreferenceConstants.RAC, null);
    }*/

    public static void setRACDetail(HashMap<Integer,String> racDetail) {
        Hawk.put(PreferenceConstants.RAC, racDetail);
    }

    public static HashMap<Integer,String> getRACDetail() {
        return Hawk.get(PreferenceConstants.RAC, null);
    }

    public static void Logout() {
        Hawk.deleteAll();
    }


    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.remove(onPropertyChangedCallback);
    }

}