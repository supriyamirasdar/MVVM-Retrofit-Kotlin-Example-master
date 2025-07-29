package com.lifestyle.retail_dashboard.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityAttendanceHomeBinding
import com.lifestyle.retail_dashboard.databinding.ActivityBarchartBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.DashboardUtils
import java.util.*

class BarChartActivityMultiDataset : AppCompatActivity(), OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityBarchartBinding
    private val xValues = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barchart)

        intiView()
        getConceptList()
        plotBarchart()
    }

    private fun getConceptList() {
        val  conceptList:ArrayList<String> = arrayListOf()
        conceptList.addAll(resources.getStringArray(R.array.concept).toList())
        conceptList.add(0,"Choose Concept")
        //setDropdown(binding.spConcept, conceptList, "Choose Concept", "Concept")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, conceptList)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spConcept.adapter = adapter
        binding.spConcept.setPadding(0, 0, 0, 0)
        binding.spConcept.setSelection(0,false)
        binding.spConcept.onItemSelectedListener = this
    }

    private fun intiView() {
        xValues.add("FTD")
        xValues.add("WTD")
        xValues.add("MTD")
        xValues.add("STD")
        xValues.add("YTD")


        binding.chart.setOnChartValueSelectedListener(this)
        binding.chart.description.isEnabled = false
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawGridBackground(false)

        val l: Legend = binding.chart.legend
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val leftAxis: YAxis = binding.chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DashboardUtils.decimalFormat1.format(value.toDouble())
            }
        }

        val rightAxs: YAxis = binding.chart.axisRight
        rightAxs.isEnabled = false

        val xAxis: XAxis = binding.chart.xAxis
        xAxis.axisMinimum = 0f
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xValues[value.toInt() % xValues.size]
            }
        }
    }

    private fun plotBarchart() {
        val groupSpace = 0.08f
        val barSpace = 0.06f // x4 DataSet
        val barWidth = 0.4f // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        val groupCount = xValues.size
        val start = 1
        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()
        /*val values3 = ArrayList<BarEntry>()
        val values4 = ArrayList<BarEntry>()
        val values5 = ArrayList<BarEntry>()*/
        val randomMultiplier = 45
        for (i in 0 until xValues.size) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values2.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            //values3.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            //values4.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            //values5.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
        }

        val set1 = BarDataSet(values1, "Company A")
        set1.color = resources.getColor(R.color.lineChart)
        set1.valueTextColor = Color.BLACK
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DashboardUtils.decimalFormat1.format(value.toDouble())
            }
        }

        val set2 = BarDataSet(values2, "Company B")
        set2.color = resources.getColor(R.color.cummulative)
        set2.valueTextColor = Color.BLACK
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT
        set2.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DashboardUtils.decimalFormat1.format(value.toDouble())
            }
        }

        val data = BarData(set1, set2)
        binding.chart.data = data
        binding.chart.barData.barWidth = barWidth
        binding.chart.xAxis.axisMinimum = start.toFloat()
        binding.chart.xAxis.axisMaximum = start + binding.chart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
        binding.chart.groupBars(start.toFloat(), groupSpace, barSpace)
        binding.chart.setDrawValueAboveBar(true)
        //binding.chart.legend.isEnabled = false
        binding.chart.invalidate()
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.d("Abrar", "Selected: " + e.toString() + ", dataSet: " + h.dataSetIndex)
    }

    override fun onNothingSelected() {
        Log.d("Abrar", "Nothing selected.")
    }

    override fun onItemSelected(prnt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(prnt?.id == R.id.spConcept) {
            val selectedItem = binding.spConcept.selectedItem.toString()
            Log.d("Abrar","Concept $selectedItem")
            plotBarchart()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}