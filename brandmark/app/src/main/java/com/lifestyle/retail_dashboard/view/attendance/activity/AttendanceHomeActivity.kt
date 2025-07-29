package com.lifestyle.retail_dashboard.view.attendance.activity

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityAttendanceHomeBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.DashboardUtils
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.attendance.DB.DatabaseClient
import com.lifestyle.retail_dashboard.view.attendance.DB.Task
import com.lifestyle.retail_dashboard.view.attendance.model.*
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import java.text.SimpleDateFormat
import java.util.*


class AttendanceHomeActivity : AppCompatActivity(), AttendanceApis.StoreListListener, AdapterView.OnItemSelectedListener, AttendanceApis.BrandEmployeeListListener, AttendanceApis.AttendanceGraphListener {
    private lateinit var binding: ActivityAttendanceHomeBinding
    private lateinit var activity: AttendanceHomeActivity
    //private lateinit var plotCombineChart: PlotCombineChart
    private lateinit var api: AttendanceApis
    private val saleData = mutableListOf<Double>()
    private val xValues = mutableListOf<String>()
    private var databaseInstance: DatabaseClient? = null
    private var area: String? = null
    private var region: String? = null
    private var store: String? = null
    private var bu: String? = null
    private var tsfEnty: String? = null
    private var subTitle = ""
    private var brandId: String? = null
    private var brandName: String? = null
    private var toDate: String? = null
    private var fromDate: String? = null
    private val brandNameList = mutableListOf<String>()
    //private val marginGrData = mutableListOf<Double>()
    //private val marginGrData = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance_home)

        initView()
        //getStoreList()
        getAccessData()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this
        api = AttendanceApis(activity)

        //plotCombineChart = PlotCombineChart(activity, binding.combinedChart)

        binding.toolbarLayout.tvToolBarTitle.text = "Attendance"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        binding.root.setOnClickListener {
            Utility.goToNextScreen(this, AttendanceActivity::class.java)
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("yy", Locale.US)
        Log.d("Abrar", "Date ${dateFormat.format(calendar.timeInMillis)}")
        toDate = "01-Apr-${dateFormat.format(calendar.timeInMillis)}"
        val from = dateFormat.format(calendar.timeInMillis).toInt() + 1
        fromDate = "31-Mar-$from"
    }

    private fun getStoreList() {
        val request = StoreListRequest()
        request.ouCode = "3"
        api.getStoreList(activity, request)
    }

    private fun getAccessData() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                brands.brandName?.let { brandNameList.add(it) }
            }
        }

        initDropDown()
    }

    private fun initDropDown() {
        Log.d("Abrar", "DropDown $brandNameList")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spBrands.adapter = adapter
        binding.spBrands.setPadding(0, 0, 0, 0)
        binding.spBrands.onItemSelectedListener = activity
    }


    override fun storeList(response: StoreListResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            val storeList = mutableListOf<StoreList>()
            if (response.regionList != null)
                for (regions in response.regionList)
                    if (regions.districtList != null)
                        for (state in regions.districtList)
                            if (state.storeList != null)
                                for (store in state.storeList) {
                                    if (store.storeName!!.contains("VENUE SALE",true))
                                        Log.d("Abrar","Found")
                                    else{
                                        store.region = regions.region
                                        storeList.add(store)
                                    }
                                }

            savedinDB(storeList)
            Log.d("Abrar", "Stores ${Gson().toJson(storeList)}")
        }
    }

    private fun savedinDB(storeList: MutableList<StoreList>) {
        class GetTasks : AsyncTask<Void?, Void?, String?>() {
            override fun doInBackground(vararg voids: Void?): String? {
                databaseInstance = DatabaseClient.getInstance(activity)

                for (listofStore in storeList) {
                    val store = Task(listofStore.storeId!!, listofStore.storeName!!, listofStore.region!!, "store")
                    databaseInstance
                            ?.appDatabase
                            ?.taskDao()
                            ?.insert(store)
                }

                return "done"
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d("Abrar", "Saved $result")
                if (result != null && result.isNotEmpty()) {
                    PreferenceUtils.setIsStoreListFound(true)
                }
            }
        }

        val gt = GetTasks()
        gt.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_filter -> {
                Utility.goToNextScreen(activity, FilterAttendanceListActivity::class.java)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /*private fun ploatingChart() {
        Log.d("Abrar","X size: ${xValues.size}");
        try {
            if (xValues.size >= 1) {
                plotCombineChart.plotChart(xValues)
                val dataSets = ArrayList<ILineDataSet>()
                val lineData = LineData(dataSets)
                val combinedData = CombinedData()
                combinedData.setData(lineData)

                //plotCombineChart.generateLineChart("", saleData, "left", resources.getColor(R.color.colorAccent))?.let { dataSets.add(it) }
                combinedData.setData(plotCombineChart.generateBarChart("Employee", saleData, "left", resources.getColor(R.color.cummulative)))
                binding.combinedChart.clear()
                binding.combinedChart.data = combinedData
                binding.combinedChart.setDrawValueAboveBar(false)

                binding.combinedChart.invalidate()
            }
            //else
            //    CommonUtility.showSnackBar(activity,"At-least 2 data required to plot the graph")
        } catch (e: Exception) {
            Log.d("Abrar", "Exceptions: " + e.localizedMessage)
        }
    }*/


    private fun ploatingChart() {
        binding.combinedChart.description.isEnabled = false
        binding.combinedChart.setTouchEnabled(true)
        binding.combinedChart.isDragEnabled = true
        binding.combinedChart.setScaleEnabled(true)
        binding.combinedChart.setPinchZoom(true)
        binding.combinedChart.setDrawGridBackground(false)
        binding.combinedChart.setDrawGridBackground(false)
        binding.combinedChart.setDrawBarShadow(false)
        binding.combinedChart.isHighlightFullBarEnabled = false
        binding.combinedChart.animateX(600)

        val l: Legend = binding.combinedChart.legend
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val leftAxis: YAxis = binding.combinedChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DashboardUtils.decimalFormat1.format(value.toDouble())
            }
        }

        val rightAxs: YAxis = binding.combinedChart.axisRight
        rightAxs.isEnabled = false

        val xAxis: XAxis = binding.combinedChart.xAxis
        //xAxis.position = XAxisPosition.BOTH_SIDED
        xAxis.axisMinimum = 0f
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xValues[value.toInt() % xValues.size]
            }
        }

        //val data = CombinedData()
        val barData = generateBarData()
        //data.setData(barData)

        //xAxis.axisMaximum = data.xMax + 0.25f

        val x: XAxis = binding.combinedChart.xAxis
        x.spaceMin = barData.barWidth / 2f
        x.spaceMax = barData.barWidth / 2f

        binding.combinedChart.data = barData
        binding.combinedChart.invalidate()

        //val high = Highlight(5f, 5f, 4)
        //high.dataIndex = 7
        //binding.combinedChart.highlightValue(high, false)

    }

    private fun generateBarData(): BarData {
        val entries1 = ArrayList<BarEntry>()
        //val entries2 = ArrayList<BarEntry>()
        for (index in 0 until xValues.size) {
            entries1.add(BarEntry(index + 0.5f, saleData.get(index).toFloat()))

            // stacked
            //entries2.add(BarEntry(0f, floatArrayOf(getRandom(13f, 12f), getRandom(13f, 12f))))
        }
        val set1 = BarDataSet(entries1, "Employee")
        set1.color = resources.getColor(R.color.blue)
        set1.valueTextColor = Color.BLACK
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DashboardUtils.decimalFormat1.format(value.toDouble())
            }
        }


        /*val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
        set2.valueTextColor = Color.WHITE
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT*/
        //val groupSpace = 0.06f
        //val barSpace = 0.02f // x2 dataset
        val barWidth = 0.8f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        val d = BarData(set1)
        d.barWidth = barWidth

        // make this BarData object grouped
        //d.groupBars(0f, groupSpace, barSpace) // start at x = 0
        return d
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedItem = binding.spBrands.selectedItem.toString()
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                if (brands.brandName.equals(selectedItem)) {
                    area = brands.areaNum
                    region = brands.rgnNum
                    store = brands.locNum
                    brandId = brands.brandId
                    brandName = brands.brandName
                    bu = brands.bu
                    tsfEnty = brands.tsfEnt
                    break
                }
            }
            PreferenceUtils.setAttendanceBrandId(brandId);
            getEmployeeList()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getEmployeeList() {
        val request = BrandPerformanceRequest()
        request.areas = area
        request.region = region
        request.store = store
        request.bu = bu
        request.tsfEnty = tsfEnty
        request.brandId = brandId
        api.getBrandUserDetail(activity, request)
    }

    override fun employeeList(response: EmployeeListResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            binding.tvTotalEmplyee.text = "${response.brandUsrDetailsList?.size}"
            PreferenceUtils.setEmpAttendanceDetail(response.brandUsrDetailsList)
            getAttedanceGraph()
        }
    }

    private fun getAttedanceGraph() {
        xValues.clear()
        saleData.clear()
        val request = AttendanceRequest()
        request.toDate = toDate
        request.fromDate = fromDate
        api.getAttendanceGraphData(activity, request)
    }

    override fun onDestroy() {
        PreferenceUtils.setSavedFilter(null)
        PreferenceUtils.setEmpAttendanceDetail(null)
        PreferenceUtils.setAttendanceBrandId(null);
        super.onDestroy()
    }

    override fun attendanceGraph(response: AttendanceGraphResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else{
            Log.d("Abrar", "Graph ${Gson().toJson(response.brandStaffAttReprtList)}")
            if (response.brandStaffAttReprtList != null) {
                for (graph in response.brandStaffAttReprtList) {
                    if (graph.month != null) {
                        xValues.add(graph.month)
                        saleData.add(graph.usrCount)
                    }
                }
                ploatingChart()
            }
            val isStoreListFound = PreferenceUtils.isStoreListFound()
            Log.d("Abrar", "isStoreListFound $isStoreListFound")
            if (!isStoreListFound)
                getStoreList()
        }
    }
}


/*

Collections.sort(yourTasksList, new Comparator<Task>() {

    @Override
    public int compare(Task o1, Task o2) {
        try {
            return new SimpleDateFormat("DD/MM/yyyy").parse(o1.getDate().toString()).compareTo(new SimpleDateFormat("DD/MM/yyyy").parse(o2.getDate().toString()));
        } catch (ParseException e) {
            return 0;
        }
    }
});*/
