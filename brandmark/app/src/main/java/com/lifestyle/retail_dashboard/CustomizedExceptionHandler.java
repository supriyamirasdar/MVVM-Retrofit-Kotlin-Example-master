package com.lifestyle.retail_dashboard;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static com.lifestyle.retail_dashboard.utils.ExternalStorageUtil.writeToFile;

public class CustomizedExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    public CustomizedExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        String stacktrace = stringBuffSync.toString();
        printWriter.close();

        writeToFile(" : "+stacktrace);
        defaultUEH.uncaughtException(t, e);
    }
}
