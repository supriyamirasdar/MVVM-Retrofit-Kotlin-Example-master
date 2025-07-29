package com.landmarkgroup.smartkiosk.ui.homescreen;

import static com.landmarkgroup.smartkiosk.util.Utils.displaySnackBar;
import static com.lifestyle.retail.utils.CommonUtility.showSnackBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.databinding.ActivityOrderListBinding;
import com.landmarkgroup.smartkiosk.model.PDPOrderConfirmResponse;
import com.landmarkgroup.smartkiosk.model.PDPPageLoadRequest;
import com.landmarkgroup.smartkiosk.model.PDPPageResponse;
import com.landmarkgroup.smartkiosk.model.StatusResponse;
import com.landmarkgroup.smartkiosk.storage.ContextManager;
import com.landmarkgroup.smartkiosk.storage.DBManager;
import com.landmarkgroup.smartkiosk.storage.TransactionData;
import com.landmarkgroup.smartkiosk.ui.adapter.ItemListAdapter;
import com.landmarkgroup.smartkiosk.ui.pdppage.MissingSizeActivity;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColorDto;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColourSizeListModel;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemDetailsAtOptionLevelResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.GetItemImgUrlResponse;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeCountListmodel;
import com.landmarkgroup.smartkiosk.util.Logger;
import com.landmarkgroup.smartkiosk.util.Utils;
import com.landmarkgroup.smartkiosk.widgets.ProgressBarHandler;
import com.lifestyle.retail.base.BaseCompatActivity;
import com.lifestyle.retail.constants.Constant;
import com.lifestyle.retail.utils.CommonUtility;
import com.lifestyle.retail.utils.GeneralConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class OrderListActivity extends BaseCompatActivity {
    private OrderListActivity activity;

    ActivityOrderListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_order_list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list);
        ContextManager.setAppContext(OrderListActivity.this);
        activity = this;
        Logger.writeToFile("@@@@@@@@@ 07-SEP_2022 @@@@@@@@@ ");
        initView();
    }

    private RecyclerView.LayoutManager layoutManager;

    private void initView() {

        binding.btnBackIV.setOnClickListener(view -> finish());
        binding.btnSyncIV.setOnClickListener(view -> uploadOrderOnServer());



        getOrderList();
    }

    public void getOrderList(){
        //recyclerView = binding.recyclerVieww;
        binding.recyclerVieww.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerVieww.setLayoutManager(layoutManager);
        binding.recyclerVieww.setItemAnimator(new DefaultItemAnimator());

        ArrayList<TransactionData> orderList = (ArrayList<TransactionData>) (ArrayList<?>) DBManager.getInstance().getAllTableData(DBManager.TBL_TRANSACTION_MAIN, "", "", 1);
        // List<ScanBarcodeDto> scanBarcodeDtoList= new ArrayList<>();

        ItemListAdapter adapter = new ItemListAdapter(orderList, OrderListActivity.this,
                (arg1, position) -> {
                    TransactionData transData = orderList.get(position);
                    openOrderDetail(transData);
                });
        binding.recyclerVieww.setAdapter(adapter);
    }

    int transId=-1;
    private void openOrderDetail(TransactionData transData) {

        transId= transData.get_id();
        //orderLineType = transData.getOrdLineType();

        PDPPageLoadRequest request = new PDPPageLoadRequest();
        request.setStoreId(transData.getStoreId());
        request.setCustMobileNum(transData.getCustMobileNum());
        request.setOrdConfirmUrl(transData.getOrdConfirmUrl());
        request.setKioskId(transData.getKioskId());
        request.setUsrId(transData.getEmpId());
        //Log.e("ORDER_CONFIRM", "callType :: " + callType);
        // request.setReqSource(callType);
        request.setReqSource(""+ transData.getOrderType());
        // isAutoAcceptOrder = 2;
        saveAcceptedUrlRequest(request);

    }

    private void uploadOrderOnServer() {

    }

    String tagType = "OrderListActivity";

    //Order Accept URL Request
    private void saveAcceptedUrlRequest(PDPPageLoadRequest request) {
        //saveAcceptedOrderUrl(storeId, custMobile, barCodeNo, tabSelected, userId, url);
        showProgressDialog();
        Gson gson = new Gson();
        String json = gson.toJson(request);
        Log.e(tagType, " json :: " + json);

        Logger.writeToFile(":::::::::: API saveAcceptedUrlRequest Called :::::::::: ");
        Logger.writeToFile("API URL :: StylusRest/saveKioskOrdDlts ");
        Logger.writeToFile("API Request :: " + json);
        network(getApiInterface().saveOrderAcceptedUrl(request), GeneralConstant.SAVE_ORDER_URL);
    }


    @Override
    public void onSuccess(StatusResponse statusResponse, int requestCode) {
        hideKeyboard();
        hideProgressDialog();
        if (requestCode == GeneralConstant.SAVE_ORDER_URL) {
            if (statusResponse instanceof PDPOrderConfirmResponse) {
                PDPOrderConfirmResponse response = (PDPOrderConfirmResponse) statusResponse;

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
                DBManager.getInstance().deleteRows(DBManager.TBL_TRANSACTION_MAIN, "" + transId);
                // remove from map as well..
                //autoAcceptOrderMapMain.remove(autoAcceptOrderMapMain.get(confirmOrderIdMain));
                Log.e("DB", transId + " deleted from table TBL_TRANSACTION_MAIN;");
                getOrderList();

            }
        }
    }

    public void statusMessage(String statusMessage) {
        displaySnackBar(activity, statusMessage);
    }

    @Override
    public void onClick(View v) {

    }
}