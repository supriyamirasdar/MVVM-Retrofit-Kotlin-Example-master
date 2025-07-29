package com.lifestyle.buddydetagging.view.detagging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.databinding.ActivityOrderDetagDoneBinding;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;

public class OrderDetagDoneActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_order_detag_done);

        ActivityOrderDetagDoneBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detag_done);

        setSupportActionBar(binding.toolbarTop.toolbar);
        binding.toolbarTop.tvToolBarTitle.setText("Order Detag");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.VISIBLE);
        //binding.toolbarTop.tvToolBarSubtitle.setText("Gopalan Signature Mall-Bangalore(01346)");
        binding.toolbarTop.tvToolBarSubtitle.setText(PreferenceUtils.getStoreName() + " (" + PreferenceUtils.getStoreId() + ")");
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intentt = getIntent();
        String selOrderId = intentt.getStringExtra("OrderId");
        String message = "Order ID #" + selOrderId + " has been processed.";
        binding.orderidvalTV.setText("" + message);
        binding.moveToHomeBT.setOnClickListener(view -> {
            gotoHome();
        });
    }

    @Override
    public void onBackPressed() {
        gotoHome();
    }

    public void gotoHome() {
        Intent intent = new Intent(OrderDetagDoneActivity.this, DtHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}