package com.lifestyle.retail_dashboard.view.report

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityCombinedBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.DashboardUtils.*
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility.parseDatewithFormate
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandChartResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.api.BrandPerformanceApis
import com.lifestyle.retail_dashboard.view.report.charts.PlotCombineChart
import com.lifestyle.retail_dashboard.view.report.model.ProductTrends
import com.lifestyle.retail_dashboard.view.report.model.ProductTrendsData
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


class PlotingGraphActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, BrandPerformanceApis.BrandGraphListener, OnChartValueSelectedListener {

    private var intervalType: String = "1M - By Day"
    private var chartType: String = "Sale(L)"
    private lateinit var binding: ActivityCombinedBinding
    private lateinit var activity: PlotingGraphActivity
    private lateinit var plotCombineChart: PlotCombineChart

    private val xValues = mutableListOf<String>()
    private val saleData = mutableListOf<Double>()
    private val growthData = mutableListOf<Double>()
    private val budgt = mutableListOf<Double>()
    private val budgtAchvPerc = mutableListOf<Double>()
    private val saleQtyData = mutableListOf<Double>()
    private val saleQtyGrData = mutableListOf<Double>()
    private val cumulativeSalesData = mutableListOf<Double>()
    private val cumulativeSalesQtyData = mutableListOf<Double>()
    private val discountData = mutableListOf<Double>()
    private val discountGrData = mutableListOf<Double>()
    private val marginData = mutableListOf<Double>()
    private val marginGrData = mutableListOf<Double>()

    private val wSaleChartSaleList = mutableListOf<ProductTrends>()
    private val mSaleChartSaleList = mutableListOf<ProductTrends>()
    private val qSaleChartSaleList = mutableListOf<ProductTrends>()
    private val ySaleChartSaleList = mutableListOf<ProductTrends>()
    private val hSaleChartSaleList = mutableListOf<ProductTrends>()

    private var area: String? = null
    private var region: String? = null
    private var store: String? = null
    private var bu: String? = null
    private var tsfEnty: String? = null

    private var pickedDate = ""
    private var brandId: String? = null
    private var brandName: String? = null
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_combined)

        initView()
        initSaleTypeSpinner()
        initGraphTypeSpinner()
        fetchBrandPerfDetail()
    }

    @SuppressLint("InflateParams")
    private fun dynamic(data: Double) {
        val layoutInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = layoutInflater.inflate(R.layout.chart_below, null)
        val textView = v.findViewById<View>(R.id.tv) as TextView
        textView.text = floatingDecimalFormate1.format(data)
        binding.parentLayout.addView(v)
    }

    private fun initView() {
        activity = this
        plotCombineChart = PlotCombineChart(activity, binding.combinedChart)

        area = intent.getStringExtra("area")
        region = intent.getStringExtra("region")
        store = intent.getStringExtra("store")
        brandId = intent.getStringExtra("brandId")
        brandName = intent.getStringExtra("brandName")
        bu = intent.getStringExtra("bu")
        tsfEnty = intent.getStringExtra("tsfEnty")

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        pickedDate = dateFormat.format(calendar.timeInMillis)
        Log.d("Abrar", "Dates: $pickedDate")

        binding.tvEmployeeId.text = PreferenceUtils.getUserId()

        binding.tvBrandName.text = "$brandName"
        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.combinedChart.setOnChartValueSelectedListener(activity)
    }

    private fun initSaleTypeSpinner() {
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.chartTypes, R.layout.spinner_simple_text_black)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spGraphType.adapter = adapter
        binding.spGraphType.setPadding(0, 0, 0, 0)
        binding.spGraphType.post { binding.spGraphType.onItemSelectedListener = activity }
    }

    private fun initGraphTypeSpinner() {
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.options, R.layout.spinner_simple_text_black)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spOptions.adapter = adapter
        binding.spOptions.setPadding(0, 0, 0, 0)
        binding.spOptions.post { binding.spOptions.onItemSelectedListener = activity }
    }

    private fun fetchBrandPerfDetail() {
        if (area != null && region != null && store != null) {
            val fileName = "BrandPerformanceGraph_$brandName _$bu _$area _$region _$store _$pickedDate"
            Log.d("Abrar", "File: $fileName")
            file = ExternalStorageUtil.isFileExists(activity, fileName)
            if (!file?.exists()!!) {
                val api = BrandPerformanceApis(activity)
                val request = BrandPerformanceRequest()
                request.areas = area
                request.region = region
                request.store = store
                request.bu = bu
                request.tsfEnty = tsfEnty
                request.brandId = brandId
                request.reqType = "BRND"
                request.busDate = pickedDate
                api.getBrandPerfGraph(activity, request)
            } else {
                readingFile()
            }
        } else {
            binding.combinedChart.visibility = View.GONE
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.tvErrorMsg.setText("You don't have authorized to access Brand Performance")
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        binding.parentLayout.removeAllViews()
        if (parent?.id == R.id.spOptions) {
            chartType = binding.spOptions.selectedItem.toString().trim()
            updatingChart()
        } else if (parent?.id == R.id.spGraphType) {
            intervalType = binding.spGraphType.selectedItem.toString().trim()
            //PreferenceUtils.setGraphInterval(intervalType)
            updatingChart()
        }

        Log.d("Abrar", "Type: $chartType interval $intervalType")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun brandPerfGraph(response: BrandChartResponse) {
        if (response.serverErrormsg != null) {
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
            binding.combinedChart.visibility = View.GONE
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.tvErrorMsg.text = response.serverErrormsg
        } else {
            if (response.productTrendsData != null
                    && response.productTrendsData!!.isNotEmpty()) {
                savingResponse(response)
                displayingResponseData(response.productTrendsData!!)
            } else {
                binding.combinedChart.visibility = View.GONE
                binding.tvErrorMsg.visibility = View.VISIBLE
            }
        }
    }

    private fun savingResponse(response: BrandChartResponse) {
        try {
            val fileOutputStream = FileOutputStream(file)
            val o = ObjectOutputStream(fileOutputStream)
            o.writeObject(response)
            o.close()
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e.localizedMessage)
        }
    }

    private fun readingFile() {
        try {
            val fileInputStream = FileInputStream(file)
            val oi = ObjectInputStream(fileInputStream)
            val response = oi.readObject() as BrandChartResponse
            fileInputStream.close()
            if (response.productTrendsData != null)
                displayingResponseData(response.productTrendsData!!)
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e.localizedMessage)
        }
    }

    private fun displayingResponseData(productTrendsDataList: List<ProductTrendsData>) {
        binding.combinedChart.visibility = View.VISIBLE
        binding.tvErrorMsg.visibility = View.GONE
        clearResponse()

        for (productData in productTrendsDataList) {
            if (productData.productTrendsList != null) {
                for (productTrends in productData.productTrendsList!!) {
                    fetchGraphData(productTrends)
                }
            }
        }

        updatingChart()
    }

    private fun fetchGraphData(productTrends: ProductTrends) {
        when {
            productTrends.respType.equals("Y", true) -> {
                ySaleChartSaleList.add(productTrends)
            }
            productTrends.respType.equals("W", true) -> {
                wSaleChartSaleList.add(productTrends)
            }
            productTrends.respType.equals("M", true) -> {
                mSaleChartSaleList.add(productTrends)
            }
            productTrends.respType.equals("Q", true) -> {
                qSaleChartSaleList.add(productTrends)
            }
            productTrends.respType.equals("H", true) -> {
                hSaleChartSaleList.add(productTrends)
            }
        }
    }

    private fun clearResponse() {
        ySaleChartSaleList.clear()
        mSaleChartSaleList.clear()
        wSaleChartSaleList.clear()
        hSaleChartSaleList.clear()
        qSaleChartSaleList.clear()
    }

    private fun cleaData() {
        xValues.clear()
        saleData.clear()
        cumulativeSalesData.clear()
        cumulativeSalesQtyData.clear()
        growthData.clear()
        budgt.clear()
        budgtAchvPerc.clear()
        saleQtyData.clear()
        discountData.clear()
        marginData.clear()
    }

    private fun updatingChart() {
        binding.tvTitle.text = chartType
        binding.tvChartValue.text = ""
        try {
            fetchData(intervalType)
            if (xValues.size >= 1)
                ploatingChart()
            else
                binding.combinedChart.clear()
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e + " msg " + e.localizedMessage)
        }
    }

    private fun fetchData(intervalType: String) {
        cleaData()
        when {
            intervalType.equals("1W - By Day", ignoreCase = true) -> {
                for (productTrends in wSaleChartSaleList) {
                    settingChartData(productTrends)
                }
            }
            intervalType.equals("1M - By Day", ignoreCase = true) -> {
                for (productTrends in mSaleChartSaleList) {
                    settingChartData(productTrends)
                }
            }
            intervalType.equals("1Q - By Week", ignoreCase = true) -> {
                for (productTrends in qSaleChartSaleList) {
                    settingChartData(productTrends)
                }
            }
            intervalType.equals("1H - By Week", ignoreCase = true) -> {
                for (productTrends in hSaleChartSaleList) {
                    settingChartData(productTrends)
                }
            }
            intervalType.equals("1Y - By Month", ignoreCase = true) -> {
                for (productTrends in ySaleChartSaleList) {
                    settingChartData(productTrends)
                }
            }
            else -> {
                binding.combinedChart.clear()
            }
        }

        var sum = 0.0
        for (i in saleData.indices) {
            dynamic(saleData[i])
            sum += saleData[i]
            cumulativeSalesData.add(sum)
        }
        var sumQty = 0.0
        for (i in saleQtyData.indices) {
            dynamic(saleQtyData[i])
            sumQty += saleQtyData[i]
            cumulativeSalesQtyData.add(sumQty)
        }
    }

    private fun settingChartData(productTrends: ProductTrends) {
        //val calendar = Calendar.getInstance()
        //Log.d("Abrar", "Day ${calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())}")
        xValues.add(parseDatewithFormate(productTrends.busDate, "yyyyMMdd"))
        saleData.add(productTrends.sales / LACS)
        growthData.add(productTrends.slsGrowthPerc * 100)
        budgtAchvPerc.add(productTrends.budgtAchvPerc)
        budgt.add(productTrends.budgt / LACS)
        saleQtyData.add(productTrends.slsQty)
        saleQtyGrData.add(productTrends.slsQtyGrowthPerc)
        discountData.add(productTrends.slsDscPerc)
        discountGrData.add(productTrends.slsDscGrowthPerc)
        marginData.add(productTrends.slsMrgPerc)
        marginGrData.add(productTrends.slsMrgGrowthPerc)
    }

    private fun ploatingChart() {
        Log.d("Abrar", "Ach: $budgtAchvPerc")
        try {
            plotCombineChart.plotChart(xValues,chartType)
            val rightMax = growthData.indexOf(Collections.max(growthData))
            var maxIndex  = 0.0f
            var minIndex = 0.0f
            val dataSets = ArrayList<ILineDataSet>()
            val lineData = LineData(dataSets)
            val combinedData = CombinedData()
            var barData = BarData()
            //val barDateLists = ArrayList<BarDataSet>()
            combinedData.setData(lineData)
            when {
                chartType.equals("Sale(L)", true) -> {
                    //plotCombineChart.generateLineChart("Sale(L)", saleData, "left", resources.getColor(R.color.green))?.let { dataSets.add(it) }
                    //plotCombineChart.generateLineChart("Cumulative Sale(L)", cumulativeSalesData, "left", resources.getColor(R.color.cummulative))?.let { dataSets.add(it) }
                    //barData = plotCombineChart.generateBarChart("Sale(L)", saleData, "left", resources.getColor(R.color.colorAccent))
                    //barData = plotCombineChart.generateBarChart("Sale(L)", cumulativeSalesData, "left", resources.getColor(R.color.cummulative))
                    barData = plotCombineChart.generateStackBarChart("Sale(L)", saleData, resources.getColor(R.color.colorAccent), "Cumulative Sale(L)", cumulativeSalesData, resources.getColor(R.color.cummulative), "left")
                    plotCombineChart.generateLineChart("Growth%", growthData, "right", resources.getColor(R.color.green))?.let { dataSets.add(it) }
                    //combinedData.setData(plotCombineChart.generateCandleData("Growth", growthData, "right"))
                    //combinedData.setData(barData)
                    maxIndex = (cumulativeSalesData[cumulativeSalesData.size - 1] * 1.25).toFloat()
                    minIndex = (-6 * abs(growthData[rightMax])).toFloat()
                }
                chartType.equals("Sale Qty", true) -> {
                    //plotCombineChart.generateLineChart(chartType, saleQtyData, "left", resources.getColor(R.color.green))?.let { dataSets.add(it) }
                    //plotCombineChart.generateLineChart("Cumulative Sale Qty", cumulativeSalesQtyData, "left", resources.getColor(R.color.cummulative))?.let { dataSets.add(it) }
                    barData = plotCombineChart.generateStackBarChart("Sale Qty", saleQtyData, resources.getColor(R.color.lineChart), "Cumulative Sale Qty", cumulativeSalesQtyData, resources.getColor(R.color.cummulative), "left")
                    plotCombineChart.generateLineChart("Growth%", growthData, "right", resources.getColor(R.color.green))?.let { dataSets.add(it) }
                    //combinedData.setData(plotCombineChart.generateCandleData("Growth", growthData, "right"))
                    maxIndex = (cumulativeSalesQtyData[cumulativeSalesQtyData.size - 1]  * 1.25).toFloat()
                    minIndex = (-6 * abs(growthData[rightMax])).toFloat()
                }
                chartType.equals("Budget Ach", true) -> {
                    plotCombineChart.generateLineChart("$chartType%", budgtAchvPerc, "left", resources.getColor(R.color.green))?.let { dataSets.add(it) }
                    barData = plotCombineChart.generateStackBarChart("Budget(L)", budgt, resources.getColor(R.color.lineChart), "Sale(L)", saleData, resources.getColor(R.color.cummulative), "right")
                    val right = budgtAchvPerc.indexOf(Collections.max(budgtAchvPerc))
                    val left = budgtAchvPerc.indexOf(Collections.min(budgtAchvPerc))
                    minIndex =  0.0f
                    maxIndex = budgtAchvPerc[right].toFloat()
                    //minIndex = budgtAchvPerc[right].toFloat()
                    //maxIndex = budgtAchvPerc[left].toFloat()

                    Log.d("Abrar","Left $maxIndex right: $minIndex")
                    //plotCombineChart.generateBarChart("Plan Sale", saleData, "left")?.let { barData = it}
                }
            }

            combinedData.setData(barData)
            val xAxis: XAxis = binding.combinedChart.xAxis
            xAxis.spaceMin = barData.barWidth / 2f
            xAxis.spaceMax = barData.barWidth / 2f


            binding.combinedChart.axisLeft.axisMaximum = maxIndex
            binding.combinedChart.axisRight.axisMinimum = minIndex

            binding.combinedChart.clear()
            binding.combinedChart.data = combinedData
            binding.combinedChart.setDrawValueAboveBar(true)
            binding.combinedChart.invalidate()

            //Log.d("Abrar", "Growth: $growthData")
            //Log.d("Abrar", "Right Axis: " + rightMax + " Cum: " + -1 * Math.abs(growthData[rightMax]))
        } catch (e: Exception) {
            Log.d("Abrar", "Exceptions: " + e.localizedMessage)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        binding.tvChartValue.visibility = View.VISIBLE
        if (e != null) {
            Log.d("Abrar", "X: ${e.x}")
            when (e) {
                is CandleEntry ->
                    binding.tvChartValue.text = " Growth on " + xValues[(e.getX() - 0.5).toInt()] + " is " + floatingDecimalFormate1.format(e.high * 100) + "%"
                is BarEntry -> {
                    if (chartType.equals("Sale Qty", true))
                        binding.tvChartValue.text = " on " + xValues[(e.x - 0.5).roundToInt()] + " is " + decimalFormat1.format(e.y)
                    else
                        binding.tvChartValue.text = " on " + xValues[(e.x - 0.5).roundToInt()] + " is " + floatingDecimalFormate1.format(e.y)+ "L"
                }
                else ->
                    if (chartType.equals("Budget Ach", true))
                        binding.tvChartValue.text = " on ${xValues[(e.x - 0.5).toInt()]} is ${decimalFormat1.format(e.y)}%"
                    else
                        binding.tvChartValue.text = "Growth on ${xValues[(e.x - 0.5).toInt()]} is ${decimalFormat1.format(e.y)}%"
            }
        }
    }

    override fun onNothingSelected() {
        binding.tvChartValue.visibility = View.INVISIBLE
    }
}