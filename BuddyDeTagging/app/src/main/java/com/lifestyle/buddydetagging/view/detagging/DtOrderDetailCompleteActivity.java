package com.lifestyle.buddydetagging.view.detagging;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.constant.ApiConstant;
import com.lifestyle.buddydetagging.databinding.ActivityDtOrderDetailBinding;
import com.lifestyle.buddydetagging.databinding.ActivityDtOrderDetailCompleteBinding;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.view.detagging.adapter.OrderItemDetailCompleteAdapter;
import com.lifestyle.buddydetagging.view.detagging.model.CarryBagItem;
import com.lifestyle.buddydetagging.view.detagging.model.ItemList;
import com.lifestyle.buddydetagging.view.detagging.model.Order;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDetailDto;
import com.lifestyle.buddydetagging.view.detagging.model.SearchOrderDto;
import com.lifestyle.buddydetagging.widget.AppToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DtOrderDetailCompleteActivity extends AppCompatActivity {
    // ActivityDtOrderDetailBinding binding;
    ActivityDtOrderDetailCompleteBinding binding;
    String selOrderId = "";
    String selCustName = "";
    String selCustMobNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dt_order_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_order_detail_complete);

        //setSupportActionBar(binding.toolbarTop.toolbar);
        binding.toolbarTop.tvToolBarTitle.setText("Order Detail");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.VISIBLE);
        //binding.toolbarTop.tvToolBarSubtitle.setText("Gopalan Signature Mall-Bangalore(01346)");
        binding.toolbarTop.tvToolBarSubtitle.setText(PreferenceUtils.getStoreName() + " (" + PreferenceUtils.getStoreId() + ")");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.toolbarTop.btnBack.setOnClickListener(view -> {
            finish();
        });


        activity = DtOrderDetailCompleteActivity.this;
        Intent intent = getIntent();
        selOrderId = intent.getStringExtra("OrderId");
        selCustName = intent.getStringExtra("CustName");
        selCustMobNo = intent.getStringExtra("CustMobNo");

        binding.toolbarTop.tvToolBarTitle.setText("" + selOrderId);
        binding.toolbarTop.tvToolBarSubtitle.setText("Mob: " + selCustMobNo + "");


        orderDetailDtoList = new ArrayList<>();

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());


        getOrderItemList();
    }


    String TAG = "TAG";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private void getOrderItemList() {

        String orderData = PreferenceUtils.getOrderData();
        Log.e("TAGG", "" + orderData);
        try {
            JSONObject obj = new JSONObject(orderData);
            Log.d("My App", obj.toString());
            Gson gson = new Gson();
            SearchOrderDto searchOrderDto = gson.fromJson(obj.toString(), SearchOrderDto.class);
            Log.d("My App", "Status MSG :: " + searchOrderDto.getStatusMessage());
            // Log.d("My App", "Order Id :: "+searchOrderDto.getOrders().get(0).getOrderId());

            List<Order> orders = searchOrderDto.getOrders();
            for (Order ord : orders) {
                if (selOrderId.equals(ord.getOrderId())) {
                    selOrder = ord;
                }
            }
            Log.d("My App", "selOrder :: " + selOrder.getOrderId());
            Log.d("My App", "selOrder item list :: " + selOrder.getItemList().size());

            orderDetailDtoList = new ArrayList<>();
            if (selOrder != null) {
                List<ItemList> itemList = selOrder.getItemList();
                for (int i = 0; i < itemList.size(); i++) {
                    OrderDetailDto orderDetailDto = new OrderDetailDto();
                    orderDetailDto.setOrderItemStatus(1);
                    orderDetailDto.setTagStatus(0);
                    orderDetailDto.setTagStatusVal("De-Tagged");
                    orderDetailDto.setItemList(itemList.get(i));

                    // orderDetailDtoList.add(new OrderDetailDto(1, 0, "Tag"));
                    orderDetailDtoList.add(orderDetailDto);
                }

                if (selOrder.getCarryBagItem() != null) {
                    CarryBagItem carryBagItem = selOrder.getCarryBagItem();
                    ItemList itemList1 = new ItemList();
                    if(itemList1.getItemID() !=null && itemList1.getItemEAN() !=null) {
                        itemList1.setItemIdentifier(carryBagItem.getItemIdentifier());
                        itemList1.setItemID(carryBagItem.getItemID());
                        itemList1.setItemEAN(carryBagItem.getItemEAN());

                        itemList1.setDiscountAmount(carryBagItem.getDiscountAmount());
                        itemList1.setExtSellPrice(carryBagItem.getExtSellPrice());
                        itemList1.setPromoList(carryBagItem.getPromoList());
                        itemList1.setQuantity(carryBagItem.getQuantity());

                        itemList1.setSellPrice(carryBagItem.getSellPrice());
                        itemList1.setItemDesc(carryBagItem.getItemDesc());
                        itemList1.setItemDept(carryBagItem.getItemDept());
                        itemList1.setItemBrand(carryBagItem.getItemBrand());
                        itemList1.setItemColour(carryBagItem.getItemColour());
                        itemList1.setItemSize(carryBagItem.getItemSize());
                        itemList1.setMmrpFlag(carryBagItem.getMmrpFlag());

                        OrderDetailDto orderDetailDto = new OrderDetailDto();
                        orderDetailDto.setOrderItemStatus(1);
                        orderDetailDto.setTagStatus(0);
                        orderDetailDto.setTagStatusVal("De-Tagged");
                        orderDetailDto.setItemList(itemList1);

                        // orderDetailDtoList.add(new OrderDetailDto(1, 0, "Tag"));
                        orderDetailDtoList.add(orderDetailDto);
                    }
                }
            }
            if (orderDetailDtoList.size() > 0) {
                //mAdapter = new OrderItemDetailCompleteAdapter(activity, orderDetailDtoList, activity);
                mAdapter = new OrderItemDetailCompleteAdapter(activity, orderDetailDtoList);
                binding.recyclerView.setAdapter(mAdapter);
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + orderData + "\"");
        }
    }

    private Order selOrder;
    private List<OrderDetailDto> orderDetailDtoList;
    private DtOrderDetailCompleteActivity activity;
    private OrderItemDetailCompleteAdapter mAdapter;
    private String selItemID = "";
    private String selItemIdentifier = "";
    private int selOrderPos = -1;



}