package com.lifestyle.buddydetagging.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.base.BaseApplication;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.lifestyle.buddydetagging.base.BaseApplication.getBaseAppInstance;

@SuppressLint("HardwareIds")
public class Utility {

    private static AlertDialog alertDialog;

    public static void goToNextScreen(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void goToNextScreen(Activity activity, Class<?> destination, String empName, String empId, String empMobile) {
        Intent intent = new Intent(activity, destination);
        intent.putExtra("NAME", empName);
        intent.putExtra("EMP_ID", empId);
        intent.putExtra("MOBILE", empMobile);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void goToNextScreenEmpDetail(Activity activity, Class<?> destination, String empName, String empId, String empMobile) {
        Intent intent = new Intent(activity, destination);
        intent.putExtra("NAME", empName);
        intent.putExtra("EMP_ID", empId);
        intent.putExtra("MOBILE", empMobile);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static Drawable changeDrawableColor(Context context, int icon, int newColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    public static void changeIconColor(Context context, ImageView icon, int drawableId, int colorId) {
        Drawable drawable = context.getResources().getDrawable(drawableId).mutate();
        drawable.setColorFilter(context.getResources().getColor(colorId), PorterDuff.Mode.SRC_ATOP);
        icon.setImageDrawable(drawable);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void shareApk(Activity activity) {
        ApplicationInfo app = activity.getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        File originalApk = new File(filePath);

        try {
            File tempFile = new File(activity.getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            tempFile = new File(tempFile.getPath() + "/" + activity.getResources().
                    getString(app.labelRes).replace(" ", "").toLowerCase() + ".apk");
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            activity.startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void goToNextScreen(Context context, Class<?> destination, String parameter) {
        Intent intent = new Intent(context, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("itemSelected", parameter);
        context.startActivity(intent);
    }


    public static String parseDatewithFormate(String inputDate, String myFormate) {
        SimpleDateFormat format = new SimpleDateFormat(myFormate);
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            return dateFormat.format(date);
        } catch (ParseException e) {
            Log.d("Abrar", e.getLocalizedMessage());
            return inputDate;
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
            return inputDate;
        }
    }


    public static String parsingDateFormate(String originalDate, SimpleDateFormat inputDateFormate,
                                            SimpleDateFormat outputDateFormate) {

        if (originalDate != null) {
            try {
                Date outputDate = inputDateFormate.parse(originalDate);
                return outputDateFormate.format(outputDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
                return "";
            }

            //SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm");
        } else
            return "";
    }


    /*public static String appVersion() {
        Context context = getBaseAppInstance().getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        String myVersionName; // initialize String

        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
            return myVersionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    public static boolean appInstalledOrNot(String uri) {
        Context context = getBaseAppInstance().getApplicationContext();
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getDate() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return date;
    }

    public static long getDateInMilliseconds(String dateSt) {
        //String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            date = sdf.parse(dateSt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        return millis;
    }

    public static long getCurrentDateInMilliseconds() {
        String dateSt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            date = sdf.parse(dateSt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        return millis;
    }

    public static String getDateforComparison() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        return date;
    }

    public static boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }

    public static String compareDates(String currentDate, String recDate) {
        String difference = "";
        try {
            String[] beforeDateArry = recDate.split("\\s+");

            //String beforeDate = beforeDateArry[0];
            //String pattern = "E, dd MMM yyyy HH:mm:ss";
            // String pattern ="dd-MMM-yy HH.mm.ss";
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            Date date1 = formatter.parse(currentDate);
            Date date2 = formatter.parse(recDate);

            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
//                long days = (diff / (1000 * 60 * 60 * 24)) % 365;
            if (days > 0) {
                difference = "N";
            } else {
                difference = "Y";
            }
            /*if (days > 0) {
                difference = "" + days + " days ago";
            } else if (hours > 0) {
                difference = "" + hours + " hours ago";
            } else if (minutes > 0) {
                difference = "" + minutes + " minutes ago";
            } else if (seconds > 0) {
                difference = "Just Now";
            }*/

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return difference;
    }


    public static String isSameDate(String currentDate, String recDate) {

        String difference = "";

        String[] currDateArry = currentDate.split("\\s+");
        String[] recDateArry = recDate.split("\\s+");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = format.parse(currDateArry[0]);
            Date date2 = format.parse(recDateArry[0]);


            if (fmt.format(date1).equals(fmt.format(date2))) {
                difference = "Y";
            } else {
                difference = "N";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return difference;
    }

    public static String changeDateTimeFormat(String inputdate) {
       /* try {
            SimpleDateFormat reqFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat actFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
            Date reqFormatDate = actFormat.parse(date);
            return reqFormat.format(reqFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";*/


        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy hh:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(inputdate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }


    public static long findDateDifference(String start_date, String end_date) {
        long difference_In_Days = 0;
        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time = d2.getTime() - d1.getTime();

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds = (difference_In_Time / 1000) % 60;

            long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

            long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

            long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));

            difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            System.out.print("Difference " + "between two dates is: ");

            System.out.println(difference_In_Years + " years, "
                    + difference_In_Days + " days, "
                    + difference_In_Hours + " hours, "
                    + difference_In_Minutes + " minutes, "
                    + difference_In_Seconds + " seconds");


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return difference_In_Days;
    }



    public static String getIMEI(Context context) {
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                if (TelephonyMgr.getDeviceId() != null) {
                    return TelephonyMgr.getDeviceId();
                } /*else if (TelephonyMgr.getImei() != null) {
                return TelephonyMgr.getImei();
            } */ else {
                    return "";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            return "";
        }
    }

    public static String getMacNumber(Context context) {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                // LogMe.d("Stylus", "mac : " + mac);

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    // LogMe.d("Stylus", "aMac : " + aMac);
                    buf.append(String.format("%02x:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
            Log.d("Abrar", "Exception: " + ex.getLocalizedMessage());
            return null;
        }
        return "";

       /* WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            info = manager.getConnectionInfo();
            if (info.getMacAddress() != null) {
                return info.getMacAddress();
            } else {
                return null;
            }
        } else {
            return null;
        }*/
        //permission.ACCESS_NETWORK_STATE

    }


    public static String getAndroidID(Context context) {
        try {
            if (Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID) != null) {
                return Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } else {
                return "Android10+";
            }
        } catch (Exception e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            return "Android10+";
        }
    }

    public static String appVersion() {
        Context context = BaseApplication.getBaseAppInstance().getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();

        String myVersionName; // initialize String

        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
            return myVersionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public static void saveMap(String key, Map<String, String> inputMap) {
        SharedPreferences pSharedPref = BaseApplication.getBaseAppInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(key).apply();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }
    public static final String USERDATA = "storelist";
    public static Map<String, String> loadMap(String key) {
        Map<String, String> outputMap = new HashMap<String, String>();
        SharedPreferences pSharedPref = BaseApplication.getBaseAppInstance().getSharedPreferences(USERDATA, Context.MODE_PRIVATE);
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

    public static void showInfoDialog(Context context, String message) {
        final View dialogView = View.inflate(context, R.layout.dialog_info_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        alertDialog = builder.create();

        TextView infoText = dialogView.findViewById(R.id.info_message);
        Button okBtn = dialogView.findViewById(R.id.okButton);
        infoText.setText(message);
        alertDialog.show();

        okBtn.setOnClickListener(v -> {
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        });

    }
}
