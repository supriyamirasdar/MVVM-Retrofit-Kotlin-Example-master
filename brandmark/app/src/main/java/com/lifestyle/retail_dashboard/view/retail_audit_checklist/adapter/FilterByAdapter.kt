package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.BottomSheetItemBinding

class FilterByAdapter(val filterByList: MutableList<String>,val listener: ItemListener): RecyclerView.Adapter<FilterByViewHolder>() {
    private var selected = "All"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterByViewHolder {
        val binding: BottomSheetItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.bottom_sheet_item, parent, false)
        return FilterByViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filterByList.size
    }

    override fun onBindViewHolder(holder: FilterByViewHolder, position: Int) {
        val filterby = filterByList[position]
        holder.bindItems(filterby)

        holder.binding.rbItem.isChecked = filterby.equals(selected,true)


        holder.binding.rbItem.setOnClickListener {
            listener.onItemClick(filterby)
        }
    }

    fun filterBy(filterBy: String){
        selected = filterBy
        notifyDataSetChanged()
    }
}

interface ItemListener {
    fun onItemClick(assignTo: String)
}

class FilterByViewHolder(val binding: BottomSheetItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(filterBy: String) {
        binding.item = filterBy
        binding.rbItem.isSelected = true
    }
}