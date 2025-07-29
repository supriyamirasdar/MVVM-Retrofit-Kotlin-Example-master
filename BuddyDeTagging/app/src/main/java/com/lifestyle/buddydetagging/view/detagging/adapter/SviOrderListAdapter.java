package com.lifestyle.buddydetagging.view.detagging.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDto;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SviOrderListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    //private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<OrderDto> mFilteredList;
    private SviOrderClickLisner sviOrderClickLisner;
    private Context context;

    public SviOrderListAdapter(Context context, List<OrderDto> mFilteredList) {
        this.context = context;
        this.mFilteredList = mFilteredList;

    }

    public SviOrderListAdapter(Context context, List<OrderDto> mFilteredList, SviOrderClickLisner sviOrderClickLisner) {
        this.context = context;
        this.mFilteredList = mFilteredList;
        this.sviOrderClickLisner = sviOrderClickLisner;

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mFilteredList != null ? mFilteredList.size() : 0;
    }


    private OrderDto getItem(int position) {
        return mFilteredList.get(position);
    }

    public class ViewHolder extends BaseViewHolder {

        private TextView orderIdTV;
        private TextView orderDateTV;
        private TextView orderAmountTV;
        private TextView custMobileNumTV;
        private TextView orderDetaggedStatusTV, detagDateTV, detqgByTV;
LinearLayout detqgDateLL, detqgByLL;
        ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            orderIdTV = itemView.findViewById(R.id.orderIdTV);
            orderDateTV = itemView.findViewById(R.id.orderDateTV);
            orderAmountTV = itemView.findViewById(R.id.orderAmountTV);
            custMobileNumTV = itemView.findViewById(R.id.custMobileNumTV);
            orderDetaggedStatusTV = itemView.findViewById(R.id.orderDetaggedStatusTV);
            detagDateTV= itemView.findViewById(R.id.detagDateTV);
            detqgByTV= itemView.findViewById(R.id.detqgByTV);

            detqgDateLL= itemView.findViewById(R.id.detqgDateLL);
            detqgByLL= itemView.findViewById(R.id.detqgByLL);
        }

        protected void clearView() {

        }

        @SuppressLint("SetTextI18n")
        public void onBind(final int position) {
            super.onBind(position);
            try {
                final OrderDto orders = mFilteredList.get(position);

                orderIdTV.setText(": " + orders.getOrderId());
                //String curFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
                // String reqFormat = "yyyy-MM-dd HH:mm:ss";
                //String dateST = CommonUtility.convertDateToRequiredFormat(orders.getCreatedAt(), curFormat, reqFormat);
                String dateST = orders.getCreatedAt();

                /*String[] dateSTArry = dateST.split("T");
                if (dateSTArry.length > 1) {
                    dateST = dateSTArry[0] + " " + dateSTArry[1];
                } else {
                    dateST = dateSTArry[0];a
                }
                dateST = dateST.substring(0, dateST.length() - 0);
                */
              /*  String curFormat = "EEE MMM dd HH:mm:ss z yyyy";
                String reqFormat = "dd-MMM-yyyy HH:mm:ss";
                dateST = CommonUtility.convertDateToRequiredFormat(dateST, curFormat, reqFormat);*/

              /*  SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                Date parsedDate = inputFormat.parse(dateST);
                SimpleDateFormat outputDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");*/

               /* SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.getDefault());
                Date date = inputDateFormat.parse(dateST);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
                String outputDate = outputDateFormat.format(date);*/


                orderDateTV.setText(": " + dateST); //COMMENT BY SUPRIYA
                //orderDateTV.setText("" + CommonUtility.getCurrentDate());

                String price =orders.getExtSellPrice();

                // Parse the string value to a double
                double value = Double.parseDouble(price);
                // Create a DecimalFormat object with the desired format
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                // Apply the rounding format to the value
                double roundedValue = Double.parseDouble(decimalFormat.format(value));
                // Convert the rounded value back to a string if necessary
                String roundedPrice = Double.toString(roundedValue);


                orderAmountTV.setText(": Rs. " + roundedPrice);
                custMobileNumTV.setText(": " + orders.getCustomerPhoneNo());

                if (orders.getOrderDetagged() == 0) {
                    orderDetaggedStatusTV.setText(": Order DeTag Pending");
                    orderDetaggedStatusTV.setTextColor(context.getResources().getColor(R.color.red));

                    detqgByLL.setVisibility(View.GONE);
                    detqgDateLL.setVisibility(View.GONE);
                } else if (orders.getOrderDetagged() == 1) {
                    //orderDetaggedStatusTV.setText(""+ orders.getDetaggedByST());
                    orderDetaggedStatusTV.setText(": Order DeTag Completed");
                    orderDetaggedStatusTV.setTextColor(context.getResources().getColor(R.color.green));

                    detqgByLL.setVisibility(View.VISIBLE);
                    detqgDateLL.setVisibility(View.VISIBLE);

                    detagDateTV.setText(": "+ orders.getDetagDate());
                    detqgByTV.setText(": "+ orders.getDetagBy());

                }
                itemView.setOnClickListener(v -> {
                    try {
                        sviOrderClickLisner.onOrderClick(position, orders);
                    } catch (Exception e) {
                        Log.d("Abrar", e.getLocalizedMessage());
                    }
                });
            } catch (Exception e) {
                Log.d("Abrar", e.getLocalizedMessage());
            }
        }
    }


    public List<OrderDto> getData() {
        return mFilteredList;
    }

    public interface SviOrderClickLisner {
        void onOrderClick(int position, OrderDto orders);
    }

    public interface SviOsdOrderCheckLisner {
        void onOrderCheckClick(int action, String orderNo, OrderDto sviOrders);
    }
}
