package com.lifestyle.buddydetagging.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.gson.Gson;
import com.lifestyle.buddydetagging.constant.GeneralConstant;
import com.lifestyle.buddydetagging.view.login.model.BrandPerfUserDetail;
import com.lifestyle.buddydetagging.view.login.model.UserDetailsResponse;
import com.orhanobut.hawk.Hawk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;


public class PreferenceUtils implements Observable {
    private PropertyChangeRegistry registry = new PropertyChangeRegistry();

    public PreferenceUtils() {
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


    public static void setStoreId(String storeId) {
        Hawk.put(PreferenceConstants.STORE_ID, storeId);
    }

    public static String getStoreId() {
        return Hawk.get(PreferenceConstants.STORE_ID, null);
    }


    public static void setStoreName(String storeId) {
        Hawk.put(PreferenceConstants.STORE_NAME, storeId);
    }

    public static String getStoreName() {
        return Hawk.get(PreferenceConstants.STORE_NAME, null);
    }

    public static void setRole(int ouCode) {
        Hawk.put(PreferenceConstants.ROLE, ouCode);
    }

    public static int getRole() {
        return Hawk.get(PreferenceConstants.ROLE, 2);
    }

    public static void setUserStatus(String storeId) {
        Hawk.put(PreferenceConstants.STATUS, storeId);
    }

    public static String getUserStatus() {
        return Hawk.get(PreferenceConstants.STATUS, null);
    }

    public static void setOuCode(String storeId) {
        Hawk.put(PreferenceConstants.OU_CODE, storeId);
    }

    public static String getOuCode() {
        return Hawk.get(PreferenceConstants.OU_CODE, null);
    }


    public static void setPwdChangeReq(String storeId) {
        Hawk.put(PreferenceConstants.PWD_CHANGE_REQ, storeId);
    }

    public static String getPwdChangeReq() {
        return Hawk.get(PreferenceConstants.PWD_CHANGE_REQ, null);
    }

    public static void Logout() {
        Hawk.deleteAll();
    }


    public static void isLoggedIn(boolean isLoggedIn) {
        Hawk.put(PreferenceConstants.IS_LOGGED_IN, isLoggedIn);
    }

    public static boolean isLoggedIn() {
        return Hawk.get(PreferenceConstants.IS_LOGGED_IN, false);
    }

    public static void setAuthToken(String token) {
        Hawk.put(PreferenceConstants.TOKEN, token);
    }

    public static String getAuthToken() {
        return Hawk.get(PreferenceConstants.TOKEN, null);

    }

    /**
     * Set Store Type (Service or Product)
     *
     * @param categoryId
     */

    public static void setFireBaseToken(String token) {
        Hawk.put(PreferenceConstants.FIREBASE_TOKEN, token);
    }

    public static String getFireBaseToken() {
        return Hawk.get(PreferenceConstants.FIREBASE_TOKEN, null);
    }


    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        registry.remove(onPropertyChangedCallback);
    }

    public static void delleteAll() {
        Hawk.deleteAll();
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

    public static LocationEvent getAddress(Context context, double latitude, double longitude) {
        try {
            LocationEvent event = null;
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

            String address = "";
            if (CommonUtility.isNotNull(addresses) && !addresses.isEmpty()) {
                if (addresses.get(0).getAddressLine(0) != null)
                    address = addresses.get(0).getAddressLine(0);

                if (addresses.get(0).getAddressLine(1) != null)
                    address += ", " + addresses.get(0).getAddressLine(1);

                if (addresses.get(0).getAddressLine(2) != null)
                    address += ", " + addresses.get(0).getAddressLine(2);

                event = new LocationEvent(latitude, longitude, address);
                getLocationAddress(addresses.get(0), event);
            }
            return event;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getLocationAddress(Address address, LocationEvent event) {
        event.setStoreLocation(address.getAddressLine(0));
        if (CommonUtility.isNotNull(address.getFeatureName())) {
            event.setLocality(address.getFeatureName());
        }
        if (CommonUtility.isNotNull(address.getSubLocality()) && CommonUtility.isNotNull(event.getLocality())) {
            event.setLocality(event.getLocality() + " " + address.getSubLocality());
        } else if (CommonUtility.isNotNull(address.getSubLocality())) {
            event.setLocality(address.getSubLocality());
        }

        event.setPostalCode(address.getPostalCode());
        event.setCity(address.getLocality());
        event.setState(address.getAdminArea());
        event.setCountry(address.getCountryName());
    }

    public static String getLocationCityName(double lat, double lon) {
        JSONObject result = getLocationFormGoogle(lat + "," + lon);
        return getCityAddress(result);
    }

    private static JSONObject getLocationFormGoogle(String placesName) {

        String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + placesName + "&sensor=true" + "&key=" + "AIzaSyARxWYiJxPPGiVAzsO-v4aA_Shfjzckjwg";

        HttpGet httpGet = new HttpGet(apiRequest);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private static String getCityAddress(JSONObject result) {
        if (result.has("results")) {
            try {
                JSONArray array = result.getJSONArray("results");
                if (array.length() > 0) {
                    JSONObject place = array.getJSONObject(0);
                    JSONArray components = place.getJSONArray("address_components");
                    StringBuilder address = new StringBuilder();
                    for (int i = 0; i < components.length(); i++) {
                        JSONObject component = components.getJSONObject(i);
                        String shortAdd = component.getString("long_name");
                        address.append(shortAdd);
                    }
                    return address.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static int getLocalNotifiCounter() {
        int counter = Hawk.get(GeneralConstant.KEY_LOCAL_NOTIF, 0) + 1;
        setLocalNotificationCounter(counter);
        return counter;
    }

    private static void setLocalNotificationCounter(int counter) {
        Hawk.put(GeneralConstant.KEY_LOCAL_NOTIF, counter);
    }

   /* public static void setStatuses(List<String> statuses) {
        Gson gson = new Gson();
        String json = gson.toJson(statuses);
        Hawk.put(PreferenceConstants.STATUSES, json);
    }

    public static String getStatuses() {
        return Hawk.get(PreferenceConstants.STATUSES, null);
    }
*/

    public static void isConfirmed(boolean isConfirmed) {
        Hawk.put(PreferenceConstants.IS_CONFIRMED, isConfirmed);
    }

    public static boolean isConfirmed() {
        return Hawk.get(PreferenceConstants.IS_CONFIRMED, false);
    }

    public static void SET_USER_NAME(String name) {
        Hawk.put(PreferenceConstants.USER_NAME, name);
    }

    public static String GET_USER_NAME() {
        return Hawk.get(PreferenceConstants.USER_NAME, null);
    }

    public static void setMobileNo(String mobileNo) {
        Hawk.put(PreferenceConstants.MOBILE_NO, mobileNo);
    }

    public static String getMobileNo() {
        return Hawk.get(PreferenceConstants.MOBILE_NO, "");
    }

    public static void setOrderData(String orderData) {
        Hawk.put(PreferenceConstants.ORDER_DATA, orderData);
    }

    public static String getOrderData() {
        return Hawk.get(PreferenceConstants.ORDER_DATA, "");
    }


    public static void set_devicedata_type(String type) {
        Hawk.put(PreferenceConstants.DEVICE_TYPE_DATA, type);
    }

    public static String get_devicedata_type() {
        return Hawk.get(PreferenceConstants.DEVICE_TYPE_DATA, null);
    }

    public static void setEmployeeId(String employeeId) {
        Hawk.put(PreferenceConstants.EMPLOYEE_ID, employeeId);
    }

    public static String getEmployeeId() {
        return Hawk.get(PreferenceConstants.EMPLOYEE_ID, null);

    }

    public static void setEmployeeName(String employeeName) {
        Hawk.put(PreferenceConstants.EMPLOYEE_NAME, employeeName);

    }

    public static String getEmployeeName() {
        return Hawk.get(PreferenceConstants.EMPLOYEE_NAME, null);
    }

}