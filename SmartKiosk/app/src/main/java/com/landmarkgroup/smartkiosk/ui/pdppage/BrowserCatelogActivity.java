package com.landmarkgroup.smartkiosk.ui.pdppage;

import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.landmarkgroup.smartkiosk.model.PDPPageLoadRequest;
import com.landmarkgroup.smartkiosk.model.PDPPageResponse;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.R;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.landmarkgroup.smartkiosk.databinding.ActivityBrowserCatelogBinding;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.lifestyle.retail.utils.GeneralConstant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.landmarkgroup.smartkiosk.util.CustomDialogs.idealTimeOut;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showGoingHome;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showTimeoutDialog;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.stopFinishHandler;
import static com.landmarkgroup.smartkiosk.util.Utils.clearCookies;

public class BrowserCatelogActivity extends BaseCompatActivity {


    private Handler handler;
    private Runnable runnable;
    private PrefManager prefManager;
    private int count = 0;
    private String storeId, tabSelected;
    private String custMobile;

    ActivityBrowserCatelogBinding binding;
    String tagType = "BrowserCatelogActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_browser_catelog);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_browser_catelog);
        initialize();
    }

    protected void initialize() {
        //ButterKnife.bind(this,this);
        clearCookies(this);
        initView();

        prefManager = new PrefManager(BrowserCatelogActivity.this);
       int  ouCode = prefManager.getOuCode();
        binding.toolbarLayout.btnBack.setOnClickListener(v -> showGoingHome(BrowserCatelogActivity.this, ouCode));

        handler = new Handler();
        runnable = () -> {
            count++;
            Log.d("Abrar", "Finish");
            if (count == 1)
                showTimeoutDialog(BrowserCatelogActivity.this, ouCode);
        };
    }


    private void initView() {
        prefManager = new PrefManager(this);
        String ouName = prefManager.getOuName();
        String userId = prefManager.getEmployeeId();
        storeId = prefManager.getStoreId();
        tabSelected = prefManager.getTabSelected();
        //tabSelected = "K1";
        custMobile = getIntent().getStringExtra("custMobile");
        //new GetKioskUrl(this,this).getUrl(storeId,null,"BC",tabSelected,ouName,custMobile);
         getPDPPageUrl(storeId, custMobile, ouName, null, tabSelected, "BC", userId);
        constructingUrl();
    }

    private void getPDPPageUrl(String storeCode, String mobileNo, String entName, String itemId, String reqFrom, String reqType, String userId) {
        //view.showProgressDialog(view.getContext().getString(R.string.message_loading_data), false);
        showProgressDialog();
        PDPPageLoadRequest request = new PDPPageLoadRequest();
        request.setStoreId(storeCode);
        request.setCustMobileNum(mobileNo);
        request.setEntName(entName);
        request.setItemId(itemId);
        request.setReqFrom(reqFrom);
        request.setReqType(reqType);
        request.setUsrId(userId);

        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().getPDPPageReq(request), GeneralConstant.GET_PDP_REQUEST);
    }

    private void constructingUrl() {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy");
        String formattedDate = df.format(date);
        String medium = "BC_" + storeId + "_" + tabSelected;
        Log.d("Abrar", formattedDate + " " + medium);

        if (prefManager.getOuName().equalsIgnoreCase("LifeStyle")) {
            webviewSetting("https://www.lifestylestores.com/?&utm_source=kiosk&utm_medium=" + medium + "&utm_campaign=" + formattedDate);
        } else if (prefManager.getOuName().equalsIgnoreCase("Max")) {
            webviewSetting("https://www.maxfashion.in/shopbycategory-and-offers?&utm_source=kiosk&utm_medium=" + medium + "&utm_campaign=" + formattedDate);
        } else if (prefManager.getOuName().equalsIgnoreCase("Splash")) {
            webviewSetting("https://www.splashfashions.com/?&utm_source=kiosk&utm_medium=" + medium + "&utm_campaign=" + formattedDate);
        } else if (prefManager.getOuName().equalsIgnoreCase("EazyBuy")) {
            webviewSetting("http://www.easybuyindia.com/?&utm_source=kiosk&utm_medium=" + medium + "&utm_campaign=" + formattedDate);
        } else if (prefManager.getOuName().equalsIgnoreCase("HomeCenter")) {
            webviewSetting("https://www.homecentre.in/?&utm_source=kiosk&utm_medium=" + medium + "&utm_campaign=" + formattedDate);
        }
    }

    public void stopHandler() {
        handler.removeCallbacks(runnable);
    }

    public void startHandler() {
        count = 0;
        handler.postDelayed(runnable, idealTimeOut);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }



    @Override
    protected void onDestroy() {
        //killBackground();
        stopHandler();
        stopFinishHandler();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        binding.browserLL.webView.clearView();
        super.onStop();
    }

    /*@Override
    public void fetchUrl(String url) {
        *//*if (url != null)
            webviewSetting(url);
        else {
            if (prefManager.getOuName().equalsIgnoreCase("LifeStyle")){
                webviewSetting("https://www.lifestylestores.com/in/en/");
            }else if (prefManager.getOuName().equalsIgnoreCase("Max")){
                webviewSetting("https://www.maxfashion.in/in/en/");
            }else if (prefManager.getOuName().equalsIgnoreCase("Splash")){
                webviewSetting("https://www.splashfashions.com/ae/en/");
            }else if (prefManager.getOuName().equalsIgnoreCase("EazyBuy")){
                webviewSetting("http://www.easybuyindia.com/");
            }else if (prefManager.getOuName().equalsIgnoreCase("HomeCenter")){
                webviewSetting("https://www.homecentre.in/in/en/");
            }
        }*//*
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    private void webviewSetting(String url) {
        final String LOG_TAG = "Abrar";

        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);*/

        WebSettings webSettings = binding.browserLL.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //webSettings.setPluginsEnabled(true);
        webSettings.setAllowFileAccess(true);
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.browserLL.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            binding.browserLL.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        binding.browserLL.webView.setWebChromeClient(new WebChromeClient());
        binding.browserLL.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                Log.d(LOG_TAG, description);
            }

           /* @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                *//*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*//*
                return super.shouldOverrideUrlLoading(view, request);
            }*/

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url) ;  // Why?
                //view.loadUrl(url);
                return true;
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return shouldOverrideUrlLoading(view, request.toString());

                return true;
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                Log.d(LOG_TAG, req.getMethod() + " " + rerr.getDescription());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("Abrar", view.getUrl() + " " + url);
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                startHandler();
            }
        });

        binding.browserLL.webView.loadUrl(url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.browserLL.webView.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String html) {
                            Log.d("Abrar", html);
                        }
                    });
        }
    }

    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.GET_PDP_REQUEST) {
            if (statusResponse instanceof PDPPageResponse) {
                PDPPageResponse response = (PDPPageResponse) statusResponse;
                if (response.getStatusErrMessage() != null) {

                    //showSnackBar(activity, response.getStatusErrMessage());
                    showErrorWhenGetData();
                } else {
                    if (response.getValidateMessage() != null && response.getValidateMessage().equalsIgnoreCase("Y"))
                        loadPageUrl(response.getItemLongUrl());
                    else
                        showErrorWhenGetData();
                }
            }
        }
    }

    public void loadPageUrl(String url) {

    }


    public void statusMessage(String statusMessage) {

    }


    public void showErrorWhenGetData() {

    }

    @Override
    public void onClick(View view) {

    }
}