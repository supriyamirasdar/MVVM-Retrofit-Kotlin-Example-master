package com.lifestyle.retail.constants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lifestyle.retail.StylusApp;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressLint({"SimpleDateFormat", "SetTextI18n", "HardwareIds"})
public class Constant {

    public static final int UPLODA_PLANOGRAM = 500;
    public static final int GET_PLANOGRAM_LIST = 501;
    public static final int DELETE_PLANOGRAM = 502;
    public static final int GET_BRAND_PERFROMANCE = 503;
    public static final int GET_BRAND_GRAPH = 504;
    public static final int LOGIN_DETAIL = 505;
    public static final int CHANGE_PASSWORD = 506;
    public static final int GET_EMPLOYEE_LIST = 507;
    public static final int STORE_LIST = 508;
    public static final int ATTENDANCE_HISTORY = 509;
    public static final int GET_OJE_LIST = 510;
    public static final int UPDATE_OJE_SCORE = 511;
    public static final int CREATE_NEW_REATIL_AUDIT = 512;
    public static final int GET_RAC_LIST = 513;
    public static final int GET_BRAND_RANK = 514;
    public static final int GET_INVENTORY_MANAGMENT = 515;

    public static final int GET_BRAND_LIST = 516;
    public static final String  FIRST_ARGUMENT = "PassArgument1";
    public static final String  SECOND_ARGUMENT = "PassArgument2";
    public static final int NO_OF_CELL = 3;
    public static final long  MEGABYTE = 1024L * 1024L;


    public static final int REQUEST_CAMERA_FOR_ITEM_IMAGE = 1;

    public static final String PENDING = "PENDING";
    public static final String MODIFY = "MODIFY";
    public static final String ACK = "ACK";
    public static final String ACCEPT = "ACCEPT";
    public static final String REJECT = "REJECT";
    public static final String PICKED = "PICKED";
    public static final String PICKED_FAILED = "PICK FAILED";
    public static final String CANCELLED = "CANCELLED";
    public static final String DELIVERED = "DELIVERED";
    public static final String AUTO_CANCELLED = "AUTO CANCELLED";
    public static final String CONFIRM = "CONFIRM";

    public static final String DROPDOWN_OU = "dropdownOU";
    public static final String DROPDOWN_REGION = "dropdownRegion";
    public static final String DROPDOWN_STATE = "dropdownState";
    public static final String DROPDOWN_DISTRICT = "dropdownDistrict";
    public static final String DROPDOWN_STORE = "dropdownStore";
    public static final String DROPDOWN_ACTOR = "dropdownActor";
    public static final String DROPDOWN_ZONE = "dropdownZone";
    public static final String DROPDOWN_CITY = "dropdownCity";
    public static final String DROPDOWN_BARCODE_DIVISION = "barcodeDivision";
    public static final String DROPDOWN_REASON = "dropdownReason";
    public static final String DROPDOWN_CONCEPT = "dropdownConcept";

    public static final String DROPDOWN_SHELFEDGE_LABEL = "selfedgeLabel";
    public static final String DROPDOWN_SHELFEDGE_TYPE = "selfedgeType";
    public static final String DROPDOWN_SHELFEDGE_DIVISION = "selfedgeDivison";

    public static final String ALIGNMENT_LEFT = "left";
    public static final String ALIGNMENT_RIGHT = "right";
    public static final String ALIGNMENT_CENTER = "center";
    public static final String ACTION_REFRESH_DB_FRS = "refreshFrsDashboard";
    public static final String USE_CASE_ID_FRA = "7";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String FULL_PICKED = "FULL_PICKED";
    public static final String NIL_PICKED = "NIL_PICKED";
    public static final String SHORT_PICKED = "SHORT_PICKED";
    public static final String CANCEL = "CANCEL";
    public static final String USERDATA = "storelist";
    public static final String AUTOCANCEL = "AUTOCANCEL";
    public static final String AUTOCANCELLED = "AUTOCANCELLED";
    public static final String SUPPORT_EMAIL = "central.support@landmarkgroup.in";
    public static final String SUPPORT_No = "080-41796550";
    public static final String FUTURE_TO_DATE = "01-Jan-2050";
    public static final String PAST_FROM_DATE = "01-Jan-1980";

    public static final String PRODUCT_TYPE = "productType";
    public static final String CLICKED_PRODUCT = "clickedProduct";
    public static final String DIVISION_LIST = "divisions";
    public static final String CLICKED_POSITION = "position";
    public static final String GROUP_LIST = "groups";
    public static final String CLICKED_GROUP_POSITION = "clicked_group";
    public static final String CLICKED_DIVI_POSITION = "clicked_divi";
    public static final String IMAGE_DIRECTORY_NAME = "Planogram";
    public static final String appType = "Stylus";

    public static final String RAC_STANDARD = "Standard";
    public static final String RAC_BUSINESS = "Business";
    public static final String RAC_BACKSTORE = "BackStore";

    public static String soldDateFormate(String serverDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        String dateInString = serverDate;

        try {

            Date date = formatter.parse(dateInString);
            //System.out.println(date);
            //System.out.println(formatter1.format(date));
            return formatter1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String dateDDMMMFormate(String serverDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = formatter.parse(serverDate);
            //System.out.println(date);
            //System.out.println(formatter1.format(date));
            return formatter1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String dateDDMMMFormate1(String serverDate) {
        Log.e("dateDDMMMFormate1", "serverDate....................." + serverDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");

        try {

            Date date = formatter.parse(serverDate);
            //System.out.println(date);
            //System.out.println(formatter1.format(date));
            return formatter1.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Date getCurrentDate() {
        Calendar dateCl = Calendar.getInstance();
        dateCl.setTimeInMillis(System.currentTimeMillis());
        int day = dateCl.get(Calendar.DATE);
        int month = dateCl.get(Calendar.MONTH) + 1;
        int year = dateCl.get(Calendar.YEAR);
        String strDate = String.valueOf(day);
        String strMonth = String.valueOf(month);
        String strYear = String.valueOf(year);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strYear + "-" + strMonth + "-" + strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    final public static int REQUEST_CODE_ASK_PERMISSIONS = 125;

    public static boolean checkPermissionAPI(final Activity mActivity, final String androidManifestPermissionName) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(mActivity, androidManifestPermissionName);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, androidManifestPermissionName)) {
                showMessageOKCancel("You need to allow permission", mActivity,
                        (dialog, which) -> ActivityCompat.requestPermissions(mActivity,
                                new String[]{androidManifestPermissionName},
                                REQUEST_CODE_ASK_PERMISSIONS));
                return false;
            }
            ActivityCompat.requestPermissions(mActivity, new String[]
                            {
                                    androidManifestPermissionName
                            },
                    REQUEST_CODE_ASK_PERMISSIONS);
            return false;
        } else {
            return true;
        }

    }

    public static void showMessageOKCancel(String message, Activity mActivity, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static boolean getSuppourtedFocusedModes() {
        try {
            Camera camera1 = Camera.open();
            final Camera.Parameters parameters = camera1.getParameters();
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            Log.v("supportedFocusModes ", "" + supportedFocusModes);
            if (supportedFocusModes.contains("continuous-picture")) {
                camera1.release();
                return true;
            } else {
                camera1.release();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void saveMap(String key, Map<String, String> inputMap) {
        SharedPreferences pSharedPref = StylusApp.getInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).apply();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }

    public static Map<String, String> loadMap(String key) {
        Map<String, String> outputMap = new HashMap<String, String>();
        SharedPreferences pSharedPref = StylusApp.getInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k, v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    public static Rect getViewLocation(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    public static int getScreenHeightBelowView(View v, int screenHeight) {
        int[] loc_int = new int[2];
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return screenHeight - location.bottom;
    }

    public static void removeFragment(AppCompatActivity activity, String tag) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            activity.getSupportFragmentManager().popBackStack();
        }
    }


}