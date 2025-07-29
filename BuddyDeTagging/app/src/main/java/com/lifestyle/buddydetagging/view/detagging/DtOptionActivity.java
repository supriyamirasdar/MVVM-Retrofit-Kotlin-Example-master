package com.lifestyle.buddydetagging.view.detagging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

 import com.lifestyle.buddydetagging.R;

import java.util.List;

public class DtOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dt_option);

        findViewById(R.id.btnsearchTrans).setOnClickListener(view -> {
            Intent intent = new Intent(DtOptionActivity.this, DtHomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnorderlist).setOnClickListener(view -> {
            Intent intent = new Intent(DtOptionActivity.this, DtOrderListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnOrderDetail).setOnClickListener(view -> {
            Intent intent = new Intent(DtOptionActivity.this, DtOrderDetailActivity.class);
            intent.putExtra("Type", "1");
            startActivity(intent);
        });
        findViewById(R.id.btnOrderDetail_two).setOnClickListener(view -> {
            Intent intent = new Intent(DtOptionActivity.this, DtOrderDetailActivity.class);
            intent.putExtra("Type", "2");
            startActivity(intent);
        });
        findViewById(R.id.btnOrderDetail_three).setOnClickListener(view -> {
            Intent intent = new Intent(DtOptionActivity.this, DtOrderDetailActivity.class);
            intent.putExtra("Type", "3");
            startActivity(intent);
        });


        findViewById(R.id.codeScsannedBT).setOnClickListener(view -> {
            scanBarcode();
        });


        findViewById(R.id.confirmDialogBT).setOnClickListener(view -> {
            showMessageOKCancel("De-tag","carry bag is opted, please provide carry bag.", DtOptionActivity.this,
                    (dialog, which) ->  Log.e("TAGG", ""));
        });
    }


    private void scanBarcode() {
        if ((Build.BRAND.equalsIgnoreCase("SUNMI"))) {
            Intent intent = new Intent();
            intent.setAction("com.sunmi.scan");
            intent.putExtra("IS_SHOW_SETTING", false);
            intent.putExtra("IDENTIFY_MORE_CODE", true);
            intent.setClassName("com.sunmi.sunmiqrcodescanner", "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
            intent.putExtra("CURRENT_PPI", 0X0003);
            intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);
            startActivityForResult(intent, 120);

        } else {
            /*if (checkPermissionAPI(DtOptionActivity.this, Manifest.permission.CAMERA)) {
                if (getSuppourtedFocusedModes()) {
                    Intent intent = new Intent(DtOptionActivity.this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, 9001);
                } else {
                    Intent intent = new Intent(DtOptionActivity.this, CaptureActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 101);
                }

            }*/
        }
    }


    final public static int REQUEST_CODE_ASK_PERMISSIONS = 125;

    public static boolean checkPermissionAPI(final Activity mActivity, final String androidManifestPermissionName) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(mActivity, androidManifestPermissionName);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, androidManifestPermissionName)) {
                showMessageOKCancel("De-tag", "You need to allow permission", mActivity,
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


    public static void showMessageOKCancel(String title, String message, Activity mActivity, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity).setTitle(""+ title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}