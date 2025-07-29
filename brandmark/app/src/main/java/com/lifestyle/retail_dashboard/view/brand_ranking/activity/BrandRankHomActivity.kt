package com.lifestyle.retail_dashboard.view.brand_ranking.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityBrandRankHomBinding
import com.lifestyle.retail_dashboard.utils.*
import com.lifestyle.retail_dashboard.view.brand_ranking.adpter.BrandRankingAdapter
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankDTO
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankData
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.api.BrandPerformanceApis
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.SingleRACDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.GetRetailAuditAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACList
import java.io.*
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class BrandRankHomActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, BrandPerformanceApis.BrandRankListener {
    private lateinit var activity: BrandRankHomActivity
    private lateinit var binding: ActivityBrandRankHomBinding
    private var subTitle = ""
    private var area: String? = null
    private var region: String? = null
    private var store: String? = null
    private var bu: String? = null
    private var tsfEnty: String? = null
    private var brandId: String? = null
    private var brandName: String? = null
    private var file: File? = null
    private var pickedDate:String? = null
    private val brandPerDetail = mutableListOf<BrandPerfUserDetail>()
    private val brandRankList = mutableListOf<BrandRankData>()
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    private val df = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    //BackUp
    private val myDataBackup = LinkedHashMap<String, MutableList<BrandRankData>>()
    private var sum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_brand_rank_hom)

        intiView()
        getBrandList()
    }

    private fun intiView() {
        activity = this

        binding.toolbarLayout.tvTitle.text = "Brand Ranking"
        setSubtitle()

        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DATE, -1)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        pickedDate = dateFormat.format(calendar.timeInMillis)
        binding.btnDatePick.text = df.format(calendar.timeInMillis)

        binding.tvEmployeeId.text = PreferenceUtils.getUserId()

        binding.toolbarLayout.btnHome.setOnClickListener {
            binding.toolbarLayout.btnHome.visibility = View.GONE
            itemSelected()
        }

        binding.toolbarLayout.btnDrillDown.visibility = View.INVISIBLE

        binding.toolbarLayout.btnDrillDown.setOnClickListener {
            binding.toolbarLayout.btnHome.visibility = View.VISIBLE
            val api = apiRequestValidation()
            if (api.equals("call", true)) {
                setSubtitle()
                getBrandRankList()
            } else
                CommonUtility.showSnackBar(activity, "No Drill down")
        }


        binding.toolbarLayout.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnDatePick.setOnClickListener {
            val dp = DatePickerDialog(activity, { datePicker, year, monthOfYear, dayOfMonth ->
                try {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    pickedDate = dateFormat.format(calendar.timeInMillis)
                    binding.btnDatePick.text = df.format(calendar.timeInMillis)
                    myDataBackup.clear()
                    getBrandRankList()
                }catch (e: Exception){
                    Log.d("Abrar","Exception: ${e.localizedMessage}")
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))

            dp.datePicker.maxDate = maxDate.timeInMillis
            dp.show()
        }
    }

    private fun itemSelected() {
        myDataBackup.clear()
        val brandName = binding.spBrand.selectedItem.toString()
        if (brandName.equals("Choose Brand", true)) {
            brandId = null
            CommonUtility.showSnackBar(activity, "Choose Brand")
        } else {
            for (brands in brandPerDetail) {
                if (brands.brandName.equals(brandName, true)) {
                    initAccess(brands)
                    break
                }
            }
        }
    }

    private fun getBrandList() {
        val brandNameList = mutableListOf<String>()
        brandPerDetail.addAll(PreferenceUtils.getBrandPerfDetail())
        for (brands in brandPerDetail) {
            brands.brandName?.let { brandNameList.add(it) }
        }
        brandNameList.add(0, "Choose Brand")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spBrand.adapter = adapter
        binding.spBrand.setPadding(0, 0, 0, 0)
        binding.spBrand.setSelection(0, false)
        binding.spBrand.onItemSelectedListener = activity
    }

    private fun setSubtitle(){
        binding.toolbarLayout.tvSubtitle.text = subTitle
    }

    private fun apiRequestValidation(): String? {
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

    private fun initAccess(brands: BrandPerfUserDetail) {
        myDataBackup.clear()
        area = brands.areaNum
        region = brands.rgnNum
        store = brands.locNum
        bu = brands.bu
        tsfEnty = brands.tsfEnt
        brandId = brands.brandId
        brandName = brands.brandName

        getBrandRankList()
    }

    private fun getBrandRankList() {
        if (area.equals("-1",true)
                && region.equals("-1",true)
                && store.equals("-1",true)){
            binding.toolbarLayout.btnDrillDown.visibility = View.VISIBLE
        }else
            binding.toolbarLayout.btnDrillDown.visibility = View.INVISIBLE

        brandRankList.clear()
        if (area != null && region != null && store != null) {
            val fileName = "BrandRanking_${brandId}_${bu}_${area}_${region}_${store}_$pickedDate"
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
                //request.storeId = store
                api.getBrandRankList(activity, request)
            } else {
                readingFile()
            }
        } else
            showError("You don't have authorized to access Brand Ranking")
    }

    override fun brandRank(response: BrandRankResponse) {
        if (response.serverErrormsg != null) {
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
            showError(response.serverErrormsg!!)
        }
        else{
            if (response.brandRankData != null &&
                    response.brandRankData.size > 0){
                sum++
                myDataBackup["list $sum"] = response.brandRankData
                savingResponse(response.brandRankData)
                displayingResponseData(response.brandRankData)
            }else
                showError("No Data Found!!!")
        }
    }

    private fun showError(errorMsg: String) {
        binding.tvErrorMsg.visibility = View.VISIBLE
        binding.spConcept.visibility = View.GONE
        binding.dataLayout.visibility = View.GONE
        binding.tvErrorMsg.text = errorMsg


        myDataBackup["Error$sum"] = brandRankList

    }

    private fun readingFile() {
        try {
            val fileInputStream = FileInputStream(file)
            val oi = ObjectInputStream(fileInputStream)
            val response = oi.readObject() as MutableList<BrandRankData>
            fileInputStream.close()
            sum++
            myDataBackup["list $sum"] = response
            displayingResponseData(response)
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e.localizedMessage)
        }
    }

    private fun savingResponse(response: MutableList<BrandRankData>) {
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

    private fun displayingResponseData(response: MutableList<BrandRankData>) {
        binding.dataLayout.visibility = View.VISIBLE
        binding.tvErrorMsg.visibility = View.GONE
        binding.spConcept.visibility = View.VISIBLE
        brandRankList.addAll(response)

        area = brandRankList[brandRankList.size -1].areaNum.toString()
        region = brandRankList[brandRankList.size -1].rgnNum.toString()
        store = brandRankList[brandRankList.size -1].locNum.toString()

        settingSubtitle()

        val conceptList = mutableListOf<String>()
        val groupName = mutableSetOf<String>()
        for (brandRank in brandRankList){
            groupName.add(brandRank.grpNm.toString())
        }

        when {
            subTitle.equals("Region List",true) -> binding.etSearch.hint = "Search By Region Name"
            subTitle.equals("State List",true) -> binding.etSearch.hint = "Search By State Name"
            subTitle.equals("Store List",true) -> binding.etSearch.hint = "Search By Store Name"
        }

        conceptList.addAll(groupName)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, conceptList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spConcept.adapter = adapter
        binding.spConcept.setPadding(0, 0, 0, 0)
        binding.spConcept.onItemSelectedListener = activity
    }

    fun onItemSelected(brandRank : BrandRankData){
        binding.toolbarLayout.btnHome.visibility = View.VISIBLE
        Log.d("Abrar","On Clicked ${Gson().toJson(brandRank)}")
        area = "${brandRank.areaNum}"
        region = "${brandRank.rgnNum}"
        store = "${brandRank.locNum}"
        Log.d("Abrar", "On Clicked Area: $area Region: $region Store: $store")

        val api = apiRequestValidation()
        Log.d("Abrar", "Drill Down Area: $area Region: $region Store: $store")
        if (api.equals("call", true)) {
            setSubtitle()
            getBrandRankList()
        } else
            CommonUtility.showSnackBar(activity, "No Drill down")
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spBrand) {
            itemSelected()
        }else if (prt?.id == R.id.spConcept) {
            setBrandRankAdapter(binding.spConcept.selectedItem.toString())
        }
    }

    private fun setBrandRankAdapter(selectedItem: String) {
        val perticulerBrandRankList = mutableListOf<BrandRankData>()
        for (brandRank in brandRankList){
            if (brandRank.grpNm != null){
                if (brandRank.grpNm.equals(selectedItem,true)){
                    perticulerBrandRankList.add(brandRank)
                }
            }
        }

        binding.etSearch.text.clear()
        Utility.hideKeyboard(activity)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        val mAdapter = BrandRankingAdapter(activity,selectedItem,perticulerBrandRankList)
        binding.recyclerView.adapter = mAdapter

        if (perticulerBrandRankList.size > 1)
            binding.etSearch.visibility = View.VISIBLE
        else
            binding.etSearch.visibility = View.GONE

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mAdapter.filter.filter(s.toString().toLowerCase(Locale.getDefault()))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun settingSubtitle() {
        if (area != null && region != null && store != null) {
            if (area.equals("-1", ignoreCase = true)
                    && region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "PAN India"
            } else if (!area.equals("-1", ignoreCase = true)
                    && region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "Region List"
            } else if (!area.equals("-1", ignoreCase = true)
                    && !region.equals("-1", ignoreCase = true)
                    && store.equals("-1", ignoreCase = true)) {
                subTitle = "State List"
            } else if (!area.equals("-1", ignoreCase = true)
                    && !region.equals("-1", ignoreCase = true)
                    && !store.equals("-1", ignoreCase = true)) {
                subTitle = "Store List"
            }
            setSubtitle()
        }
    }

    private fun getLastKey(): String? {
        val entryList: List<Map.Entry<String, MutableList<BrandRankData>>> = ArrayList<Map.Entry<String, MutableList<BrandRankData>>>(myDataBackup.entries)
        return entryList[entryList.size - 1].key
    }

    private fun getLastValue(): MutableList<BrandRankData>? {
        val entryList: List<Map.Entry<String, MutableList<BrandRankData>>> = ArrayList<Map.Entry<String, MutableList<BrandRankData>>>(myDataBackup.entries)
        return entryList[entryList.size - 1].value
    }

    private fun getSavedData(key: String?, response: MutableList<BrandRankData>?) {
        if (key != null && response != null) {
            brandRankList.clear()
            displayingResponseData(response)
        }
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
        sum = 0
        myDataBackup.clear()
        super.onDestroy()
    }
}