package com.lifestyle.retail_dashboard.view.planogram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleRightFilterByListBinding
import com.lifestyle.retail_dashboard.view.attendance.model.FilterItemSelected

class PlanogramRightFilter(val context: Context, private val rightFilterByList: List<FilterItemSelected>, val onItemSelected: FilterItemSelected.OnItemCheckListener, val itemSelectedList: MutableList<FilterItemSelected>): RecyclerView.Adapter<RightFilterAttendanceViewHolder>() {

    var selectedPosition = -1
    var selectedType: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RightFilterAttendanceViewHolder {
        val binding: SingleRightFilterByListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_right_filter_by_list, parent, false)
        return RightFilterAttendanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return rightFilterByList.size
    }

    fun rightSelect(selectedType:String){
        this.selectedType = selectedType
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RightFilterAttendanceViewHolder, position: Int) {
        val item = rightFilterByList[position]
        holder.bindItems(item)

        holder.binding.chkItem.isChecked = false
        for (selected in itemSelectedList){
            if (item.itemId.equals(selected.itemId,true)){
                holder.binding.chkItem.isChecked = true
            }
        }
        /*if (selectedType.equals("Brand",true)){
            holder.binding.chkItem.isChecked = selectedPosition == position
        }
        holder.binding.chkItem.setOnClickListener{
            if (selectedType.equals("Brand",true)) {
                selectedPosition = position
                notifyDataSetChanged()
            }
        }*/

        holder.binding.chkItem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                onItemSelected.onItemCheck(item)
            else
                onItemSelected.onItemUncheck(item)
        }
    }

}

class RightFilterAttendanceViewHolder(val binding: SingleRightFilterByListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(items: FilterItemSelected) {
        binding.items = items
        binding.chkItem.isSelected = true
    }
}