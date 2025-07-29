package com.landmarkgroup.smartkiosk.ui.welcome_screen;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.landmarkgroup.smartkiosk.model.DistrictListModel;
import com.landmarkgroup.smartkiosk.model.FetchAllDataRequest;
import com.landmarkgroup.smartkiosk.model.FetchAllDataResponse;
import com.landmarkgroup.smartkiosk.model.RegionModel;
import com.landmarkgroup.smartkiosk.model.StoreListModel;
import com.landmarkgroup.smartkiosk.ui.adapter.StoreListAdapter;
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

public class StoreCodeActivity extends BaseCompatActivity {

    FrameLayout frameLayout;
    LinearLayout dotsLayout;
    Button btnNext;
    Button btnTyrAgain;

    private EditText etStoreSelected;
    private RecyclerView recyclerView;
    private StoreCodeActivity activity;
    private PrefManager prefManager;
    private boolean goBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        activity = this;
        intView();
        storeSelection();

        btnTyrAgain.setOnClickListener(v -> {
            if (goBack) {
                finish();
                startActivity(new Intent(activity, ChooseOUCodeActivity.class));
            } else
                storeSelection();
        });

        btnNext.setOnClickListener(v -> goNext(TabConfigrationActivity.class));
    }


    private void goNext(Class aClass) {
        if (!prefManager.getStoreId().isEmpty()) {
            finish();
            Intent intent = new Intent(activity, aClass);
            startActivity(intent);
        } else {
            displaySnackBar(activity, "Select Store");
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

        View view = LayoutInflater.from(activity).inflate(R.layout.welcome_slide2, frameLayout, false);
        etStoreSelected = view.findViewById(R.id.etStoreSelected);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        frameLayout.addView(view);

        etStoreSelected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.startsWith(" ")) {
                    etStoreSelected.setText("");
                }
            }
        });

        addBottomDots(activity, 1, dotsLayout);
    }

    private void storeSelection() {
        int ouCode = prefManager.getOuCode();
        if (ouCode == 0) {
            displaySnackBar(activity, "Choose OU Code");
            goNext(ChooseOUCodeActivity.class);
        } else {
            String ou_code = String.valueOf(ouCode);
            //presenter.getStoreList(ou_code);
            getStoreList(ou_code);
        }
    }

    String tagType = "StoreCodeActivity";

    public void getStoreList(String ouCode) {
        FetchAllDataRequest fetchAllDataRequest = new FetchAllDataRequest();
        fetchAllDataRequest.setOuCode(ouCode);

        Gson gson = new Gson();
        String json = gson.toJson(fetchAllDataRequest);

        Log.e(tagType, " json :: " + json);
        network(getApiInterface().fetchAllStoreData(fetchAllDataRequest), GeneralConstant.FETCH_ALL_STORE_DATA);

    }

    public void storeSelected(String storeCode, String storeName) {
        prefManager.setStoreId("0" + storeCode);
        prefManager.setStoreName(storeName);

        String store = storeCode + " - " + storeName;
        etStoreSelected.setText(store);
        etStoreSelected.setSelection(store.length());
        recyclerView.setVisibility(View.GONE);
        //hideSoftKeyboard(this, etStoreSelected);

        new Handler().postDelayed(() -> goNext(TabConfigrationActivity.class), 200);
//        new Handler().postDelayed(() -> goNext(HomeScreenActivity.class), 200);

    }

    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.FETCH_ALL_STORE_DATA) {
            if (statusResponse instanceof FetchAllDataResponse) {
                FetchAllDataResponse model = (FetchAllDataResponse) statusResponse;
                if (model != null) {
                    if (model.getRegionModelList() != null) {
                        List<StoreListModel> storeListModels = new ArrayList<>();
                        for (RegionModel regionModel : model.getRegionModelList()) {
                            for (DistrictListModel districtListModel : regionModel.getDistrictListModels()) {
                                storeListModels.addAll(districtListModel.getStoreList());
                            }
                        }

                        if (storeListModels.size() > 0) {
                            showDataListIntoRecyclerView(storeListModels);
                        } else {
                            showDataIsEmpty();
                        }
                    } else {
                        showErrorWhenGetData();
                    }
                } else {
                    showErrorWhenGetData();
                }
            }
        }
    }


    public void showDataListIntoRecyclerView(List<StoreListModel> storeList) {
        btnTyrAgain.setText(getString(R.string.goBack));
        goBack = true;
        StoreListAdapter mAdapter = new StoreListAdapter(activity, storeList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
        etStoreSelected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.isEmpty() && text.length() >= 2) {
                    recyclerView.setVisibility(View.VISIBLE);
                    mAdapter.getFilter().filter(text);
                } else {
                    if (recyclerView.getVisibility() == View.VISIBLE)
                        recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }


    public void showDataIsEmpty() {
        btnTyrAgain.setText(getString(R.string.tryAgain));
        goBack = false;
    }


    public void showErrorWhenGetData() {
        btnTyrAgain.setText(getString(R.string.tryAgain));
        goBack = false;
    }

    @Override
    public void onClick(View view) {

    }
}