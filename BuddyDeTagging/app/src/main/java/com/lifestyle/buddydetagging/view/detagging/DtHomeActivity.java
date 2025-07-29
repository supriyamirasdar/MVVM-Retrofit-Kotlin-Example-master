package com.lifestyle.buddydetagging.view.detagging;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.lifestyle.buddydetagging.BuildConfig;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.constant.ApiConstant;
import com.lifestyle.buddydetagging.constant.GeneralConstant;
import com.lifestyle.buddydetagging.databinding.ActivityDtHomeBinding;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.PrefManager;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.base.BaseCompatActivity;
import com.lifestyle.buddydetagging.view.detagging.model.Order;
import com.lifestyle.buddydetagging.view.detagging.model.OrderLS;
import com.lifestyle.buddydetagging.view.detagging.model.SearchOrderDto;
import com.lifestyle.buddydetagging.view.detagging.model.SearchOrderDtoLS;
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppVrsnResponse;
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppsVrsnCheckRequest;
import com.lifestyle.buddydetagging.view.login.LoginActivityNew;
import com.lifestyle.buddydetagging.view.login.api.LoginApis;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;
import com.lifestyle.buddydetagging.view.login.dto.UserDTO;
import com.lifestyle.buddydetagging.view.login.dto.UserLoginDTO;
import com.validateemp.ValidEmployeeRequest;
import com.validateemp.ValidEmployeeResponse;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lifestyle.buddydetagging.utils.VersionHelper.versionCompare;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DtHomeActivity extends BaseCompatActivity {

    ActivityDtHomeBinding binding;
    int searchOption = 0;
    private DtHomeActivity activity;
    private static Dialog dialog = null;
    private static EditText etEmpID;
    private String employeeCode, employeeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_dt_home);
        Window window = this.getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        activity = this;
        binding = DataBindingUtil.setContentView(DtHomeActivity.this, R.layout.activity_dt_home);
        binding.toolbarTop.tvToolBarTitle.setText("Search Transaction");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.VISIBLE);
        //binding.toolbarTop.tvToolBarSubtitle.setText("Gopalan Signature Mall-Bangalore(01346)");
        binding.toolbarTop.tvToolBarSubtitle.setText(PreferenceUtils.getStoreName() + " (" + PreferenceUtils.getStoreId() + ")");

        binding.toolbarTop.btnLogout.setVisibility(View.VISIBLE);
        binding.toolbarTop.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleST = "De-Tagging";
                String messageST = "Are you sure? You want to Logout?";
                showLogoutAlertDialog(DtHomeActivity.this, titleST, messageST);
            }
        });
        binding.searchTransMainLL.setVisibility(View.GONE);
        binding.searchTransViewLL.setVisibility(View.GONE);
        binding.orderIdViewLL.setVisibility(View.GONE);
        binding.LytOrderIdForLifestyle.setVisibility(View.GONE);//added supriya 25-01-2023
        binding.mobileNumViewLL.setVisibility(View.GONE);
        binding.btnsearchTrans.setVisibility(View.GONE);
        binding.searchoptionRGB.clearCheck();

        binding.scanOrderCodeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarcode();
            }
        });

        binding.unableToScanTV.setOnClickListener(view -> {
            binding.searchTransMainLL.setVisibility(View.VISIBLE);
            binding.searchTransViewLL.setVisibility(View.VISIBLE);
            binding.mobileNumViewLL.setVisibility(View.GONE);
            binding.orderIdViewLL.setVisibility(View.GONE);
            binding.LytOrderIdForLifestyle.setVisibility(View.GONE);//added supriya 25-01-2023

            binding.btnsearchTrans.setVisibility(View.GONE);
        });


        binding.searchoptionRGB.clearCheck();

        binding.searchoptionRGB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    if (rb.getText().toString().trim().equals("Order Id")) {
                        binding.btnsearchTrans.setVisibility(View.VISIBLE);
                        binding.mobileNumViewLL.setVisibility(View.GONE);

                        //added supriya 25-01-2023
                        if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                            binding.LytOrderIdForLifestyle.setVisibility(View.VISIBLE);
                            binding.orderIdViewLL.setVisibility(View.GONE);
                            binding.orderIDValfORLifeStyle.setText("");
                            binding.mobileNumValET.setText("");
                        } else {
                            binding.orderIdViewLL.setVisibility(View.VISIBLE);
                            binding.LytOrderIdForLifestyle.setVisibility(View.GONE);//ADDED SUPRIYA 30-01-2023
                            binding.searchValET.setText("");
                            binding.searchValTwoET.setText("");
                            binding.searchValThreeET.setText("");
                            binding.mobileNumValET.setText("");
                        }
                        searchOption = 2;
                        binding.btnsearchTrans.setBackgroundResource(R.drawable.button_disable_bg);
                    } else if (rb.getText().toString().trim().equals("Mobile Number")) {
                        binding.btnsearchTrans.setVisibility(View.VISIBLE);
                        binding.orderIdViewLL.setVisibility(View.GONE);
                        binding.LytOrderIdForLifestyle.setVisibility(View.GONE);//ADDED SUPRIYA 30-01-2023
                        binding.mobileNumViewLL.setVisibility(View.VISIBLE);

                        binding.searchValET.setText("");
                        binding.searchValTwoET.setText("");
                        binding.searchValThreeET.setText("");
                        binding.mobileNumValET.setText("");
                        searchOption = 1;
                        binding.btnsearchTrans.setBackgroundResource(R.drawable.button_disable_bg);
                    }
                }

            }
        });
        //binding.mobileNumValET.setText("9667080770");// 9540390487
        binding.mobileNumValET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                if (searchingOrder.length() == 10) {
                    //seachOrderFromList(searchingOrder.trim());
                    // enable proceed button...
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }
            }
        });
        ///////////////ADDED SUPRIYA 25-01-2023 CLICK OF orderIDValfORLifeStyle EDITTEXT//////////
        binding.orderIDValfORLifeStyle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                if (searchingOrder.length() > 9) {
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }
            }
        });

        ////////////////END OF orderIDValfORLifseStyle EDITTEXT///////////////////////////////////////////////


        binding.searchValET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                /*if (searchingOrder.length() > 6) {
                    //seachOrderFromList(searchingOrder.trim());
                    // enable proceed button...
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }*/
                // added dt: 08-10-2021
                if (searchingOrder.length() > 3) {
                    binding.searchValTwoET.requestFocus();
                }
            }
        });

        binding.searchValTwoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                /*if (searchingOrder.length() > 6) {
                    //seachOrderFromList(searchingOrder.trim());
                    // enable proceed button...
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }*/
                // added dt: 08-10-2021
                if (searchingOrder.length() > 5) {
                    binding.searchValThreeET.requestFocus();
                }
            }
        });

        binding.searchValThreeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                /*if (searchingOrder.length() > 6) {
                    //seachOrderFromList(searchingOrder.trim());
                    // enable proceed button...
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }*/
                // added dt: 08-10-2021
                if (searchingOrder.length() > 3) {
                    binding.btnsearchTrans.setBackgroundResource(R.drawable.toolbar_bg);
                }
            }
        });

        binding.btnsearchTrans.setOnClickListener(view -> {
            if (searchOption == 1) {
                if (binding.mobileNumValET.getText().toString().length() == 10) {
                    validateMobileNumberData();
                }
            } else if (searchOption == 2) {
                /*if (binding.searchValET.getText().toString().length() > 10) {
                    validateOrderIdData();
                }*/
                // added dt: 08-10-2021

                //ADDED BY SUPRIYA
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                    String str = binding.orderIDValfORLifeStyle.getText().toString().trim();
                    if (str.length() > 9) {
                        validateOrderIdDataForLS();
                    }
                } else {
                    String str1 = binding.searchValET.getText().toString().trim();
                    String str2 = binding.searchValTwoET.getText().toString().trim();
                    String str3 = binding.searchValThreeET.getText().toString().trim();
                    String str = str1 + "-" + str2 + "-" + str3;
                    if (str.length() > 10) {
                        validateOrderIdData();
                    }
                }


            }
        });

        binding.mobileNumValET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.mobileNumValET.getWindowToken(), 0);
                    String itemCode = binding.mobileNumValET.getText().toString().trim();
                    if (itemCode.length() == 10) {
                        validateMobileNumberData();
                    }
                    return true;
                }
                return false;
            }
        });
        /////////////////////ADDED BY SUPRIYA 25-01-2023 bindig.  ///////////////////////

        ///////////ended by supriuya 25-01-2023////////////////////////////////////////////


        binding.searchValET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.searchValET.getWindowToken(), 0);
                    String str1 = binding.searchValET.getText().toString().trim();

                    if (str1.length() > 3) {
                        binding.searchValTwoET.requestFocus();
                    } else if (str1.length() < 4) {
                        binding.searchValET.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        binding.searchValTwoET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.searchValTwoET.getWindowToken(), 0);
                    String str1 = binding.searchValET.getText().toString().trim();
                    String str2 = binding.searchValTwoET.getText().toString().trim();

                    if (str1.length() > 3 && str2.length() > 5) {
                        binding.searchValThreeET.requestFocus();
                    } else if (str1.length() < 4) {
                        binding.searchValET.requestFocus();
                    } else if (str2.length() < 6) {
                        binding.searchValTwoET.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });


        binding.searchValThreeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.searchValThreeET.getWindowToken(), 0);
                    String str1 = binding.searchValET.getText().toString().trim();
                    String str2 = binding.searchValTwoET.getText().toString().trim();
                    String str3 = binding.searchValThreeET.getText().toString().trim();
                    if (str1.length() > 3 && str2.length() > 5 && str3.length() > 3) {
                        validateOrderIdData();
                    } else if (str1.length() < 4) {
                        binding.searchValET.requestFocus();
                    } else if (str2.length() < 6) {
                        binding.searchValTwoET.requestFocus();
                    } else if (str3.length() < 4) {
                        binding.searchValThreeET.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        //Adding click listener for emp id icon
        binding.ivEmpId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterEmployeeCode(DtHomeActivity.this);
            }
        });

        //info icon on click
        binding.ivMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.showInfoDialog(DtHomeActivity.this, getString(R.string.enter_csr_id_info_dialod_text));
            }
        });
    }

    public void enterEmployeeCode(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_employee_ls);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        etEmpID = dialog.findViewById(R.id.etEmployeeCode);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        etEmpID.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetterOrDigit(c);//|| Character.isSpaceChar(c)
            }
        }, new InputFilter.LengthFilter(11)});

        btnYes.setOnClickListener(v -> {
            employeeCode = etEmpID.getText().toString().trim();
            if (employeeCode.length() > 0) {
                if (employeeCode.length() < 7) {
                    etEmpID.setError("Enter correct Employee Id");
                } else {
                    // call api to validate employee
                    validateEmployee(employeeCode);
                }

            } else
                etEmpID.setError("Enter Employee Id");

        });

        btnNo.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnCancelListener(dialog -> dialog.cancel());

        if (!activity.isFinishing())
            if (!dialog.isShowing())
                dialog.show();
    }

    private void validateEmployee(String empCode) {
        ValidEmployeeRequest validEmployeeRequest = new ValidEmployeeRequest();
        validEmployeeRequest.employeeId = empCode;
        validEmployeeRequest.deviceType = "";
        validEmployeeRequest.macId = "";
        validEmployeeRequest.imei = "";
        validEmployeeRequest.uniqueAndroidId = "";
        validEmployeeRequest.currentVersion = "";
        Log.e("Validate emp request : ", new Gson().toJson(validEmployeeRequest));
        network(getApiInterfaceNew().getValidEmployee(validEmployeeRequest), GeneralConstant.REGISTRATION_VALID_EMPLOYEE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.searchTransMainLL.setVisibility(View.GONE);
        binding.searchTransViewLL.setVisibility(View.GONE);
        binding.orderIdViewLL.setVisibility(View.GONE);
        binding.mobileNumViewLL.setVisibility(View.GONE);
        binding.btnsearchTrans.setVisibility(View.GONE);
        binding.LytOrderIdForLifestyle.setVisibility(View.GONE);//added supriya 30-01-2023
        binding.searchoptionRGB.clearCheck();

    }

    public void validateMobileNumberData() {
        String searchNumber = binding.mobileNumValET.getText().toString().trim();

        if (searchNumber.equals("0")) {
            String titleST = "Search Order";
            String messageST = "No pending detag order found against this mobile number.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else {
           /* binding.searchTransViewLL.setVisibility(View.GONE);
            binding.orderIdViewLL.setVisibility(View.GONE);
            binding.mobileNumViewLL.setVisibility(View.GONE);
            binding.btnsearchTrans.setVisibility(View.GONE);
            binding.searchoptionRGB.clearCheck();

            Intent intent = new Intent(DtHomeActivity.this, DtOrderListActivity.class);
            startActivity(intent);*/
            binding.mobileNumValET.setText("");
            binding.btnsearchTrans.setBackgroundResource(R.drawable.button_disable_bg);

            if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                searchOrderByMobileNo(searchNumber);//LS
            } else {
                searchOrderByMobileNoMAX(searchNumber);//MAX

            }

        }
    }

    ///added by supriya 25-01-2023 validateOrderiddataForLS()///////
    public void validateOrderIdDataForLS() {


        String str = binding.orderIDValfORLifeStyle.getText().toString().trim();
        String searchOrderId = str;

        if (searchOrderId.length() < 10) {
            String titleST = "Search Order";
            String messageST = "Invalid order id.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else {
            binding.orderIDValfORLifeStyle.setText("");
            binding.btnsearchTrans.setBackgroundResource(R.drawable.button_disable_bg);
            searchOrderByOrderId(searchOrderId);
        }

    }
    ////////////////////////end supriya validateOrderiddataForLS///////////////////////////////////////////////


    //MAX
    public void validateOrderIdData() {
        // String searchOrderId = binding.searchValET.getText().toString().trim();

        String str1 = binding.searchValET.getText().toString().trim();
        String str2 = binding.searchValTwoET.getText().toString().trim();
        String str3 = binding.searchValThreeET.getText().toString().trim();
        String searchOrderId = str1 + "-" + str2 + "-" + str3;

        /*if (searchNumber.equals("0")) {
            String titleST = "Search Order";
            String messageST = "Invalid order id.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else if (searchNumber.equals("1")) {
            String titleST = "Search Order";
            String messageST = "This order id not belongs to this store.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else if (searchNumber.equals("9")) {
            String titleST = "Search Order";
            String messageST = "This order id already de-tagged by Krishna Reddy (1063699) on 25-Jul-2021.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else {
            binding.searchTransViewLL.setVisibility(View.GONE);
            binding.orderIdViewLL.setVisibility(View.GONE);
            binding.mobileNumViewLL.setVisibility(View.GONE);
            binding.btnsearchTrans.setVisibility(View.GONE);
            binding.searchoptionRGB.clearCheck();

            Intent intent = new Intent(DtHomeActivity.this, DtOrderDetailActivity.class);
            intent.putExtra("Type", "1");
            startActivity(intent);
        }*/
        if (searchOrderId.length() < 10) {
            String titleST = "Search Order";
            String messageST = "Invalid order id.";
            showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
        } else {
            binding.searchValET.setText("");
            binding.searchValTwoET.setText("");
            binding.searchValThreeET.setText("");
            binding.btnsearchTrans.setBackgroundResource(R.drawable.button_disable_bg);
            /*Log.e("ORDER", "11 searchOrderId::" + searchOrderId);
            searchOrderId = searchOrderId.substring(0, 4) + "-" + searchOrderId.substring(4);
            Log.e("ORDER", "22 searchOrderId::" + searchOrderId);
            searchOrderId = searchOrderId.substring(0, 10) + "-" + searchOrderId.substring(10);
            Log.e("ORDER", "33 searchOrderId::" + searchOrderId);
            */
            searchOrderByOrderIdMAX(searchOrderId);
        }
    }

    String searchOrderId = "";

    public void searchOrderByOrderId(String orderid) {
        final JSONObject json = new JSONObject();
        try {
            /*{
                "storeCode": "02185",
                    "orderId": "1398-904968-4049",
                    "orderDate": "2021-08-30"
            }*/
            searchOrderId = "" + orderid;
            json.put("orderId", "" + orderid);
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                // json.put("storeCode", "02185");//comment supriya 06-04-2023
                //json.put("orderDate", "2021-08-30");//8810396479
//                json.put("orderDate", "2021-11-11");
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
                json.put("orderDate", CommonUtility.getCurrentDate());
                json.put("storeCode", PreferenceUtils.getStoreId());
                json.put("accessToken", LoginActivityNew.accessToken);
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
                json.put("accessToken", LoginActivityNew.accessToken);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderSearchType = 2;
        // getOrderData(json);
        getOrderDataLS(json);
    }

    /// Search orderid for max
    public void searchOrderByOrderIdMAX(String orderid) {
        final JSONObject json = new JSONObject();
        try {
            /*{
                "storeCode": "02185",
                    "orderId": "1398-904968-4049",
                    "orderDate": "2021-08-30"
            }*/
            searchOrderId = "" + orderid;
            json.put("orderId", "" + orderid);
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                json.put("storeCode", "02185");//
                //json.put("orderDate", "2021-08-30");//8810396479
//                json.put("orderDate", "2021-11-11");
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderSearchType = 2;
        getOrderData(json);
    }
    /////////////////////////////


    ////FOR LS TO SERACH BY MOBILE NO
    public void searchOrderByMobileNo(String mobileNo) {
        final JSONObject json = new JSONObject();
        try {

            /*{
                "storeCode": "02185",
                    "mobileNo": "9540390487",
                    "orderDate": "2021-08-30"
            }*/

            json.put("mobileNo", "" + mobileNo);// 9540390487
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                // json.put("storeCode", PreferenceUtils.getStoreId());//
                // json.put("orderDate", "2021-08-30");//8810396479
//                json.put("orderDate", "2021-11-11");
                json.put("orderId", "");
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
                json.put("storeCode", PreferenceUtils.getStoreId());
                json.put("orderDate", CommonUtility.getCurrentDate());
                json.put("accessToken", LoginActivityNew.accessToken);
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("orderId", "");
                json.put("userId", PreferenceUtils.getUserId());
                json.put("userName", PreferenceUtils.getUserName());
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
                json.put("accessToken", LoginActivityNew.accessToken);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderSearchType = 1;
        //getOrderData(json);
        getOrderDataLS(json);
    }

    /////////////////MOBILE NUMBER SERACH FOR MAX ///////////////////
    public void searchOrderByMobileNoMAX(String mobileNo) {
        final JSONObject json = new JSONObject();
        try {

            /*{
                "storeCode": "02185",
                    "mobileNo": "9540390487",
                    "orderDate": "2021-08-30"
            }*/

            json.put("mobileNo", "" + mobileNo);// 9540390487
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                json.put("storeCode", PreferenceUtils.getStoreId());//
                // json.put("orderDate", "2021-08-30");//8810396479
//                json.put("orderDate", "2021-11-11");
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
                json.put("orderDate", "" + CommonUtility.getCurrentDate());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderSearchType = 1;
        getOrderData(json);
    }


    //////////////////END //////////////////////////////////////////////////


    int orderSearchType = 0;

    public void getOrderData(JSONObject json) {
        showProgressDialog();
        OkHttpClient client = new OkHttpClient();

        String url = getResources().getString(R.string.server_address_naggaro) + "" + ApiConstant.ORDER_SEARCH;
        Log.e(TAG, "Request :: " + json.toString());

        String AUTH_TOKEN = "";
        if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_UAT;
        } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_PROD;
        }
        RequestBody body1 = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url("" + url).post(body1)
                .addHeader("Authorization", "" + AUTH_TOKEN)
                .addHeader("Accept", "" + ApiConstant.ACCEPT)
                .addHeader("Content-Type", "" + ApiConstant.CONTENT_TYPE)
                .build();
        Log.e(TAG, "url :: " + url.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure :: " + url.toString());
                e.printStackTrace();

                hideProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    DtHomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            //mTextViewResult.setText(myResponse);
                            //Toast.makeText(AddressListAct.this, "Failed to sync with server", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "myResponse :: " + myResponse);
                            try {
                                JSONObject jsonObjectTop = new JSONObject(myResponse);
                                String statusCode = jsonObjectTop.optString("statusCode");
                                if (statusCode.equals("200")) {
                                    parseOrderDataResponse(jsonObjectTop);
                                } else {
                                    String titleST = "Search Order";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
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

    public void getOrderDataLS(JSONObject json) {
        showProgressDialog();
        OkHttpClient client = new OkHttpClient();
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

        String url = getResources().getString(R.string.server_address_LS) + "" + ApiConstant.ORDER_SEARCH_LS;
        Log.e(TAG, "Request :: " + json.toString());

        String AUTH_TOKEN = "";
        if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_UAT;
        } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_PROD;
        }
        RequestBody body1 = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url("" + url).post(body1)
                //.addHeader("Authorization", "" + AUTH_TOKEN)
                .addHeader("Accept", "" + ApiConstant.ACCEPT)
                .addHeader("Content-Type", "" + ApiConstant.CONTENT_TYPE)
                .build();
        Log.e(TAG, "url :: " + url.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure :: " + url.toString());
                e.printStackTrace();

                hideProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    DtHomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            //mTextViewResult.setText(myResponse);
                            //Toast.makeText(AddressListAct.this, "Failed to sync with server", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "myResponse :: " + myResponse);
                            try {
                                JSONObject jsonObjectTop = new JSONObject(myResponse);
                                // JSONArray jsonarray = new JSONArray(myResponse);
                                String statusCode = jsonObjectTop.optString("statusCode");
                                if (statusCode.equals("200")) {
                                    // parseOrderDataResponseLS(jsonObjectTop);
                                    parseOrderDataResponse(jsonObjectTop);
                                } else {
                                    String titleST = "Search Order";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
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

    private void parseOrderDataResponse(JSONObject jsonObjectTop) {
        //JSONArray jsonArray = new JSONArray(myResponse);
        try {
            JSONArray jsonArray = jsonObjectTop.getJSONArray("orders");
            if (jsonArray.length() > 0) {
                Log.e(TAG, "jsonArray.length()  > 0 :: delvery available ");
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                String orderId = jsonObject.optString("orderId");
                String customerPhoneNo = jsonObject.optString("customerPhoneNo");
                Log.e(TAG, "orderId ::  " + orderId);
                Log.e(TAG, "customerPhoneNo ::  " + customerPhoneNo);
                Log.e(TAG, "jsonObjectTop.toString() ::  " + jsonObjectTop.toString());

                List<Order> orderListComplete = new ArrayList<>();
                Gson gson = new Gson();
                SearchOrderDto searchOrderDto = gson.fromJson(jsonObjectTop.toString(), SearchOrderDto.class);
                int orderStatus = 0;
                if (searchOrderDto.getOrders().size() > 0) {
                    List<Order> orderList = searchOrderDto.getOrders();
                    for (int i = 0; i < orderList.size(); i++) {
                        Order order = orderList.get(i);

                        if (order.isHasDetagged() == false) {
                            orderListComplete.add(order);
                        } else if (order.isHasDetagged() == true) {
                            orderStatus = 1;
                        } else {
                            orderStatus = 0;
                            Log.e(TAG, "orderId ::  " + order.getOrderId() + " is not completed");
                        }

                        // COMMENT BY SUPRIYA 11-04-2023
                        // orderListComplete.add(order);

                    }
                }

                if (orderListComplete.size() > 0) {

                    PreferenceUtils.setOrderData(jsonObjectTop.toString());
                    if (orderListComplete.size() == 1 && orderSearchType == 2) {
                        // redirect to order detail..
                        Intent intent = new Intent(DtHomeActivity.this, DtOrderDetailActivity.class);
                        intent.putExtra("Type", "1");
                        intent.putExtra("OrderId", "" + orderId);
                        startActivity(intent);

                        // added 28-09-2021
                        // Intent intent = new Intent(DtHomeActivity.this, DtOrderListActivity.class);
                        //startActivity(intent);
                    } else if (orderListComplete.size() >= 1) {
                        // redirect to order list..
                        Intent intent = new Intent(DtHomeActivity.this, DtOrderListActivity.class);
                        startActivity(intent);
                    }

                } else {

                    if (orderStatus == 1 && orderSearchType == 2) {
                        // order detagedd..
                        String detaggedBy = jsonObject.optString("detaggedUsername");
                        String detaggedUsername = jsonObject.optString("detaggedUsername");
                        String detaggedAt = jsonObject.optString("detaggedAt"); //COMMENT BY SUPRIYA BECAUSE ITS FOR MAX NOT FOR LS


                        String detagBy = detaggedUsername;// + " (" + detaggedBy + ")";
                        String dateST = detaggedAt;

                      /*  String[] dateSTArry = dateST.split("T");
                        if (dateSTArry.length > 1) {
                            dateST = dateSTArry[0] + " " + dateSTArry[1];
                        } else {
                            dateST = dateSTArry[0];
                        }
                        dateST = dateST.substring(0, dateST.length() - 5);*/
                       /* String curFormat = "yyyy-MM-dd HH:mm:ss";
                        String reqFormat = "dd-MMM-yyyy HH:mm:ss";
                        dateST = CommonUtility.convertDateToRequiredFormat(dateST, curFormat, reqFormat);*/
                        String titleST = "Search Order";
                        String messageST = "This order id (" + searchOrderId + ") already De-Tagged by " + detagBy + " on " + dateST + ".";
                        //String messageST = "This order id (" + searchOrderId + ") is already De-Tagged"+".";
                        showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);

                    } else {
                        String titleST = "Search Order";
                        //String messageST = "" + jsonObjectTop.optString("statusMessage");
                        String messageST = "No pending detag order found against this mobile number.";
                        if (orderSearchType == 2) {
                            messageST = "No pending detag order found against this mobile number.";
                        } else if (orderSearchType == 2) {
                            messageST = "No pending detag order found.";
                        }
                        showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
                    }
                }

            } else {
                String titleST = "Search Order";
                //String messageST = "" + jsonObjectTop.optString("statusMessage");
                String messageST = "No pending detag order found against this mobile number.";
                if (orderSearchType == 1) {
                    messageST = "No pending detag order found against this mobile number.";
                } else if (orderSearchType == 2) {
                    messageST = "No pending detag order found.";
                }
                showAlertDialog(DtHomeActivity.this, titleST, messageST, 0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String TAG = "TAG";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // https://codinginflow.com/tutorials/android/okhttp-simple-get-request


    public void showAlertDialog(final Context context, String title, String message, int isDisplayNo) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.layout_dt_alert_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setInverseBackgroundForced(true).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(deleteDialogView);
        TextView titleTV = deleteDialogView.findViewById(R.id.titleTV);
        TextView alertMessageTV = deleteDialogView.findViewById(R.id.alertMessageTV);
        TextView yesBt = deleteDialogView.findViewById(R.id.yesBt);
        TextView noBt = deleteDialogView.findViewById(R.id.noBt);

        if (isDisplayNo == 1) {
            noBt.setVisibility(View.VISIBLE);
        } else if (isDisplayNo == 0) {
            noBt.setVisibility(View.GONE);
        }

        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        yesBt.setText("Okay");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                // finish();
            }
        });


        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void showLogoutAlertDialog(final Context context, String title, String message) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.layout_dt_alert_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setInverseBackgroundForced(true).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(deleteDialogView);
        TextView titleTV = deleteDialogView.findViewById(R.id.titleTV);
        TextView alertMessageTV = deleteDialogView.findViewById(R.id.alertMessageTV);
        TextView yesBt = deleteDialogView.findViewById(R.id.yesBt);
        TextView noBt = deleteDialogView.findViewById(R.id.noBt);

        noBt.setVisibility(View.VISIBLE);
        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        yesBt.setText("Yes");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                // finish();
                CommonUtility.autoLogout(DtHomeActivity.this);
            }
        });

        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    // private ProgressBarHandler progressBar;
    private ProgressBar mProgressBar;

    public void showProgressDialog() {
        try {
            /*if (progressBar == null) {
                progressBar = new ProgressBarHandler(this);
            } else {
                progressBar.hide(DtHomeActivity.this);
            }
            progressBar.show();*/
            ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
            mProgressBar = new ProgressBar(DtHomeActivity.this, null, android.R.attr.progressBarStyleLarge);

            mProgressBar.getIndeterminateDrawable()
                    .setColorFilter(Color.parseColor("#ffcf00"), PorterDuff.Mode.SRC_IN);//ProgressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.default_spinner));
            //mProgressBar.setBackground(context.getDrawable(R.drawable.progress_background));
            mProgressBar.setPadding(CommonUtility.convertDpToPx(10, DtHomeActivity.this), CommonUtility.convertDpToPx(10, DtHomeActivity.this),
                    CommonUtility.convertDpToPx(10, DtHomeActivity.this), CommonUtility.convertDpToPx(10, DtHomeActivity.this));
            RelativeLayout.LayoutParams params = new
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout rl = new RelativeLayout(DtHomeActivity.this);
            rl.setGravity(Gravity.CENTER);
            rl.addView(mProgressBar);
            layout.addView(rl, params);
            show();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    public void hideProgressDialog() {
        /*if (CommonUtility.isNotNull(progressBar)) {
            progressBar.hide(DtHomeActivity.this);
        }*/
        hide();
    }

    public void show() {
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        try {
            // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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
            if (CommonUtility.checkPermissionAPI(DtHomeActivity.this, Manifest.permission.CAMERA)) {
                if (CommonUtility.getSuppourtedFocusedModes()) {
                    Intent intent = new Intent(DtHomeActivity.this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, 9001);
                } else {
                    Intent intent = new Intent(DtHomeActivity.this, CaptureActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                noCodeCapture();
                //CommonUtility.showSnackBar(DtOrderDetailActivity.this, "No barcode captured, Please Enter Item barcode");
            } else {
                findOrderToPick(resultBarcode);
            }
        } else if (resultCode == CommonStatusCodes.SUCCESS && requestCode == 9001) {
            if (intent != null) {
                Barcode barcode = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                String resultBarcode = barcode.displayValue;
                findOrderToPick(resultBarcode);
            } else {
                noCodeCapture();
            }
        } else if ((Build.BRAND.equalsIgnoreCase("SUNMI"))) {
            if (requestCode == 120 && intent != null) {
                Bundle bundle = intent.getExtras();
                ArrayList<HashMap<String, String>> results = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
                assert results != null;
                String resultBarcode = results.get(0).get("VALUE");
                assert resultBarcode != null;
                findOrderToPick(resultBarcode);
            } else {
                noCodeCapture();
                //CommonUtility.showSnackBar(DtOrderDetailActivity.this, "No barcode captured, Please Enter Item barcode");
            }
        } else if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    installAPK();
                }
            }
        }
    }

    ///calling both for LS AND ADDED BY SUPRIYA 28-04-2023
    private void findOrderToPick(String resultBarcode) {

        if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {//LS
            searchOrderByOrderId(resultBarcode);
        } else {
            searchOrderByOrderIdMAX(resultBarcode);//MAX
        }
    }

    public void noCodeCapture() {
        CommonUtility.showSnackBar(DtHomeActivity.this, "No order code captured.");

    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(binding.homeScreenRL, "Please click back again to exit from the App!!", Snackbar.LENGTH_LONG);
        snackbar.show();

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#BD071D"));
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextSize(getResources().getDimension(R.dimen._5sdp));
        textView.setTextColor(Color.WHITE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
        } else if (requestCode == GeneralConstant.REGISTRATION_VALID_EMPLOYEE) {
            if (statusResponse instanceof ValidEmployeeResponse) {
                ValidEmployeeResponse validEmployeeResponse = (ValidEmployeeResponse) statusResponse;
                if (validEmployeeResponse.getSuccessIndi().equalsIgnoreCase("true")) {
                    // save data in preference manager..
                    Log.d(TAG, validEmployeeResponse.toString());
                    if (null != validEmployeeResponse.getUserDetailModel()) {
                        employeeName = nullCheck(validEmployeeResponse.getUserDetailModel().getFirstName()) + " " +
                                nullCheck(validEmployeeResponse.getUserDetailModel().getLastName());
                    } else {
                        employeeName = null;
                    }

                    saveEmpIdandDismissDialog(employeeCode, employeeName);
                } else {
                    // show error message..
                    String errorMsg = validEmployeeResponse.getStatusErrMessage();
                    // showSnackBar(activity, "" + errorMsg);
                    showErrorEmpDialog(errorMsg);
                }
            }
        }
    }

    public static void saveEmpIdandDismissDialog(String empCode, String empName) {
        PreferenceUtils.setEmployeeId(empCode);
        PreferenceUtils.setEmployeeName(empName);
        //Toast.makeText(dtHomeActivity, empCode + " - " +empName + " saved succesfully", Toast.LENGTH_LONG).show();
        if (dialog != null)
            dialog.dismiss();
    }

    // added 01-12-2022
    public static void showErrorEmpDialog(String errormsg) {
        etEmpID.setError("" + errormsg);

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
                }
            } else {
                Log.d("Abrar", "BuddyDetag curVerTes  and  sysVerTes  are null ::" + curVerTes + " new " + sysVerTes);
                // check for store id and move forward..
                //checkForStoreId();
            }
        } else {
            Log.d("Abrar", "response.getMobAppsVersion() == null :" + response.getMobAppsVersion());
            // check for store id and move forward..
            //checkForStoreId();
        }
    }


    @Override
    public void onClick(View view) {

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
}