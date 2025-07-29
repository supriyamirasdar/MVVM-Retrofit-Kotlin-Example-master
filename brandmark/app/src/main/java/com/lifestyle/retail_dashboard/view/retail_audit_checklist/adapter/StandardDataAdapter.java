package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.AuditList;

import java.util.List;

public class StandardDataAdapter extends RecyclerView.Adapter<StandardDataAdapter.MyViewHolder> {

    private List<AuditList> auditListList;
    private Activity mContext;
    private OnItemClickListener listener;
    static int selectedPosition = -1;

    public StandardDataAdapter(Activity mContext, List<AuditList> auditListList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.auditListList = auditListList;
        this.listener = listener;
        selectedPosition = -1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_retail_audit_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        AuditList auditList = auditListList.get(position);
        holder.titleTV.setText("" + auditList.getTextLabel());

        if (auditList.isShowYes()) {
            holder.yesRB.setVisibility(View.VISIBLE);
        } else {
            holder.yesRB.setVisibility(View.GONE);
        }

        if (auditList.isShowNo()) {
            holder.noRB.setVisibility(View.VISIBLE);
        } else {
            holder.noRB.setVisibility(View.GONE);
        }

       /* if (selectedPosition == position) {
            holder.listItem.setBackgroundColor(Color.parseColor("#b3b3b3"));
        } else {
            holder.listItem.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/


        holder.radioGroupBT.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yesRB) {
                //some code
                holder.remarksViewLL.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.noRB) {
                //some code
                holder.remarksViewLL.setVisibility(View.GONE);
            }
        });

        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return auditListList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int item);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV;
        LinearLayout listItem, remarksViewLL;
        RadioGroup radioGroupBT;
        RadioButton yesRB, noRB;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            yesRB = itemView.findViewById(R.id.yesRB);
            noRB = itemView.findViewById(R.id.noRB);
            radioGroupBT = itemView.findViewById(R.id.radioGroupBT);
            remarksViewLL = itemView.findViewById(R.id.remarksViewLL);
            listItem = itemView.findViewById(R.id.listItem);
        }

        public void bind(final int item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = item;
                    listener.onItemClick(item);
                }
            });
        }
    }
}