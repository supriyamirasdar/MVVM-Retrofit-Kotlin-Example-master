package com.landmarkgroup.smartkiosk.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.storage.TransactionData;
import com.landmarkgroup.smartkiosk.ui.homescreen.OrderDto;
import com.landmarkgroup.smartkiosk.ui.homescreen.POSListner;

import org.json.JSONArray;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MyViewHolder> {

      static POSListner posListner;
     private JSONArray dataSet;
    Activity mContext;
    String itemcount = "", desc = "";
    private float x1, x2;
    AlertDialog.Builder builder;
    List<TransactionData> scanBarcodeDtoList;
    int selectedPosition = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderurlTV, transIdTV, kioskIdTV, orderTypeTV, finalPrice, hash;
        TextView orderDateTV;
        LinearLayout mainLin;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.transIdTV = itemView.findViewById(R.id.transIdTV);
            this.orderurlTV = itemView.findViewById(R.id.orderurlTV);

            this.orderDateTV = itemView.findViewById(R.id.orderDateTV);
            this.kioskIdTV = itemView.findViewById(R.id.kioskIdTV);

this.orderTypeTV= itemView.findViewById(R.id.orderTypeTV);

        }

        public void bind(int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      // itemClickListener.onItemClick(item);
                     posListner.onClick( "", position);
                }
            });
        }

    }

    public ItemListAdapter(List<TransactionData> scanBarcodeDtoList, Activity mContext, POSListner posListner1) {
        //this.dataSet = data;
        this.scanBarcodeDtoList = scanBarcodeDtoList;
        this.mContext = mContext;
        this.posListner = posListner1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_box, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        TransactionData scanBarcodeDto = scanBarcodeDtoList.get(position);


        //LinearLayout mainLin = holder.mainLin;
        holder.transIdTV.setSelected(true);
        holder.orderurlTV.setSelected(true);

        holder.orderDateTV.setSelected(true);
        holder.kioskIdTV.setSelected(true);
        holder.orderTypeTV.setSelected(true);


        holder.transIdTV.setText("Trans Id : "+scanBarcodeDto.get_id());
        holder.orderurlTV.setText("Order url : "+scanBarcodeDto.getOrdConfirmUrl());

        holder.orderDateTV.setText("Order Date : " + scanBarcodeDto.getInvoice_date());
        holder.kioskIdTV.setText("Kiosk Id : " + scanBarcodeDto.getKioskId());
        holder.orderTypeTV.setText("Order Type : " + scanBarcodeDto.getOrderType());

        holder.bind(position);
        /*holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = -1;
                notifyDataSetChanged();
                posListner.onClick("Desc : " + scanBarcodeDto.getItemDescription(), position);
            }
        });

        holder.discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                posListner.onClick("Discount", position);
            }
        });

        holder.qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = -1;
                notifyDataSetChanged();
                posListner.onClick("QtyUpdate", position);
            }
        });*/


    }



    @Override
    public int getItemCount() {
        return scanBarcodeDtoList.size();
    }


}