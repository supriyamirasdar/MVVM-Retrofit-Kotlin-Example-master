package com.lifestyle.retail_dashboard.view.brandperformance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleBrandPerfListItemBinding
import com.lifestyle.retail_dashboard.utils.DashboardUtils.*
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList
import com.lifestyle.retail_dashboard.view.brandperformance.model.SalePerformance

class BrandPerAdapter (private val brandType: String, private val brandPerformanceList: List<BrandPerformanceList>, private val onItemClickListener: BrandPerformanceList.OnItemClickListener): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SingleBrandPerfListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_brand_perf_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return brandPerformanceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val brandPerformance = brandPerformanceList[position]
        var name : String? = null
        if (brandPerformance.areaNm != null)
            name = brandPerformance.areaNm

        if (brandPerformance.rgnNm != null)
            name = brandPerformance.rgnNm

        if (brandPerformance.locNm != null)
            name = brandPerformance.locNm

        for (saleperformance in brandPerformance.productTimeIntervalList!!){
            if (saleperformance.respType.equals(brandType,true)){
                if (name != null) {
                    holder.bindItems(saleperformance,onItemClickListener,name,brandType,brandPerformance)
                }
                break
            }
        }
    }
}

class ViewHolder(val binding: SingleBrandPerfListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(salePerformance: SalePerformance, onItemClickListener: BrandPerformanceList.OnItemClickListener, name: String, brandType: String, brandPerformance: BrandPerformanceList) {
        binding.salePerformance = salePerformance
        binding.onItemClick = onItemClickListener
        binding.name = name
        binding.brand = brandPerformance
        binding.brandType = brandType
        binding.lacs = LACS
        binding.crore = CRORE
        binding.oneDecimal = floatingDecimalFormate1
        binding.twoDecimal = floatingDecimalFormate5
    }
}