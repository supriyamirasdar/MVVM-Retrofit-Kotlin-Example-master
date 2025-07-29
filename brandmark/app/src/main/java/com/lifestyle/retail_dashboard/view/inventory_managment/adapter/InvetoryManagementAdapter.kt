package com.lifestyle.retail_dashboard.view.inventory_managment.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
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
import com.lifestyle.retail_dashboard.databinding.SingleInventoryManagementItemBinding
import com.lifestyle.retail_dashboard.utils.DashboardUtils
import com.lifestyle.retail_dashboard.view.inventory_managment.activity.InventoryManagementActivity
import com.lifestyle.retail_dashboard.view.inventory_managment.model.InventoryManagement
import java.util.*
import kotlin.collections.ArrayList

class InvetoryManagementAdapter(val context: Context,
                                private var inventoryManagementList: List<InventoryManagement>) : RecyclerView.Adapter<InventoryManagementViewHolder>(), Filterable {
    private var filterationList = inventoryManagementList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryManagementViewHolder {
        val binding: SingleInventoryManagementItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_inventory_management_item, parent, false)
        return InventoryManagementViewHolder(context, binding)
    }

    override fun getItemCount(): Int {
        return inventoryManagementList.size
    }

    override fun onBindViewHolder(holder: InventoryManagementViewHolder, position: Int) {
        val inventory = inventoryManagementList[position]
        var title = ""
        if (inventory.areaNm == null
                && inventory.rgnNm == null
                && inventory.locNm == null)
            title = "PAN India"

        if (inventory.areaNm != null)
            title = "${inventory.areaNm}"

        if (inventory.rgnNm != null)
            title = "${inventory.rgnNm}"

        if (inventory.locNm != null)
            title = "${inventory.locNm}"

        holder.bindItems(title, inventory)

        holder.binding.tvTitle.setOnClickListener {
            if (context is InventoryManagementActivity) {
                val activity = context as InventoryManagementActivity
                activity.onItemSelected(inventory)
            }
        }

        holder.binding.normsTV.setOnClickListener {
            if (context is InventoryManagementActivity) {
                val activity = context as InventoryManagementActivity
                activity.onNormsClicked()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty())
                    inventoryManagementList = filterationList
                else {
                    val resultList: MutableList<InventoryManagement> = arrayListOf()
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
                    inventoryManagementList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = inventoryManagementList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                inventoryManagementList = results?.values as MutableList<InventoryManagement>
                notifyDataSetChanged()
            }
        }
    }
}

class InventoryManagementViewHolder(val context: Context, val binding: SingleInventoryManagementItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    private val xValues = mutableListOf<String>()
    private val freshnessData = mutableListOf<Double>()
    private val fillRateDate = mutableListOf<Double>()
    private val brokenData = mutableListOf<Double>()
    fun bindItems(title: String, inventory: InventoryManagement) {
        binding.invetory = inventory
        binding.title = title
        binding.oneDecimal = DashboardUtils.floatingDecimalFormate1
        binding.noDecimal = DashboardUtils.decimalFormat1

        binding.tvTitle.isSelected = true
        binding.tvDispCapcty.isSelected = true
        binding.tvSoh.isSelected = true
        binding.tvBroken.isSelected = true
        binding.tvFreshness.isSelected = true

        xValues.clear()
        freshnessData.clear()
        brokenData.clear()
        fillRateDate.clear()
        Log.d("Abrar", "Graph ${Gson().toJson(inventory)}")
        try {
            binding.barChart.clear()
            binding.barChart.invalidate()

            xValues.add("Brand")
            freshnessData.add(inventory.freshnessPer)
            fillRateDate.add(inventory.fillratePer)
            brokenData.add(inventory.brokenPer)

            if (xValues.size > 0) {
                xValues.add(0, "")
                initialize()
                plotBarchart()
            }
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
    }

    private fun initialize() {
        try {
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
            xAxis.axisLineColor = Color.BLACK
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return ""
                }
            }

            //Left Axis
            val leftAxis: YAxis = binding.barChart.axisLeft
            leftAxis.axisMinimum = 0f
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
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
    }

    private fun plotBarchart() {
        try {
            val groupSpace = 0.08f
            val barSpace = 0.06f
            val barWidth = 0.3f
            // (barWidth + barSpace) * No.Of.Bar + groupSpace = 1.00 -> interval per "group"
            val groupCount = xValues.size - 1
            val start = 1
            val values1 = ArrayList<BarEntry>()
            val values2 = ArrayList<BarEntry>()
            val values3 = ArrayList<BarEntry>()
            for (i in 0 until groupCount) {
                values1.add(BarEntry(i.toFloat(), fillRateDate[i].toFloat()))
                values2.add(BarEntry(i.toFloat(), brokenData[i].toFloat()))
                values3.add(BarEntry(i.toFloat(), freshnessData[i].toFloat()))
            }

            val set1 = BarDataSet(values1, "Fill Rate")
            set1.color = context.resources.getColor(R.color.lineChart)
            set1.valueTextColor = Color.BLACK
            set1.valueTextSize = 10f
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return DashboardUtils.decimalFormat1.format(value.toDouble()) + "%"
                }
            }

            val set2 = BarDataSet(values2, "Broken")
            set2.color = context.resources.getColor(R.color.cummulative)
            set2.valueTextColor = Color.BLACK
            set2.valueTextSize = 10f
            set2.axisDependency = YAxis.AxisDependency.LEFT
            set2.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return DashboardUtils.decimalFormat1.format(value.toDouble()) + "%"
                }
            }


            val set3 = BarDataSet(values3, "Freshness")
            set3.color = context.resources.getColor(R.color.bar3)
            set3.valueTextColor = Color.BLACK
            set3.valueTextSize = 10f
            set3.axisDependency = YAxis.AxisDependency.LEFT
            set3.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return DashboardUtils.decimalFormat1.format(value.toDouble()) + "%"
                }
            }

            val data = BarData(set1, set2, set3)
            binding.barChart.data = data
            binding.barChart.barData.barWidth = barWidth
            binding.barChart.xAxis.axisMinimum = start.toFloat()
            binding.barChart.xAxis.axisMaximum = start + binding.barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
            binding.barChart.groupBars(start.toFloat(), groupSpace, barSpace)
            binding.barChart.setDrawValueAboveBar(true)
            //binding.chart.legend.isEnabled = false
            binding.barChart.invalidate()
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
    }
}