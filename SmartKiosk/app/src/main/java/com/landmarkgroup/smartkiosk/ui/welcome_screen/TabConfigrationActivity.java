package com.landmarkgroup.smartkiosk.ui.welcome_screen;

import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.landmarkgroup.smartkiosk.model.EComTabConfigRequest;
import com.landmarkgroup.smartkiosk.model.EComTabDetails;
import com.landmarkgroup.smartkiosk.model.EComTabDetailsResponse;
import com.landmarkgroup.smartkiosk.model.EComTabValidateResponse;
import com.landmarkgroup.smartkiosk.ui.homescreen.HomeScreenActivity;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.R;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.lifestyle.retail.utils.GeneralConstant;

import java.util.ArrayList;
import java.util.List;

import static com.landmarkgroup.smartkiosk.util.Utils.addBottomDots;
import static com.landmarkgroup.smartkiosk.util.Utils.changeStatusBarColor;
import static com.landmarkgroup.smartkiosk.util.Utils.displaySnackBar;
import static com.landmarkgroup.smartkiosk.util.Utils.getMacAddress;
import static com.lifestyle.retail.utils.CommonUtility.showSnackBar;

public class TabConfigrationActivity extends BaseCompatActivity {

    FrameLayout frameLayout;
    LinearLayout dotsLayout;
    Button btnNext;
    Button btnTyrAgain;

    private TabConfigrationActivity activity;
    private PrefManager prefManager;
    private Spinner spTabs;
    private CheckBox chkReleaseTab;
    private Spinner spReleaseTabs;

    private boolean goBack = true;
    private List<String> tabList = new ArrayList<>();
    private List<String> releasetabList = new ArrayList<>();
    private List<EComTabDetails> listofAvailableKioskTabs = new ArrayList<>();
    String tagType = "TabConfigrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        activity = this;
        intView();
        getTabList(prefManager.getStoreId());

        btnTyrAgain.setOnClickListener(v -> {
            if (goBack) {
                finish();
                startActivity(new Intent(activity, StoreCodeActivity.class));
            } else
                getTabList(prefManager.getStoreId());
        });

        btnNext.setText(getString(R.string.start));
        btnNext.setOnClickListener(v -> goNext());
    }

    private void goNext() {
        if (!prefManager.getTabSelected().isEmpty()
                && prefManager.getTabSelected() != null) {
            finish();
            Intent intent = new Intent(activity, HomeScreenActivity.class);
            startActivity(intent);
        } else {
            displaySnackBar(activity, "Choose Kiosk");
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    private void intView() {

        frameLayout = findViewById(R.id.frameLayout);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);
        btnTyrAgain = findViewById(R.id.btnTyrAgain);

        prefManager = new PrefManager(activity);
        ButterKnife.bind(activity, activity);
        changeStatusBarColor(activity);

        View view = LayoutInflater.from(activity).inflate(R.layout.welcome_slide3, frameLayout, false);
        spTabs = view.findViewById(R.id.spTabs);
        spReleaseTabs = view.findViewById(R.id.spReleaseTabs);
        chkReleaseTab = view.findViewById(R.id.chkReleaseTab);
        LinearLayout spTabsSelect = view.findViewById(R.id.spTabsSelect);
        LinearLayout spReleaseTabsSelect = view.findViewById(R.id.spReleaseTabsSelect);
        frameLayout.addView(view);

        addBottomDots(activity, 2, dotsLayout);

        spTabsSelect.setOnClickListener(v -> spTabs.performClick());

        spReleaseTabsSelect.setOnClickListener(v -> spReleaseTabs.performClick());
    }


    public void showDataListIntoRecyclerView(List<EComTabDetails> eComTabDetailsList) {
        listofAvailableKioskTabs.clear();
        listofAvailableKioskTabs.addAll(eComTabDetailsList);
        tabList.clear();
        tabList.add(0, "Select Kiosk");
        releasetabList.clear();
        releasetabList.add(0, "Select Kiosk");
        btnTyrAgain.setText(getString(R.string.goBack));
        goBack = true;
        for (EComTabDetails tabs : eComTabDetailsList) {
            if (tabs.getTabName().startsWith("K")) {

                if (tabs.getStatus().equals("A")) {
                    releasetabList.add(tabs.getTabName());
                } else if (tabs.getStatus().equals("I")) {
                    tabList.add(tabs.getTabName());
                }

                //tabList.add(tabs.getTabName());
            }
        }

        // Active tab
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TabConfigrationActivity.this, android.R.layout.simple_spinner_item, tabList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTabs.setAdapter(dataAdapter);

        // validateTabAllocated(prefManager.getStoreId(), getMacAddress());

        spTabs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spTabs.getSelectedItemPosition() == 0) {
                    chkReleaseTab.setEnabled(false);
                } else {
                    chkReleaseTab.setEnabled(true);
                    for (EComTabDetails tabs : listofAvailableKioskTabs) {
                        if (tabs.getTabName().equalsIgnoreCase(spTabs.getSelectedItem().toString())) {
                            isReleaseTab = 0;
                            updatingTabSelecte(prefManager.getStoreId(), null, tabs.getTabId(), "A", getMacAddress(), spTabs.getSelectedItem().toString());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // dt: 28-07-2021
        // inactive tabs..
        ArrayAdapter<String> dataAdapterNew = new ArrayAdapter<String>(TabConfigrationActivity.this, android.R.layout.simple_spinner_item, releasetabList);
        dataAdapterNew.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReleaseTabs.setAdapter(dataAdapterNew);

        spReleaseTabs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spReleaseTabs.getSelectedItemPosition() == 0) {
                    //chkReleaseTab.setEnabled(false);
                } else {
                    //chkReleaseTab.setEnabled(true);
                    for (EComTabDetails tabs : listofAvailableKioskTabs) {
                        if (tabs.getTabName().equalsIgnoreCase(spReleaseTabs.getSelectedItem().toString())) {
                            isReleaseTab = 1;
                            updatingTabSelecte(prefManager.getStoreId(), null, tabs.getTabId(), "I", getMacAddress(), spReleaseTabs.getSelectedItem().toString());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    int isReleaseTab = 0;

    public void showDataIsEmpty() {
        tabList.clear();
        releasetabList.clear();
        btnTyrAgain.setText(getString(R.string.goBack));
        goBack = true;
        tabList.add(0, "Select Kiosk");
        releasetabList.add(0, "Select Kiosk");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TabConfigrationActivity.this, android.R.layout.simple_spinner_item, tabList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTabs.setAdapter(dataAdapter);

        spTabs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displaySnackBar(activity, "No Kiosk Available");
                isReleaseTab = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayAdapter<String> dataAdapterNew = new ArrayAdapter<String>(TabConfigrationActivity.this, android.R.layout.simple_spinner_item, releasetabList);
        dataAdapterNew.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReleaseTabs.setAdapter(dataAdapterNew);

        spReleaseTabs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displaySnackBar(activity, "No Kiosk Available");
                isReleaseTab = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void showErrorWhenGetData() {
        btnTyrAgain.setText(getString(R.string.tryAgain));
        goBack = false;
    }


    public void statusMessageUpate(String statusMessage, String tabName) {
        if (statusMessage != null) {
            if (statusMessage.equalsIgnoreCase("Y")) {
                if (tabName == null) {
                    Log.d("Abrar", "Release");
                    displaySnackBar(activity, "Kiosk has release successfully");
                    prefManager.setTabSelected(null);
                    chkReleaseTab.setChecked(false);
                } else {
                    Log.d("Abrar", "Update");
                    if (isReleaseTab == 0) {
                        displaySnackBar(activity, "Kiosk has updated successfully");
                        for (EComTabDetails tabs : listofAvailableKioskTabs) {
                            if (tabs.getTabName().equalsIgnoreCase(tabName)) {
                                prefManager.setTabSelected(tabs.getTabId());
                            }
                        }
                    } else if (isReleaseTab == 1) {
                        displaySnackBar(activity, "Kiosk has release successfully");
                    }
                }

                if (isReleaseTab == 0) {
                    // tab is active.. move to next screen
                    new Handler().postDelayed(this::goNext, 500);
                } else if (isReleaseTab == 1) {

                    // tab is inactive..
                    // call tab list api again..
                    prefManager.setTabSelected(null);
                    getTabList(prefManager.getStoreId());

                }

            }
        }
    }


    public void validatingTab(String statusMessage, String tabId, String tabName, String tabNo) {
        if (statusMessage != null) {
            /*if (statusMessage.equalsIgnoreCase("Y")) {
                prefManager.setTabSelected(tabId);
                if (tabId != null && tabName != null && tabNo != null) {
                    if (tabName.startsWith("K")) {
                        tabList.add(1, tabName);
                        spTabs.setSelection(1, false);
                    }
                }

                if (tabId != null) {
                    chkReleaseTab.setEnabled(true);
                    chkReleaseTab.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            new AlertDialog.Builder(activity)
                                    .setTitle("Release Kiosk")
                                    .setMessage("Are you sure you want to release this Kiosk?")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        spTabs.setEnabled(false);
                                        updatingTabSelecte(prefManager.getStoreId(), null, tabId, "I", getMacAddress(), null);
                                    })
                                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                        dialog.dismiss();
                                        chkReleaseTab.setChecked(false);
                                    })
                                    .setIcon(R.drawable.ic_alert)
                                    .show();
                        } else {
                            spTabs.setEnabled(true);
                        }
                    });
                } else {
                    chkReleaseTab.setEnabled(false);
                }

                //prefManager.setTabSelected(selectedTabName);
                //prefManager.setTabSelected(selectedTabNo);
            } else {
                prefManager.setTabSelected(null);
                chkReleaseTab.setEnabled(false);
                //prefManager.setTabSelected(null);
                //prefManager.setTabSelected(null);
            }*/

            // dt: 28-07-2021
            /*spTabs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spTabs.getSelectedItemPosition() == 0) {
                        chkReleaseTab.setEnabled(false);
                    } else {
                        chkReleaseTab.setEnabled(true);
                        for (EComTabDetails tabs : listofAvailableKioskTabs) {
                            if (tabs.getTabName().equalsIgnoreCase(spTabs.getSelectedItem().toString())) {
                                updatingTabSelecte(prefManager.getStoreId(), tabId, tabs.getTabId(), "A", getMacAddress(), spTabs.getSelectedItem().toString());
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });*/
        }
    }


    public void getTabList(String storeCode) {
        showProgressDialog();
        EComTabConfigRequest request = new EComTabConfigRequest();
        request.setStoreId(storeCode);
        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().tabListRequest(request), GeneralConstant.GET_TAB_LIST);
    }

    private void validateTabAllocated(String storeId, String macAddress) {
        showProgressDialog();
        EComTabConfigRequest request = new EComTabConfigRequest();
        request.setStoreId(storeId);
        request.setMacId(macAddress);
        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().tabValidateRequest(request), GeneralConstant.VALIDATE_TAB);
    }

    String tabName = "";

    private void updatingTabSelecte(String storeId, String oldTabId, String newTabId, String status, String macId, String tabName) {
        this.tabName = tabName;
        showProgressDialog();
        EComTabConfigRequest request = new EComTabConfigRequest();
        request.setStoreId(storeId);
        request.setOldTabId(oldTabId);
        request.setNewTabId(newTabId);
        request.setStatus(status);
        request.setMacId(macId);
        request.setUsrId(storeId);

        Gson gson = new Gson();
        String json = gson.toJson(request);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().tabUpdateRequestNew(request), GeneralConstant.UPDATE_TAB);
    }

    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.GET_TAB_LIST) {
            if (statusResponse instanceof EComTabDetailsResponse) {
                EComTabDetailsResponse response = (EComTabDetailsResponse) statusResponse;
                if (response != null) {
                    if (response.getServerErrormsg() != null) {
                        showSnackBar(activity, response.getServerErrormsg());
                        showErrorWhenGetData();
                    } else {
                        if (response.getStoreEnable().equalsIgnoreCase("N")) {
                            showDataIsEmpty();
                        } else {
                            showDataListIntoRecyclerView(response.getTabList());
                        }
                    }
                } else {
                    showSnackBar(activity, "Please Try Again!!!");
                    showErrorWhenGetData();
                }
            }
        } else if (requestCode == GeneralConstant.VALIDATE_TAB) {
            if (statusResponse instanceof EComTabValidateResponse) {
                EComTabValidateResponse response = (EComTabValidateResponse) statusResponse;
                if (response != null) {
                    if (response.getServerErrormsg() != null) {
                        showSnackBar(activity, response.getServerErrormsg());
                        showErrorWhenGetData();
                    } else {
                        validatingTab(response.getStatusMessage(), response.getTabId(), response.getTabName(), response.getTabNo());
                    }
                }
            }
        } else if (requestCode == GeneralConstant.UPDATE_TAB) {
            if (statusResponse instanceof EComTabValidateResponse) {
                EComTabValidateResponse response = (EComTabValidateResponse) statusResponse;
                if (response != null) {
                    if (response.getServerErrormsg() != null) {
                        showSnackBar(activity, response.getServerErrormsg());
                        showErrorWhenGetData();
                    } else {
                        statusMessageUpate(response.getStatusMessage(), tabName);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}