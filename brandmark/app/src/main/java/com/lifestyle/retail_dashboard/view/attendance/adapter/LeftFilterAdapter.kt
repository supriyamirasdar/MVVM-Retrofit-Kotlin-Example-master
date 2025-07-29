package com.lifestyle.retail_dashboard.view.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleLeftFilterByListBinding

class LeftFilterAdapter(val context:Context, private val leftFilterByList: List<String>): RecyclerView.Adapter<FilterAttendanceViewHolder>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAttendanceViewHolder {
        val binding: SingleLeftFilterByListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_left_filter_by_list, parent, false)
        return FilterAttendanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return leftFilterByList.size
    }

    override fun onBindViewHolder(holder: FilterAttendanceViewHolder, position: Int) {
        holder.bindItems(leftFilterByList[position])

        if (position == selectedPosition) {
            holder.binding.card.setBackgroundColor(context.resources.getColor(R.color.white))
            //holder.binding.tvName.setTextColor(context.resources.getColor(R.color.blue))
        }else {
            holder.binding.card.setBackgroundColor(context.resources.getColor(R.color.grey_bg))
            //holder.binding.tvName.setTextColor(context.resources.getColor(R.color.text))
        }


        holder.binding.root.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }
}

class FilterAttendanceViewHolder(val binding: SingleLeftFilterByListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(filterBy: String) {
        binding.filterBy = filterBy
    }
}