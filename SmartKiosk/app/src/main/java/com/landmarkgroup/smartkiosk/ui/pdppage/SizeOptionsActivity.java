package com.landmarkgroup.smartkiosk.ui.pdppage;

import static com.landmarkgroup.smartkiosk.util.CustomDialogs.idealTimeOut;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showGoingHome;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showIdelBefore;
import static com.landmarkgroup.smartkiosk.util.CustomDialogs.showTimeoutDialog;
import static com.landmarkgroup.smartkiosk.util.Utils.clearCookies;
import static com.landmarkgroup.smartkiosk.util.Utils.displaySnackBar;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.databinding.ActivitySizeOptionsBinding;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.landmarkgroup.smartkiosk.storage.ContextManager;
import com.landmarkgroup.smartkiosk.ui.adapter.ColorOptionsAdapter;
import com.landmarkgroup.smartkiosk.ui.adapter.SizeOptionsAdapter;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColorDto;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColourSizeListModel;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelRequest;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeCountListmodel;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeDto;
import com.landmarkgroup.smartkiosk.util.Logger;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.util.Utils;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.lifestyle.retail.constants.Constant;
import com.lifestyle.retail.constants.UserDataDefs;
import com.lifestyle.retail.utils.GeneralConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class SizeOptionsActivity extends BaseCompatActivity implements AlertDialogListener.AlertPositiveListener {
    SizeOptionsActivity activity;
    String errorMsg;


    ActivitySizeOptionsBinding binding;
    private SharedPreferences sharedPreferences;
    private String[] modes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_size_options);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_size_options);
        activity = this;
        ContextManager.setAppContext(SizeOptionsActivity.this);

         initlistandmap();
        initView();

    }

    private void initlistandmap() {
        completeColorHashMap = new HashMap<String, String>();
        colourSet = new HashSet<String>();
        sizeSet = new HashSet<String>();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
//        screenSize = display.getWidth();
    }
    private Handler handler;
    private Runnable runnable;
    private int count = 0;
    private StringBuilder builder = new StringBuilder();
    int ouCode;
    private String ouName, storeId, tabSelected, userId, barCodeNo;

    private void initView() {

        clearCookies(activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SizeOptionsActivity.this);
        binding.toolbarLayout.etBarCode.setVisibility(View.VISIBLE);
        binding.toolbarLayout.btnScan.setVisibility(View.VISIBLE);
        binding.toolbarLayout.btnChooseCamera.setVisibility(View.VISIBLE);

        PrefManager prefManager = new PrefManager(activity);
        ouCode = prefManager.getOuCode();
        ouName = prefManager.getOuName();
        storeId = prefManager.getStoreId();
        tabSelected = prefManager.getTabSelected();
        userId = prefManager.getEmployeeId();

        userId = "1063694";//prefManager.getEmployeeId();
        storeId = "01346";//prefManager.getStoreId();

        int ouCode = prefManager.getOuCode();


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


        binding.itemEanCodeValTV.setText("");
        //getallatoptionlevel("1000005115260");
        // performSearch("1000005115260");


        /*if (ouCode == 5) {
             binding.sizeOptionScreenLL.setBackgroundColor(getResources().getColor(R.color.ls_theme_color));
            binding.textoptionOne.setTextColor(getResources().getColor(R.color.text));
            binding.textoptionTwo.setTextColor(getResources().getColor(R.color.text));

            binding.toolbarLayout.etBarCode.setBackground(getResources().getDrawable(R.drawable.edittext_ls));
            binding.toolbarLayout.etBarCode.setTextColor(getResources().getColor(R.color.text));
        } else {
            binding.sizeOptionScreenLL.setBackground(getResources().getDrawable(R.drawable.background));
            binding.textoptionOne.setTextColor(getResources().getColor(R.color.white));
            binding.textoptionTwo.setTextColor(getResources().getColor(R.color.white));

            binding.toolbarLayout.etBarCode.setBackground(getResources().getDrawable(R.drawable.edittext_background));
            binding.toolbarLayout.etBarCode.setTextColor(getResources().getColor(R.color.text));

        }*/
    }


    private void performSearch(String itemCode) {

        if (itemCode.trim().length() > 0) {
            Logger.writeToFile("## BARCODE ##: perform search barCode ::" + itemCode + "::");
            Logger.writeToFile("## BARCODE ##: perform search :: above barCode is used in api.");

            stopHandler();
            Log.e("SizeOption", " itemCode :: " + itemCode);
            builder.delete(0, builder.length());
            barCodeNo = itemCode;
            // getPDPPageUrl(storeId, custMobile, ouName, itemCode, tabSelected, "MSC", userId);
            binding.toolbarLayout.etBarCode.setText("");
            getallatoptionlevel(itemCode);
            //binding.browserLL.webView.requestFocus();
            //hideSoftKeyboard(activity, etBarCode);
        } else {
            displaySnackBar(activity, "Please Enter Item code");
        }
    }

    public void stopHandler() {
        handler.removeCallbacks(runnable);
    }

    public void startHandler() {
        count = 0;
        handler.postDelayed(runnable, (idealTimeOut));
    }


    private HashMap<String, String> completeColorHashMap;
    ArrayList<String> list = new ArrayList<>();
    private HashSet<String> colourSet, sizeSet;

    private void getallatoptionlevel(String lastScanItem) {
        showProgressDialog();
        GetItemDetailsAtOptionLevelRequest request = new GetItemDetailsAtOptionLevelRequest();
        request.itemCode = lastScanItem;
        /*request.usecaseId = "3";
        request.userId = userId;
        request.storeId = storeId;*/
        // SaveLastScanItem(lastScanItem, "0");
        Log.d("SOH_REQUEST==>", new Gson().toJson(request));
        network(getApiInterface().getItemDetailsAtOptionLevel(request), GeneralConstant.SOH_OPTIONDETAILS);
    }

    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        if (requestCode == GeneralConstant.SOH_OPTIONDETAILS) {
            if (statusResponse instanceof GetItemDetailsAtOptionLevelResponse) {

                GetItemDetailsAtOptionLevelResponse getItemDetailsAtOptionLevelResponse = (GetItemDetailsAtOptionLevelResponse) statusResponse;
                if (getItemDetailsAtOptionLevelResponse != null) {
                    Log.d("response===>", new Gson().toJson(getItemDetailsAtOptionLevelResponse));
                    binding.itemEanCodeValTV.setText("" + barCodeNo);
                    if (getItemDetailsAtOptionLevelResponse.getColourSizeListModelArrayList() != null && getItemDetailsAtOptionLevelResponse.getColourSizeListModelArrayList().size() > 0) {
                        int i = 1, j = 1;
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
                                if (tempSize != null && tempCount != null) {
                                    completeColorHashMap.put(tempColour + "." + tempSize, tempCount);


                                    if (tempItemCode != null && tempEANCode != null) {
                                        completeColorHashMap.put("." + tempColour + "." + tempSize + "." + tempCount, j + "00000" + i);
                                        completeColorHashMap.put(j + "00000" + i, "EAN Code: " + tempEANCode + "\nItem Code: " + tempItemCode + "\nLast Sold  : " + tempLastSold + "\nSize           : " + tempSize + "\nColor         : " + tempColour);//+"_"+tempCount  );

                                        // added 23-12-2021..

                                        sizeDataMap.put(id, colourSizeListModel.getSizeCountListmodelArrayList());

                                    }
                                }
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
                    }
                }

                display("");
            }
        }
    }

    private void display(String s) {
        try {
            if (errorMsg != null) {
                showAlert("" + errorMsg);
                removeAllViews();
            } else {
                // createSohMatrixDetailsDataTable();
                createNewSizeColorView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Abrar", "Errro: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPositiveClick(int position) {

    }

    @Override
    public void onNegativeClick(int position) {

    }

    protected void removeAllViews() {

    }

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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
            if (Constant.checkPermissionAPI(SizeOptionsActivity.this, Manifest.permission.CAMERA)) {
                if (Constant.getSuppourtedFocusedModes()) {
                    Log.e("SCANNER", "22  BarcodeCaptureActivity");
                    Intent intent = new Intent(SizeOptionsActivity.this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intent, 9001);
                } else {
                    Log.e("SCANNER", "33  CaptureActivity");
                    Intent intent = new Intent(SizeOptionsActivity.this, CaptureActivity.class);
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

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


    private void createNewSizeColorView() {

        binding.colorOptionsRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        binding.colorOptionsRV.setLayoutManager(layoutManager);
        binding.colorOptionsRV.setItemAnimator(new DefaultItemAnimator());


        int mNoOfColumns = Utils.calculateNoOfColumns(activity, 80);

        binding.sizeOptionsRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerGrid = new GridLayoutManager(activity, mNoOfColumns);
        binding.sizeOptionsRV.setLayoutManager(layoutManagerGrid);
        binding.sizeOptionsRV.setItemAnimator(new DefaultItemAnimator());

        setColorDataView();


    }

    ColorOptionsAdapter colorOptionsAdapter;
    List<ColorDto> colorDtoList = new ArrayList<>();
    SizeOptionsAdapter sizeOptionsAdapter;
    List<SizeDto> sizeDtoList = new ArrayList<>();
    HashMap<String, ArrayList<SizeCountListmodel>> sizeDataMap = new HashMap<String, ArrayList<SizeCountListmodel>>();
    ArrayList<SizeCountListmodel> sizeDtoArrayList = new ArrayList<>();

    private void setColorDataView() {

        colorOptionsAdapter = new ColorOptionsAdapter(activity, colorDtoList, new ColorOptionsAdapter.OnItemClickListener() {
            LinearLayout previousView = null;

            @Override
            public void onItemClick(int item, View view) {
                // showOsdPickedOrderView();
                // replaceFragment(StoreStatusDetailFragment.newInstance(), "StoreStatusDetail");
                // RTLogStoreStatusDto rtLogStoreStatusDto = rtlogResaStatusList.get(item);
                // showStoreStatusDetailDialog(rtLogStoreStatusDto);

                //TextView textView = (TextView) view.findViewById(R.id.colorNameTV);
                LinearLayout textView = (LinearLayout) view.findViewById(R.id.listItemNew);
                colorOptionsAdapter.setPositionSelected(item);
                if (previousView != null) {
                    // revert the previous view when a new item is clicked
                    //previousView.setTextColor(Color.parseColor("#cccccc"));
                    //previousView.setBackgroundColor(getResources().getColor(R.color.white));
                    previousView.setBackgroundResource(R.drawable.layout_border_unsel);
                }
                //textView.setBackgroundColor(getResources().getColor(R.color.grey_bg));

                //textView.setBackgroundResource(R.drawable.layout_border_unsel);
                textView.setBackgroundResource(R.drawable.layout_border_sel);

                previousView = textView;
                setSizeDataView(item);
            }
        });
        binding.colorOptionsRV.setAdapter(colorOptionsAdapter);

        colorOptionsAdapter.setPositionSelected(0);
        setSizeDataView(0);
    }

    private void setSizeDataView(int position) {
        sizeDtoList = new ArrayList<>();
        sizeDtoArrayList = new ArrayList<>();
        String colorId = colorDtoList.get(position).getId();
        sizeDtoArrayList = sizeDataMap.get(colorId);
        int count = 0;
        for (SizeCountListmodel sizeCountListmodel : sizeDtoArrayList) {
            SizeDto sizeDto = new SizeDto();
            sizeDto.setId(colorId);
            sizeDto.setSize(sizeCountListmodel.getSize());
            sizeDto.setCount(sizeCountListmodel.getCount());
            sizeDto.setItemBarCode(sizeCountListmodel.getItemBarCode());
            sizeDto.setSkuCode(sizeCountListmodel.getSkuCode());
            sizeDto.setStyleCode(sizeCountListmodel.getStyleCode());
            sizeDto.setItemDesc(sizeCountListmodel.getItemDesc());
            sizeDto.setLastSold(sizeCountListmodel.getLastSold());
            sizeDto.setMrp(sizeCountListmodel.getMrp());


            sizeDtoList.add(sizeDto);
        }
        Collections.sort(sizeDtoList, SizeDto.sizeComparatorDesc);
        sizeOptionsAdapter = new SizeOptionsAdapter(activity, sizeDtoList, new SizeOptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int item, View view) {
                // showOsdPickedOrderView();
                // replaceFragment(StoreStatusDetailFragment.newInstance(), "StoreStatusDetail");
                // RTLogStoreStatusDto rtLogStoreStatusDto = rtlogResaStatusList.get(item);
                // showStoreStatusDetailDialog(rtLogStoreStatusDto);

                //setSizeDataView(item);

                SizeDto selSizeDto = sizeDtoList.get(item);
                barCodeNo = selSizeDto.getItemBarCode();
                /*Intent intent = new Intent(activity, MissingSizeActivity.class);
                intent.putExtra("custMobile", "0000000000");
                intent.putExtra("ItemCode", "" + barCodeNo);
                startActivity(intent);
                overridePendingTransition(0, 0);*/


            }
        });
        binding.sizeOptionsRV.setAdapter(sizeOptionsAdapter);

    }

    //  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

}