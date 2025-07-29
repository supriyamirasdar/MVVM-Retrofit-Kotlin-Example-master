package com.lifestyle.retail_dashboard.view.planogram.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SinglePlanogrammItemBinding
import com.lifestyle.retail_dashboard.utils.Constant.dateFormat
import com.lifestyle.retail_dashboard.utils.Constant.inputDateFormat
import com.lifestyle.retail_dashboard.utils.DownloadTask
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.planogram.activity.SinlgePlanogramActivity
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram


class PlanogramAdapter(val context: Context, var planogramList: List<Planogram>, val deletePlanoListner: Planogram.DeletePlanogramListner): RecyclerView.Adapter<ViewHolder>() ,Filterable{

    private var filterationList = planogramList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SinglePlanogrammItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_planogramm_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return planogramList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val planogram = planogramList[position]
        holder.bindItems(planogram)
        holder.binding.deleteListner = deletePlanoListner

        holder.binding.tvBrand.setOnClickListener {

            //val URL = "https://121.244.54.173:9011/ImageViewer/T-BASE/153/153_Test.pdf";
            DownloadTask(context, planogram.filePath)

            /*val path = Uri.parse(planogram.filePath)
            val intent = Intent(Intent.ACTION_VIEW, path)
            //intent.setDataAndType(path, "application/pdf")
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            //context.startActivity(intent)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.d("Abrar","Exception: ${e.localizedMessage}")
                intent.setPackage(null)
                context.startActivity(intent)
            }*/
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(context,SinlgePlanogramActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("planogram", planogram)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty())
                    planogramList = filterationList
                else {
                    val resultList : MutableList<Planogram> = arrayListOf()
                    for (myData in filterationList) {
                        if (myData.fileName!!.toLowerCase().contains(charSearch)
                                || myData.season!!.toLowerCase().contains(charSearch))
                        resultList.add(myData)
                    }
                    planogramList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = planogramList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                planogramList = results?.values as MutableList<Planogram>
                notifyDataSetChanged()
            }
        }
    }
}

class ViewHolder(val binding: SinglePlanogrammItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(planogram: Planogram) {
        binding.planogram = planogram
        binding.inputDF = inputDateFormat
        binding.outputDF = dateFormat
        binding.tvBrand.isSelected = true
        binding.userId = PreferenceUtils.getUserId()
    }
}