package com.lifestyle.retail_dashboard.view.brandperformance.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityBrandPerformanceBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil.deleteLogFile
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.brandperformance.adapter.SectionsPagerAdapter
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.api.BrandPerformanceApis
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.lifestyle.retail_dashboard.view.report.PlotingGraphActivity
import java.io.*
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class BrandPerformanceActivity : AppCompatActivity(), SendDetailData, BrandPerformanceApis.BrandPerformanceDetailListener, AdapterView.OnItemSelectedListener {
    private var area: String? = null
    private var region: String? = null
    private var store: String? = null
    private var bu: String? = null
    private var tsfEnty: String? = null
    private var subTitle = ""
    private var menuVisible = true
    private var pickedDate = "20201020"
    private var brandId: String? = null
    private var brandName: String? = null
    private var file: File? = null
    private lateinit var activity: BrandPerformanceActivity
    private lateinit var binding: ActivityBrandPerformanceBinding
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null
    private val brandNameList =  mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    private val df = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val myDataBackup = LinkedHashMap<String, BrandPerformanceResponse>()
    private var sum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brand_performance)

        initView()
        getAccessData()
        initDropDown()
    }

    private fun initDropDown() {
        Log.d("Abrar", "DropDown $brandNameList")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spBrandName.adapter = adapter
        binding.spBrandName.setPadding(0, 0, 0, 0)
        binding.spBrandName.onItemSelectedListener = activity
    }

    private fun getAccessData() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                brands.brandName?.let { brandNameList.add(it) }
            }
        } else
            CommonUtility.showSnackBar(activity, "You Don't have access to Brand Performance")
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {
        activity = this

        binding.tvTitle.text = "Brand Performance"
        binding.tvSubtitle.text = subTitle

        sectionsPagerAdapter = SectionsPagerAdapter(activity, supportFragmentManager, "", mutableListOf())
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DATE, -1)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        pickedDate = dateFormat.format(calendar.timeInMillis)
        val dateSelected = df.format(calendar.timeInMillis)
        binding.btnDatePick.text = dateSelected
        PreferenceUtils.setBPCalender(calendar)
        Log.d("Abrar", "Previous: ${calendar.timeInMillis}")

        binding.tvEmployeeId.text = PreferenceUtils.getUserId()

        binding.btnHome.setOnClickListener {
            //myDataBackup.clear()
            itemSelected()
            binding.btnHome.visibility = View.GONE
        }

        binding.btnDatePick.setOnClickListener {
            val dp = DatePickerDialog(activity, { datePicker, year, monthOfYear, dayOfMonth ->
                try {

                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    pickedDate = dateFormat.format(calendar.timeInMillis)
                    binding.btnDatePick.text = df.format(calendar.timeInMillis)
                    Log.d("Abrar", "Selected: ${calendar.timeInMillis}")
                    PreferenceUtils.setBPCalender(calendar)
                    myDataBackup.clear()
                    fetchBrandPerfDetail()
                }catch (e: Exception){
                    Log.d("Abrar","Exception: ${e.localizedMessage}")
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))

            dp.datePicker.maxDate = maxDate.timeInMillis
            dp.show()
        }


        binding.btnGraph.setOnClickListener {
            val intent = Intent(activity, PlotingGraphActivity::class.java)
            intent.putExtra("area", area)
            intent.putExtra("region", region)
            intent.putExtra("store", store)
            intent.putExtra("brandId", brandId)
            intent.putExtra("brandName", brandName)
            intent.putExtra("bu", bu)
            intent.putExtra("tsfEnty", tsfEnty)
            startActivity(intent)
        }


        binding.btnDrillDown.setOnClickListener {
            binding.btnHome.visibility = View.VISIBLE
            val api = apiRequestValidation()
            if (api.equals("call", true)) {
                binding.tvSubtitle.text = subTitle
                fetchBrandPerfDetail()
                menuVisible = false
                invalidateOptionsMenu()
            } else
                CommonUtility.showSnackBar(activity, "No Drill down")
        }


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }
    
    private fun fetchBrandPerfDetail() {
        if (area != null && region != null && store != null) {
            val fileName = "BrandPerformance" + "_" +  brandId + "_" +bu + "_" + area + "_" + region + "_" + store + "_" + pickedDate
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
                api.getBrandPerformance(activity, request)
            } else {
                readingFile()
            }
        } else
            showError("You don't have authorized to access Brand Performance")
    }

    private fun showError(errorMsg: String) {
        menuVisible = false
        invalidateOptionsMenu()
        binding.tvSubtitle.visibility = View.GONE
        binding.tabs.visibility = View.GONE
        binding.viewPager.visibility = View.GONE
        binding.tvErrorMsg.visibility = View.VISIBLE
        binding.tvErrorMsg.text = errorMsg

    }

    override fun brandPerformance(response: BrandPerformanceResponse) {
        if (response.serverErrormsg != null) {
            showError(response.serverErrormsg!!)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        } else if (response.productDashboard.isNotEmpty()) {
            response.reqArea = area
            response.reqRegion = region
            response.reqStore = store
            savingResponse(response)
            displayingResponseData(response)
        } else
            showError("No Data Found!!!")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_brand_perf, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuFilter = menu.findItem(R.id.action_access_filter)
        val menuHide = menu.findItem(R.id.action_hide)
        if (menuVisible) {
            menuFilter.isVisible = true
            menuHide.isVisible = false
        } else {
            menuFilter.isVisible = false
            menuHide.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_access_filter -> {
                binding.btnHome.visibility = View.VISIBLE
                val api = apiRequestValidation()
                if (api.equals("call", true)) {
                    binding.tvSubtitle.text = subTitle
                    fetchBrandPerfDetail()
                    menuVisible = false
                    invalidateOptionsMenu()
                } else
                    CommonUtility.showSnackBar(activity, "No Drill down")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun apiRequestValidation(): String? {
        Log.d("Abrar", "Area: $area Region: $region Store: $store")
        val apiCalled: String
        if (area.equals("-1", ignoreCase = true)
                && region.equals("-1", ignoreCase = true)
                && store.equals("-1", ignoreCase = true)) {
            area = "0"
            apiCalled = "call"
            subTitle = "Region List"
        } else if (!area.equals("-1", ignoreCase = true)
                && region.equals("-1", ignoreCase = true)
                && store.equals("-1", ignoreCase = true)) {
            region = "0"
            apiCalled = "call"
            subTitle = "State List"
        } else if (!area.equals("-1", ignoreCase = true)
                && !region.equals("-1", ignoreCase = true)
                && store.equals("-1", ignoreCase = true)) {
            store = "0"
            apiCalled = "call"
            subTitle = "Store List"
        } else {
            apiCalled = "NoCall"
        }
        return apiCalled
    }

    private fun settingSubtitle() {
        if (area != null && region != null && store != null) {
            if (area.equals("-1", ignoreCase = true) && region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "PAN India"
            } else if (!area.equals("-1", ignoreCase = true) && region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "Region List"
            } else if (!area.equals("-1", ignoreCase = true) && !region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "State List"
            } else if (!area.equals("-1", ignoreCase = true) && !region.equals("-1", ignoreCase = true)
                    && !store.equals("-1", ignoreCase = true)) {
                subTitle = "Store List"
            }
            binding.tvSubtitle.text = subTitle
        }
    }

    override fun sendData(brands: BrandPerformanceList) {
        menuVisible = true
        binding.btnDrillDown.visibility = View.VISIBLE
        invalidateOptionsMenu()

        if (area.equals("0", ignoreCase = true) || area?.contains(",")!!) {
            area = brands.areaNum
            subTitle = brands.areaNm!!
        }

        if (region.equals("0", ignoreCase = true) || region?.contains(",")!!) {
            region = brands.rgnNum
            subTitle = brands.rgnNm!!
        }

        if (store.equals("0", ignoreCase = true) || store?.contains(",")!!) {
            store = brands.locNum
            subTitle = brands.locNm.toString()
        }

        val selectedBrandPerList = mutableListOf<BrandPerformanceList>()
        selectedBrandPerList.add(brands)
        sum += 1
        binding.spinnerLayout.visibility = View.VISIBLE
        val response = BrandPerformanceResponse()
        response.productDashboard = selectedBrandPerList
        response.reqArea = area
        response.reqRegion = region
        response.reqStore = store
        myDataBackup["detail $sum"] = response
        sectionsPagerAdapter?.updateBrandPerfList("detail", selectedBrandPerList)
        binding.tvSubtitle.text = subTitle
        binding.btnGraph.visibility = View.VISIBLE
        binding.btnDatePick.visibility = View.VISIBLE
        binding.tvLblLac.visibility = View.GONE
    }

    private fun savingResponse(response: BrandPerformanceResponse) {
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
            val response = oi.readObject() as BrandPerformanceResponse
            fileInputStream.close()
            displayingResponseData(response)
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e.localizedMessage)
        }
    }

    private fun displayingResponseData(response: BrandPerformanceResponse) {
        binding.tvErrorMsg.visibility = View.GONE
        binding.tabs.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE
        binding.tvSubtitle.visibility = View.VISIBLE

        //settingSubtitle()
        if (area.equals("-1") && region.equals("-1") && store.equals("-1"))
            subTitle = "PAN India"

        sum += 1

        if (response.productDashboard.size == 1) {
            binding.spinnerLayout.visibility = View.VISIBLE
            menuVisible = true
            binding.btnDrillDown.visibility = View.VISIBLE
            invalidateOptionsMenu()
            Log.d("Abrar", "Title:${response.productDashboard[0].areaNm}  ${response.productDashboard[0].rgnNm}  ${response.productDashboard[0].locNm}")

            if (area.equals("-1") && region.equals("-1") && store.equals("-1"))
                subTitle = "PAN India"

            if (response.productDashboard[0].areaNm != null)
                subTitle = response.productDashboard[0].areaNm.toString()

            if (response.productDashboard[0].rgnNm != null)
                subTitle = response.productDashboard[0].rgnNm.toString()

            if (response.productDashboard[0].locNm != null)
                subTitle = response.productDashboard[0].locNm.toString()

            binding.tvSubtitle.text = subTitle

            myDataBackup["detail $sum"] = response
            sectionsPagerAdapter?.updateBrandPerfList("detail", response.productDashboard)
            binding.btnGraph.visibility = View.VISIBLE
            binding.btnDatePick.visibility = View.VISIBLE
            binding.tvLblLac.visibility = View.GONE
        } else {
            binding.btnDrillDown.visibility = View.INVISIBLE
            menuVisible = false
            invalidateOptionsMenu()

            settingSubtitle()
            myDataBackup["list $sum"] = response
            sectionsPagerAdapter?.updateBrandPerfList("list", response.productDashboard)
            binding.btnGraph.visibility = View.GONE
            binding.btnDatePick.visibility = View.GONE
            binding.tvLblLac.visibility = View.VISIBLE
        }
    }

    private fun getLastKey(): String? {
        val entryList: List<Map.Entry<String, BrandPerformanceResponse>> = ArrayList<Map.Entry<String, BrandPerformanceResponse>>(myDataBackup.entries)
        return entryList[entryList.size - 1].key
    }

    private fun getLastValue(): BrandPerformanceResponse? {
        val entryList: List<Map.Entry<String, BrandPerformanceResponse>> = ArrayList<Map.Entry<String, BrandPerformanceResponse>>(myDataBackup.entries)
        return entryList[entryList.size - 1].value
    }

    private fun getSavedData(key: String?, response: BrandPerformanceResponse?) {
        if (key != null && response != null) {
            //Log.d("Abrar","Back ${Gson().toJson(response)}")
            /*val brands = response[response.size-1]
            Log.d("Abrar","Area: ${brands.areaNm} Region: ${brands.rgnNm} Store: ${brands.locNm}")
            area = brands.areaNum
            region = brands.rgnNum
            store = brands.locNum*/


            Log.d("Abrar", "Area ${response.reqArea} Region ${response.reqRegion} Store ${response.reqStore}")
            area = response.reqArea
            region = response.reqRegion
            store = response.reqStore
            val brandPerList = response.productDashboard

            if (key.contains("detail")) {
                binding.btnDrillDown.visibility = View.VISIBLE
                binding.btnGraph.visibility = View.VISIBLE
                binding.btnDatePick.visibility = View.VISIBLE
                binding.tvLblLac.visibility = View.GONE
                val brands = brandPerList[brandPerList.size - 1]

                if (brands.areaNm == null && brands.rgnNm == null
                        && brands.locNm == null)
                    subTitle = "PAN India"
                if (brands.areaNm != null)
                    subTitle = brands.areaNm!!
                if (brands.rgnNm != null)
                    subTitle = brands.rgnNm!!
                if (brands.locNm != null)
                    subTitle = brands.locNm!!

                binding.tvSubtitle.text = subTitle
                menuVisible = true
                sectionsPagerAdapter?.updateBrandPerfList("detail", brandPerList)
            } else {
                binding.btnDrillDown.visibility = View.INVISIBLE
                binding.btnGraph.visibility = View.GONE
                binding.btnDatePick.visibility = View.GONE
                binding.tvLblLac.visibility = View.VISIBLE
                Log.d("Abrar", "List Size: ${brandPerList.size}")
                menuVisible = false
                Log.d("Abrar", "Before Area:$area Region:$region Store:$store")
                //val brands = response[response.size - 1]
                //area = brands.areaNum
                //region = brands.rgnNum
                //store = brands.locNum
                //Log.d("Abrar","After Area:$area Region:$region Store:$store")
                settingSubtitle()
                sectionsPagerAdapter?.updateBrandPerfList("list", brandPerList)
            }

            Log.d("Abrar", "MenuVisible $menuVisible")
            invalidateOptionsMenu()
        }


        /*if (key?.contains("detail")!!){
            if (response != null) {
                menuVisible = true
                invalidateOptionsMenu()
                val brands = response[response.size-1]
                Log.d("Abrar","Area: ${brands.areaNm} Region: ${brands.rgnNm} Store: ${brands.locNm}")
                if (area.equals("0", ignoreCase = true) || area?.contains(",")!!) {
                    area = brands.areaNum!!
                    subTitle = brands.areaNm!!
                }
                else if (region.equals("0", ignoreCase = true) || region?.contains(",")!!) {
                    region = brands.rgnNum!!
                    subTitle = brands.rgnNm!!
                }
                else if (store.equals("0", ignoreCase = true) || store?.contains(",")!!) {
                    store = brands.locNum!!
                    subTitle = brands.locNm!!
                }

                binding.toolbarLayout.tvToolBarSubtitle.text = subTitle
                sectionsPagerAdapter?.updateBrandPerfList("detail", response)
            }
        }else{
            if (response != null) {
                menuVisible = false
                invalidateOptionsMenu()
                settingSubtitle()
                sectionsPagerAdapter?.updateBrandPerfList("list", response)
            }
        }*/
    }

    override fun onBackPressed() {
        if (myDataBackup.size > 1) {
            myDataBackup.remove(getLastKey())
            if (myDataBackup.isNotEmpty()) {
                getSavedData(getLastKey(), getLastValue())
            }
        } else
            super.onBackPressed()
    }

    override fun onDestroy() {
        PreferenceUtils.setBPCalender(null)
        sum = 0
        myDataBackup.clear()
        super.onDestroy()
    }

    private fun initalizingAccess(brands: BrandPerfUserDetail) {
        area = brands.areaNum
        region = brands.rgnNum
        store = brands.locNum
        bu = brands.bu
        tsfEnty = brands.tsfEnt
        brandId = brands.brandId
        brandName = brands.brandName
    }

    private fun itemSelected(){
        Log.d("Abrar", "Selected: ${binding.spBrandName.selectedItem}")
        myDataBackup.clear()
        val selectedItem = binding.spBrandName.selectedItem.toString()
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                if (brands.brandName.equals(selectedItem)){
                    initalizingAccess(brands)
                    break
                }
            }
        }
        fetchBrandPerfDetail()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        itemSelected()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}

interface SendDetailData {
    fun sendData(brands: BrandPerformanceList)
}