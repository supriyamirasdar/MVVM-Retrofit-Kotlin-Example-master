package com.landmarkgroup.smartkiosk.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.SizeDto;

import java.util.List;

public class SizeOptionsAdapter extends RecyclerView.Adapter<SizeOptionsAdapter.MyViewHolder> {

    private List<SizeDto> additionalCapacityArrayList;
    private List<SizeDto> additionalCapacityArrayListFiltered;
    private Activity mContext;
    private OnItemClickListener listener;
    private int PositionSelected = 0;

    public SizeOptionsAdapter(Activity mContext, List<SizeDto> additionalCapacityArrayList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.additionalCapacityArrayList = additionalCapacityArrayList;
        this.additionalCapacityArrayListFiltered = additionalCapacityArrayList;
        this.listener = listener;
    }

    public void setPositionSelected(int position) {
        PositionSelected = position;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_size_options, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        SizeDto storeStatusDto = additionalCapacityArrayListFiltered.get(position);
        if (storeStatusDto.getSize().contains("(")) {
            // String sizee = storeStatusDto.getSize().substring()
            String[] parts = storeStatusDto.getSize().split("\\(");
            String part1 = parts[0]; // 004
            holder.sizeNameTV.setText(part1.trim());
        } else
            holder.sizeNameTV.setText(storeStatusDto.getSize().trim());
        // holder.boStoreStatusTV.setText("" + storeStatusDto.getStoreBOStatus());
        // holder.resaStoreStatusTV.setText("" + storeStatusDto.getResaStatus());


        if (position == PositionSelected) {
            //holder.nameTV.setTextColor(Color.parseColor("#000000"));
            //holder.colorNameTV.setBackgroundColor(mContext.getResources().getColor(R.color.grey_bg));
            holder.listItemSize.setBackgroundResource(R.drawable.layout_border_unsel);
        } else {
            // holder.nameTV.setTextColor(Color.parseColor("#000000"));
            //holder.colorNameTV.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.listItemSize.setBackgroundResource(R.drawable.layout_border_sel);
        }

        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return additionalCapacityArrayListFiltered.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int item, View view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView sizeNameTV;//, boStoreStatusTV, resaStoreStatusTV, toDateValTV;
        LinearLayout listItem, listItemSize;

        public MyViewHolder(View itemView) {
            super(itemView);
            sizeNameTV = itemView.findViewById(R.id.sizeNameTV);
            //boStoreStatusTV = itemView.findViewById(R.id.boStoreStatusTV);
            //resaStoreStatusTV = itemView.findViewById(R.id.resaStoreStatusTV);
            listItemSize = itemView.findViewById(R.id.listItemSize);
            listItem = itemView.findViewById(R.id.listItem);
        }

        public void bind(final int item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
        }
    }

    private SizeDto getItem(int position) {
        return additionalCapacityArrayListFiltered.get(position);
    }


    public void clear() {

        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void remove(SizeDto ProductDeliverys) {
        int position = additionalCapacityArrayListFiltered.indexOf(ProductDeliverys);
        if (position > -1) {
            additionalCapacityArrayListFiltered.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void addAll(List<SizeDto> ProductDeliverys) {
        for (SizeDto response : ProductDeliverys) {
            add(response);
        }
    }

    public void add(SizeDto response) {
        additionalCapacityArrayListFiltered.add(response);
        notifyItemInserted(additionalCapacityArrayListFiltered.size() - 1);
    }

}
