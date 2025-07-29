package com.lifestyle.retail_dashboard.view.homescreen.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleHomeItemBinding
import com.lifestyle.retail_dashboard.utils.DialogBox
import com.lifestyle.retail_dashboard.utils.Utility.goToNextScreen
import com.lifestyle.retail_dashboard.view.attendance.activity.AttendanceHomeActivity
import com.lifestyle.retail_dashboard.view.brand_ranking.activity.BrandRankHomActivity
import com.lifestyle.retail_dashboard.view.brandperformance.activity.BrandPerformanceActivity
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase
import com.lifestyle.retail_dashboard.view.inventory_managment.activity.InventoryManagementActivity
import com.lifestyle.retail_dashboard.view.oje.activity.OJEScoreActivity
import com.lifestyle.retail_dashboard.view.planogram.activity.PlanogramActivity
import com.lifestyle.retail_dashboard.view.planogram.activity.PlanogramHomeActivity
import com.lifestyle.retail_dashboard.view.report.PlotingGraphActivity
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.GetRACActivity
import com.lifestyle.retail_dashboard.view.store_contact.activity.StoreContactNumberActivity

class HomeAdapter(val data: LiveData<List<Usecase>>): RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding: SingleHomeItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_home_item, parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.value!!.size

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        data.value?.get(position)?.let { holder.bindItems(it) }
    }
}

class HomeViewHolder(val binding: SingleHomeItemBinding) : RecyclerView.ViewHolder(binding.root), Usecase.ItemClickListner {
    fun bindItems(usecase: Usecase) {
        binding.usecase = usecase
        binding.onItemClick = this
    }

    override fun onItemClick(view: View, item: String) {
        val context = view.context
        if (item.equals("Brand Performance", true))
            goToNextScreen(context, BrandPerformanceActivity::class.java,item)
        else if (item.equals("Visual Merchandising", true))
            goToNextScreen(context, PlanogramHomeActivity::class.java,item)
        else if (item.equals("Learning & Development", true))
            goToNextScreen(context, PlanogramHomeActivity::class.java,item)
        else if (item.equals("Employee Attendance", true))
            goToNextScreen(context, AttendanceHomeActivity::class.java,item)
        else if (item.equals("Report", true))
            goToNextScreen(context, PlotingGraphActivity::class.java,item)
        else if (item.equals("Planogram ",true)
                ||item.equals("Catalog & Content",true)
                ||item.equals("Training",true))
            goToNextScreen(context, PlanogramActivity::class.java,item)
        else if (item.equals("OJE Score",true))
            goToNextScreen(context, OJEScoreActivity::class.java,item)
        else if (item.equals(context.resources.getString(R.string.retail_audit_translation),true))
            goToNextScreen(context, GetRACActivity::class.java,item)
        else if (item.equals(context.resources.getString(R.string.brand_rank),true))
            goToNextScreen(context, BrandRankHomActivity::class.java,item)
        else if (item.equals(context.resources.getString(R.string.invetory),true))
            goToNextScreen(context, InventoryManagementActivity::class.java,item)
        else if (item.equals(context.resources.getString(R.string.store_contact_num),true))
            goToNextScreen(context, StoreContactNumberActivity::class.java,item)
        else
            DialogBox.showSorryDialog(context as Activity)
    }
}