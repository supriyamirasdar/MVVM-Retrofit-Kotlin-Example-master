package com.lifestyle.buddydetagging.view.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.databinding.ActivitySplashBinding;
import com.lifestyle.buddydetagging.view.login.LoginActivityNew;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import static com.lifestyle.buddydetagging.utils.DialogBox.showSimplgeDialog;
import static com.lifestyle.buddydetagging.utils.Utility.goToNextScreen;


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
            goToNextScreen(SplashActivity.this, LoginActivityNew.class);
            //goToNextScreen(SplashActivity.this, BrandRankHomActivity.class);
            finish();
        };

        /*if (android.os.Build.VERSION.SDK_INT >= 23) {
            try {
                permissionWrapperMultiple();
            } catch (Exception e) {
                Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            }
        } else {
            callToNext();
        }*/

        // added ..  12-10-2021
        if (checkPermission()) {
            Log.e("SPLASH", "11 checkPermission :: " + checkPermission());
            callToNext();
        } else {
            Log.e("SPLASH", "22 checkPermission :: " + checkPermission());
            requestPermission();
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


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean result = Environment.isExternalStorageManager();
            int result2 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA);
            return result == true && result2 == PackageManager.PERMISSION_GRANTED;
        } else {
            int result = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CAMERA);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                    && result2 == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else {
               /* if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    //return true;
                } else {
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.VIBRATE}, 2);
                    //return false;
                }*/

                try {
                    permissionWrapperMultiple();
                } catch (Exception e) {
                    Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
                }
            }
        } else {
            callToNext();
        }
    }


    private void callToNext() {
        handler.postDelayed(runnable, 2000);
    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_NEW = 125;

    /*
         This function is used for multiple permission check
     */
    private void permissionWrapperMultiple() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    callToNext();
                } else {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    //finish();
                }
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_NEW:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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

    // Handling permission callback for Android 11 or above versions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success

                   // callToNext();

                    permissionWrapperMultipleNew();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /*
         This function is used for multiple permission check
     */
    private void permissionWrapperMultipleNew() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermissionNew(permissionsList, Manifest.permission.VIBRATE))
            permissionsNeeded.add("Vibrate");
        if (!addPermissionNew(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("Access Network");
        if (!addPermissionNew(permissionsList, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermissionNew(permissionsList, Manifest.permission.CHANGE_WIFI_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermissionNew(permissionsList, Manifest.permission.ACCESS_WIFI_STATE))
            permissionsNeeded.add("WI-FI");
        if (!addPermissionNew(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showSimplgeDialog(this, "Allow Permissions", message, () ->
                        ActivityCompat.requestPermissions(SplashActivity.this,
                                permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_NEW));
                return;
            }
            ActivityCompat.requestPermissions(SplashActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_NEW);
            return;
        }
        callToNext();
    }

    private boolean addPermissionNew(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission);
        }
        return true;
    }
}