package com.lifestyle.retail_dashboard.view.brand_ranking.adpter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleBrandRankItemBinding
import com.lifestyle.retail_dashboard.utils.DashboardUtils
import com.lifestyle.retail_dashboard.view.brand_ranking.activity.BrandRankHomActivity
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankDTO
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankData
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram
import java.util.*
import kotlin.collections.ArrayList

class BrandRankingAdapter(val context: Context, var chartType: String,
                          private var brandRankList: List<BrandRankData>) : RecyclerView.Adapter<BrandRankViewHolder>(), Filterable {
    private var filterationList = brandRankList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandRankViewHolder {
        val binding: SingleBrandRankItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_brand_rank_item, parent, false)
        return BrandRankViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return brandRankList.size
    }

    override fun onBindViewHolder(holder: BrandRankViewHolder, position: Int) {
        val brandRank = brandRankList[position]
        var title = ""
        var type = 0
        if (brandRank.areaNm == null
                && brandRank.rgnNm == null
                && brandRank.locNm == null) {
            title = "PAN India"
            type = 1
        }
        if (brandRank.areaNm != null) {
            title = "${brandRank.areaNm}"
            type = 2
        }
        if (brandRank.rgnNm != null) {
            title = "${brandRank.rgnNm}"
            type = 3
        }
        if (brandRank.locNm != null) {
            title = "${brandRank.locNm}"
            type = 4
        }
        if (brandRank.grpNm != null) {
            if (brandRank.grpNm!!.equals(chartType, true))
                holder.bindItems(title, brandRank.brandRankList, type)
        }


        holder.binding.tvTitle.setOnClickListener {
            if (context is BrandRankHomActivity) {
                val activity = context as BrandRankHomActivity
                activity.onItemSelected(brandRank)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty())
                    brandRankList = filterationList
                else {
                    val resultList: MutableList<BrandRankData> = arrayListOf()
                    for (myData in filterationList) {
                        var title = ""
                        if (myData.areaNm != null)
                            title = "${myData.areaNm}"
                        if (myData.rgnNm != null)
                            title = "${myData.rgnNm}"
                        if (myData.locNm != null)
                            title = "${myData.locNm}"
                        if (title.toLowerCase(Locale.getDefault()).contains(charSearch))
                            resultList.add(myData)
                    }
                    brandRankList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = brandRankList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                brandRankList = results?.values as MutableList<BrandRankData>
                notifyDataSetChanged()
            }
        }
    }
}

class BrandRankViewHolder(val context: Context, val binding: SingleBrandRankItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    private val xValues = mutableListOf<String>()
    private val cyRankData = mutableListOf<Double>()
    private val lyRankData = mutableListOf<Double>()
    fun bindItems(title: String, brandRankList: MutableList<BrandRankDTO>, type: Int) {
        binding.tvTitle.isSelected = true
        binding.tvTitle.text = title
        xValues.clear()
        cyRankData.clear()
        lyRankData.clear()
        Log.d("Abrar", "Graph ${Gson().toJson(brandRankList)}")
        try {
            binding.barChart.clear()
            binding.barChart.invalidate()
            for (br in brandRankList) {
                xValues.add(br.respType)
                if (type == 1) {
                    cyRankData.add(br.cy_rank_p)
                    lyRankData.add(br.ly_rank_p)
                } else if (type == 2) {
                    cyRankData.add(br.cy_rank_R)
                    lyRankData.add(br.ly_rank_R)
                } else if (type == 3) {

                } else if (type == 4) {
                    cyRankData.add(br.cy_rank_L)
                    lyRankData.add(br.ly_rank_L)
                }
                //cyRankData.add(br.cy_rank)
                //lyRankData.add(br.ly_rank)
            }

            if (xValues.size > 0) {
                xValues.add(0, "")
                initialize()
                plotBarchart()
            }
        } catch (e: Exception) {
            Log.d("Abrar", "11 Exception: ${e.localizedMessage}")
        }
    }

    private fun initialize() {
        try {
            val max1 = Collections.max(cyRankData);
            val max2 = Collections.max(lyRankData);
            var max = 0.0;
            if(max1 > max2){
                max = max1
            }else {
                max = max2
            }

            binding.barChart.description.isEnabled = false
            binding.barChart.setPinchZoom(false)
            binding.barChart.setDrawBarShadow(false)
            binding.barChart.setDrawGridBackground(false)
            binding.barChart.setScaleEnabled(false)
            binding.barChart.setTouchEnabled(false)

            val l: Legend = binding.barChart.legend
            l.isWordWrapEnabled = true
            l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)

            //X Axis
            val xAxis: XAxis = binding.barChart.xAxis
            xAxis.axisMinimum = 0f
//            xAxis.axisMaximum = max.toFloat()
            xAxis.axisLineColor = Color.BLACK
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (xValues.size > 0 && value >= 0) {
                        xValues[value.toInt() % xValues.size]
                    } else
                        ""
                }
            }

            //Left Axis
            val leftAxis: YAxis = binding.barChart.axisLeft
            leftAxis.axisMinimum = 0f
//            leftAxis.axisMaximum = max.toFloat()
            leftAxis.axisLineColor = Color.BLACK
            leftAxis.granularity = 1f
            leftAxis.isGranularityEnabled = true
            leftAxis.setCenterAxisLabels(true)
            leftAxis.setDrawGridLines(false)
            leftAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return DashboardUtils.decimalFormat1.format(value.toDouble())
                }
            }

            //Right Axis
            binding.barChart.axisRight.isEnabled = false
        } catch (e: Exception) {
            Log.d("Abrar", "22 Exception: ${e.localizedMessage}")
        }
    }

    private fun plotBarchart() {
        try {
            if (cyRankData.size > 0 || lyRankData.size > 0) {
                binding.barChart.visibility = View.VISIBLE
                val groupSpace = 0.12f
                val barSpace = 0.04f
                val barWidth = 0.4f
                // (barWidth + barSpace) * No.Of.Bar + groupSpace = 1.00 -> interval per "group"
                val groupCount = xValues.size - 1
                val start = 1
                val values1 = ArrayList<BarEntry>()
                val values2 = ArrayList<BarEntry>()
                for (i in 0 until groupCount) {
                    values1.add(BarEntry(i.toFloat(), cyRankData[i].toFloat()))
                    values2.add(BarEntry(i.toFloat(), lyRankData[i].toFloat()))
                }

                val set1 = BarDataSet(values1, "CY Rank")
                set1.color = context.resources.getColor(R.color.lineChart)
                set1.valueTextColor = Color.BLACK
                set1.valueTextSize = 10f
                set1.axisDependency = YAxis.AxisDependency.LEFT
                set1.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return DashboardUtils.decimalFormat1.format(value.toDouble())
                    }
                }

                val set2 = BarDataSet(values2, "LY Rank")
                set2.color = context.resources.getColor(R.color.cummulative)
                set2.valueTextColor = Color.BLACK
                set2.valueTextSize = 10f
                set2.axisDependency = YAxis.AxisDependency.LEFT
                set2.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return DashboardUtils.decimalFormat1.format(value.toDouble())
                    }
                }

                val data = BarData(set1, set2)
                binding.barChart.data = data
                binding.barChart.barData.barWidth = barWidth
                binding.barChart.xAxis.axisMinimum = start.toFloat()
                binding.barChart.xAxis.axisMaximum = start + binding.barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
                binding.barChart.groupBars(start.toFloat(), groupSpace, barSpace)
                binding.barChart.setDrawValueAboveBar(true)
                //binding.chart.legend.isEnabled = false
                binding.barChart.invalidate()
            } else {
                binding.barChart.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.d("Abrar", "33 Exception: ${e.localizedMessage}")
            Log.d("Abrar", "33 Exception: ${e.printStackTrace()}")
        }
    }
}