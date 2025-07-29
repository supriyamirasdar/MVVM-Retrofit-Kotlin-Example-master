package com.lifestyle.buddydetagging.view.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.BuildConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.constant.ApiConstant;
import com.lifestyle.buddydetagging.constant.GeneralConstant;
import com.lifestyle.buddydetagging.databinding.ActivityLoginNewBinding;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.Constant;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.base.BaseCompatActivity;
import com.lifestyle.buddydetagging.view.detagging.DtHomeActivity;
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppVrsnResponse;
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppsVrsnCheckRequest;
import com.lifestyle.buddydetagging.view.login.api.LoginApis;
import com.lifestyle.buddydetagging.view.login.dto.LoginResponseDTO;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;
import com.lifestyle.buddydetagging.view.login.dto.UserDTO;
import com.lifestyle.buddydetagging.view.login.dto.UserLoginDTO;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;


import static com.lifestyle.buddydetagging.utils.VersionHelper.versionCompare;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivityNew extends BaseCompatActivity implements LoginApis.LoginDetailListener {

    public static String accessToken;
    ActivityLoginNewBinding binding;
    LoginActivityNew activity;
    UserDTO dto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login_new);
        activity = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_new);
        initView();
        loginTextWatcher();

    }

    public void initView() {
        binding.toolbarTop.tvToolBarTitle.setText("LOGIN");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);

        activity = this;

        binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg);

        binding.btnForgotPassword.setOnClickListener(view -> {
            goToNextScreen(activity, DtForgotPasswordActivity.class);
        });

        binding.btnLogin.setOnClickListener(view -> {
            doLogin();
        });

        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.password.getWindowToken(), 0);
                    String itemCode = binding.password.getText().toString().trim();

                    if (validLogin()) {
                        binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg);
                    } else {
                        binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg);
                    }
                    doLogin();
                    return true;
                }
                return false;
            }
        });

        String appVersion = getResources().getString(R.string.dev_mode) + " " + Utility.appVersion()
                + " Dt:" + getResources().getString(R.string.releaseDate);
        binding.tvAppVersion.setText(appVersion);

        binding.showPassBtn.setOnClickListener(view -> {
            ShowHidePass();
        });
    }

    private void loginTextWatcher() {

        binding.userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.length() > 5) {
                    binding.correct.setVisibility(View.VISIBLE);
                    binding.wrong.setVisibility(View.INVISIBLE);
                } else {
                    binding.correct.setVisibility(View.GONE);
                    binding.wrong.setVisibility(View.VISIBLE);
                }
                if (validLogin()) {

                    binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();

            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validLogin()) {
                    binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
            }
        });
    }

    private String userId = null;
    private String password = null;

    private void doLogin() {
        if (validLogin()) {
            String user = binding.userId.getText().toString();
            String pass = binding.password.getText().toString();
            if (TextUtils.isEmpty(user)) {
                String titleST = "De-Tagging";
                String messageST = "Enter User Id.";
                showAlertDialog(activity, titleST, messageST);
            } else if (user.length() < 7) {
                String titleST = "De-Tagging";
                String messageST = "Enter Valid User Id.";
                showAlertDialog(activity, titleST, messageST);
            } else if (TextUtils.isEmpty(pass)) {
                String titleST = "De-Tagging";
                String messageST = "Enter Password.";
                showAlertDialog(activity, titleST, messageST);
            } else if (pass.length() < 6) {
                String titleST = "De-Tagging";
                String messageST = "Enter Valid password.";
                showAlertDialog(activity, titleST, messageST);
            } else {
                userId = user;
                password = pass;
                checkLogin();
            }
        }
    }

    private boolean validLogin() {
        String user = binding.userId.getText().toString();
        String pass = binding.password.getText().toString();

        if (TextUtils.isEmpty(user)) {
            return false;
        } else if (user.length() < 7) {
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            return false;
        } else if (pass.length() < 6) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoLogin();
    }

    private void autoLogin() {
        Log.d("Abrar", "User ${PreferenceUtils.getUserId()} pass ${PreferenceUtils.getUserPass()}");
        if (PreferenceUtils.getUserId() != null
                && PreferenceUtils.getUserPass() != null) {

            userId = PreferenceUtils.getUserId();
            password = PreferenceUtils.getUserPass();
            binding.userId.setText(userId);
            checkLogin();
        }
    }

    private void checkLogin() {
        LoginApis api = new LoginApis(activity);
        UserLoginDTO request = new UserLoginDTO();
        request.setUserId(userId);
        request.setPwd(password);// CryptoUtil.encrypt(password)
        request.setRole(2); //= 2;
        Log.e("Login APi request==", new Gson().toJson(request));
        api.checkLogin(activity, request);
        //network(getApiInterface().changePassword(request), Constant.LOGIN_DETAIL);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void loginDetail(@NotNull LoginResponseDTO response) {

        if (response.getStatusCode() == 200) {

            UserDTO userDto = new UserDTO();
            userDto = response.getData();
            PreferenceUtils.setUserId(response.getData().getUserId());
            PreferenceUtils.setUserPass(password);
            String username = response.getData().getfName() + " " + response.getData().getlName();
            PreferenceUtils.setUserName(username);
            //PreferenceUtils.setAuthKey(response.authKey)
            PreferenceUtils.setMobileNo(response.getData().getContactNo());
            PreferenceUtils.setEmailId(response.getData().getEmail());
            PreferenceUtils.setStoreId("0" + response.getData().getStroreId());
            PreferenceUtils.setStoreName(response.getData().getStroreName());
            PreferenceUtils.setRole(response.getData().getRole());
            PreferenceUtils.setUserStatus(response.getData().getStatus());
            PreferenceUtils.setOuCode(response.getData().getOuCode());
            PreferenceUtils.setPwdChangeReq(response.getData().getPwdChgReq());

               /* if(response.getData().getOuCode().equalsIgnoreCase("MAX")) {
                PreferenceUtils.setStoreId("0" + response.getData().getStroreId());
            }else{
                PreferenceUtils.setStoreId(response.getData().getStroreId());
            }*/

            if (response.getData().getPwdChgReq().equals("Y")) {
                CommonUtility.showSnackBar(activity, response.getStatusMessage());
                new android.os.Handler().postDelayed(() -> {
                    goToNextScreen(activity, DtChangePasswordActivity.class, userId);
                    finish();
                }, 500);
            } else {
                CommonUtility.showSnackBar(activity, "Login Successful.");
               /* new android.os.Handler().postDelayed(() -> {
                    goToNextScreen(activity, DtHomeActivity.class);
                    finish();
                }, 500);*/

                //added by supriya FOR ACCESS TOKEN 24-03-2023


                checkingVersionControll();


            }

        } else if (response.getStatusCode() == 400) {
            String titleST = "De-Tagging";
            String messageST = "" + response.getStatusMessage();
            showAlertDialog(activity, titleST, messageST);
        }
    }

    AlertDialog alertDialog;

    private void showAlertDialog(Context context, String title, String message) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.layout_dt_alert_dialog, null);
        alertDialog = new AlertDialog.Builder(context).setInverseBackgroundForced(true).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(deleteDialogView);
        TextView titleTV = deleteDialogView.findViewById(R.id.titleTV);
        TextView alertMessageTV = deleteDialogView.findViewById(R.id.alertMessageTV);
        TextView yesBt = deleteDialogView.findViewById(R.id.yesBt);
        TextView noBt = deleteDialogView.findViewById(R.id.noBt);

        noBt.setVisibility(View.GONE);

        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        yesBt.setText("Okay");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void ShowHidePass() {

        if (binding.password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            binding.showPassBtn.setImageResource(R.drawable.ic_eye_open);

            //Show Password
            binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            binding.showPassBtn.setImageResource(R.drawable.ic_eye_close);

            //Hide Password
            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

    }

    public void openHomeScreen() {
        goToNextScreen(activity, DtHomeActivity.class);
        finish();
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // check for app version..


    private String curVerTes;

    private void checkingVersionControll() {
        StylusAppsVrsnCheckRequest stylusAppsVrsnCheckRequest = new StylusAppsVrsnCheckRequest();
        stylusAppsVrsnCheckRequest.deviceType = "Personal"; //PreferenceUtils.get_devicedata_type();
        // network(getApiInterface().checkMobAppVersion(stylusAppsVrsnCheckRequest), GeneralConstant.CHECK_APP_VERSION);
        network(getApiInterface().checkMobAppVersion(), GeneralConstant.CHECK_APP_VERSION);
    }

    @Override
    public void onSuccess(ResponseDTO statusResponse, int requestCode) {
        if (requestCode == GeneralConstant.CHECK_APP_VERSION) {
            if (statusResponse instanceof StylusAppVrsnResponse) {
                StylusAppVrsnResponse vrsnResponse = (StylusAppVrsnResponse) statusResponse;
                if (vrsnResponse.getStatusCode() != 200) {
                    showAlert(vrsnResponse.getStatusMessage());
                } else {
                    parseResponse(vrsnResponse);
                }
            }
        }
    }

    private void parseResponse(StylusAppVrsnResponse response) {
        try {
            curVerTes = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
        }

        if (response.getMobAppsVersion() != null) {
            String sysVerTes = response.getMobAppsVersion();
            Log.d("Abrar", "BuddyDetag 11 V :" + curVerTes + " new " + sysVerTes);
           /* sysVerTes = "1.1";
            curVerTes = "1.0";
            Log.d("Abrar", "BuddyDetag 22 V :" + curVerTes + " new " + sysVerTes);*/
            if (curVerTes != null && sysVerTes != null) {
                if (versionCompare(curVerTes, sysVerTes) < 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Download APK");
                    builder.setCancelable(false);
                    builder.setMessage("BuddyDetag App version has been upgraded to " + sysVerTes + ", Do you want to download..?");
                    builder.setPositiveButton("YES", (arg0, arg1) ->
                            new DownloadFile().execute(response.getFtpHostName(), response.getFtpPortNum(),
                                    response.getFtpPwd(), response.getFtpUsr()));

                    builder.show();
                } else {
                    Log.d("Abrar", "curVerTes  greater then or equal to  sys V :");
                    // check for store id and move forward..  P1
                    //checkForStoreId();

                    if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {

                        AcessToken();
                    } else {
                        openHomeScreen();
                    }

                }
            } else {
                Log.d("Abrar", "BuddyDetag curVerTes  and  sysVerTes  are null ::" + curVerTes + " new " + sysVerTes);
                // check for store id and move forward..
                //checkForStoreId();
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {

                    AcessToken();
                } else {
                    openHomeScreen();
                }
            }
        } else {
            Log.d("Abrar", "response.getMobAppsVersion() == null :" + response.getMobAppsVersion());
            // check for store id and move forward..
            //checkForStoreId();
            openHomeScreen();
        }
    }

    public class DownloadFile extends AsyncTask<String, Integer, Void> {
        int status = 0;
        int notificationID = 1;
        NotificationManager manager;
        NotificationCompat.Builder builder;
        private Dialog dialog;
        private ProgressBar mProgressBar;
        private TextView messageTV, tvdownload, tvPercentage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(activity);
            builder.setContentTitle("BuddyDetag");
            builder.setContentText("Download in progress");
            builder.setSmallIcon(R.mipmap.ic_launcher);

            manager.notify(notificationID, builder.build());

            final View dialogView = View.inflate(activity, R.layout.apk_download_layout, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView);
            builder.setTitle("Downloading");
            builder.setCancelable(false);

            dialog = builder.create();
            assert dialog.getWindow() != null;
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            messageTV = dialogView.findViewById(R.id.messageTV);
            mProgressBar = dialogView.findViewById(R.id.progressBar);
            tvdownload = dialogView.findViewById(R.id.tvdownload);
            tvPercentage = dialogView.findViewById(R.id.tvPercentage);
            messageTV.setText("Downloading new version of BuddyDetag");

            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // super.onProgressUpdate(values);
            builder.setProgress(100, values[0], true);
            manager.notify(notificationID, builder.build());
            mProgressBar.setIndeterminate(false);
            mProgressBar.setMax(100);

            if (values[0] >= 99) {

            } else {
                mProgressBar.setProgress(values[0]);

                tvdownload.setText(values[0] + "/100");
                tvPercentage.setText(values[0] + "%");
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            runOnUiThread(() -> {
                publishProgress(100);
            });

            if (dialog.isShowing()) {
                mProgressBar.setProgress(100);
                tvPercentage.setText("100%");
                tvdownload.setText("100/100");

                dialog.dismiss();
            }

            builder.setContentText("Download Complete");
            builder.setProgress(0, 0, false);
            manager.notify(notificationID, builder.build());

            new Handler().postDelayed(() -> {
                manager.cancel(notificationID);
                Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(intent);
            }, 2000);

            if (status == 1)
                CommonUtility.showSnackBar(activity, "Download Failed. Please Check Network Connection.");
            else {
                CommonUtility.showSnackBar(activity, "File downloaded");
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
        protected Void doInBackground(String... params) {
            String host = params[0];
            String user = params[3];
            String pass = params[2];
            String filePath = "/BuddyDetag.apk";
            String ftpUrl = "ftp://" + user + ":" + pass + "@" + host + filePath;

            Log.e("HOME", "ftpUrl :: " + ftpUrl);
            try {
                File apkFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "BuddyDetag.apk");
                if (apkFile.exists()) {
                    apkFile.delete();
                    apkFile.createNewFile();
                }

                ftpUrl = String.format(ftpUrl, user, pass, host);

                URL url = new URL(ftpUrl);
                URLConnection conn = url.openConnection();

                int totalLength = 12000000;

                runOnUiThread(() -> CommonUtility.showSnackBar(activity, "Downloading started.."));

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
                    Uri apkUri = FileProvider.getUriForFile(activity, "com.lifestyle.retail",
                            new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "BuddyDetag.apk"));
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Uri apkUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "BuddyDetag.apk"));
                    Intent mintent = new Intent(Intent.ACTION_VIEW);
                    mintent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(mintent);
                }*/
            } catch (Exception e) {
                Log.d("Abrar", "ftpUrl : " + e);
                // writeToFile("\nDownload APK:  " + e.getLocalizedMessage());
                status = 1;
            }

            return null;
        }

        private static final int BUFFER_SIZE = 4096;
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
        String PATH = Environment.getExternalStorageDirectory() + "/BuddyDetag.apk";
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
                // writeToFile("\nInstall APK:  " + e.getLocalizedMessage());
            }
        }
    }

    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // ADDED BY SUPRIYA FOR ACESS TOKEN API

    public void AcessToken() {
        final JSONObject json = new JSONObject();
        try {
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                json.put("storeCode", PreferenceUtils.getStoreId());//
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("storeCode", PreferenceUtils.getStoreId());//02185
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
            }
            getAcessTokenData(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //getAcessTokenData(json);
    }


    public void getAcessTokenData(JSONObject json) {
        showProgressDialog();

        OkHttpClient client = new OkHttpClient();

        // client.sslSocketFactory(CommonUtility.getSslFactory(), CommonUtility.getTrustManager());
        try {
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(CommonUtility.getSSLConfigLS().getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession session) {
                            return true;
                        }
                    })
                    .build();


        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

       /* OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.MINUTES);
        builder.readTimeout(5, TimeUnit.MINUTES);
        builder.writeTimeout(5, TimeUnit.MINUTES);
        client = builder.build();*/

        //OkHttpClient Client = new OkHttpClient().newBuilder() .connectTimeout(20, TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS) .build();

        String url = getResources().getString(R.string.server_address_LS) + "" + ApiConstant.ACESS_TOKEN_LS;
        Log.e(TAG, "Request :: " + json.toString());

        String AUTH_TOKEN = "";
        if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_UAT;
        } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_PROD;
        }
        RequestBody body1 = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url("" + url).post(body1)
                /*  .addHeader("Authorization", "" + AUTH_TOKEN)*/
                .addHeader("Accept", "" + ApiConstant.ACCEPT)
                .addHeader("Content-Type", "" + ApiConstant.CONTENT_TYPE)
                .build();
        Log.e(TAG, "url :: " + url.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure :: " + url.toString());
                e.printStackTrace();

                // hideProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    LoginActivityNew.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Log.e(TAG, "myResponse :: " + myResponse);
                            try {
                                JSONObject jsonObjectTop = new JSONObject(myResponse);
                                accessToken = jsonObjectTop.optString("accessToken");
                                String statusCode = jsonObjectTop.optString("statusCode");
                                if (statusCode.equals("200")) {
                                    // showAlertDialog(LoginActivityNew.this, "", "access token generated successfully");
                                    openHomeScreen();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    hideProgressDialog();
                    Log.e(TAG, "response.isSuccessful() :: false " + url.toString());
                }
            }
        });
    }

    String TAG = "TAG";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    //////////////////////end access token api///////////////////////////////////

}