package com.lifestyle.buddydetagging.utils;

import static com.lifestyle.buddydetagging.base.BaseApplication.getBaseAppInstance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.view.login.LoginActivity;
import com.lifestyle.buddydetagging.view.login.LoginActivityNew;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


@SuppressLint("SimpleDateFormat")
public class CommonUtility {

    //Check object is null
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    //Check object is null
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static void showSnackBar(Activity activity, String msg) {
        if (activity != null) {
            Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            params.width = getWidth(activity);
            view.setLayoutParams(params);
            snack.show();
        }
    }


    //supriya added 06-10-2023
    public static void showSnackBarr(Context context, String msg) {
        if (context != null && context instanceof Activity) {
            Activity activity = (Activity) context;
            View rootView = activity.findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar snack = Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG);
                View view = snack.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = Gravity.BOTTOM;
                params.width = getWidth((Activity) context);
                view.setLayoutParams(params);
                snack.show();
            }
        }
    }




    public static int getWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
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

    public static int convertDpToPx(int dp, Context context) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    public static String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return (df.format(cal.getTime()));
    }


    public static String getCurrentDatee() {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return (df.format(cal.getTime()));
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToRequiredFormat(String date, String currentFormat, String requiredFormat) {
        try {
            SimpleDateFormat actFormat = new SimpleDateFormat(currentFormat);
            SimpleDateFormat reqFormat = new SimpleDateFormat(requiredFormat);
            Date reqFormatDate = actFormat.parse(date);
            return reqFormat.format(reqFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }


    public static void autoLogout(Context context) {

        System.gc();
        PreferenceUtils.delleteAll();
        Intent intent = new Intent(context, LoginActivityNew.class);//supriya call LoginActivity.kt to LoginActivityNew
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) context).startActivity(intent);
        ((Activity) context).finish();

    }

    public static SSLContext getSSLConfigLS() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        InputStream cer = getBaseAppInstance().getResources().getAssets().open(getBaseAppInstance().getResources().getString(R.string.uarCertificatelss));


        ca = cf.generateCertificate(cer);

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        Log.d("Abrar", "SSL: " + sslContext);
        return sslContext;
    }

    public static X509TrustManager getTrustManager() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

        TrustManager[] trustManagers = getTrustManagerFactory().getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        return trustManager;
    }


    public static TrustManagerFactory getTrustManagerFactory() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

// Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
// I'm using Java7. If you used Java6 close it manually with finally.

       // InputStream cer = StylusMvvmApp.getInstance().getResources().getAssets().open(StylusMvvmApp.mInstance.getResources().getString(R.string.uarCertificate));
        InputStream cer = getBaseAppInstance().getResources().getAssets().open(getBaseAppInstance().getResources().getString(R.string.uarCertificatelss));
        ca = cf.generateCertificate(cer);

// Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

// Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        return tmf;

    }

    public static SSLSocketFactory getSslFactory() {

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, getTrustManagerFactory().getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return sslSocketFactory;
    }



}