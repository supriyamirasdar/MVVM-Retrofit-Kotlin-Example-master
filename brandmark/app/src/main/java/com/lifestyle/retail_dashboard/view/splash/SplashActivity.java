package com.lifestyle.retail_dashboard.view.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.databinding.ActivitySplashBinding;
import com.lifestyle.retail_dashboard.view.BarChartActivityMultiDataset;
import com.lifestyle.retail_dashboard.view.brand_ranking.activity.BrandRankHomActivity;
import com.lifestyle.retail_dashboard.view.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import static com.lifestyle.retail_dashboard.utils.DialogBox.showSimplgeDialog;
import static com.lifestyle.retail_dashboard.utils.Utility.goToNextScreen;


public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding mBinding;
    Handler handler;
    Runnable runnable;
    Animation fadeAnimation, slideUpAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        fadeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);

        startFadeAnimation();

        ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(800);
        fade_in.setFillAfter(true);
        mBinding.imageView3.startAnimation(fade_in);

        handler = new Handler();
        runnable = () -> {
            goToNextScreen(SplashActivity.this, LoginActivity.class);
            //goToNextScreen(SplashActivity.this, BrandRankHomActivity.class);
            finish();
        };

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            try {
                permissionWrapperMultiple();
            } catch (Exception e) {
                Log.d("Abrar", "Exception: "+e.getLocalizedMessage());
            }
        } else {
            callToNext();
        }

        Intent intent = getIntent();
        String statusPushReceived = "";
        if (intent.hasExtra("PUSH")) {
            statusPushReceived = intent.getExtras().getString("PUSH");
            //AppSingleton.getInstance().setStatusPushReceived(statusPushReceived);
        } else if (intent.hasExtra("PUSH_FRA")) {
            statusPushReceived = intent.getExtras().getString("PUSH_FRA");
            //AppSingleton.getInstance().setStatusPushReceivedFRA(statusPushReceived);
        }

    }

    private void callToNext() {
        handler.postDelayed(runnable, 2000);
    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    /*
         This function is used for multiple permission check
     */
    private void permissionWrapperMultiple() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Location");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Phone State");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
        if (!addPermission(permissionsList, Manifest.permission.BLUETOOTH))
            permissionsNeeded.add("Bluetooth");
        if (!addPermission(permissionsList, Manifest.permission.BLUETOOTH_ADMIN))
            permissionsNeeded.add("Bluetooth Admin");
        if (!addPermission(permissionsList, Manifest.permission.VIBRATE))
            permissionsNeeded.add("Vibrate");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("Access Network");
        if (!addPermission(permissionsList, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermission(permissionsList, Manifest.permission.CHANGE_WIFI_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_WIFI_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
            permissionsNeeded.add("Receive SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Call Phones");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showSimplgeDialog(this, "Allow Permissions", message, () ->
                        ActivityCompat.requestPermissions(SplashActivity.this,
                                permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS));
                return;
            }
            ActivityCompat.requestPermissions(SplashActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        callToNext();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    callToNext();
                } else {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    callToNext();
                } else {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    //finish();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startFadeAnimation() {
        mBinding.imageView4.startAnimation(fadeAnimation);
        mBinding.imageView3.startAnimation(fadeAnimation);
    }
}