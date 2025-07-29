package com.lifestyle.retail_dashboard.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.lifestyle.retail_dashboard.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lifestyle.retail_dashboard.base.BaseApplication.getBaseAppInstance;

@SuppressLint("HardwareIds")
public class Utility {

    public static void goToNextScreen(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
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


    public static String appVersion() {
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
    }

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
}
