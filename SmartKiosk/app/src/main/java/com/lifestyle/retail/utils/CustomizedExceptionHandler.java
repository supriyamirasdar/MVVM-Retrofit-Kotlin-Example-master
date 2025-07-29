package com.lifestyle.retail.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomizedExceptionHandler implements Thread.UncaughtExceptionHandler {


    private Thread.UncaughtExceptionHandler defaultUEH;
    public CustomizedExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        writeToFile(" : "+stacktrace);
        defaultUEH.uncaughtException(t, e);
    }

    public static void writeToFile(String currentStacktrace) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "Stylus/CrashReports");
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

    public static void writeToFileAllUseCase(String currentStacktrace) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    "SmartKiosk/Logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd-MMM-yyyy");
            Date date = new Date();
            String filename = dateFormat.format(date) + "_Log.txt";

            File reportFile = new File(dir, filename);
            FileOutputStream f = new FileOutputStream(reportFile, true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date) + currentStacktrace);
            pw.close();
            f.close();
            // Check and delete log files older than two day            pw.flush();s
            deleteOldLogFiles(dir);


        } catch (Exception e) {
            Log.e("Abrar", "Exception: " + e.getLocalizedMessage());
        }
    }

    public static void deleteOldLogFiles(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Get the file's creation date
                    if (file.exists()) {
                        Calendar time = Calendar.getInstance();
                        time.add(Calendar.DAY_OF_YEAR, -2);
                        //I store the required attributes here and delete them
                        Date lastModified = new Date(file.lastModified());
                        Log.i(" ", "lastModified: " + lastModified);

                        if (lastModified.before(time.getTime())) {
                            //file is older than two days
                            boolean deleted = file.delete();
                            if (deleted) {
                                Log.d(" ", "Deleted old log file: " + file.getName());
                            } else {
                                Log.d(" ", "Failed to delete old log file: " + file.getName());
                            }

                        }
                    }
                }
            }
        }
    }

}
