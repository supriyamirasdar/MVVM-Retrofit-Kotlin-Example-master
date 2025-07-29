package com.landmarkgroup.smartkiosk.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.ui.pdppage.model.ColorDto;

import java.util.List;

public class ColorOptionsAdapter extends RecyclerView.Adapter<ColorOptionsAdapter.MyViewHolder> {

    private List<ColorDto> additionalCapacityArrayList;
    private List<ColorDto> additionalCapacityArrayListFiltered;
    private Activity mContext;
    private OnItemClickListener listener;

    private int PositionSelected = 0;

    public ColorOptionsAdapter(Activity mContext, List<ColorDto> additionalCapacityArrayList, OnItemClickListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_color_option, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ColorDto storeStatusDto = additionalCapacityArrayListFiltered.get(position);
        holder.colorNameTV.setText(storeStatusDto.getColour()     );
        // holder.boStoreStatusTV.setText("" + storeStatusDto.getStoreBOStatus());
        // holder.resaStoreStatusTV.setText("" + storeStatusDto.getResaStatus());

        /*if (storeStatusDto.getStoreBOStatus().equals("STORE CLOSED")) {
            holder.boStoreStatusTV.setText("CLOSED");
            holder.boStoreStatusTV.setTextColor(mContext.getResources().getColor(R.color.green));
        } else if (storeStatusDto.getStoreBOStatus().equals("DAY END PENDING")) {
            holder.boStoreStatusTV.setText("NOT CLOSED");
            holder.boStoreStatusTV.setTextColor(mContext.getResources().getColor(R.color.Red));
        }*/

        /*if (storeStatusDto.getResaStatus().equals("C")) {
            holder.resaStoreStatusTV.setText("CLOSED");
            holder.resaStoreStatusTV.setTextColor(mContext.getResources().getColor(R.color.green));
        } else if (storeStatusDto.getResaStatus().equals("W")) {
            holder.resaStoreStatusTV.setText("NOT CLOSED");
            holder.resaStoreStatusTV.setTextColor(mContext.getResources().getColor(R.color.Red));
        }*/


        if (position == PositionSelected) {
            //holder.nameTV.setTextColor(Color.parseColor("#000000"));
            //holder.colorNameTV.setBackgroundColor(mContext.getResources().getColor(R.color.grey_bg));
            holder.listItemNew.setBackgroundResource(R.drawable.layout_border_unsel);
        } else {
            // holder.nameTV.setTextColor(Color.parseColor("#000000"));
            //holder.colorNameTV.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.listItemNew.setBackgroundResource(R.drawable.layout_border_sel);
        }
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return additionalCapacityArrayListFiltered.size();
    }

    public interface OnItemClickListener {
       // void onItemClick(int item);
        void onItemClick(int item, View view);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView colorNameTV;//, boStoreStatusTV, resaStoreStatusTV, toDateValTV;
        LinearLayout listItem, listItemNew;

        public MyViewHolder(View itemView) {
            super(itemView);
            colorNameTV = itemView.findViewById(R.id.colorNameTV);
            //boStoreStatusTV = itemView.findViewById(R.id.boStoreStatusTV);
            //resaStoreStatusTV = itemView.findViewById(R.id.resaStoreStatusTV);

            listItem = itemView.findViewById(R.id.listItem);
            listItemNew = itemView.findViewById(R.id.listItemNew);
        }

        public void bind(final int item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // listener.onItemClick(item);
                    listener.onItemClick(item, v);
                }
            });
        }
    }

    private ColorDto getItem(int position) {
        return additionalCapacityArrayListFiltered.get(position);
    }


    public void clear() {

        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void remove(ColorDto ProductDeliverys) {
        int position = additionalCapacityArrayListFiltered.indexOf(ProductDeliverys);
        if (position > -1) {
            additionalCapacityArrayListFiltered.remove(position);
            notifyItemRemoved(position);
        }
    }


    public void addAll(List<ColorDto> ProductDeliverys) {
        for (ColorDto response : ProductDeliverys) {
            add(response);
        }
    }

    public void add(ColorDto response) {
        additionalCapacityArrayListFiltered.add(response);
        notifyItemInserted(additionalCapacityArrayListFiltered.size() - 1);
    }

}
