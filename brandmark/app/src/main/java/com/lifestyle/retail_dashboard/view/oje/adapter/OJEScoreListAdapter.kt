package com.lifestyle.retail_dashboard.view.oje.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleOjeScoreScreenBinding
import com.lifestyle.retail_dashboard.view.oje.model.OJEList

class OJEScoreListAdapter (val ojeScoreList: List<OJEList>, val onOjeListener : OJEList.OnOJEReviewListener): RecyclerView.Adapter<FilterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding: SingleOjeScoreScreenBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_oje_score_screen, parent, false)
        return FilterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ojeScoreList.size

    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val ojeQue = ojeScoreList[position]
        holder.bindItems(ojeQue)


        holder.binding.swScore.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                ojeQue.ojeScore = 1
            else
                ojeQue.ojeScore = 0


            onOjeListener.onItemCheck(position,ojeQue)
        }
    }
}

class FilterViewHolder(val binding: SingleOjeScoreScreenBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindItems(ojeScore: OJEList) {
        binding.ojeScore = ojeScore
    }
}