package com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleRetailAuditBinding
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACList
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRetailAudit

class GetRetailAuditAdapter (var getRetailList: MutableList<GetRACList>): RecyclerView.Adapter<GetRetailAuditViewHolder>(){
    private var filterationList = getRetailList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetRetailAuditViewHolder {
        val binding: SingleRetailAuditBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_retail_audit, parent, false)
        return GetRetailAuditViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return getRetailList.size
    }

    override fun onBindViewHolder(holder: GetRetailAuditViewHolder, position: Int) {
        val retailAudit = getRetailList[position]
        holder.bindItems(retailAudit)
    }

    /*override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty() || charSearch.equals("All",true))
                    getRetailList = filterationList
                else {
                    val resultList : MutableList<GetRetailAudit> = arrayListOf()
                    for (myData in filterationList) {
                        if (myData.assignTo.equals(charSearch,true))
                            resultList.add(myData)
                    }
                    getRetailList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = getRetailList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                getRetailList = results?.values as MutableList<GetRetailAudit>
                notifyDataSetChanged()
            }
        }
    }*/
}

class GetRetailAuditViewHolder(val binding: SingleRetailAuditBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(retailAudit: GetRACList) {
        binding.retailAudit = retailAudit
    }
}