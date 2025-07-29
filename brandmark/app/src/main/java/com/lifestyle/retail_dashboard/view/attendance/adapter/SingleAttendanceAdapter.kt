package com.lifestyle.retail_dashboard.view.attendance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleAttendanceHistoryItemBinding
import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeDetail

class SingleAttendanceAdapter (val employeeList: List<EmployeeDetail>): RecyclerView.Adapter<SingleAttendanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleAttendanceViewHolder {
        val binding: SingleAttendanceHistoryItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_attendance_history_item, parent, false)
        return SingleAttendanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: SingleAttendanceViewHolder, position: Int) {
        holder.bindItems(employeeList[position])

        holder.binding.root.setOnClickListener {
        }
    }
}

class SingleAttendanceViewHolder(val binding: SingleAttendanceHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(employeeDetail: EmployeeDetail) {
        binding.employee = employeeDetail

        /*binding.tvDate.isSelected = true
        binding.tvCheckIn.isSelected = true
        binding.tvCheckOut.isSelected = true
        binding.tvStatus.isSelected = true*/
    }
}