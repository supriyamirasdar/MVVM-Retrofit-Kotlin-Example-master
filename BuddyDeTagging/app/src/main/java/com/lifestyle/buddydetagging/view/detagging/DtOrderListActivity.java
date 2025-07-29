package com.lifestyle.buddydetagging.view.detagging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.databinding.ActivityDtOrderListBinding;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.view.detagging.adapter.OrderItemDetailAdapter;
import com.lifestyle.buddydetagging.view.detagging.adapter.SviOrderListAdapter;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DtOrderListActivity extends AppCompatActivity implements SviOrderListAdapter.SviOrderClickLisner {

    ActivityDtOrderListBinding binding;

    DtOrderListActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dt_order_list);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_order_list);
        //setSupportActionBar(binding.toolbarTop.toolbar);
        binding.toolbarTop.tvToolBarTitle.setText("Order List");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.VISIBLE);
        //binding.toolbarTop.tvToolBarSubtitle.setText("Gopalan Signature Mall-Bangalore(01346)");
        binding.toolbarTop.tvToolBarSubtitle.setText(PreferenceUtils.getStoreName() + " (" + PreferenceUtils.getStoreId() + ")");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarTop.btnBack.setOnClickListener(view -> {
            finish();
        });

        activity = DtOrderListActivity.this;

        List<OrderDto> orderDtoList = new ArrayList<>();
       /* orderDtoList.add(new OrderDto(1));
        orderDtoList.add(new OrderDto(1));
        orderDtoList.add(new OrderDto(1));
        orderDtoList.add(new OrderDto(1));*/

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());



        String orderData = PreferenceUtils.getOrderData();

        Log.e("TAGG", "" + orderData);
        try {

            JSONObject obj = new JSONObject(orderData);
            Log.d("My App", obj.toString());
            JSONArray jsonArray = obj.getJSONArray("orders");
            if (jsonArray.length() > 0) {
                OrderDto orderDto;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                   // boolean isCompleted = jsonObject.getBoolean("isCompleted");
                   // boolean isHasPaid = jsonObject.getBoolean("hasPaid");
                    boolean isHasDetagged = jsonObject.getBoolean("hasDetagged");
                    String detaggedByST="";
                    String detagBy="";
                    String dateST="";
                    int orderDetagged=0;
                   // if (isCompleted == true && isHasPaid == true && isHasDetagged == true) {
                    if (isHasDetagged == true) {
                        String orderId = jsonObject.optString("orderId");
                        String detaggedBy = jsonObject.optString("detaggedUsername");
                        String detaggedUsername = jsonObject.optString("detaggedUsername");
                        String detaggedAt = jsonObject.optString("detaggedAt");

                       /* String detaggedUsername = PreferenceUtils.getUserName();
                        String detaggedAtt = CommonUtility.getCurrentDate();
                        detagBy =   " (" + detaggedUsername + ")";*/

                          detagBy = detaggedUsername; //+ " (" + detaggedBy + ")";
                          dateST = detaggedAt;

                      /*  String[] dateSTArry = dateST.split("T");
                        if (dateSTArry.length > 1) {
                            dateST = dateSTArry[0] + " " + dateSTArry[1];
                        } else {
                            dateST = dateSTArry[0];
                        }
                        dateST = dateST.substring(0, dateST.length() - 5);
                        String curFormat = "yyyy-MM-dd HH:mm:ss";
                        String reqFormat = "dd-MMM-yyyy HH:mm:ss";
                        dateST = CommonUtility.convertDateToRequiredFormat(dateST, curFormat, reqFormat);*/
                        String titleST = "Search Order";
                        String messageST = "This order id (" + orderId + ") is already De-Tagged by " + detagBy + " on " + dateST + ".";


                        detaggedByST=""+ messageST;
                        orderDetagged=1;
                    } else {
                        detaggedByST="";
                        orderDetagged=0;
                    }
                    if(PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                        if (isHasDetagged == false) { //ADDDED SUPRIYA 04-10-23 only pending orderlist for  ls

                            String orderId = jsonObject.optString("orderId");
                            String customerPhoneNo = jsonObject.optString("customerPhoneNo");
                            Log.e("TAG", "orderId ::  " + orderId);
                            Log.e("TAG", "customerPhoneNo ::  " + customerPhoneNo);
                            String createdAt = jsonObject.optString("createdAt");//change supriya createdAt to orderDate
                            double extSellPrice = 0.0;
                            double sellPrice = 0.0;

                            JSONArray jsonArray2 = jsonObject.getJSONArray("itemList");
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
                                double extSellPriced = jsonObject2.optDouble("extSellPrice");
                                double sellPriced = jsonObject2.optDouble("sellPrice");

                                Log.e("TAG", "extSellPriced ::  " + extSellPriced);
                                Log.e("TAG", "sellPriced ::  " + sellPriced);

                                extSellPrice = extSellPrice + extSellPriced;
                                sellPrice = sellPrice + sellPriced;
                            }
                            if (jsonObject.has("carryBagItem")) {
                                JSONObject jsonObject2 = jsonObject.optJSONObject("carryBagItem");
                                if (jsonObject2 != null) {
                                    double extSellPriced = jsonObject2.optDouble("extSellPrice");
                                    double sellPriced = jsonObject2.optDouble("sellPrice");

                                    //added supriya because extsellprice value getting null BECAUSE CARRYBAG ITEM GETTING NULL
                                    if (Double.isNaN(extSellPriced)) {
                                        extSellPriced = 0;
                                    }

                                    Log.e("TAG", "extSellPriced ::  " + extSellPriced);
                                    Log.e("TAG", "sellPriced ::  " + sellPriced);


                                    extSellPrice = extSellPrice + extSellPriced;
                                    sellPrice = sellPrice + sellPriced;


                                }


                            }
                            Log.e("TAG", "extSellPrice ::  " + extSellPrice);
                            Log.e("TAG", "sellPrice ::  " + sellPrice);
                            orderDto = new OrderDto(1, orderId, customerPhoneNo, createdAt, "" + extSellPrice, "" + sellPrice, detaggedByST, orderDetagged, detagBy, dateST);

                            orderDtoList.add(orderDto);
                        } else {
                        }
                    }else{
                        String orderId = jsonObject.optString("orderId");
                        String customerPhoneNo = jsonObject.optString("customerPhoneNo");
                        Log.e("TAG", "orderId ::  " + orderId);
                        Log.e("TAG", "customerPhoneNo ::  " + customerPhoneNo);
                        String createdAt = jsonObject.optString("createdAt");//change supriya createdAt to orderDate
                        double extSellPrice = 0.0;
                        double sellPrice = 0.0;

                        JSONArray jsonArray2 = jsonObject.getJSONArray("itemList");
                        for (int j = 0; j < jsonArray2.length(); j++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
                            double extSellPriced = jsonObject2.optDouble("extSellPrice");
                            double sellPriced = jsonObject2.optDouble("sellPrice");

                            Log.e("TAG", "extSellPriced ::  " + extSellPriced);
                            Log.e("TAG", "sellPriced ::  " + sellPriced);

                            extSellPrice = extSellPrice + extSellPriced;
                            sellPrice = sellPrice + sellPriced;
                        }
                        if (jsonObject.has("carryBagItem")) {
                            JSONObject jsonObject2 = jsonObject.optJSONObject("carryBagItem");
                            if(jsonObject2!=null){
                                double extSellPriced = jsonObject2.optDouble("extSellPrice");
                                double sellPriced = jsonObject2.optDouble("sellPrice");

                                //added supriya because extsellprice value getting null BECAUSE CARRYBAG ITEM GETTING NULL
                                if (Double.isNaN(extSellPriced)) {
                                    extSellPriced = 0;
                                }

                                Log.e("TAG", "extSellPriced ::  " + extSellPriced);
                                Log.e("TAG", "sellPriced ::  " + sellPriced);


                                extSellPrice = extSellPrice + extSellPriced;
                                sellPrice = sellPrice + sellPriced;



                            }


                        }
                        Log.e("TAG", "extSellPrice ::  " + extSellPrice);
                        Log.e("TAG", "sellPrice ::  " + sellPrice);
                        orderDto = new OrderDto(1, orderId, customerPhoneNo, createdAt, "" + extSellPrice, "" + sellPrice, detaggedByST, orderDetagged, detagBy, dateST);

                        orderDtoList.add(orderDto);
                    }
                }
            }

            if (orderDtoList.size() > 0  ) {
                SviOrderListAdapter mAdapter = new SviOrderListAdapter(activity, orderDtoList, activity);
                binding.recyclerView.setAdapter(mAdapter);
            }

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + orderData + "\"");
        }
    }

    @Override
    public void onOrderClick(int position, OrderDto orders) {
        if (orders.getOrderDetagged() == 0) {
            Intent intent = new Intent(activity, DtOrderDetailActivity.class);
            intent.putExtra("Type", "1");
            intent.putExtra("OrderId", "" + orders.getOrderId());
            intent.putExtra("CustName", "");
            intent.putExtra("CustMobNo", "" + orders.getCustomerPhoneNo());
            startActivity(intent);
        }else if (orders.getOrderDetagged() == 1) {
           /* String titleST = "Info";
            String messageST = ""+ orders.getDetaggedByST();
            showAlertDialog(DtOrderListActivity.this, titleST, messageST, 0);
            Log.e("ORDER ITEM", ""+ messageST);*/

            // added dt: 07-10-2021
            //  open dt order detail completed..
            Intent intent = new Intent(activity, DtOrderDetailCompleteActivity.class);
            intent.putExtra("Type", "1");
            intent.putExtra("OrderId", "" + orders.getOrderId());
            intent.putExtra("CustName", "");
            intent.putExtra("CustMobNo", "" + orders.getCustomerPhoneNo());
            startActivity(intent);
        }
    }

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
}