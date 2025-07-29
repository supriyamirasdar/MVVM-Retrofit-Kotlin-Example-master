package com.landmarkgroup.smartkiosk.ui.pdppage;

import androidx.databinding.DataBindingUtil;

import butterknife.ButterKnife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.landmarkgroup.smartkiosk.model.PDPOrderConfirmResponse;
import com.landmarkgroup.smartkiosk.model.PDPPageLoadRequest;
import com.landmarkgroup.smartkiosk.model.PDPPageResponse;
import com.landmarkgroup.smartkiosk.storage.ContextManager;
import com.landmarkgroup.smartkiosk.storage.DBManager;
import com.landmarkgroup.smartkiosk.storage.TransactionData;
import com.landmarkgroup.smartkiosk.ui.adapter.ColorOptionsAdapter;
import com.landmarkgroup.smartkiosk.ui.adapter.SizeOptionsAdapter;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColorDto;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColourSizeListModel;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelRequest;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemImgUrlResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeCountListmodel;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeDto;
import com.landmarkgroup.smartkiosk.util.BottomSheetDialog;
import com.landmarkgroup.smartkiosk.util.Logger;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.util.Utils;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.landmarkgroup.smartkiosk.databinding.ActivityMissingSizeBinding;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.lifestyle.retail.constants.Constant;
import com.lifestyle.retail.constants.UserDataDefs;
import com.lifestyle.retail.utils.CommonUtility;
import com.lifestyle.retail.utils.GeneralConstant;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.landmarkgroup.smartkiosk.util.CustomDialogs.getDialogInstance;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.idealTimeOut;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showGoingHome;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showIdelBefore;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showTimeoutDialog;
import static com.landmarkgroup.smartkiosk.util.Utils.clearCookies;
import static com.landmarkgroup.smartkiosk.util.Utils.displaySnackBar;
import static com.lifestyle.retail.utils.CommonUtility.showSnackBar;

public class MissingSizeActivity extends BaseCompatActivity {

    private int count = 0;
    private StringBuilder builder = new StringBuilder();
    private String ouName, storeId, tabSelected, userId, barCodeNo;

    private Handler handler;
    private Runnable runnable;
    private String LOG_TAG = "Abrar";
    private String custMobile;
    private MissingSizeActivity activity;
    ActivityMissingSizeBinding binding;

    private SharedPreferences sharedPreferences;
    private String[] modes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_size);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_missing_size);
        ContextManager.setAppContext(MissingSizeActivity.this);
        Logger.writeToFile("@@@@@@@@@ 07-SEP_2022 @@@@@@@@@ ");
        initlistandmap();
        initView();
    }

    String selOuUrl;
    int ouCode;
    String callType = "";
    String callFor = "";
    String barcode = "";
    private void initView() {
        activity = this;
        ButterKnife.bind(activity, activity);
        clearCookies(activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MissingSizeActivity.this);
        binding.toolbarLayout.etBarCode.setVisibility(View.VISIBLE);
        binding.toolbarLayout.btnScan.setVisibility(View.VISIBLE);
        binding.toolbarLayout.btnChooseCamera.setVisibility(View.VISIBLE);

        // dt: 30-07-2021
        PrefManager prefManager = new PrefManager(activity);

        // @@@@@@@@@@@@@@

        ouCode = prefManager.getOuCode();
        selOuUrl = prefManager.getOuUrl();
        if(getIntent().getExtras().getString("flag").equals("1")) {
            selOuUrl = "https://www.maxfashion.in/in/en/maxcurves22";
        }

        ouName = prefManager.getOuName();
        storeId = prefManager.getStoreId();
        tabSelected = prefManager.getTabSelected();
        userId = prefManager.getEmployeeId();


        int ouCode = prefManager.getOuCode();
        custMobile = getIntent().getStringExtra("custMobile");
        String itemCode = getIntent().getStringExtra("ItemCode");
        callType = prefManager.getCallType();
        Log.e("ORDER_CONFIRM", "initView callType :: " + callType);
        // added dt: 04-03-2022
        callFor = getIntent().getStringExtra("CallFor");
        if (callFor.equals("WEB")) {
            /*if (selOuUrl.isEmpty()) {
                selOuUrl = "https://www.maxfashion.in/in/en/";
            }*/


            String storeId = prefManager.getStoreId();
            String kioskId = prefManager.getTabSelected();
            String currentDate = Utils.getCurrentDateForWeb();
            Log.e("WEB ", "utm storeId :" + storeId);
            Log.e("WEB ", "utm kioskId :" + kioskId);
            Log.e("WEB ", "utm currentDate :" + currentDate);
            // String utmParam = "utm_source=kiosk&utm_medium=WEB_" + storeId + "_" + kioskId + "&utm_campaign=" + currentDate;
            //https://www.maxfashion.in/p/1000010581440?&utm_source=kiosk&utm_medium=WEB_02049_02049_K1&utm_campaign=19May2022

            // added 30-052022
            String utmParam = "utm_source=kiosk&utm_medium=MSC_" + storeId + "_" + kioskId + "&utm_campaign=WebFlow";
            Log.e("WEB ", "utm param :" + utmParam);
            Logger.writeToFile("## WEB ##: Web Url without utm param ::" + selOuUrl);
            selOuUrl = selOuUrl + "?&" + utmParam;
            Log.e("WEB ", "Web Url :" + selOuUrl);
            Logger.writeToFile("## WEB ##: Web Url with utm param ::" + selOuUrl);

            binding.toolbarLayout.etBarCode.setVisibility(View.INVISIBLE);
            binding.toolbarLayout.btnScan.setVisibility(View.INVISIBLE);
            binding.toolbarLayout.btnChooseCamera.setVisibility(View.INVISIBLE);

            // binding.toolbarLayout.btnBack.setVisibility(View.INVISIBLE);
            //binding.toolbarLayout.btnForword.setVisibility(View.INVISIBLE);
            loadPageUrl(selOuUrl);
        } else if (callFor.equals("MAIN")) {
            // dt: 30-07-2021
            binding.toolbarLayout.etBarCode.setVisibility(View.VISIBLE);
            binding.toolbarLayout.btnScan.setVisibility(View.VISIBLE);
            binding.toolbarLayout.btnChooseCamera.setVisibility(View.VISIBLE);

            // binding.toolbarLayout.btnBack.setVisibility(View.VISIBLE);
            //  binding.toolbarLayout.btnForword.setVisibility(View.VISIBLE);

            if (ouCode == 3) {
                // ls theme..
                binding.toolbarLayout.etBarCode.setBackground(getResources().getDrawable(R.drawable.edittext_ls));
                binding.toolbarLayout.etBarCode.setTextColor(getResources().getColor(R.color.text));
                binding.bgThemeBGIV.setVisibility(View.VISIBLE);
                binding.videoDemo.setVisibility(View.GONE);
                binding.toolbarLayout.toolbarBgLL.setBackground(getResources().getDrawable(R.drawable.toolbar_bg_ls));

            } else {
                // existing theme..
                binding.toolbarLayout.etBarCode.setBackground(getResources().getDrawable(R.drawable.edittext_background));
                binding.toolbarLayout.etBarCode.setTextColor(getResources().getColor(R.color.text));
                binding.bgThemeBGIV.setVisibility(View.GONE);
                binding.videoDemo.setVisibility(View.VISIBLE);
                binding.toolbarLayout.toolbarBgLL.setBackground(getResources().getDrawable(R.drawable.toolbar_bg_max));

            }
        }
        barCodeNo = itemCode;
        //String tabSelected = "K1";
        //new GetKioskUrl(this, this).getUrl(storeId, itemCode, "MSC", tabSelected, ouName,custMobile);
        // getPDPPageUrl(storeId, custMobile, ouName, itemCode, tabSelected, "MSC", userId);

        binding.toolbarLayout.btnHome.setOnClickListener(v -> showGoingHome(activity, ouCode));

        binding.toolbarLayout.etBarCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Logger.writeToFile("## BARCODE ##: IME_ACTION_SEARCH barCode ::" + binding.toolbarLayout.etBarCode.getText().toString().trim() + "::");
                performSearch(binding.toolbarLayout.etBarCode.getText().toString().trim());
                return true;
            }
            return false;
        });

        binding.toolbarLayout.etBarCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                stopHandler();
        });

        binding.toolbarLayout.etBarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.length() > 0 && text.contains("\n")) {
                    Logger.writeToFile("## BARCODE ##: onTextChanged barCode ::" + text + "::");
                    performSearch(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        handler = new Handler();
        runnable = () -> {
            count++;
            if (count == 1) {
                showTimeoutDialog(activity, ouCode);
            }
        };

        binding.toolbarLayout.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanner();
            }
        });

        binding.toolbarLayout.btnChooseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getScanModeArray());
            }
        });

    }

    BottomSheetDialog bottomSheet;

    @Override
    protected void onDestroy() {
        stopHandler();
        barcode = "";
        lastScanBarcode = "";
        binding.toolbarLayout.etBarCode.setText("");

        PrefManager prefManager = new PrefManager(activity);
        prefManager.setCallType("");
        clearEmpId();
        /*if (bottomSheet != null)
            bottomSheet.stopCountDownTimer();*/
        deleteCache(MissingSizeActivity.this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != binding.videoDemo) {
            binding.videoDemo.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {

        stopHandler();
        barcode = "";
        lastScanBarcode = "";
        binding.toolbarLayout.etBarCode.setText("");
        clearEmpId();
        /*if (bottomSheet != null)
            bottomSheet.stopCountDownTimer();*/
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContextManager.setAppContext(MissingSizeActivity.this);
        // dt: 30-07-2021
        PrefManager prefManager = new PrefManager(activity);
        userId = prefManager.getEmployeeId();
        if (userId == null) {
            // userId = "99999999";
            userId = "";
        }

        ouCode = prefManager.getOuCode();
        if (ouCode == 3) {
            // ls theme..
            binding.bgThemeBGIV.setVisibility(View.VISIBLE);
            binding.videoDemo.setVisibility(View.GONE);
        } else {
            // existing theme..
            binding.bgThemeBGIV.setVisibility(View.GONE);
            binding.videoDemo.setVisibility(View.VISIBLE);
            if (binding.videoDemo.getVisibility() == View.VISIBLE) {
                playVideo();
            }
        }
        /*if (binding.videoDemo.getVisibility() == View.VISIBLE) {
            playVideo();
        }*/

        // check for pending order..
        // dt: 23-09-2021
        checkForPendingOrderMain();
        //checkForPendingOrder();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                //hideSoftKeyboard(activity, etBarCode);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    private void playVideo() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.max_smart_kiosk);
        binding.videoDemo.setMediaController(null);
        binding.videoDemo.setVideoURI(uri);
        binding.videoDemo.start();

        binding.videoDemo.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    private void stopVideo() {
        if (null != binding.videoDemo) {
            binding.videoDemo.stopPlayback();
        }
        assert binding.videoDemo != null;
        binding.videoDemo.setVisibility(View.GONE);
    }


    private void performSearch(String itemCode) {

        if (itemCode.trim().length() > 0) {
            Logger.writeToFile("## BARCODE ##: perform search barCode ::" + itemCode + "::");
            Logger.writeToFile("## BARCODE ##: perform search :: above barCode is used in api.");

            stopHandler();
            Log.e(tagType, " itemCode :: " + itemCode);
            builder.delete(0, builder.length());
            barCodeNo = itemCode;
            //String tabSelected = "K1";
            //new GetKioskUrl(this, this).getUrl(storeId, itemCode, "MSC", tabSelected, ouName,custMobile);
            // commented on 10-1-2022
            //  getPDPPageUrl(storeId, custMobile, ouName, itemCode, tabSelected, "MSC", userId);
            binding.toolbarLayout.etBarCode.setText("");
            //binding.webView.requestFocus();
            //hideSoftKeyboard(activity, etBarCode);

            lastScanBarcode = "";
            // added 10-1-2022
            getallatoptionlevel(itemCode);

        } else {
            lastScanBarcode = "";
            displaySnackBar(activity, "Please Enter Item code");
        }
    }

    String tagType = "MissingSizeActivity";

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

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebsite(String url) {
        binding.toolbarLayout.btnBack.setVisibility(View.VISIBLE);
        binding.toolbarLayout.btnForword.setVisibility(View.VISIBLE);

        // Manages settings state for a WebView. When a WebView is first created, it obtains a set of default settings. These default settings will be returned from any getter call. A WebSettings object obtained from WebView#getSettings() is tied to the life of the WebView.
        WebSettings webSettings = binding.webView.getSettings();
        //Tells the WebView to enable JavaScript execution.
        webSettings.setJavaScriptEnabled(true);
        // Sets whether the WebView should use its built-in zoom mechanisms.
        webSettings.setBuiltInZoomControls(false);
        //Sets whether the DOM storage API is enabled.
        webSettings.setDomStorageEnabled(true);
        // Tells JavaScript to open windows automatically.
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //This method was deprecated in API level 18. Plugins are not supported in API level Build.VERSION_CODES.KITKAT or later; enabling plugins is a no-op.
        //The plugin state effects how plugins are treated on a page.
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //Sets whether the WebView loads pages in overview mode, that is, zooms out the content to fit on screen by width.
        webSettings.setLoadWithOverviewMode(true);
        // Sets whether the WebView should enable support for the "viewport" HTML meta tag or should use a wide viewport.
        webSettings.setUseWideViewPort(true);
        // Sets whether the WebView should support zooming using its on-screen zoom controls and gestures.
        webSettings.setSupportZoom(true);
        // Sets the underlying layout algorithm. This will cause a re-layout of the WebView
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // Overrides the way the cache is used.  Don't use the cache, load from the network
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // webSettings.setAllowFileAccess(true);
        // webSettings.setLoadsImagesAutomatically(true);
        //added 10-08-2022
        // This method was deprecated in API level 26. In Android O and afterwards, this function does not have any effect, the form data will be saved to platform's autofill service if applicable.
        webSettings.setSaveFormData(false);
        //added 10-08-2022    clear mobile cookie..
        String cookieMobNumber = "_lmgmobile=''";
        CookieManager cookieManagerMobile = CookieManager.getInstance();
        cookieManagerMobile.setCookie(url, cookieMobNumber);

        // Clears the resource cache. Note that the cache is per-application, so this will clear the cache for all WebViews used.
        binding.webView.clearCache(true);


        // Loads the given URL.
        binding.webView.loadUrl(url);
        //binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                Logger.writeToFile("Display Log: " + consoleMessage.message());
                return true;
            }
        });
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                stopHandler();
                Log.d(LOG_TAG, description);
                Logger.writeToFile("Display ERROR: " + description);
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // view.loadUrl(url);  // Why?
                //view.loadUrl(url);
                return false;
                // code for alert dialog error ..12-04-2021
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // code for alert dialog error ..12-04-2021
//                return shouldOverrideUrlLoading(view, request.getUrl().toString());
//                Log.e("WebView Alert ", "request.toString() :: " + request.toString());
                return false;
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                stopHandler();
                Log.d(LOG_TAG, req.getMethod() + " " + rerr.getDescription());
                Logger.writeToFile("Display ERROR: " + req.getMethod() + " " + rerr.getDescription());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler sslErrorHandler, SslError error) {
                sslErrorHandler.proceed();
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed("lms.in", "Webteam!");
            }

            // Notify the host application that a page has finished loading.
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("Abrar", "Main " + view.getUrl());

                Log.e("ORDER_CONFIRM", "onPageFinished callType :: " + callType);

                String cookies = CookieManager.getInstance().getCookie(url);
                Log.e("SMARTKIOSK", "All the cookies in a string:" + cookies);

                Logger.writeToFile("Main onPageFinished :: " + view.getUrl());
                if (view.getUrl().contains("orderConfirmation")) {// orderco
                    // added Dt: 07-10-2022
                    String urll = view.getUrl();
                    if (urll.contains("#")) {
                        urll = urll.substring(0, urll.length() - 1);
                        Log.e("ORDER_CONFIRM", "Main onPageFinished :: remove # from url:: " + urll);
                        Logger.writeToFile("Main onPageFinished :: remove # from url:: " + urll);
                    }

                    // added 17-11-2021
                    // clear all cookie on order confirmation..
                    // clearCookies(activity);
                    // OR
                    // clear specific cookie on order confirmation..
                    String cookieString = "_lmgua=''";
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setCookie(url, cookieString);

                    String cookiesNew = CookieManager.getInstance().getCookie(url);
                    Log.e("SMARTKIOSK", "Cookies After Order confirmation::" + cookiesNew);

                    // comment : 23-09-2021
                    // isAutoAcceptOrder = 0;
                    //saveAcceptedUrlRequest(view.getUrl());
                    // added : 23-09-2021
                    saveOrderAcceptData(urll);
                }

               /*if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                if (binding.videoDemo.isPlaying())
                    stopVideo();
                onUserInteraction();
            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.webView.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
                    value -> Log.d("Abrar",value));
        }*/


        binding.toolbarLayout.btnBack.setOnClickListener(v -> {
            if (binding.webView.canGoBack())
                binding.webView.goBack();
        });

        binding.toolbarLayout.btnForword.setOnClickListener(v -> {
            if (binding.webView.canGoForward())
                binding.webView.goForward();
        });
    }

    public void stopHandler() {
        handler.removeCallbacks(runnable);
    }

    public void startHandler() {
        count = 0;
        handler.postDelayed(runnable, (idealTimeOut));
      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 120000);  // 120000 seconds*/
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        onUserInteraction();
        Dialog dialog = getDialogInstance();
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
        }

        /// new condition updated....

        if (e.getAction() == KeyEvent.ACTION_DOWN
                && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            binding.toolbarLayout.etBarCode.setText(barcode);
            performSearch(barcode);
            barcode = "";
            onUserInteraction();
        } else {
            if (e.getAction() == KeyEvent.ACTION_DOWN
                    && e.getKeyCode() != KeyEvent.KEYCODE_ENTER
                    && e.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (barcode.length() > 0) {
                    Log.e("KEY DOWN ", "barcode :: " + barcode);
                    barcode = barcode.substring(0, barcode.length() - 1);
                    Log.e("KEY DOWN ", "barcode -1 :: " + barcode);
                    Log.e("KEY DOWN ", "builder.toString() :: " + builder.toString());
                    builder = builder.deleteCharAt(builder.toString().length() - 1);
                    Log.e("KEY DOWN ", "builder.toString() -1 :: " + builder.toString());
                    binding.toolbarLayout.etBarCode.setText(builder.toString());
                    binding.toolbarLayout.etBarCode.setSelection(builder.length());
                    onUserInteraction();
                } else {
                    stopHandler();
                    barcode = "";
                    binding.toolbarLayout.etBarCode.setText("");
                }
            } else if (e.getAction() == KeyEvent.ACTION_DOWN
                    && e.getKeyCode() != KeyEvent.KEYCODE_ENTER && e.getKeyCode() != KeyEvent.KEYCODE_DEL) {
                char pressedKey = (char) e.getUnicodeChar();
                barcode += pressedKey;
                builder.append(pressedKey);
                binding.toolbarLayout.etBarCode.setText(builder.toString());
                binding.toolbarLayout.etBarCode.setSelection(builder.length());
                onUserInteraction();
            }
        }
        if (e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // your code
            stopHandler();
            barcode = "";
            binding.toolbarLayout.etBarCode.setText("");
            super.onBackPressed();
            return true;
        }
        return false;
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    // dt: 23-09-2021
    public void saveOrderAcceptData(String url) {
        try {
            Logger.writeToFile("@@@@@@@@@ INSERTING DATA IN DB @@@@@@@@@ ");
            TransactionData transactionData = new TransactionData();
            int transactionId = DBManager.getInstance().lastRowId(DBManager.TBL_TRANSACTION_MAIN) + 1;
            transactionData.setTransaction_id(transactionId);
            transactionData.setInvoice_date(Utils.getCurrentDateForOrder());
            transactionData.setTrans_status("Complete");// Complete/ Pending
            transactionData.setTrans_submit_to_server_status("N"); //Y/N  Y for yes.. N for no
            transactionData.setKioskId(tabSelected);
            transactionData.setStoreId(storeId);
            transactionData.setOrdConfirmUrl(url);
            transactionData.setCustMobileNum(custMobile);
            transactionData.setEmpId(userId);
            transactionData.setOrderType("" + callType);
            DBManager.getInstance().insertTables(DBManager.TBL_TRANSACTION_MAIN, transactionData);


            // dt: 24-09-2021
            new Handler().postDelayed(() -> {
                Logger.writeToFile("@@@@@@@@@ DATA INSERTING DATA IN DB @@@@@@@@@ ");
                checkForPendingOrderMain();
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            String s = writer.toString();
            Logger.writeToFile("@@@@@@@@@ ERROR INSERTING DATA IN DB @@@@@@@@@ ");
            Logger.writeToFile("ERROR :: " + s);
            Logger.writeToFile("@@@@@@@@@ ERROR INSERTING DATA IN DB @@@@@@@@@ ");
            // Log.e("DB", confirmOrderIdMain + " deleted from table TBL_TRANSACTION_MAIN;");

        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.GET_PDP_REQUEST) {
            if (statusResponse instanceof PDPPageResponse) {
                PDPPageResponse response = (PDPPageResponse) statusResponse;
                if (response.getServerErrormsg() != null) {

                    //showSnackBar(activity, response.getServerErrormsg());
                    // showErrorWhenGetData();
                    // dt : 27-10-2021
                    //  show error msg in dialog..
                    showErrorDialog(response.getServerErrormsg(), 0);
                    new android.os.Handler().postDelayed(() -> {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            //sizeOptionsAdapter.setPositionSelected(-1);
                        }
                    }, 10000);

                } else {
                    if (response.getValidateMessage() != null && response.getValidateMessage().equalsIgnoreCase("Y")) {
                        //loadPageUrl(response.getItemLongUrl());

                        // added dt: 17-03-2022
                        if (response.getOnlineSoh() <= 0) {
                            String errorMsg = "Size is not available";
                            // show error msg.. size is not available
                            showErrorDialog(errorMsg, 0);
                            new android.os.Handler().postDelayed(() -> {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                    //sizeOptionsAdapter.setPositionSelected(-1);
                                }
                            }, 10000);
                        } else {
                            Logger.writeToFile("## PDP ##: Pdp Url with utm param ::" + response.getItemLongUrl());

                            //added Dt: 19-07-2022 as Abhijna told
                            // if pdp url contains lifestyle then show dialog size not available..

                            // else if url contains max fashion, then redirect to pdp page..

                            // commented on 29-11-2022
                            String urlSt = response.getItemLongUrl();
                            boolean isFound = urlSt.indexOf("maxfashion") != -1 ? true : false; //true
                            if (isFound) {
                                // set call type PDP..
                                PrefManager prefManager = new PrefManager(activity);
                                prefManager.setCallType("PDP");
                                loadPageUrl(response.getItemLongUrl());
                            } else {
                                String errorMsg = "Product not available";
                                // show error msg.. size is not available
                                showErrorDialog(errorMsg, 0);
                                new android.os.Handler().postDelayed(() -> {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                        //sizeOptionsAdapter.setPositionSelected(-1);
                                    }
                                }, 10000);
                            }


                            // commented on 19-07-2022

                            // open on 29-11-2022
                           // loadPageUrl(response.getItemLongUrl());
                            // load url..
                        }
                    } else
                        showErrorWhenGetData();
                }
            }
        } else if (requestCode == GeneralConstant.SAVE_ORDER_URL) {
            if (statusResponse instanceof PDPOrderConfirmResponse) {
                PDPOrderConfirmResponse response = (PDPOrderConfirmResponse) statusResponse;
                if (isAutoAcceptOrder == 0) {
                    if (response != null) {

                        // dt: 19-11-2021
                        String url = autoAcceptOrderMapMain.get(confirmOrderIdMain).getOrdConfirmUrl();
                        String cookieString = "_lmgua=''";
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setCookie(url, cookieString);

                        Gson gson = new Gson();
                        String json = gson.toJson(response);
                        Logger.writeToFile("API Response :: " + json);
                        Logger.writeToFile(":::::::::: API END :::::::::: ");

                        if (response.getStatusMessage() != null
                                && response.getStatusMessage().equalsIgnoreCase("Y")) {
                            //statusMessage(response.getStatusErrMessage());
                            statusMessage(response.getServerErrormsg());
                        } else {
                            //statusMessage(response.getStatusErrMessage());
                            statusMessage(response.getServerErrormsg());
                        }
                    } else {
                        uploadCount++;
                        if (uploadCount > 1) {
                            // added dt: 13-09-2021
                            TransactionData transactionData = new TransactionData();
                            int transactionId = DBManager.getInstance().lastRowId(DBManager.TBL_TRANSACTION) + 1;
                            transactionData.setTransaction_id(transactionId);
                            transactionData.setInvoice_date(Utils.getCurrentDateForOrder());
                            transactionData.setTrans_status("Complete");// Complete/ Pending
                            transactionData.setTrans_submit_to_server_status("N"); //Y/N  Y for yes.. N for no
                            transactionData.setKioskId(tabSelected);
                            transactionData.setStoreId(storeId);
                            transactionData.setOrdConfirmUrl(orderConfirmUrl);
                            transactionData.setCustMobileNum(custMobile);
                            transactionData.setEmpId(userId);
                            transactionData.setOrderType("" + callType);
                            DBManager.getInstance().insertTables(DBManager.TBL_TRANSACTION, transactionData);
                            showSnackBar(activity, "Please Try Again!!!");
                        } else {

                            // call api again to cnf order..
                            saveAcceptedUrlRequest(orderConfirmUrl);
                        }

                        showErrorWhenGetData();
                    }
                } else if (isAutoAcceptOrder == 2) {
                    if (response != null) {

                        // dt: 19-11-2021
                        String url = autoAcceptOrderMapMain.get(confirmOrderIdMain).getOrdConfirmUrl();
                        String cookieString = "_lmgua=''";
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setCookie(url, cookieString);

                        Gson gson = new Gson();
                        String json = gson.toJson(response);
                        Logger.writeToFile("API Response MAIN :: " + json);
                        Logger.writeToFile(":::::::::: API END :::::::::: ");

                        if (response.getStatusMessage() != null
                                && response.getStatusMessage().equalsIgnoreCase("Y")) {
                            //statusMessage(response.getStatusErrMessage());
                            statusMessage(response.getServerErrormsg());
                        } else {
                            //statusMessage(response.getStatusErrMessage());
                            statusMessage(response.getServerErrormsg());
                        }
                        // remove from db..
                        DBManager.getInstance().deleteRows(DBManager.TBL_TRANSACTION_MAIN, "" + autoAcceptOrderMapMain.get(confirmOrderIdMain).get_id());
                        // remove from map as well..
                        //autoAcceptOrderMapMain.remove(autoAcceptOrderMapMain.get(confirmOrderIdMain));
                        Log.e("DB", confirmOrderIdMain + " deleted from table TBL_TRANSACTION_MAIN;");
                        orderCountMain++;
                        autoOrderAcceptMain(orderCountMain);
                    } else {
                        // show error
                        showSnackBar(activity, "Please Try Again!!!");
                        // remove from db..
                        DBManager.getInstance().deleteRows(DBManager.TBL_TRANSACTION_MAIN, "" + autoAcceptOrderMapMain.get(confirmOrderIdMain).get_id());
                        // remove from map as well..
                        //autoAcceptOrderMapMain.remove(autoAcceptOrderMapMain.get(confirmOrderIdMain));
                        Log.e("DB", confirmOrderIdMain + " deleted from table TBL_TRANSACTION_MAIN ;");
                        orderCountMain++;
                        autoOrderAcceptMain(orderCountMain);
                    }
                }
            }
        } else if (requestCode == GeneralConstant.SOH_OPTIONDETAILS) {
            if (statusResponse instanceof GetItemDetailsAtOptionLevelResponse) {

                GetItemDetailsAtOptionLevelResponse getItemDetailsAtOptionLevelResponse = (GetItemDetailsAtOptionLevelResponse) statusResponse;
                if (getItemDetailsAtOptionLevelResponse != null) {
                    Log.d("response===>", new Gson().toJson(getItemDetailsAtOptionLevelResponse));
                    binding.itemEanCodeValTV.setText("" + barCodeNo);

                    /*ItemDetails itemDetails = getItemDetailsAtOptionLevelResponse.getItemDetails();
                    String itemDesc= ""+ itemDetails.getItemDesc();*/

                    String itemDesc = "" + getItemDetailsAtOptionLevelResponse.getItemDesc();
                    binding.itemDescTV.setText("" + itemDesc);

                    colorDtoList = new ArrayList<>();
                    sizeDtoList = new ArrayList<>();
                    sizeSet = new HashSet<>();
                    completeColorHashMap = new HashMap<>();
                    sizeDataMap = new HashMap<>();
                    if (getItemDetailsAtOptionLevelResponse.getColourSizeListModelArrayList() != null && getItemDetailsAtOptionLevelResponse.getColourSizeListModelArrayList().size() > 0) {
                        int i = 1, j = 1;
                        errorMsg = null;
                        String tempColour = null, tempSize = null, tempCount = null, tempEANCode = null, tempItemCode = null, tempLastSold = null;
                        for (ColourSizeListModel colourSizeListModel : getItemDetailsAtOptionLevelResponse.getColourSizeListModelArrayList()) {

                            tempColour = colourSizeListModel.getColour();
                            completeColorHashMap.put(tempColour + "." + tempColour, tempColour);
                            colourSet.add(tempColour);

                            String id = j + "00000" + i;
                            colorDtoList.add(new ColorDto(id, tempColour));
                            for (SizeCountListmodel sizeCountListmodel : colourSizeListModel.getSizeCountListmodelArrayList()) {
                                tempSize = sizeCountListmodel.getSize();
                                sizeSet.add(tempSize);
                                if (sizeCountListmodel.getCount() != null)
                                    tempCount = sizeCountListmodel.getCount();
                                if (sizeCountListmodel.getItemBarCode() != null)
                                    tempEANCode = sizeCountListmodel.getItemBarCode();
                                if (sizeCountListmodel.getSkuCode() != null)
                                    tempItemCode = sizeCountListmodel.getSkuCode();

                                tempLastSold = sizeCountListmodel.getLastSold() + "";
                                if (tempLastSold != null && tempLastSold.length() > 5) {
                                    //   tempLastSold = Constant.soldDateFormate(tempLastSold);
                                    try {
                                        tempLastSold = Constant.convertDate(tempLastSold, "dd/MMM/yyyy");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                // commented on 01-02-2022
                                /*if (tempSize != null && tempCount != null) {
                                    completeColorHashMap.put(tempColour + "." + tempSize, tempCount);

                                    if (tempItemCode != null && tempEANCode != null) {
                                        completeColorHashMap.put("." + tempColour + "." + tempSize + "." + tempCount, j + "00000" + i);
                                        completeColorHashMap.put(j + "00000" + i, "EAN Code: " + tempEANCode + "\nItem Code: " + tempItemCode + "\nLast Sold  : " + tempLastSold + "\nSize           : " + tempSize + "\nColor         : " + tempColour);//+"_"+tempCount  );
                                        // added 23-12-2021..
                                        sizeDataMap.put(id, colourSizeListModel.getSizeCountListmodelArrayList());
                                    }
                                }*/

                                // @@@@@@@@@
                                // added on 01-02-2022
                                completeColorHashMap.put(tempColour + "." + tempSize, tempCount);
                                completeColorHashMap.put("." + tempColour + "." + tempSize + "." + tempCount, j + "00000" + i);
                                completeColorHashMap.put(j + "00000" + i, "EAN Code: " + tempEANCode + "\nItem Code: " + tempItemCode + "\nLast Sold  : " + tempLastSold + "\nSize           : " + tempSize + "\nColor         : " + tempColour);//+"_"+tempCount  );
                                sizeDataMap.put(id, colourSizeListModel.getSizeCountListmodelArrayList());
                                // @@@@@@@@@
                                j++;

                            }
                            i++;
                        }

                    } else if (getItemDetailsAtOptionLevelResponse.getItemNotFound() != null) {
                        errorMsg = getItemDetailsAtOptionLevelResponse.getItemNotFound();
                    } else if (getItemDetailsAtOptionLevelResponse.getServerErrormsg() != null) {
                        errorMsg = getItemDetailsAtOptionLevelResponse.getServerErrormsg();
                    } else if (getItemDetailsAtOptionLevelResponse.getItemEanErrorMsg() != null) {
                        errorMsg = getItemDetailsAtOptionLevelResponse.getItemEanErrorMsg();
                    } else if (getItemDetailsAtOptionLevelResponse.getOptionNotFound() != null) {
                        errorMsg = getItemDetailsAtOptionLevelResponse.getOptionNotFound();
                    }
                }

                display("");
            }
        } else if (requestCode == GeneralConstant.ITEM_IMAGE_URL) {
            if (statusResponse instanceof GetItemImgUrlResponse) {
                GetItemImgUrlResponse response = (GetItemImgUrlResponse) statusResponse;
                if (response.getStatusCode().equals("400")) {


                    showErrorDialog(response.getStatusErrMessage(), 0);
                    new Handler().postDelayed(() -> {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            //sizeOptionsAdapter.setPositionSelected(-1);
                        }
                    }, 10000);

                } else if (response.getStatusCode().equals("200")) {
                    if (response.getStatusMessage() != null && response.getStatusMessage().equalsIgnoreCase("Y")) {
                        if (response.getImage1() == null) {
                            binding.itemColorIV.setImageResource(R.drawable.no_image_new);
                            binding.itemColorThumbLL.setVisibility(View.GONE);

                        } else {
                            String imageUrl = response.getImage1();
                            //  Show image in righr image view..
                            // also show as thumb image in left side..
                            // binding.itemColorIV.setImageBitmap("");
                            // binding.itemColorThumbIV.setImageBitmap('');

                            binding.itemColorThumbLL.setVisibility(View.VISIBLE);

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.ic_image_placeholder);
                            requestOptions.error(R.drawable.ic_image_placeholder);

                            Glide.with(activity).setDefaultRequestOptions(requestOptions)
                                    .load(imageUrl)
                                    .into(binding.itemColorIV);

                            Glide.with(activity).setDefaultRequestOptions(requestOptions)
                                    .load(imageUrl)
                                    .into(binding.itemColorThumbIV);

                        }
                    } else {
                        String errorMsg = "No records found.";
                        showErrorDialog(errorMsg, 0);
                        new Handler().postDelayed(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }, 10000);
                    }
                }
            }
        }
    }

    int uploadCount = 0;

    public void loadPageUrl(String url) {
        if (url != null) {
            // binding.webView.setVisibility(View.VISIBLE);
            binding.sizeOptionLL.setVisibility(View.GONE);
            binding.browserLL.setVisibility(View.VISIBLE);
            binding.videoViewLL.setVisibility(View.GONE);
            loadWebsite(url);
            binding.webView.requestFocus();
        }
    }

    //int transactionId= -1;
    String orderConfirmUrl = "";

    //Order Accept URL Request
    private void saveAcceptedUrlRequest(String url) {
        //saveAcceptedOrderUrl(storeId, custMobile, barCodeNo, tabSelected, userId, url);
        showProgressDialog();
        orderConfirmUrl = url;
        PDPPageLoadRequest request = new PDPPageLoadRequest();
        request.setStoreId(storeId);
        request.setCustMobileNum(custMobile);
        request.setOrdConfirmUrl(url);
        request.setKioskId(tabSelected);
        request.setUsrId(userId);
        Log.e("ORDER_CONFIRM", "callType :: " + callType);
        request.setReqSource(callType);
        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        Logger.writeToFile(":::::::::: API MAIN saveAcceptedUrlRequest Called :::::::::: ");
        Logger.writeToFile("API URL :: StylusRest/saveKioskOrdDlts ");
        Logger.writeToFile("API Request :: " + json);
        network(getApiInterface().saveOrderAcceptedUrl(request), GeneralConstant.SAVE_ORDER_URL);
    }


    //Order Accept URL Request
    private void saveAcceptedUrlRequest(PDPPageLoadRequest request) {
        //saveAcceptedOrderUrl(storeId, custMobile, barCodeNo, tabSelected, userId, url);
        showProgressDialog();
        //orderConfirmUrl = url;
        /*PDPPageLoadRequest request = new PDPPageLoadRequest();
        request.setStoreId(storeId);
        request.setCustMobileNum(custMobile);
        request.setOrdConfirmUrl(url);
        request.setKioskId(tabSelected);
        request.setUsrId(userId);*/
        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);


        Logger.writeToFile(":::::::::: API saveAcceptedUrlRequest Called :::::::::: ");
        Logger.writeToFile("API URL :: StylusRest/saveKioskOrdDlts ");
        Logger.writeToFile("API Request :: " + json);

        network(getApiInterface().saveOrderAcceptedUrl(request), GeneralConstant.SAVE_ORDER_URL);
    }


    public void statusMessage(String statusMessage) {
        displaySnackBar(activity, statusMessage);
    }


    public void showErrorWhenGetData() {

    }


    @Override
    public void onClick(View view) {

    }

    private void openScanner() {
        if ((Build.BRAND.equalsIgnoreCase("SUNMI"))) {
            Log.e("SCANNER", "11  SUNMI");
            Intent intent = new Intent();
            intent.setAction("com.sunmi.scan");
            intent.putExtra("IS_SHOW_SETTING", false);
            intent.putExtra("IDENTIFY_MORE_CODE", true);
            intent.setClassName("com.sunmi.sunmiqrcodescanner", "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
            intent.putExtra("CURRENT_PPI", 0X0003);
            intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);
            startActivityForResult(intent, 120);

        } else {
            if (Constant.checkPermissionAPI(MissingSizeActivity.this, Manifest.permission.CAMERA)) {
                if (Constant.getSuppourtedFocusedModes()) {
                    Log.e("SCANNER", "22  BarcodeCaptureActivity");
                    Intent intent = new Intent(MissingSizeActivity.this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent, 9001);
                } else {
                    Log.e("SCANNER", "33  CaptureActivity");
                    Intent intent = new Intent(MissingSizeActivity.this, CaptureActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent, 101);
                }

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == 101) {
            String resultBarcode = intent.getStringExtra("result");
            if (resultBarcode == null || resultBarcode.length() <= 0) {
                showAlert("Please Enter Item barcode");
            } else {
                //itemScanCode.setText(resultBarcode);
                // lastScanItem = resultBarcode;
                Logger.writeToFile("## BARCODE ##: onActivityResult 11:: barCode ::" + resultBarcode + "::");
                performSearch(resultBarcode);
            }
        } else if (resultCode == CommonStatusCodes.SUCCESS && requestCode == 9001) {
            if (intent != null) {
                Barcode barcode = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                String resultBarcode = barcode.displayValue;
                // lastScanItem = resultBarcode;
                Logger.writeToFile("## BARCODE ##: onActivityResult 22:: barCode ::" + resultBarcode + "::");

                performSearch(resultBarcode);

                //setErrorMsgVisibilityGone();
                //getItemDetails();
                //activityMainShelfEdgeStoreBinding.content1.content2.shelfEdgeSpinner.setComponentName("Regular");
                //itemScanCode.performClick();
            } else {
                //Toast.makeText(this, "Please Enter Item barcode", Toast.LENGTH_LONG).show();
                showAlert("Please Enter Item barcode");
            }
        } else if ((Build.BRAND.equalsIgnoreCase("SUNMI"))) {
            if (requestCode == 120 && intent != null) {
                Bundle bundle = intent.getExtras();
                ArrayList<HashMap<String, String>> results = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
                String resultBarcode = results.get(0).get("VALUE");
                //itemScanCode.setText(resultBarcode);
                // lastScanItem = resultBarcode;
                Logger.writeToFile("## BARCODE ##: onActivityResult 33:: barCode ::" + resultBarcode + "::");

                performSearch(resultBarcode);
                //searchIcon.performClick();
            } else {
                //new CustomToast(this, "No barcode captured, intent data is null");
                showAlert("No barcode captured, intent data is null");
            }
        }
    }

    private void saveCameraMode(boolean cameraMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(UserDataDefs.USER_DATA.SCAN_MODE.key, cameraMode);
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
                editor.commit();
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
                editor.apply();
            }
        });
        dialog.show();
        okBtn.setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.cancel();
        });
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

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // dt: 14-09-2021

    int progressMultiplicationVal = 20;
    public Dialog circleProgressDialog;
    int isAutoAcceptOrder = 0;

    // Method to show Progress bar
    public void showProgressDialogWithTitle(String title, String message, int progress) {
        try {
            circleProgressDialog = new Dialog(MissingSizeActivity.this);
            circleProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            circleProgressDialog.setContentView(R.layout.layout_circle_progressbar);
            circleProgressDialog.setCancelable(false);
            ProgressBar circleProgressBar = circleProgressDialog.findViewById(R.id.progress_circular_id);
            TextView progressValTV = circleProgressDialog.findViewById(R.id.textview_progress_status_id);
            TextView tvDialogTitle = circleProgressDialog.findViewById(R.id.tvDialogTitle);
            TextView tvDialogMsg = circleProgressDialog.findViewById(R.id.tvDialogMsg);

            //Button btnSubmit = circleProgressDialog.findViewById(R.id.btnSubmit);

            int progressVal = progress * progressMultiplicationVal;
            circleProgressBar.setProgress(progressVal);
            progressValTV.setText(progressVal + "%");

            if (title != null)
                tvDialogTitle.setText(title);
            else
                tvDialogTitle.setVisibility(View.GONE);

            if (message != null)
                tvDialogMsg.setText(message);
            else
                tvDialogMsg.setVisibility(View.GONE);
            circleProgressDialog.show();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void updateProgressDialogMessage(String message, int progress) {
        try {
            //progressDialog.setMessage(substring);

            ProgressBar circleProgressBar = circleProgressDialog.findViewById(R.id.progress_circular_id);
            TextView progressValTV = circleProgressDialog.findViewById(R.id.textview_progress_status_id);
            TextView tvDialogMsg = circleProgressDialog.findViewById(R.id.tvDialogMsg);

            int progressVal = progress * progressMultiplicationVal;
            circleProgressBar.setProgress(progressVal);
            progressValTV.setText(progressVal + "%");

            tvDialogMsg.setText(message);
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        try {
            // progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // progressDialog.dismiss();

            circleProgressDialog.dismiss();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    public void showLog(String message) {
        Log.e("Pick_failed", "" + message);
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    int orderCountMain = 0;
    int totalOrderCountMain = 0;
    //int isAutoAcceptOrderMain = 0;
    private String confirmOrderIdMain;
    private Map<String, TransactionData> autoAcceptOrderMapMain = new HashMap<>();
    private List orderIdListMain = new ArrayList();


    public void checkForPendingOrderMain() {
        isAutoAcceptOrder = 2;
        ArrayList<TransactionData> transactionDataList = ((ArrayList<TransactionData>) (ArrayList<?>) (DBManager.getInstance().getAllTableData(DBManager.TBL_TRANSACTION_MAIN, "", "", GeneralConstant.FETCH_PENDING_ORDER)));

        Log.e("DB", "transactionDataList size :: " + transactionDataList.size());
        if (transactionDataList.size() > 0) {

            for (int i = 0; i < transactionDataList.size(); i++) {
                TransactionData transData = transactionDataList.get(i);
                autoAcceptOrderMapMain.put("" + transData.getTransaction_id(), transData);
                orderIdListMain.add(transData.getTransaction_id());
            }

            totalOrderCountMain = autoAcceptOrderMapMain.size();
            autoOrderAcceptMain(orderCountMain);

        } else {
            // no pending order..
            hideKeyboard();
            hideProgressDialog();
            String message = "No orders found.";
            //CommonUtility.showSnackBar(MissingSizeActivity.this, message);
            clearPickFailedDataMain(1);
        }
    }

    public void autoOrderAcceptMain(int ordCountMain) {

        showLog("orderCountMain :: " + ordCountMain);
        showLog("totalOrderCountMain :: " + totalOrderCountMain);
        if (ordCountMain < totalOrderCountMain) {

            confirmOrderIdMain = orderIdListMain.get(orderCountMain).toString();

            TransactionData transData = autoAcceptOrderMapMain.get(confirmOrderIdMain);
            //orderLineType = transData.getOrdLineType();

            PDPPageLoadRequest request = new PDPPageLoadRequest();
            request.setStoreId(transData.getStoreId());
            request.setCustMobileNum(transData.getCustMobileNum());
            request.setOrdConfirmUrl(transData.getOrdConfirmUrl());
            request.setKioskId(transData.getKioskId());
            request.setUsrId(transData.getEmpId());
            Log.e("ORDER_CONFIRM", "callType :: " + callType);
            request.setReqSource(callType);
            isAutoAcceptOrder = 2;
            saveAcceptedUrlRequest(request);

        } else if (ordCountMain == totalOrderCountMain) {

            String message = "Orders detail saved successfully.";
            showLog(totalOrderCountMain + "  " + message);
            CommonUtility.showSnackBar(MissingSizeActivity.this, totalOrderCountMain + " " + message);

            new Handler().postDelayed(() -> {
                hideKeyboard();
                hideProgressDialog();
                 clearPickFailedDataMain(0);
                // initView();
            }, 1000);


        }
    }

    public void clearPickFailedDataMain(int type) {
        orderCountMain = 0;
        totalOrderCountMain = 0;
        isAutoAcceptOrder = 0;
        orderIdListMain = new ArrayList();
        confirmOrderIdMain = "";
        //orderIdCheckForNotPicked = "";
        autoAcceptOrderMapMain.clear();
        autoAcceptOrderMapMain = new HashMap<>();

        showLog("clearPickFailedData :: autoAcceptOrderMapMain.size() :: " + autoAcceptOrderMapMain.size());
        showLog("totalOrderCountMain :: " + totalOrderCountMain);

        if (type == 0) {
             // commented on 22-11-2022
            // added on 17-11-2022
           /* bottomSheet = new BottomSheetDialog();
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");*/

            // added 22-11-2022
            new Handler().postDelayed(() -> {
                finish();
            }, 10000);

        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@
    Dialog dialog = null;

    private void showErrorDialog(String arr, int callFrom) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (ouCode == 3) {
            dialog.setContentView(R.layout.dialog_error_msg_ls);
        } else {
            dialog.setContentView(R.layout.dialog_error_msg);
        }
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button okBtn = dialog.findViewById(R.id.dialog_ok);
        TextView messageTV = dialog.findViewById(R.id.messageTV);
        messageTV.setText("" + arr + ".");

        okBtn.setOnClickListener(v -> {
            if (dialog.isShowing())
                dialog.cancel();
            if (callFrom == 1) {
                for (int i = 0; i < sizeDtoList.size(); i++) {

                    TextView textv = ((TextView) binding.sizeFlexBoxLL.getChildAt(i));
                    textv.setBackgroundResource(R.drawable.layout_border_sel);
                    //((TextView) binding.sizeFlexBoxLL.getChildAt(i)).setTextColor(Color.parseColor("#000000"));
                    textv.setPadding(15, 10, 15, 10);

                }
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    private void initlistandmap() {
        completeColorHashMap = new HashMap<String, String>();
        colourSet = new HashSet<String>();
        sizeSet = new HashSet<String>();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
//        screenSize = display.getWidth();
    }

    @Override
    public void onBackPressed(View view) {
        super.onBackPressed(view);
    }

    private void getallatoptionlevel(String lastScanItem) {
        // userId = "1063694";//prefManager.getEmployeeId();
        //  storeId = "01346";//prefManager.getStoreId();
        showProgressDialog();
        GetItemDetailsAtOptionLevelRequest request = new GetItemDetailsAtOptionLevelRequest();
        request.itemCode = lastScanItem;
        lastScanBarcode = lastScanItem;
        //request.usecaseId = "3";
        // request.userId = "";//userId;
        request.storeId = storeId;// "01346";//
        // SaveLastScanItem(lastScanItem, "0");
        Log.d("SOH_REQUEST==>", new Gson().toJson(request));
        // network(getApiInterface().getItemDetailsAtOptionLevel(request), GeneralConstant.SOH_OPTIONDETAILS);
        // network(getApiInterface().getMissingBarcodeDetails(request), GeneralConstant.SOH_OPTIONDETAILS);
        network(getApiInterface().getItemDetailsForSOH(request), GeneralConstant.SOH_OPTIONDETAILS);

    }

    private void display(String s) {
        try {
            if (errorMsg != null) {
                showAlert("" + errorMsg);
                // removeAllViews();
                binding.sizeOptionLL.setVisibility(View.GONE);
                binding.browserLL.setVisibility(View.GONE);
                binding.videoViewLL.setVisibility(View.VISIBLE);
            } else {
                // createSohMatrixDetailsDataTable();

                binding.sizeOptionLL.setVisibility(View.VISIBLE);
                binding.browserLL.setVisibility(View.GONE);
                binding.videoViewLL.setVisibility(View.GONE);

                createNewSizeColorView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Abrar", "Errro: " + e.getLocalizedMessage());
        }
    }

    private void createNewSizeColorView() {
        setColorDataView();

    }

    private HashMap<String, String> completeColorHashMap;
    ArrayList<String> list = new ArrayList<>();
    private HashSet<String> colourSet, sizeSet;

    String errorMsg = null;

    ColorOptionsAdapter colorOptionsAdapter;
    List<ColorDto> colorDtoList = new ArrayList<>();
    SizeOptionsAdapter sizeOptionsAdapter;
    List<SizeDto> sizeDtoList = new ArrayList<>();
    HashMap<String, ArrayList<SizeCountListmodel>> sizeDataMap = new HashMap<String, ArrayList<SizeCountListmodel>>();
    ArrayList<SizeCountListmodel> sizeDtoArrayList = new ArrayList<>();


    private void setColorDataView() {
        // new code..

        binding.colorFlexBoxLL.removeAllViews();
        for (int i = 0; i < colorDtoList.size(); i++) {
            TextView textView = new TextView(this);
            textView.setId(Integer.parseInt(colorDtoList.get(i).getId()));
            textView.setText(colorDtoList.get(i).getColour().toString());
            textView.setBackgroundResource(R.drawable.layout_border_sel);
            textView.setPadding(15, 10, 15, 10);
            textView.setTextColor(Color.parseColor("#000000"));
            //button.setOnClickListener(getOnClickButton(button));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            textView.setLayoutParams(params);
            textView.setTextSize(17f);
            textView.setTag(Integer.valueOf(i));
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TextView button = (TextView) v;
                    String idd = "" + button.getId();
                    int index = (int) button.getTag();
                    for (int i = 0; i < colorDtoList.size(); i++) {

                        TextView textv = ((TextView) binding.colorFlexBoxLL.getChildAt(i));
                        textv.setBackgroundResource(R.drawable.layout_border_sel);
                        //((TextView) binding.colorFlexBoxLL.getChildAt(i)).setTextColor(Color.parseColor("#000000"));
                        textv.setPadding(15, 10, 15, 10);

                    }
                    System.out.println("*************id******" + textView.getId());
                    System.out.println("and text***" + textView.getText().toString());
                    System.out.println("************* index ******" + index);
                    //String idd = "" + button.getId();
                    //selButtonValueST = textView.getText().toString();
                    textView.setBackgroundResource(R.drawable.layout_border_unsel);

                    textView.setPadding(15, 10, 15, 10);

                    binding.selColorValTV.setText("" + colorDtoList.get(index).getColour());
                    setSizeDataView(index);

                    // added dt: 02-06-2022
                    // call color image api..
                    getItemColorImages();

                }
            });

            binding.colorFlexBoxLL.addView(textView);
        }

        int defaultSelColorVal = 0;
        // commented on 02-06-2022
       /* TextView textv = ((TextView) binding.colorFlexBoxLL.getChildAt(defaultSelColorVal));
        textv.setBackgroundResource(R.drawable.layout_border_unsel);
        //((TextView) binding.colorFlexBoxLL.getChildAt(i)).setTextColor(Color.parseColor("#000000"));
        textv.setPadding(15, 10, 15, 10);
        //colorOptionsAdapter.setPositionSelected(defaultSelColorVal);
        binding.selColorValTV.setText("" + colorDtoList.get(defaultSelColorVal).getColour());*/
        setSizeDataView(defaultSelColorVal);

    }

    String lastScanBarcode = "";

    private void getItemColorImages() {
        showProgressDialog();
        Log.e("Item_Image", "lastScanBarcode ::" + lastScanBarcode);
        network(getApiInterface().getItemImgUrl(lastScanBarcode), GeneralConstant.ITEM_IMAGE_URL);

    }


    public boolean checkSizeIsAlphabet(String size) {
        boolean isAnyLetter = false;
        char[] ch = size.toCharArray();
        for (char c : ch) {
            if (Character.isLetter(c)) {
                Log.e("CHECK CHAR", "Character.isLetter(c) ::" + Character.isLetter(c));
                isAnyLetter = true;
            }
        }
        Log.e("CHECK CHAR", "isAnyLetter ::" + isAnyLetter);
        if (isAnyLetter) {
            Log.e("CHECK CHAR", "return false");
            return false;
        } else {
            Log.e("CHECK CHAR", "return true ");
            return true;

        }
    }

    int sizeInAlphabet = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    private void setSizeDataView(int position) {
        sizeDtoList = new ArrayList<>();
        sizeDtoArrayList = new ArrayList<>();
        String colorId = colorDtoList.get(position).getId();
        if (sizeDataMap == null) {
            showAlert("Size options not avaliable for selected color.");
            return;
        }

        if (sizeDataMap.size() == 0) {
            showAlert("Size options not avaliable for selected color.");
            return;
        }
        sizeDtoArrayList = sizeDataMap.get(colorId);
        int count = 0;
        for (SizeCountListmodel sizeCountListmodel : sizeDtoArrayList) {

            // changed on 10-08-2022
            if (sizeCountListmodel.getSize().contains("(")) {
                // do nothing..   // don't display it..
            } else {
                SizeDto sizeDto = new SizeDto();
                sizeDto.setId(colorId);
                sizeDto.setSize(sizeCountListmodel.getSize());
                if (checkSizeIsAlphabet(sizeCountListmodel.getSize())) {
                    sizeInAlphabet = 1;
                } else {
                    sizeInAlphabet = 0;
                }
                sizeDto.setCount(sizeCountListmodel.getCount());
                sizeDto.setItemBarCode(sizeCountListmodel.getItemBarCode());
                sizeDto.setSkuCode(sizeCountListmodel.getSkuCode());
                sizeDto.setStyleCode(sizeCountListmodel.getStyleCode());
                sizeDto.setItemDesc(sizeCountListmodel.getItemDesc());
                sizeDto.setLastSold(sizeCountListmodel.getLastSold());
                sizeDto.setMrp(sizeCountListmodel.getMrp());

                sizeDtoList.add(sizeDto);
            }

        }
        Log.e("CHECK CHAR", "sizeInAlphabet ::" + sizeInAlphabet);
        if (sizeInAlphabet == 1) {

            // Collections.sort(sizeDtoList, SizeDto.sizeComparatorDesc);
        }

        // new code..

        // 01-02-2022

        binding.sizeFlexBoxLL.removeAllViews();
        for (int i = 0; i < sizeDtoList.size(); i++) {
            // old code , before 09-08-2022..
            // its checking for () size also..
            // new code. on 0-08-2022
            // allow only without () size
            if (sizeDtoList.get(i).getSize().contains("(")) {
                // do nothing.. // don't display it..
            } else {
                createSizeItemView(i);
            }
        }

    }


    public void createSizeItemView(int i) {
        TextView textView = new TextView(this);
        textView.setId(Integer.parseInt(sizeDtoList.get(i).getId()));
        // @@@@@@@@@@@@@@@@@@@@@@@

        /*if (sizeDtoList.get(i).getSize().contains("(")) {
            // String sizee = storeStatusDto.getSize().substring()
            String[] parts = sizeDtoList.get(i).getSize().split("\\(");
            String part1 = parts[0]; // 004
            textView.setText(part1.trim());
        } else*/
        textView.setText(sizeDtoList.get(i).getSize().trim());

        // textView.setText(""+sizeDtoList.get(i).getSize());

        textView.setBackgroundResource(R.drawable.layout_border_sel);
        textView.setPadding(15, 10, 15, 10);
        textView.setTextColor(Color.parseColor("#000000"));
        //button.setOnClickListener(getOnClickButton(button));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(params);
        textView.setTextSize(18f);
        textView.setTag(Integer.valueOf(i));
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView button = (TextView) v;
                String idd = "" + button.getId();
                int index = (int) button.getTag();
                for (int i = 0; i < sizeDtoList.size(); i++) {

                    LinearLayout textvLL = ((LinearLayout) binding.sizeFlexBoxLL.getChildAt(i));
                    showLog(" on click : INDEX  i  ::" + i);
                    for (int j = 0; j < textvLL.getChildCount(); j++) {
                        showLog(" on click : INDEX  j  ::" + j);
                        View child = textvLL.getChildAt(j);
                        showLog(" on click : Child count ::" + textvLL.getChildCount());
                        showLog(" on click : Child count ::" + textvLL.getChildCount());
                        //TextView textv = ((TextView) binding.sizeFlexBoxLL.getChildAt(i));
                        // TextView textv = ((TextView) textvLL.getChildAt(j));
                        TextView textv = ((TextView) textvLL.getChildAt(0));
                        textv.setBackgroundResource(R.drawable.layout_border_sel);
                        //((TextView) binding.sizeFlexBoxLL.getChildAt(i)).setTextColor(Color.parseColor("#000000"));
                        textv.setPadding(15, 10, 15, 10);
                    }


                }
                System.out.println("*************id******" + textView.getId());
                System.out.println("and text***" + textView.getText().toString());
                System.out.println("************* index ******" + index);
                //String idd = "" + button.getId();
                //selButtonValueST = textView.getText().toString();
                textView.setBackgroundResource(R.drawable.layout_border_unsel);
                textView.setPadding(15, 10, 15, 10);
                //binding.selColorValTV.setText("" + sizeDtoList.get(index).getSize());
                //setSizeDataView(index);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SizeDto selSizeDto = sizeDtoList.get(index);
                        barCodeNo = selSizeDto.getItemBarCode();
                        getPDPPageUrl(storeId, custMobile, ouName, barCodeNo, tabSelected, "MSC", userId);
                    }
                }, 200);
            }
        });

        String resultST = sizeDtoList.get(i).getCount();
        if (resultST == null || resultST.equals("null")) {
            resultST = "0";
        }
        int result = Integer.parseInt(resultST);
        TextView sohTV = new TextView(this);
        int length = String.valueOf(result).length();
        if (length == 1) {
            sohTV.setText("  " + result + "  ");
        } else if (length == 2) {
            sohTV.setText(" " + result + " ");
        } else {
            sohTV.setText("" + result);
        }
        //sohTV.setText("" + result);
        LinearLayout.LayoutParams sohParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sohParams.setMargins(10, 10, 10, 10);
        sohTV.setLayoutParams(sohParams);
        sohTV.setTextSize(12f);
        sohTV.setPadding(15, 10, 15, 10);
        sohTV.setGravity(Gravity.CENTER_HORIZONTAL);
        //sohTV.setTextColor(Color.parseColor("#000000"));
        //sohTV.setBackgroundColor(Color.parseColor("#ffffff"));
        if (result == 0 || result == 1 || result == 2) {
            sohTV.setTextColor(Color.parseColor("#66BB6A"));// green
            // sohTV.setTextColor(Color.parseColor(ColorTransparentUtils.transparentColor(R.color.green,90)));
            //sohTV.getBackground().setAlpha(51);
        } else {
            sohTV.setTextColor(Color.parseColor("#EF5350")); // red
            //sohTV.setTextColor(Color.parseColor(ColorTransparentUtils.transparentColor(R.color.red,90)));
            //sohTV.getBackground().setAlpha(51);
        }

        // sohTV.setTextColor(Color.parseColor(ColorTransparentUtils.transparentColor(R.color.green,10)));

        LinearLayout sizeandSohLL = new LinearLayout(this);
        LinearLayout.LayoutParams sizeandSohLLParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sizeandSohLL.setOrientation(LinearLayout.VERTICAL);

        sizeandSohLL.addView(textView);
        sizeandSohLL.addView(sohTV);
        binding.sizeFlexBoxLL.addView(sizeandSohLL);
    }


    // added Dt: 16-06-2022
    static String DeleteCacheTag = "Delete CACHE";

    public static void deleteCache(Context context) {
        try {
            File cache = context.getCacheDir();
            Log.e(DeleteCacheTag, " Cache Dir Length : " + cache.listFiles().length);
            Log.e(DeleteCacheTag, " Cache Dir Path : " + cache.getAbsolutePath());
            Log.e(DeleteCacheTag, " Cache Dir Space : " + cache.getTotalSpace());

            Logger.writeToFile("## " + DeleteCacheTag + " ##: Cache Dir Length : " + cache.listFiles().length);
            Logger.writeToFile("## " + DeleteCacheTag + " ##: Cache Dir Path : " + cache.getAbsolutePath());


            //deleteDir(dir);

            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();


                for (String s : children) {
                    Log.e("Delete CACHE", "   name : " + s);

                   /* if (!s.equals("lib") || !s.equalsIgnoreCase("databases")
                    || !s.equalsIgnoreCase("shared_prefs")) {
                        deleteDir(new File(appDir, s));
                        Log.i("EEERRRROOORR", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                        Logger.writeToFile("## " + DeleteCacheTag + " ##:   ************** File /data/data/APP_PACKAGE/" + s + " DELETED ****************");

                    }
                    */

                    if (s.equals("cache")) {
                        Logger.writeToFile("## " + DeleteCacheTag + " ##:   Dir  name : " + s);

                        Log.e("Delete CACHE", " children.length : " + children.length);
                        Logger.writeToFile("## " + DeleteCacheTag + " ##: children Dir length : " + children.length);

                        Logger.writeToFile("## " + DeleteCacheTag + " ##:   Deleting.............. : " + s);
                        Log.e("Delete CACHE", "   Deleting.............. : " + s);
                        deleteDir(new File(appDir, s));
                        Log.i("EEERRRROOORR", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                        Logger.writeToFile("## " + DeleteCacheTag + " ##:   ************** File /data/data/APP_PACKAGE/" + s + " DELETED ****************");

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            Log.e("Delete Dir", " children.length : " + children.length);
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                Log.e("Delete Dir", " success : " + success);
                Logger.writeToFile("## " + DeleteCacheTag + " ##:  is Dir  deleted : " + success);

                if (!success) {
                    return false;
                }
            }

            boolean dirDelete = dir.delete();
            Log.e("Delete Dir", "111 dirDelete : " + dirDelete);
            return dirDelete;
        } else if (dir != null && dir.isFile()) {
            boolean dirDelete = dir.delete();
            Log.e("Delete Dir", "222 dirDelete : " + dirDelete);
            return dirDelete;
        } else {
            Log.e("Delete Dir", "333 false : false");
            return false;
        }
    }

    // added Dt: 10-11-2022
    public void clearEmpId() {
        PrefManager prefManager = new PrefManager(activity);
        // initView();
        prefManager.setEmployeeId(null);
    }
}
//1666
//2015// 10-08-2022
//   // 10-08-2022