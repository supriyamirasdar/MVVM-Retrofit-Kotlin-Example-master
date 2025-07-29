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
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.view.detagging.adapter.OrderItemDetailAdapter;
import com.lifestyle.buddydetagging.view.detagging.model.CarryBagItem;
import com.lifestyle.buddydetagging.view.detagging.model.ItemList;
import com.lifestyle.buddydetagging.view.detagging.model.Order;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDetailDto;
import com.lifestyle.buddydetagging.view.detagging.model.SearchOrderDto;
import com.lifestyle.buddydetagging.view.login.LoginActivityNew;
import com.lifestyle.buddydetagging.widget.AppToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DtOrderDetailActivity extends AppCompatActivity implements OrderItemDetailAdapter.SviOrderClickLisner {
    // ActivityDtOrderDetailBinding binding;
    ActivityDtOrderDetailBinding binding;
    String selOrderId = "";
    String selCustName = "";
    String selCustMobNo = "";
    TextView txtCount;//add supriya
    Boolean scanClick = false;
    Boolean showCBDialog = false;
    int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dt_order_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_order_detail);

        //setSupportActionBar(binding.toolbarTop.toolbar);
        binding.toolbarTop.tvToolBarTitle.setText("Order Detail");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.VISIBLE);
        //binding.toolbarTop.tvToolBarSubtitle.setText("Gopalan Signature Mall-Bangalore(01346)");
        binding.toolbarTop.tvToolBarSubtitle.setText(PreferenceUtils.getStoreName() + " (" + PreferenceUtils.getStoreId() + ")");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      /*  if(PreferenceUtils.getOuCode().equalsIgnoreCase("LS"))
        {
          binding.scanViewImgLL.setVisibility(View.GONE);
        }*/

        binding.toolbarTop.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.btnSearch.setOnClickListener(view -> {
            selectedPosition = -1;
            //supriya added if else because imageurlonly for maax
            if (PreferenceUtils.getOuCode().equalsIgnoreCase("Max")) {
                seachOrderFromList(binding.searchOrder.getText().toString().trim());
            } else {
                seachOrderFromListLS(binding.searchOrder.getText().toString().trim());
            }
        });

        binding.searchOrder.setOnEditorActionListener((v, actionId, event) -> {
            selectedPosition = -1;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("Max")) {
                    seachOrderFromList(binding.searchOrder.getText().toString().trim());
                } else {
                    seachOrderFromListLS(binding.searchOrder.getText().toString().trim());
                }
                return true;
            } else
                return false;
        });


        binding.btnScan.setOnClickListener(view -> {
                /*binding.btnScan.setVisibility(View.GONE);
                binding.scanViewImgLL.setVisibility(View.GONE);
                binding.scanViewLL.setVisibility(View.VISIBLE);*/
            // commented as of now..
            selectedPosition = -1;
            if (PreferenceUtils.getOuCode().equalsIgnoreCase("Max")) {
                openScanBarcode();
            } else {
                scanClick = true;
                openScanBarcodeLS();
            }

        });

        //txtCount =(TextView)findViewById(R.id.txtCount);//add supriya

        activity = DtOrderDetailActivity.this;
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

        findViewById(R.id.btnSubmits).setOnClickListener(view -> {
            /*String titleST = "De-tag";
            String messageST = "Carry bag is opted, please provide carry bag.";
            showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 1);*/
            if (isItemPending == true) {
                String titleST = "De-Tagging";
                String messageST = "Item De-Tagging Pending for this order.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else {
                // openOrderDone();
                // call update order status api..
                //updateOrderStatus();

                //ADDED SUPRIYA UPDATE API FOR BOTH LS AND MAX
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                    updateOrderStatusLS();
                } else {
                    updateOrderStatus();
                }
            }
        });

        getOrderItemList();

       /* String messageST = "All Item De-Tagged.";
        int colorCode = Color.parseColor("#ffffff");
        AppToast.showToast(DtOrderDetailActivity.this, messageST, colorCode);*/
    }


    public void openScanBarcode() {
        if (scanItemCount == -1) {
            scanItemCount = 0;
        } else {
            scanItemCount++;
        }
        if (scanItemCount < orderDetailDtoList.size()) {

            selOrderPos = scanItemCount;
            selItemID = "" + orderDetailDtoList.get(scanItemCount).getItemList().getItemID();
            selItemIdentifier = "" + orderDetailDtoList.get(scanItemCount).getItemList().getItemIdentifier();
            isOrderItemClicked = false;

            scanBarcode();
        } else {
            if (checkAnyOrderPending()) {
                binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
                binding.btnSubmits.setText("Detagging Pending");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
                isItemPending = true;
            } else {
                binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
                binding.btnSubmits.setText("Detagging Completed");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
                isItemPending = false;
                //CommonUtility.showSnackBar(DtOrderDetailActivity.this, "All Item De-Tagged");
                // dt 07-10-2021
                // show dialog..
                /*String titleST = "De-Tagging";
                String messageST = "All Item De-Tagged.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);*/

                // dt 07-10-2021
                String messageST = "All Item De-Tagged.";
                int colorCode = Color.parseColor("#ffffff");
                AppToast.showToast(DtOrderDetailActivity.this, messageST, colorCode);

            }
        }
    }

    public void openScanBarcodeLS() {
      /*  if (scanItemCount == -1) {
            scanItemCount = 0;
            scanBarcode();
        } else {
            scanItemCount++;
        }*/
        scanItemCount = 0;
        if (scanClick) {
            scanBarcode();
        }
        //  if (scanItemCount < orderDetailDtoList.size()) {

        selOrderPos = scanItemCount;
        selItemID = "" + orderDetailDtoList.get(scanItemCount).getItemList().getItemID();
        selItemIdentifier = "" + orderDetailDtoList.get(scanItemCount).getItemList().getItemIdentifier();
        isOrderItemClicked = false;

        // } else {
        if (checkAnyOrderPending()) {
            binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
            binding.btnSubmits.setText("Detagging Pending");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
            isItemPending = true;
        } else {
            binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
            binding.btnSubmits.setText("Detagging Completed");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
            isItemPending = false;
            //CommonUtility.showSnackBar(DtOrderDetailActivity.this, "All Item De-Tagged");
            // dt 07-10-2021
            // show dialog..
                /*String titleST = "De-Tagging";
                String messageST = "All Item De-Tagged.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);*/

            // dt 07-10-2021
            String messageST = "All Item De-Tagged.";
            int colorCode = Color.parseColor("#ffffff");
            AppToast.showToast(DtOrderDetailActivity.this, messageST, colorCode);

            // }
        }
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
                    orderDetailDto.setTagStatusVal("Tag");
                    orderDetailDto.setItemList(itemList.get(i));

                    // orderDetailDtoList.add(new OrderDetailDto(1, 0, "Tag"));
                    orderDetailDtoList.add(orderDetailDto);
                    //txtCount.setText(orderDetailDtoList.size());//add supriya

                }

                if (selOrder.getCarryBagItem() != null) {
                    CarryBagItem carryBagItem = selOrder.getCarryBagItem();
                    ItemList itemList1 = new ItemList();
                    if (itemList1.getItemID() != null && itemList1.getItemEAN() != null) {
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
                        orderDetailDto.setTagStatusVal("Tag");
                        orderDetailDto.setItemList(itemList1);

                        // orderDetailDtoList.add(new OrderDetailDto(1, 0, "Tag"));
                        orderDetailDtoList.add(orderDetailDto);
                    }
                }
            }
            if (orderDetailDtoList.size() > 0) {
                mAdapter = new OrderItemDetailAdapter(activity, orderDetailDtoList, activity);
                binding.recyclerView.setAdapter(mAdapter);

            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + orderData + "\"");
        }
    }

    Order selOrder;
    List<OrderDetailDto> orderDetailDtoList;
    DtOrderDetailActivity activity;
    Context context;
    OrderItemDetailAdapter mAdapter;
    String selItemID = "";
    String selItemIdentifier = "";
    int selOrderPos = -1;

    @Override
    public void onOrderClick(int position, OrderDetailDto orders) {
        if (orders.getTagStatus() == 0) {
            selOrderPos = position;
            selItemID = "" + orders.getItemList().getItemID();
            selItemIdentifier = "" + orders.getItemList().getItemIdentifier();
            isOrderItemClicked = true;
            selectedPosition = -1;//position

            // boolean isCheckboxChecked = mAdapter.isCheckboxChecked(); // Make sure you have a valid way to check the checkbox state.
            if (orders.getItemList().getImageURL() != null && !orders.getItemList().checkboxvalue) {

                String titleST = "De-Tagging";
                String messageST = "Please validate the image and select the check box before De-Tagging..";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else {
                scanBarcode();
            }
        } else {
            CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
        }
    }

    private void openOrderDone() {
        Intent intent = new Intent(DtOrderDetailActivity.this, OrderDetagDoneActivity.class);
        intent.putExtra("OrderId", selOrderId);
        startActivity(intent);
        finish();
    }

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
            if (CommonUtility.checkPermissionAPI(DtOrderDetailActivity.this, Manifest.permission.CAMERA)) {
                if (CommonUtility.getSuppourtedFocusedModes()) {
                    Intent intent = new Intent(DtOrderDetailActivity.this, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    startActivityForResult(intent, 9001);
                } else {
                    Intent intent = new Intent(DtOrderDetailActivity.this, CaptureActivity.class);
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
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                    findOrderToPickLS(resultBarcode);
                } else {
                    findOrderToPick(resultBarcode);
                }
            }
        } else if (resultCode == CommonStatusCodes.SUCCESS && requestCode == 9001) {
            if (intent != null) {
                Barcode barcode = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                String resultBarcode = barcode.displayValue;
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                    findOrderToPickLS(resultBarcode);
                } else {
                    findOrderToPick(resultBarcode);
                }
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
                if (PreferenceUtils.getOuCode().equalsIgnoreCase("LS")) {
                    findOrderToPickLS(resultBarcode);
                } else {
                    findOrderToPick(resultBarcode);
                }
            } else {
                noCodeCapture();
                //CommonUtility.showSnackBar(DtOrderDetailActivity.this, "No barcode captured, Please Enter Item barcode");
            }
        }
    }

    int scanItemCount = -1;
    boolean isOrderItemClicked = true;

    private void findOrderToPick(String resultBarcode) {

        int itemPos = selOrderPos;
        String itemIdentifier = "" + selItemIdentifier;

        if (isOrderItemClicked == true) {

            OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(itemPos);
            if (scanOrderDetailDto.getItemList().getItemIdentifier().equals(itemIdentifier)) {
                /*if ( scanOrderDetailDto.getItemList().getItemID().equals(resultBarcode)
                        || scanOrderDetailDto.getItemList().getItemEAN().equals(resultBarcode)) {*/
                if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(resultBarcode))
                        || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(resultBarcode)))) {

                    if (scanOrderDetailDto.getTagStatus() == 0) {
                        if (scanOrderDetailDto.getItemList().getMmrpFlag().equals("N")) {

                            scanOrderDetailDto.setTagStatus(1);
                            scanOrderDetailDto.setTagStatusVal("De-Tagged");
                            mAdapter.notifyDataSetChanged();

                        } else if (scanOrderDetailDto.getItemList().getMmrpFlag().equals("Y")) {

                            String titleST = "De-tag";
                            String messageST = "This product has multiple MRP. Please enter MRP to validate product.";
                            showMultiMRPAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0, scanOrderDetailDto);
                        }
                    } else if (scanOrderDetailDto.getTagStatus() == 1) {
                        CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
                        scanItemCount--;
                    }
                } else {
                    String titleST = "De-Tagging";
                    String messageST = "This Item doesn't belong to the Order.";//"Item not found for this order.";
                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                    scanItemCount--;
                }
            } else {
                String titleST = "De-Tagging";
                String messageST = "This Item doesn't belong to the Order.";//"Item not found for this order.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                scanItemCount--;
            }

        } else if (isOrderItemClicked == false) {

            boolean isScanItemExistInList = false;
            for (int i = 0; i < orderDetailDtoList.size(); i++) {
                OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(i);
                //if (scanOrderDetailDto.getItemList().getItemIdentifier().equals(itemIdentifier)) {
                if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(resultBarcode))
                        || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(resultBarcode)))
                        && scanOrderDetailDto.getTagStatus() == 0) {

             /*   if ((scanOrderDetailDto.getItemList().getItemID() == null || scanOrderDetailDto.getItemList().getItemID().equalsIgnoreCase(resultBarcode))
                        || (scanOrderDetailDto.getItemList().getItemEAN() == null ||scanOrderDetailDto.getItemList().getItemEAN().equalsIgnoreCase(resultBarcode))
                        && scanOrderDetailDto.getTagStatus() == 0) {*/

                    Log.e("DEtail", "i :: " + i);

                    Log.e("DEtail", "getItemIdentifier :: " + scanOrderDetailDto.getItemList().getItemIdentifier());
                    if (scanOrderDetailDto.getTagStatus() == 0) {
                        scanOrderDetailDto.setTagStatus(1);
                        scanOrderDetailDto.setTagStatusVal("De-Tagged");
                        mAdapter.notifyDataSetChanged();
                        isScanItemExistInList = true;
                        // open scanner again..
                        new android.os.Handler().postDelayed(() -> {

                            // openScanBarcode();
                            if (PreferenceUtils.getOuCode().equalsIgnoreCase("Max")) {
                                openScanBarcode();
                            } else {
                                openScanBarcodeLS();
                            }

                        }, 1000);
                        break;
                    } else if (scanOrderDetailDto.getTagStatus() == 1) {
                        CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
                        scanItemCount--;
                        isScanItemExistInList = true;
                    }

                } else {

                    isScanItemExistInList = false;
                }

                /* } else {
                    String titleST = "De-Tagging";
                    String messageST = "Item not found for this order. 22";
                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                    scanItemCount--;
                }*/
            }

            if (isScanItemExistInList == false) {
                String titleST = "De-Tagging";
                String messageST = "This Item doesn't belong to the Order.";//"Item not found for this order.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                scanItemCount--;
            }


        }
        //isItemPending = checkAnyOrderPending();
        scanClick = false;
        if (checkAnyOrderPending()) {
            binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
            binding.btnSubmits.setText("Detagging Pending");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
            isItemPending = true;
        } else {
            binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
            binding.btnSubmits.setText("Detagging Completed");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
            isItemPending = false;
        }

    }


    private void findOrderToPickLS(String resultBarcode) {

        boolean isScanItemExistInList = false;
        for (int i = 0; i < orderDetailDtoList.size(); i++) {
            OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(i);
            //if (scanOrderDetailDto.getItemList().getItemIdentifier().equals(itemIdentifier)) {
            Boolean isidsareSame = false;
            if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(resultBarcode))
                    || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(resultBarcode)))) {
                isidsareSame = true;
                isScanItemExistInList = true;
            }

            if (isidsareSame && scanOrderDetailDto.getTagStatus() == 0) {

                if (scanOrderDetailDto.getItemList().getImageURL() != null) {
                    // Show an alert dialog to check the checkbox
                    if (!scanOrderDetailDto.getItemList().checkboxvalue) {
                        showCBDialog = true;

                    } else {
                        if (scanOrderDetailDto.getTagStatus() == 0) {
                            if (selectedPosition != -1) {
                                if (selectedPosition == i) {
                                    scanOrderDetailDto.setTagStatus(1);
                                    scanOrderDetailDto.setTagStatusVal("De-Tagged");
                                    mAdapter.notifyDataSetChanged();
                                    isScanItemExistInList = true;
                                    showCBDialog = false;
                                    selectedPosition = -1;
                                    break;

                                }
                                   /* else{
                                        CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Scan correct Item Code");
                                        break;
                                    }*/
                            } else {
                                scanOrderDetailDto.setTagStatus(1);
                                scanOrderDetailDto.setTagStatusVal("De-Tagged");
                                mAdapter.notifyDataSetChanged();
                                isScanItemExistInList = true;
                                showCBDialog = false;
                                break;
                            }
                        } else if (scanOrderDetailDto.getTagStatus() == 1) {
                            CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
                            scanItemCount--;
                            isScanItemExistInList = true;
                        }
                    }

                } else {
                    if (scanOrderDetailDto.getTagStatus() == 0) {
                        scanOrderDetailDto.setTagStatus(1);
                        scanOrderDetailDto.setTagStatusVal("De-Tagged");
                        mAdapter.notifyDataSetChanged();
                        isScanItemExistInList = true;

                        break;
                    } else if (scanOrderDetailDto.getTagStatus() == 1) {
                        CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
                        scanItemCount--;
                        isScanItemExistInList = true;
                    }
                }

                //Log.e("DEtail", "i :: " + i);

                //Log.e("DEtail", "getItemIdentifier :: " + scanOrderDetailDto.getItemList().getItemIdentifier());


            }

            if (showCBDialog && i == orderDetailDtoList.size() - 1) {
                String titleST = "De-Tagging";
                String messageST = "Please validate the image and select the check box before De-Tagging..";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                showCBDialog = false;
                break;
            }
            if (isScanItemExistInList && i == orderDetailDtoList.size() - 1) {
                CommonUtility.showSnackBar(DtOrderDetailActivity.this, "Item is already De-Tagged");
            }
        }

        if (isScanItemExistInList == false) {
            String titleST = "De-Tagging";
            String messageST = "This Item doesn't belong to the Order.";//"Item not found for this order.";
            showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            scanItemCount--;
        }


        //isItemPending = checkAnyOrderPending();
        scanClick = false;
        if (checkAnyOrderPending()) {
            binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
            binding.btnSubmits.setText("Detagging Pending");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
            isItemPending = true;
        } else {
            binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
            binding.btnSubmits.setText("Detagging Completed");
            binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
            isItemPending = false;
        }

    }


    public void noCodeCapture() {
        CommonUtility.showSnackBar(DtOrderDetailActivity.this, "No barcode captured, Please Enter Item barcode");
        scanItemCount--;
        /* scanCount++;
        if (scanCount == 1) {
            String titleST = "De-tag";
            String messageST = "This product has multiple MRP. Please enter MRP to validate product.";
            showMultiMRPAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
        } else {
            openOrderDone();
        }*/
    }

    boolean isItemPending = true;

    public boolean checkAnyOrderPending() {
        boolean isPending = false;
        for (OrderDetailDto orderDetailDto : orderDetailDtoList) {
            if (orderDetailDto.getTagStatus() == 0) {
                isPending = true;
                break;
            }
        }
        return isPending;
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


    private void updateOrderStatus() {
        showProgressDialog();
        final JSONObject json = new JSONObject();
        try {
            /*{
                "orderId": "1398-742139-0813",
                    "storeCode": "02185",
                    "userId": "1236",
                    "userName": "Amila",
                    "detaggingStatus": true
            }*/
            if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                json.put("storeCode", "02185");//
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
            }
            json.put("orderId", "" + selOrder.getOrderId());// 9540390487
            json.put("userId", "" + PreferenceUtils.getUserId());
            json.put("userName", "" + PreferenceUtils.getUserName());
            json.put("detaggingStatus", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        String url = getResources().getString(R.string.server_address_naggaro) + "" + ApiConstant.UPDATE_ORDER_STATUS;
        Log.e(TAG, "url :: " + url);

        Log.e(TAG, "Request :: " + json.toString());
        String AUTH_TOKEN = "";
        if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_UAT;
        } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
            AUTH_TOKEN = ApiConstant.AUTH_TOKEN_PROD;
        }
        Log.e(TAG, "AUTH_TOKEN :: " + AUTH_TOKEN);
        RequestBody body1 = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url("" + url)
                .put(body1)
                .addHeader("Authorization", "" + AUTH_TOKEN)
                .addHeader("Accept", "" + ApiConstant.ACCEPT)
                .addHeader("Content-Type", "" + ApiConstant.CONTENT_TYPE)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideProgressDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    DtOrderDetailActivity.this.runOnUiThread(new Runnable() {
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
                                    String titleST = "Order De-tagging";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 1);


                                    new android.os.Handler().postDelayed(() -> {
                                        alertDialog.dismiss();
                                        openOrderDone();
                                    }, 3000);
                                } else {
                                    String titleST = "Order De-tagging";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    hideProgressDialog();
                    Log.e("TAGGG", " response.message() ::" + response.message());
                }
            }
        });

    }


    private void updateOrderStatusLS() {
        showProgressDialog();
        final JSONObject json = new JSONObject();
        try {
            /*{
                "orderId": "1398-742139-0813",
                    "storeCode": "02185",
                    "userId": "1236",
                    "userName": "Amila",
                    "detaggingStatus": true
            }*/
           /* if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.UAT)) {
                json.put("storeCode", PreferenceUtils.getStoreId());//
            } else if (getResources().getString(R.string.dev_mode).equalsIgnoreCase(ApiConstant.PROD)) {
                json.put("storeCode", "" + PreferenceUtils.getStoreId());//02185
            }*/

            json.put("orderId", "" + selOrder.getOrderId());// 9540390487
            json.put("userId", "" + PreferenceUtils.getUserId());
            json.put("userName", "" + PreferenceUtils.getUserName());
            json.put("detaggingStatus", true);
            json.put("storeCode", PreferenceUtils.getStoreId());
            json.put("accessToken", LoginActivityNew.accessToken);//
            json.put("orderDate", CommonUtility.getCurrentDate());//
            //CSR information
            json.put("csrId", PreferenceUtils.getEmployeeId());//
            //json.put("csrName", PreferenceUtils.getEmployeeName());//
            json.put("csrName", "null");//
            //json.put("orders", LoginActivityNew.accessToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        try {
            client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES)
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

        //ADDED SUPRIYA 16-03-2023 FOR UPDATE apis

        //added by supriya
        // url = getResources().getString(R.string.server_address_naggaroNew) + "" + oucode + "" +ApiConstant.ORDER_Storeid + ""+storeid +""+ApiConstant.ORDER_UPDATENew;
        String url = getResources().getString(R.string.server_address_LS) + "" + ApiConstant.UPDATE_ORDER_STATUS_LS;
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
                e.printStackTrace();
                // hideProgressDialog();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    DtOrderDetailActivity.this.runOnUiThread(new Runnable() {
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
                                    String titleST = "Order De-tagging";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 1);


                                    new android.os.Handler().postDelayed(() -> {
                                        alertDialog.dismiss();
                                        openOrderDone();
                                    }, 3000);
                                } else {
                                    String titleST = "Order De-tagging";
                                    String messageST = "" + jsonObjectTop.optString("statusMessage");
                                    showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    hideProgressDialog();
                    Log.e("TAGGG", " response.message() ::" + response.message());
                }

                PreferenceUtils.setEmployeeId("");
                PreferenceUtils.setEmployeeName("");
            }
        });

    }


    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //serach for max
    private void seachOrderFromList(String seachOrder) {
        Log.d("PREET", "Searching");
        hideKeyboard(DtOrderDetailActivity.this);
        if (seachOrder.length() == 0) {
            //showSnackBar(DtOrderDetailActivity.this, "Please enter Item code/ Ean code.");
            String titleST = "De-tag";
            String messageST = "Please enter Item code/ Ean code.";
            showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
        } else {
            boolean isItemFound = false;
            for (int i = 0; i < orderDetailDtoList.size(); i++) {
                OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(i);
                //if (scanOrderDetailDto.getItemList().getItemIdentifier().equals(itemIdentifier)) {
                /*if ((scanOrderDetailDto.getItemList().getItemID().equals(seachOrder)
                        || scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder))) {*/
                if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(seachOrder))
                        || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder)))) {

                    isItemFound = true;
                    break;
                }
            }

            if (isItemFound) {
                for (int i = 0; i < orderDetailDtoList.size(); i++) {
                    OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(i);
                    /*if ((scanOrderDetailDto.getItemList().getItemID().equals(seachOrder)
                            || scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder))
                            && scanOrderDetailDto.getTagStatus() == 0) {*/
                    if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(seachOrder))
                            || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder)))) {
                        //&& scanOrderDetailDto.getTagStatus() == 0) {

                        if (scanOrderDetailDto.getTagStatus() == 0) {
                            // added Dt: 25-05-2022
                            if (scanOrderDetailDto.getItemList().getMmrpFlag().equals("N")) {
                                scanOrderDetailDto.setTagStatus(1);
                                scanOrderDetailDto.setTagStatusVal("De-Tagged");
                                mAdapter.notifyDataSetChanged();
                            } else if (scanOrderDetailDto.getItemList().getMmrpFlag().equals("Y")) {

                                String titleST = "De-tag";
                                String messageST = "This product has multiple MRP. Please enter MRP to validate product.";
                                showMultiMRPAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0, scanOrderDetailDto);
                            }
                            //

                            Log.e("DEtail", "i :: " + i);
                            Log.e("DEtail", "getItemIdentifier :: " + scanOrderDetailDto.getItemList().getItemIdentifier());
                            //scanOrderDetailDto.setTagStatus(1);
                            //scanOrderDetailDto.setTagStatusVal("De-Tagged");
                            //mAdapter.notifyDataSetChanged();
                            binding.searchOrder.setText("");
                            break;
                        } else {
                            binding.searchOrder.setText("");
                            String titleST = "De-Tagging";
                            String messageST = "Item is already De-Tagged.";
                            showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                        }


                    } /*else {

                        String titleST = "De-Tagging";
                        String messageST = "Item is already De-Tagged 22";
                        showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
                        break;
                    }*/
                }
            } else {
                binding.searchOrder.setText("");
                String titleST = "De-Tagging";
                String messageST = "Item not found for this order.";
                // showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);

                if (seachOrder.length() <= 12) {
                    binding.searchOrder.setText("");
                    String titleST1 = "De-Tagging";
                    String messageST1 = "Item not found for this order.";
                    showAlertDialog(DtOrderDetailActivity.this, titleST1, messageST1, 0, 0);
                } else {
                    binding.searchOrder.setText("");
                    String titleST2 = "De-Tagging";
                    String messageST2 = "Invalid Item code/ Ean code.Please Scan the Correct item code";
                    showAlertDialog(DtOrderDetailActivity.this, titleST2, messageST2, 0, 0);
                }// ADD SUPRIYA 03-11-2022

            }

            if (checkAnyOrderPending()) {
                binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
                binding.btnSubmits.setText("Detagging Pending");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
                isItemPending = true;
            } else {
                binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
                binding.btnSubmits.setText("Detagging Completed");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
                isItemPending = false;
            }

           /* if (seachOrder.equals("0")) {
                // showSnackBar(DtOrderDetailActivity.this, "Item not found for this order.");
                String titleST = " De-tag";
                String messageST = "Item not found for this order.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else if (seachOrder.equals("1")) {
                //showSnackBar(DtOrderDetailActivity.this, "Unable to scan item.");
                String titleST = "De-tag";
                String messageST = "Unable to scan item.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else if (seachOrder.equals("9")) {
                //showSnackBar(DtOrderDetailActivity.this, "Invalid Item code/ Ean code.");
                String titleST = " De-tag";
                String messageST = "Invalid Item code/ Ean code.Please Scan the Correct item code";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else {
                //showSnackBar(DtOrderDetailActivity.this, "Item Scanned.");
            }*/
        }
    }


    ///SUPRIYA ADDED FOR SERACH FOR LS WITH IMAGE 10-10-2023
    private void seachOrderFromListLS(String seachOrder) {
        Log.d("PREET", "Searching");
        hideKeyboard(DtOrderDetailActivity.this);

        if (seachOrder.length() == 0) {
            //showSnackBar(DtOrderDetailActivity.this, "Please enter Item code/ Ean code.");
            String titleST = " De-tag";
            String messageST = "Please enter Item code/ Ean code.";
            showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
        } else {
            boolean isItemFound = false;
            for (int i = 0; i < orderDetailDtoList.size(); i++) {
                OrderDetailDto scanOrderDetailDto = orderDetailDtoList.get(i);
                //if (scanOrderDetailDto.getItemList().getItemIdentifier().equals(itemIdentifier)) {
                /*if ((scanOrderDetailDto.getItemList().getItemID().equals(seachOrder)
                        || scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder))) {*/
                if (((scanOrderDetailDto.getItemList().getItemID() != null && scanOrderDetailDto.getItemList().getItemID().equals(seachOrder))
                        || (scanOrderDetailDto.getItemList().getItemEAN() != null && scanOrderDetailDto.getItemList().getItemEAN().equals(seachOrder)))) {

                    isItemFound = true;
                    break;
                }
            }

            if (isItemFound) {

                findOrderToPickLS(seachOrder);

            } else {
                binding.searchOrder.setText("");
                String titleST = "De-Tagging";
                String messageST = "Item not found for this order.";
                // showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);

                if (seachOrder.length() <= 12) {
                    binding.searchOrder.setText("");
                    String titleST1 = "De-Tagging";
                    String messageST1 = "Item not found for this order.";
                    showAlertDialog(DtOrderDetailActivity.this, titleST1, messageST1, 0, 0);
                } else {
                    binding.searchOrder.setText("");
                    String titleST2 = "De-Tagging";
                    String messageST2 = "Invalid Item code/ Ean code.Please Scan the Correct item code";
                    showAlertDialog(DtOrderDetailActivity.this, titleST2, messageST2, 0, 0);
                }// ADD SUPRIYA 03-11-2022

            }

            if (checkAnyOrderPending()) {
                binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
                binding.btnSubmits.setText("Detagging Pending");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
                isItemPending = true;
            } else {
                binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
                binding.btnSubmits.setText("Detagging Completed");
                binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
                isItemPending = false;
            }

           /* if (seachOrder.equals("0")) {
                // showSnackBar(DtOrderDetailActivity.this, "Item not found for this order.");
                String titleST = " De-tag";
                String messageST = "Item not found for this order.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else if (seachOrder.equals("1")) {
                //showSnackBar(DtOrderDetailActivity.this, "Unable to scan item.");
                String titleST = " De-tag";
                String messageST = "Unable to scan item.";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else if (seachOrder.equals("9")) {
                //showSnackBar(DtOrderDetailActivity.this, "Invalid Item code/ Ean code.");
                String titleST = " De-tag";
                String messageST = "Invalid Item code/ Ean code.Please Scan the Correct item code";
                showAlertDialog(DtOrderDetailActivity.this, titleST, messageST, 0, 0);
            } else {
                //showSnackBar(DtOrderDetailActivity.this, "Item Scanned.");
            }*/
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static void showMessageOKCancel(String title, String message, Activity mActivity, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity).setTitle("" + title)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                //.setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void showAlertDialog(final Context context, String title, String message, int isDisplayNo, int callFrom) {
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

    AlertDialog alertDialog;

    public void showAlertDialog(final Context context, String title, String message, int isDisplayNo) {
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
        /*if (isDisplayNo == 1) {
            noBt.setVisibility(View.VISIBLE);
        } else if (isDisplayNo == 0) {
            noBt.setVisibility(View.GONE);
        }*/

        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        yesBt.setText("Okay");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (isDisplayNo == 1) {
                    openOrderDone();
                }
            }
        });

        alertDialog.show();
    }

    public void showMultiMRPAlertDialog(final Context context, String title, String message, int isDisplayNo,
                                        int callFrom, OrderDetailDto scanOrderDetailDto) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.layout_dt_mmrp_alert_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setInverseBackgroundForced(true).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(deleteDialogView);
        TextView titleTV = deleteDialogView.findViewById(R.id.titleTV);
        TextView alertMessageTV = deleteDialogView.findViewById(R.id.alertMessageTV);
        TextView yesBt = deleteDialogView.findViewById(R.id.yesBt);
        TextView noBt = deleteDialogView.findViewById(R.id.noBt);
        EditText mmrpET = deleteDialogView.findViewById(R.id.mmrpET);
        TextView errorMsgTv = deleteDialogView.findViewById(R.id.errorMsgTv);
        errorMsgTv.setText("");
        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        //yesBt.setText("Okay");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mmrpSt = mmrpET.getText().toString().trim();

                if (TextUtils.isEmpty(mmrpSt)) {
                    errorMsgTv.setText("Enter MRP.");
                } else {

                    //int sellPrice = scanOrderDetailDto.getItemList().getSellPrice();
                    //int enterMrpPrice = Integer.parseInt(mmrpSt);
                    // added dt 25-05-2022
                    double sellPrice = scanOrderDetailDto.getItemList().getSellPrice();
                    double enterMrpPrice = Double.parseDouble(mmrpSt);

                    if (sellPrice == enterMrpPrice) {

                        scanOrderDetailDto.setTagStatus(1);
                        scanOrderDetailDto.setTagStatusVal("De-Tagged");
                        mAdapter.notifyDataSetChanged();

                        if (checkAnyOrderPending()) {
                            binding.btnSubmits.setBackgroundResource(R.drawable.button_disable_bg);
                            binding.btnSubmits.setText("Detagging Pending");
                            binding.btnSubmits.setTextColor(getResources().getColor(R.color.black));
                            isItemPending = true;
                        } else {
                            binding.btnSubmits.setBackgroundResource(R.drawable.toolbar_bg);
                            binding.btnSubmits.setText("Detagging Completed");
                            binding.btnSubmits.setTextColor(getResources().getColor(R.color.white));
                            isItemPending = false;
                        }
                        errorMsgTv.setText("");
                        alertDialog.dismiss();
                    } else {
                        errorMsgTv.setText("Enter correct MRP.");
                    }
                }
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


    public static void showSnackBar(Activity activity, String msg) {
        if (activity != null) {
            Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            params.width = getWidth(activity);
            view.setLayoutParams(params);
            snack.show();
        }
    }

    public static int getWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static void hideKeyboard(DtOrderDetailActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

            mProgressBar = new ProgressBar(DtOrderDetailActivity.this, null, android.R.attr.progressBarStyleLarge);

            mProgressBar.getIndeterminateDrawable()
                    .setColorFilter(Color.parseColor("#ffcf00"), PorterDuff.Mode.SRC_IN);//ProgressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.default_spinner));
            //mProgressBar.setBackground(context.getDrawable(R.drawable.progress_background));
            mProgressBar.setPadding(CommonUtility.convertDpToPx(10, DtOrderDetailActivity.this), CommonUtility.convertDpToPx(10, DtOrderDetailActivity.this),
                    CommonUtility.convertDpToPx(10, DtOrderDetailActivity.this), CommonUtility.convertDpToPx(10, DtOrderDetailActivity.this));
            RelativeLayout.LayoutParams params = new
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(DtOrderDetailActivity.this);

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}