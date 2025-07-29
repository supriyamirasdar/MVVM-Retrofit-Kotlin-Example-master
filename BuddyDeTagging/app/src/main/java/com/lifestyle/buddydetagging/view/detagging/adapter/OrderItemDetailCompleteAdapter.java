package com.lifestyle.buddydetagging.view.detagging.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.view.detagging.model.ItemList;
import com.lifestyle.buddydetagging.view.detagging.model.OrderDetailDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class OrderItemDetailCompleteAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    //private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<OrderDetailDto> mFilteredList;
    private SviOrderClickLisner sviOrderClickLisner;
    private Context context;

    public OrderItemDetailCompleteAdapter(Context context, List<OrderDetailDto> mFilteredList) {
        this.context = context;
        this.mFilteredList = mFilteredList;

    }

    public OrderItemDetailCompleteAdapter(Context context, List<OrderDetailDto> mFilteredList, SviOrderClickLisner sviOrderClickLisner) {
        this.context = context;
        this.mFilteredList = mFilteredList;
        this.sviOrderClickLisner = sviOrderClickLisner;

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dt_order_item_tag_complete, parent, false));
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


    private OrderDetailDto getItem(int position) {
        return mFilteredList.get(position);
    }

    public class ViewHolder extends BaseViewHolder {

        private CardView itemDetailCV;


        private TextView itemCodeValTV;
        private TextView eanCodeValTV;
        private TextView itemDescValTV;

        private TextView itemColorTV;
        private TextView itemMrpTV;
        private TextView itemRspTV;
        private TextView itemSizeTV;
        private TextView orderTagStatusValTV;

        ViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            itemDetailCV = itemView.findViewById(R.id.itemDetailCV);
            orderTagStatusValTV = itemView.findViewById(R.id.orderTagStatusValTV);

            itemCodeValTV = itemView.findViewById(R.id.itemCodeValTV);
            eanCodeValTV = itemView.findViewById(R.id.eanCodeValTV);
            itemDescValTV = itemView.findViewById(R.id.itemDescValTV);

            itemColorTV = itemView.findViewById(R.id.itemColorTV);
            itemMrpTV = itemView.findViewById(R.id.itemMrpTV);
            itemRspTV = itemView.findViewById(R.id.itemRspTV);
            itemSizeTV = itemView.findViewById(R.id.itemSizeTV);

        }

        protected void clearView() {

        }

        @SuppressLint("SetTextI18n")
        public void onBind(final int position) {
            super.onBind(position);
            try {
                final OrderDetailDto orders = mFilteredList.get(position);
                ItemList itemData = orders.getItemList();
                itemCodeValTV.setText("" + itemData.getItemID());
                eanCodeValTV.setText("" + itemData.getItemEAN());

                itemDescValTV.setText("Desc: " + itemData.getItemDesc());
                itemColorTV.setText("Color: " + itemData.getItemColour());

                itemMrpTV.setText("MRP: " + itemData.getSellPrice());
                itemRspTV.setText("RSP: " + itemData.getExtSellPrice());
                itemSizeTV.setText("Size: " + itemData.getItemSize());

                orderTagStatusValTV.setText("" + orders.getTagStatusVal());

                /*if (orders.getTagStatusVal().equals("Tag")) {
                    orderTagStatusValTV.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    orderTagStatusValTV.setTextColor(context.getResources().getColor(R.color.green));
                }
                if (orders.getTagStatus() == 0) {
                    itemDetailCV.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                } else if (orders.getTagStatus() == 1) {
                    itemDetailCV.setCardBackgroundColor(context.getResources().getColor(R.color.light_green));
                }*/

                orderTagStatusValTV.setTextColor(context.getResources().getColor(R.color.green));
                itemDetailCV.setCardBackgroundColor(context.getResources().getColor(R.color.grey_bg));

                itemView.setOnClickListener(v -> {
                    try {
                        //sviOrderClickLisner.onOrderClick(position, orders);
                    } catch (Exception e) {
                        Log.d("Abrar", e.getLocalizedMessage());
                    }
                });
            } catch (Exception e) {
                Log.d("Abrar", e.getLocalizedMessage());
            }
        }
    }


    public List<OrderDetailDto> getData() {
        return mFilteredList;
    }

    public interface SviOrderClickLisner {
        void onOrderClick(int position, OrderDetailDto orders);
    }


}
