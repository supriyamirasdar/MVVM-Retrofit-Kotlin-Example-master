package com.lifestyle.retail_dashboard.view.inventory_managment.activity

//import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityInventoryManagementBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.api.BrandPerformanceApis
import com.lifestyle.retail_dashboard.view.inventory_managment.adapter.InvetoryManagementAdapter
import com.lifestyle.retail_dashboard.view.inventory_managment.model.InventoryManagement
import com.lifestyle.retail_dashboard.view.inventory_managment.model.InventoryManagementResponse
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class InventoryManagementActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
        BrandPerformanceApis.InventoryManagementListener, DatePickerDialog.OnDateSetListener {
    private lateinit var activity: InventoryManagementActivity
    private lateinit var binding: ActivityInventoryManagementBinding
    private var subTitle = ""
    private var area: String? = null
    private var region: String? = null
    private var store: String? = null
    private var bu: String? = null
    private var tsfEnty: String? = null
    private var brandId: String? = null
    private var brandName: String? = null
    private var file: File? = null
    private var pickedDate: String? = null
    private val brandPerDetail = mutableListOf<BrandPerfUserDetail>()
    private val inventoryManagList = mutableListOf<InventoryManagement>()
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
    private val df = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    //BackUp
    private val myDataBackup = LinkedHashMap<String, MutableList<InventoryManagement>>()
    private var sum = 0
    val MAX_SELECTABLE_DATE_IN_FUTURE = 365 // 365 days into the future max
    val MIN_SELECTABLE_DATE_IN_FUTURE = 3650
    private var dpd: DatePickerDialog? = null
    var selyear: Int = 0
    var selmonthOfYear: Int = 0
    var seldayOfMonth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_management)

        intiView()
        getBrandList()
    }

    private fun intiView() {
        activity = this

        binding.toolbarLayout.tvTitle.text = "Inventory Management"
        setSubtitle()

        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DATE, -1)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        //pickedDate = dateFormat.format(calendar.timeInMillis)
        //binding.btnDatePick.text = df.format(calendar.timeInMillis)

        var dayCount = 0;
        val lastSunday = Calendar.getInstance()
        for (i in 7 downTo 1) {
            if (lastSunday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                val d = lastSunday.clone() as Calendar
                // weekdays.add(d)
                break;
            } else {
                dayCount++
            }
            lastSunday.add(Calendar.DATE, -1)
        }
        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.DATE, -dayCount)
        pickedDate = dateFormat.format(calendar2.timeInMillis)
        binding.btnDatePick.text = df.format(calendar2.timeInMillis)

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
                getInventoryManagmentList()
            } else
                CommonUtility.showSnackBar(activity, "No Drill down")
        }


        binding.toolbarLayout.btnBack.setOnClickListener {
            onBackPressed()
        }

        /*binding.btnDatePick.setOnClickListener {
            val dp = DatePickerDialog(activity, { datePicker, year, monthOfYear, dayOfMonth ->
                try {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    pickedDate = dateFormat.format(calendar.timeInMillis)
                    binding.btnDatePick.text = df.format(calendar.timeInMillis)
                    myDataBackup.clear()
                    getInventoryManagmentList()
                } catch (e: Exception) {
                    Log.d("Abrar", "Exception: ${e.localizedMessage}")
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))


            val weekdays = ArrayList<Calendar>()
            val day = Calendar.getInstance()
            for (i in 0 until MAX_SELECTABLE_DATE_IN_FUTURE) {
                if (day[Calendar.DAY_OF_WEEK] !== Calendar.SATURDAY && day[Calendar.DAY_OF_WEEK] !== Calendar.MONDAY
                        && day[Calendar.DAY_OF_WEEK] !== Calendar.TUESDAY && day[Calendar.DAY_OF_WEEK] !== Calendar.WEDNESDAY
                        && day[Calendar.DAY_OF_WEEK] !== Calendar.THURSDAY && day[Calendar.DAY_OF_WEEK] !== Calendar.FRIDAY) {
                    val d = day.clone() as Calendar
                    weekdays.add(d)
                }
                day.add(Calendar.DATE, 1)
            }
            val weekdayDays: Array<Calendar> = weekdays.toArray(arrayOfNulls(weekdays.size))
           // dp.datePicker.setSelectableDays(weekdayDays)
            dp.setSelectedDays(weekdayDays)
            dp.datePicker.maxDate = maxDate.timeInMillis
            dp.show()
        }*/

        binding.btnDatePick.setOnClickListener {
            val now = Calendar.getInstance()
            if (dpd == null) {
                dpd = DatePickerDialog.newInstance(
                        activity!!,
                        now[Calendar.YEAR],
                        now[Calendar.MONTH],
                        now[Calendar.DAY_OF_MONTH]
                )
            } else {
                dpd!!.initialize(activity,
                        now[Calendar.YEAR],
                        now[Calendar.MONTH],
                        now[Calendar.DAY_OF_MONTH]
                )
                /*if (seldayOfMonth == 0) {
                    dpd!!.initialize(activity,
                            now[Calendar.YEAR],
                            now[Calendar.MONTH],
                            now[Calendar.DAY_OF_MONTH]
                    )
                } else {
                    dpd!!.initialize(activity,
                            now[selyear],
                            now[selmonthOfYear],
                            now[seldayOfMonth]
                    )
                }*/
            }

            // restrict to sunday only

            val weekdays = ArrayList<Calendar>()
            val day = Calendar.getInstance()
            for (i in MIN_SELECTABLE_DATE_IN_FUTURE downTo 1) {
                if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    val d = day.clone() as Calendar
                    weekdays.add(d)
                }
                day.add(Calendar.DATE, -1)
            }
            val weekdayDays: Array<Calendar> = weekdays.toArray(arrayOfNulls(weekdays.size))
            dpd?.setSelectableDays(weekdayDays)

            dpd?.setOnCancelListener { dialog ->
                Log.d("DatePickerDialog", "Dialog was cancelled")
                dpd = null
            }

            dpd?.show(activity.supportFragmentManager, "Datepickerdialog")
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

    private fun setSubtitle() {
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

        getInventoryManagmentList()
    }

    private fun getInventoryManagmentList() {
        if (area.equals("-1", true)
                && region.equals("-1", true)
                && store.equals("-1", true)) {
            binding.toolbarLayout.btnDrillDown.visibility = View.VISIBLE
        } else
            binding.toolbarLayout.btnDrillDown.visibility = View.INVISIBLE

        inventoryManagList.clear()


        if (brandId != null) {
            if (area != null && region != null && store != null) {
                val fileName = "InventoryManagement_${brandId}_${bu}_${area}_${region}_${store}_$pickedDate"
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
                    request.reqType = "Brndapp"
                    //request.busDate = "20191214"
                    request.busDate = pickedDate
                    //request.storeId = store
                    api.getInventoryManagement(activity, request)
                } else {
                    readingFile()
                }
            } else
                showError("You don't have authorized to access Brand Ranking")
        } else {
            CommonUtility.showSnackBar(activity, "Choose Brand")
        }
    }

    override fun inventoryManagement(response: InventoryManagementResponse) {
        if (response.serverErrormsg != null) {
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
            showError(response.serverErrormsg!!)
        } else {
            if (response.brandAppInvntData != null &&
                    response.brandAppInvntData.size > 0) {
                sum++
                myDataBackup["list $sum"] = response.brandAppInvntData
                savingResponse(response.brandAppInvntData)
                displayingResponseData(response.brandAppInvntData)
            } else
                showError("No Data Found!!!")
        }
    }

    private fun showError(errorMsg: String) {
        binding.tvErrorMsg.visibility = View.VISIBLE
        binding.dataLayout.visibility = View.GONE
        binding.tvErrorMsg.text = errorMsg

        myDataBackup["Error$sum"] = inventoryManagList
    }

    private fun readingFile() {
        try {
            val fileInputStream = FileInputStream(file)
            val oi = ObjectInputStream(fileInputStream)
            val response = oi.readObject() as MutableList<InventoryManagement>
            fileInputStream.close()
            sum++
            myDataBackup["list $sum"] = response
            displayingResponseData(response)
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: " + e.localizedMessage)
        }
    }

    private fun savingResponse(response: MutableList<InventoryManagement>) {
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

    private fun displayingResponseData(response: MutableList<InventoryManagement>) {
        binding.dataLayout.visibility = View.VISIBLE
        binding.tvErrorMsg.visibility = View.GONE
        inventoryManagList.addAll(response)

        area = inventoryManagList[inventoryManagList.size - 1].areaNum.toString()
        region = inventoryManagList[inventoryManagList.size - 1].rgnNum.toString()
        store = inventoryManagList[inventoryManagList.size - 1].locNum.toString()
        settingSubtitle()

        when {
            subTitle.equals("Region List", true) -> binding.etSearch.hint = "Search By Region Name"
            subTitle.equals("State List", true) -> binding.etSearch.hint = "Search By State Name"
            subTitle.equals("Store List", true) -> binding.etSearch.hint = "Search By Store Name"
        }

        binding.etSearch.text.clear()
        Utility.hideKeyboard(activity)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        val mAdapter = InvetoryManagementAdapter(activity, inventoryManagList)
        binding.recyclerView.adapter = mAdapter

        if (inventoryManagList.size > 1)
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

    fun onItemSelected(brandRank: InventoryManagement) {
        binding.toolbarLayout.btnHome.visibility = View.VISIBLE
        Log.d("Abrar", "On Clicked ${Gson().toJson(brandRank)}")
        area = "${brandRank.areaNum}"
        region = "${brandRank.rgnNum}"
        store = "${brandRank.locNum}"
        Log.d("Abrar", "On Clicked Area: $area Region: $region Store: $store")

        val api = apiRequestValidation()
        Log.d("Abrar", "Drill Down Area: $area Region: $region Store: $store")
        if (api.equals("call", true)) {
            setSubtitle()
            getInventoryManagmentList()
        } else
            CommonUtility.showSnackBar(activity, "No Drill down")
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spBrand) {
            itemSelected()
        }/*else if (prt?.id == R.id.spConcept) {
            setBrandRankAdapter(binding.spConcept.selectedItem.toString())
        }*/
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

    fun onNormsClicked() {
        Utility.goToNextScreen(activity, NormsActivity::class.java)
    }

    private fun getLastKey(): String? {
        val entryList: List<Map.Entry<String, MutableList<InventoryManagement>>> = ArrayList<Map.Entry<String, MutableList<InventoryManagement>>>(myDataBackup.entries)
        return entryList[entryList.size - 1].key
    }

    private fun getLastValue(): MutableList<InventoryManagement>? {
        val entryList: List<Map.Entry<String, MutableList<InventoryManagement>>> = ArrayList<Map.Entry<String, MutableList<InventoryManagement>>>(myDataBackup.entries)
        return entryList[entryList.size - 1].value
    }

    private fun getSavedData(key: String?, response: MutableList<InventoryManagement>?) {
        if (key != null && response != null) {
            Log.d("Abrar", "My BackUp: $key")
            inventoryManagList.clear()
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

    override fun onResume() {
        super.onResume()
        activity = this
        val now = Calendar.getInstance()
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    activity!!,
                    now[Calendar.YEAR],
                    now[Calendar.MONTH],
                    now[Calendar.DAY_OF_MONTH]
            )
        } else {
            dpd!!.initialize(
                    activity,
                    now[Calendar.YEAR],
                    now[Calendar.MONTH],
                    now[Calendar.DAY_OF_MONTH]
            )
        }
        //  dpd = activity!!.supportFragmentManager!!.findFragmentByTag("Datepickerdialog") as DatePickerDialog
        if (dpd != null) dpd?.onDateSetListener = this
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        //val date = "You picked the following date: " + dayOfMonth.toString() + "/" + (++monthOfYear).toString() + "/" + year
        try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selyear = year;
            selmonthOfYear = monthOfYear + 1;
            seldayOfMonth = dayOfMonth;
            pickedDate = dateFormat.format(calendar.timeInMillis)
            binding.btnDatePick.text = df.format(calendar.timeInMillis)
            myDataBackup.clear()
            getInventoryManagmentList()
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
        dpd = null
    }
}