package com.landmarkgroup.smartkiosk.ui.homescreen;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.BuildConfig;

import butterknife.ButterKnife;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.databinding.ActivityHomeScreenLsBinding;
import com.landmarkgroup.smartkiosk.model.DistrictListModel;
import com.landmarkgroup.smartkiosk.model.FetchAllDataResponse;
import com.landmarkgroup.smartkiosk.model.StoreListModel;
import com.landmarkgroup.smartkiosk.model.ValidEmployeeRequest;
import com.landmarkgroup.smartkiosk.model.ValidEmployeeResponse;
import com.landmarkgroup.smartkiosk.storage.ContextManager;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.util.Utils;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.landmarkgroup.smartkiosk.databinding.ActivityHomeScreenBinding;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.MissingSizeActivity;
import com.landmarkgroup.smartkiosk.model.AppVersion;
import com.landmarkgroup.smartkiosk.model.DeviceType;
import com.lifestyle.retail.constants.UserDataDefs;
import com.lifestyle.retail.utils.GeneralConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.landmarkgroup.smartkiosk.util.CustomDialogs.saveEmpIdandDismissDialog;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showErrorEmpDialog;
import static com.lifestyle.retail.utils.CommonUtility.showSnackBar;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.enterEmployeeCode;
import static com.lifestyle.retail.utils.VersionHelper.versionCompare;

public class HomeScreenActivity extends BaseCompatActivity {

    private HomeScreenActivity activity;
    private static final int BUFFER_SIZE = 4096;
    // ActivityHomeScreenBinding binding;

    private SharedPreferences sharedPreferences;
    private String[] modes;
    private PrefManager prefManager;
    int ouCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_home_screen);

        // binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);
        ContextManager.setAppContext(HomeScreenActivity.this);
        // added Dt: 29-07-2021
        activity = this;

        prefManager = new PrefManager(activity);
        ouCode = prefManager.getOuCode();
        // ouCode=3;
        if (ouCode == 3) {
            ActivityHomeScreenLsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen_ls);

            binding.btnSize.setOnClickListener(activity);
            binding.btnColor.setOnClickListener(activity);
            //btnStyle.setOnClickListener(activity);

            binding.btnEmployeeId.setOnClickListener(activity);
            binding.btnScanMode.setOnClickListener(activity);
            binding.btnWebsiteId.setOnClickListener(activity);
            if (prefManager.getOuName().equals("Max")) {
                // binding.btnWebsiteId.setVisibility(View.VISIBLE);

            } else {
                binding.btnWebsiteId.setVisibility(View.GONE);
            }
        } else {
            ActivityHomeScreenBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

            binding.btnSize.setOnClickListener(activity);
            // binding.btnColor.setOnClickListener(activity);//comment by supriya because only size button 11-04-2023
            //btnStyle.setOnClickListener(activity);
            binding.btnSettingId.setOnClickListener(activity);
            binding.btnEmployeeId.setOnClickListener(activity);
            binding.btnWebsiteplussizeId.setOnClickListener(activity);//added by supriya 27-03-2023
            binding.btnScanMode.setOnClickListener(activity);
            binding.btnWebsiteId.setOnClickListener(activity);
            if (prefManager.getOuName().equals("Max")) {
                // binding.btnWebsiteId.setVisibility(View.VISIBLE);
                binding.btnWebsiteplussizeId.setVisibility(View.VISIBLE);//added by supriya 28-03-2023
            } else {
                binding.btnWebsiteId.setVisibility(View.GONE);
                binding.btnWebsiteplussizeId.setVisibility(View.VISIBLE);//adde by supriya 8-03-2023
            }

            // added Dt: 01-12-2022
            // show app version on home screen ..
            binding.appversionTV.setText(Utils.getAppVersionWithDate(HomeScreenActivity.this));
            binding.StoreId.setText(prefManager.getStoreId() + " - " + prefManager.getStoreName());

        }
        initialize();
    }

    protected void initialize() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeScreenActivity.this);
        activity = this;
        ButterKnife.bind(activity, activity);
        prefManager = new PrefManager(activity);
        // initView();
        prefManager.setEmployeeId(null);
        getUpdatedVersion();

    }

    private void initView() {
        /*binding.btnSize.setOnClickListener(activity);
        binding.btnColor.setOnClickListener(activity);
        //btnStyle.setOnClickListener(activity);
        binding.btnEmployeeId.setOnClickListener(activity);
        binding.btnScanMode.setOnClickListener(activity);*/

        /*int ouCode = prefManager.getOuCode();
        if (ouCode == 3) {
            // lifestyle selected..
            binding.homeScreenRL.setBackgroundColor(getResources().getColor(R.color.ls_theme_color));
            binding.homeLogoIV.setImageDrawable(getResources().getDrawable(R.drawable.lifestyle_logo));
        } else {
            // remain default theme..ic_omni_kiosk_logo
            binding.homeScreenRL.setBackgroundResource(R.drawable.background);
            binding.homeLogoIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_omni_kiosk_logo));
        }*/

        //  chnage button drawable
        // v.setBackground(getResources().getDrawable(R.drawable.ic_background));
    }


    String tagType = "HomeScreen";

    private void getUpdatedVersion() {
        showProgressDialog();
        DeviceType request = new DeviceType();
        request.setDeviceType("Personal");
        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().getUpdatedAppVersion(), GeneralConstant.LOGIN_DETAILS);
    }


    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.LOGIN_DETAILS) {
            if (statusResponse instanceof AppVersion) {

                if (statusResponse != null) {
                    AppVersion response = (AppVersion) statusResponse;
                    if (response.getServerErrormsg() != null) {
                        showSnackBar(activity, response.getServerErrormsg());
                        showErrorWhenGetData();
                    } else {
                        getUpdatedVersion(response.getMobAppsVersion(),
                                response.getFtpHostName(), response.getFtpPortNum(),
                                response.getFtpUsr(), response.getFtpPwd());
                    }
                } else {
                    showSnackBar(activity, "Please Try Again!!!");
                    showErrorWhenGetData();
                }
            }
        } else if (requestCode == GeneralConstant.REGISTRATION_VALID_EMPLOYEE) {
            if (statusResponse instanceof ValidEmployeeResponse) {
                ValidEmployeeResponse validEmployeeResponse = (ValidEmployeeResponse) statusResponse;
                if (validEmployeeResponse.getSuccessIndi().equalsIgnoreCase("true")) {
                    // save data in preference manager..
                    saveEmpIdandDismissDialog(HomeScreenActivity.this, empCode);
                } else {
                    // show error message..
                    String errorMsg = validEmployeeResponse.getStatusErrMessage();
                    // showSnackBar(activity, "" + errorMsg);
                    showErrorEmpDialog(errorMsg);
                }
            }
        }
    }

    public void showErrorWhenGetData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSettingId:
                showLocalOrders(activity, ouCode);
                break;
            case R.id.btnEmployeeId:
                enterEmployeeCode(activity, ouCode);
                break;

            case R.id.btnWebsiteId:
                // openWebsitePage(activity, ouCode);
                break;
            case R.id.btnWebsiteplussizeId:
                openWebsitePageCurve(activity, ouCode);
                break;
            case R.id.btnSize:
            case R.id.btnColor:
                /*enterMobileNo(activity, data -> new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activity, MissingSizeActivity.class);
                        intent.putExtra("custMobile", data);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }, 200), ouCode);*/
                prefManager.setCallType("PDP");
                Intent intent = new Intent(activity, MissingSizeActivity.class);
                intent.putExtra("custMobile", "0000000000");
                intent.putExtra("CallFor", "MAIN");
                intent.putExtra("flag", "");
//                Intent intent = new Intent(activity, SizeOptionsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.btnScanMode:
                showDialog(getScanModeArray());
                break;
            /*case R.id.btnStyle:
                startActivity(new Intent(activity, BrowserCatelogActivity.class));
                break;*/
        }
    }


    private void openWebsitePage(HomeScreenActivity activity, int ouCode) {


        prefManager.setCallType("WEB");
        // open wensite page..
        // selOuUrl..
        Intent intent = new Intent(activity, MissingSizeActivity.class);
        intent.putExtra("custMobile", "0000000000");
        intent.putExtra("CallFor", "WEB");
        intent.putExtra("flag", "0");
        //Intent intent = new Intent(activity, SizeOptionsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void openWebsitePageCurve(HomeScreenActivity activity, int ouCode) {

        prefManager.setCallType("WEB");
        // open wensite page..
        // selOuUrl..

        Intent intent = new Intent(activity, MissingSizeActivity.class);
        intent.putExtra("custMobile", "0000000000");
        intent.putExtra("CallFor", "WEB");
        intent.putExtra("flag", "1");
        //Intent intent = new Intent(activity, SizeOptionsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void displayFragment(String sysVerTes, String hostName, String portNum, String ftpUser, String ftpPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Download APK");
        builder.setCancelable(false);
        builder.setMessage("Smart Kiosk App version has been upgraded to " + sysVerTes + " version. Please update.");
        builder.setPositiveButton("Update", (dialog, which) ->
                new DownloadFile().execute(hostName, portNum, ftpUser, ftpPassword));

        builder.show();
    }


    public void getUpdatedVersion(String version, String hostName, String portNum, String ftpUser, String ftpPassword) {
        if (version != null) {
            try {
                String curVerTes = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                /*Log.d("Abrar", "SMART KIOSK 11 V :" + curVerTes + " new " + version);
                version = "1.1";
                curVerTes = "1.0";
                Log.d("Abrar", "SMART KIOSK 22 V :" + curVerTes + " new " + version);*/

                if (versionCompare(curVerTes, version) < 0) {
                    displayFragment(version, hostName, portNum, ftpUser, ftpPassword);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public class DownloadFile extends AsyncTask<String, Integer, Void> {
        int status = 0;
        int notificationID = 1;
        NotificationManager manager;
        NotificationCompat.Builder builder;
        private Dialog dialog;
        private ProgressBar mProgressBar;
        private TextView tvdownload, tvPercentage;
        private String fileUrl = getExternalFilesDir("SmartKiosk").getAbsoluteFile().getAbsolutePath();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(activity);
            builder.setContentTitle("Smart Kiosk");
            builder.setContentText("Download in Progress");
            builder.setSmallIcon(R.mipmap.ic_launcher);

            manager.notify(notificationID, builder.build());

            final View dialogView = View.inflate(activity, R.layout.apk_download_layout, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView);
            builder.setTitle("Downloading...");
            builder.setCancelable(false);

            dialog = builder.create();
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            mProgressBar = dialogView.findViewById(R.id.progressBar);
            tvdownload = dialogView.findViewById(R.id.tvdownload);
            tvPercentage = dialogView.findViewById(R.id.tvPercentage);

            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            builder.setProgress(100, values[0], true);
            manager.notify(notificationID, builder.build());
            mProgressBar.setIndeterminate(false);
            mProgressBar.setMax(100);

            /*mProgressBar.setProgress(values[0]);
            tvdownload.setText(values[0] + "/100");
            tvPercentage.setText(values[0] + "%");*/

            if (values[0] >= 99) {

            } else {
                mProgressBar.setProgress(values[0]);

                tvdownload.setText(values[0] + "/100");
                tvPercentage.setText(values[0] + "%");
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            publishProgress(100);

            if (dialog.isShowing()) {
                mProgressBar.setProgress(100);
                tvPercentage.setText("100%");
                tvdownload.setText("100/100");

                dialog.dismiss();
            }

            builder.setContentText("Download Complete");
            builder.setProgress(0, 0, false);
            manager.notify(notificationID, builder.build());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    manager.cancel(notificationID);
                    Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                    sendBroadcast(intent);
                }
            }, 2000);

            if (status == 1)
                Toast.makeText(activity, "Download Failed. Please Check Network Connection.", Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(activity, "File downloaded", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls())
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                    else
                        installAPK();
                } else
                    installAPK();
            }
        }

        @Override
        protected Void doInBackground(String... param) {
            String ftpHostName = param[0];
            String ftpUsr = param[2];
            String ftpPwd = param[3];
            String filePath = "/SmartKiosk.apk";
            String ftpUrl = "ftp://" + ftpUsr + ":" + ftpPwd + "@" + ftpHostName + filePath;
            boolean runOnes = true;

            /*File f = new File(fileUrl, "SmartKiosk.apk");
            if (f.exists()) {
                f.delete();
                try {
                    f.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
            try {
                File apkFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "SmartKiosk.apk");
                if (apkFile.exists()) {
                    apkFile.delete();
                    apkFile.createNewFile();
                }

                ftpUrl = String.format(ftpUrl, ftpUsr, ftpPwd, ftpHostName);
                Log.d("Abrar", "FTPUrl : " + ftpUrl);


                java.net.URL url = new URL(ftpUrl);
                URLConnection conn = url.openConnection();

                //int totalLength = conn.getContentLength();
                int totalLength = 16000000;

                if (runOnes) {
                    runOnes = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(activity, "Downloading started..", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                //publishProgress(0);

                InputStream inputStream = conn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(apkFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                int total = 0;
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                long fileLength = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    fileLength = conn.getContentLengthLong();
                }
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    total += bytesRead;
                    //publishProgress((total * 100) / totalLength);
                    //System.out.println(" fileLength :: " + fileLength);
                    //count += total;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    else
                        publishProgress((total * 100) / totalLength);
                    outputStream.write(buffer, 0, bytesRead);
                }


                outputStream.close();
                inputStream.close();

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(activity, getPackageName(),
                            new File(fileUrl, "SmartKiosk.apk"));
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Uri apkUri = Uri.fromFile(new File(fileUrl, "SmartKiosk.apk"));
                    Intent mintent = new Intent(Intent.ACTION_VIEW);
                    mintent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(mintent);
                }*/
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("Abrar", "FTPUrl : " + ex);
                status = 1;
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    installAPK();
                }
            }
        }
    }

    void installAPK() {
        String PATH = Environment.getExternalStorageDirectory() + "/SmartKiosk.apk";
        File file = new File(PATH);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(uriFromFile(getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                getApplicationContext().startActivity(intent);
            } catch (Exception e) {
                Log.d("Abrar", "Exception:  " + e.getLocalizedMessage());
                //writeToFile("\nInstall APK:  " + e.getLocalizedMessage());
            }
        } else {
            Log.d("Abrar", "File Not found:  ");
        }
    }

    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    protected String[] getScanModeArray() {
        boolean isPortrait = sharedPreferences.getBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, true);
        if (isPortrait) {
            modes = new String[]{"Back Camera", "Front Camera"};
        } else {
            modes = new String[]{"Front Camera", "Back Camera"};
        }
        return modes;
    }

    private void showDialog(String[] arr) {
        final View dialogView = View.inflate(this, R.layout.settings_selection_background, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        RadioGroup radioGroup = dialog.findViewById(R.id.dialog_radio_group);
        RadioButton rb1 = dialog.findViewById(R.id.rb1);
        RadioButton rb2 = dialog.findViewById(R.id.rb2);
        rb1.setText(arr[0]);
        rb2.setText(arr[1]);
        Button okBtn = dialog.findViewById(R.id.dialog_ok);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (group.getCheckedRadioButtonId() == R.id.rb1) {
                Log.i("RadioGroup", "First Button");
                if (rb1.getText().toString().equals("Portrait")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCANNER_ROTATION.key, true);
                } else if (rb1.getText().toString().equals("Landscape")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCANNER_ROTATION.key, false);
                } else if (rb1.getText().toString().equalsIgnoreCase("Front Camera")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, false);
                } else if (rb1.getText().toString().equalsIgnoreCase("Back Camera")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, true);
                } else if (rb1.getText().toString().equalsIgnoreCase("Sale")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SALE_WITHOUT_SALE.key, true);
                } else if (rb1.getText().toString().equalsIgnoreCase("Without Sale")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SALE_WITHOUT_SALE.key, false);
                }
                editor.apply();
            } else {
                Log.i("RadioGroup", "Second Button");
                if (rb2.getText().toString().equals("Portrait")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCANNER_ROTATION.key, true);
                } else if (rb2.getText().toString().equals("Landscape")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCANNER_ROTATION.key, false);
                } else if (rb2.getText().toString().equalsIgnoreCase("Front Camera")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, false);
                } else if (rb2.getText().toString().equalsIgnoreCase("Back Camera")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, true);
                } else if (rb2.getText().toString().equalsIgnoreCase("Sale")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SALE_WITHOUT_SALE.key, true);
                } else if (rb2.getText().toString().equalsIgnoreCase("Without Sale")) {
                    editor.putBoolean(UserDataDefs.USER_DATA.SALE_WITHOUT_SALE.key, false);
                }
                editor.commit();
            }
        });
        dialog.show();
        okBtn.setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.cancel();
        });


    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // added Dt: 08-sep-2022
    private void showLocalOrders(HomeScreenActivity activity, int ouCode) {
        Intent intent = new Intent(activity, OrderListActivity.class);
        // intent.putExtra("custMobile", "0000000000");
        // intent.putExtra("CallFor", "WEB");
        // Intent intent = new Intent(activity, SizeOptionsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // added Dt: 01-12-2022
    String empCode = null;
    String successIndi = "false";

    public void validateEmployeeId(String empCode) {
        this.empCode = empCode;
        ValidEmployeeRequest validEmployeeRequest = new ValidEmployeeRequest();
        validEmployeeRequest.employeeId = empCode;
        validEmployeeRequest.deviceType = "";
        validEmployeeRequest.macId = "";
        validEmployeeRequest.imei = "";
        validEmployeeRequest.uniqueAndroidId = "";
        validEmployeeRequest.currentVersion = "";
        network(getApiInterface().validateEmployee(validEmployeeRequest), GeneralConstant.REGISTRATION_VALID_EMPLOYEE);

    }


}