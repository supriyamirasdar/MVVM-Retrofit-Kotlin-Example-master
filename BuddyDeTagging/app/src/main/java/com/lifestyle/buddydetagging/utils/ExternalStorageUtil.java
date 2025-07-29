package com.lifestyle.buddydetagging.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExternalStorageUtil {

    private static boolean isExternalStorageMounted() {
        String dirState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(dirState)) {
            return true;
        } else {
            return false;
        }
    }

    public static File isFileExists(Context context, String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory(),
                "BrandMark/Logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }


        return new File(dir, fileName);
    }

    public static File isSviFileExists(Context context, String fileName) {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/Stylus_Log");
        dir.mkdirs();
        /*if (isExternalStorageMounted()) {
            assert context != null;
            file = new File(context.getExternalFilesDir("stylus_cache").getAbsoluteFile(), fileName);
        }*/
        return new File(dir, fileName);
    }


    public static void writeToFile(String currentStacktrace) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "BrandMark/CrashReports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MMM-yyyy");
            Date date = new Date();
            String filename = dateFormat.format(date) + "_Log.txt";

            File reportFile = new File(dir, filename);
            FileOutputStream f = new FileOutputStream(reportFile,true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)+ currentStacktrace);
            pw.flush();
            pw.close();
            f.close();

        } catch (Exception e) {
            Log.e("Abrar", "Exception: "+e.getLocalizedMessage());
        }
    }


    public static void printLogs(String issue,String usecaseName) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "Stylus/Logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MMM-yyyy");
            Date date = new Date();
            String filename = usecaseName+ dateFormat.format(date) + "_Log.txt";

            File reportFile = new File(dir, filename);
            FileOutputStream f = new FileOutputStream(reportFile,true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(new SimpleDateFormat("\nHH:mm:ss", Locale.getDefault()).format(date)+ issue);
            pw.flush();
            pw.close();
            f.close();

        } catch (Exception e) {
            Log.e("Abrar", "Exception: "+e.getLocalizedMessage());
        }
    }

    public static void deleteLogFile() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            File directory = new File(Environment.getExternalStorageDirectory(),
                    "BrandMark/Logs");
            if (directory.exists()) {
                File[] listFiles = directory.listFiles();
                Date todayDate = format.parse(format.format(Calendar.getInstance().getTime()));
                assert listFiles != null;
                for (File file : listFiles) {
                    if (file.exists()) {
                        Log.d("Abrar","File: "+file);
                        Date fileDate = format.parse(format.format(new Date(file.lastModified())));
                        assert todayDate != null;
                        if (todayDate.after(fileDate)) {
                            Log.d("Abrar","Delete");
                            file.delete();
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.d("Abrar","Exception: "+e.getLocalizedMessage());
        }
    }

    public static void deleteCrashFile() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            File directory = new File(Environment.getExternalStorageDirectory(),
                    "BrandMark/CrashReports");
            if (directory.exists()) {
                File[] listFiles = directory.listFiles();
                Date todayDate = format.parse(format.format(Calendar.getInstance().getTime()));
                assert listFiles != null;
                for (File file : listFiles) {
                    if (file.exists()) {
                        Log.d("Abrar","File: "+file);
                        Date fileDate = format.parse(format.format(new Date(file.lastModified())));
                        assert todayDate != null;
                        if (todayDate.after(fileDate)) {
                            Log.d("Abrar","Delete");
                            file.delete();
                        }
                    }
                }
            }
        }catch (Exception e){
            Log.d("Abrar","Exception: "+e.getLocalizedMessage());
        }
    }
}