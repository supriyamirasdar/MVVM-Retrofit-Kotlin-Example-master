package com.landmarkgroup.smartkiosk.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.landmarkgroup.smartkiosk.model.StoreListModel;
import com.landmarkgroup.smartkiosk.ui.welcome_screen.StoreCodeActivity;
import com.landmarkgroup.smartkiosk.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyViewHolder> implements Filterable {
    private List<StoreListModel> storeList;
    private List<StoreListModel> mFilteredStoreList;
    private StoreCodeActivity activity;

    public StoreListAdapter(StoreCodeActivity activity, List<StoreListModel> storeList) {
        this.activity = activity;
        this.storeList = storeList;
        mFilteredStoreList = storeList;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_store_detail, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final StoreListModel storeDetail = mFilteredStoreList.get(position);
        holder.storeName.setText(storeDetail.getStoreId()+" - "+storeDetail.getStoreName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.storeSelected(storeDetail.getStoreId(),storeDetail.getStoreName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredStoreList != null ? mFilteredStoreList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredStoreList = storeList;
                } else {
                    ArrayList<StoreListModel> filteredList = new ArrayList<>();
                    for (StoreListModel requestData : storeList) {
                        if (requestData.getStoreName().toLowerCase().contains(charString)
                                || requestData.getStoreId().toLowerCase().contains(charString)) {
                            filteredList.add(requestData);
                        }
                    }
                    mFilteredStoreList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredStoreList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredStoreList = (ArrayList<StoreListModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;

        private MyViewHolder(View view) {
            super(view);
            storeName = (TextView) view.findViewById(R.id.storeName);

        }
    }
}