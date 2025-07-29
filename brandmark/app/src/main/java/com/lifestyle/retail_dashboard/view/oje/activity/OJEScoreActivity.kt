package com.lifestyle.retail_dashboard.view.oje.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityOjeScoreBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeDetail
import com.lifestyle.retail_dashboard.view.attendance.model.StoreList
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListRequest
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.oje.adapter.OJEScoreListAdapter
import com.lifestyle.retail_dashboard.view.oje.model.OJEList
import com.lifestyle.retail_dashboard.view.oje.model.OJERequest
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.oje.model.OJEScore
import com.lifestyle.retail_dashboard.view.oje.model.api.OJEApis
import java.util.*


class OJEScoreActivity : AppCompatActivity(), OJEApis.OJEListListener,
        AttendanceApis.StoreListListener, OJEList.OnOJEReviewListener, OJEApis.OJEUpdateListener, AttendanceApis.BrandEmployeeListListener, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityOjeScoreBinding
    private lateinit var activity: OJEScoreActivity
    private val regionList = mutableListOf<String>()
    private val storeList = mutableListOf<String>()
    private val employeeList = mutableListOf<String>()
    private val brandNameList = mutableListOf<String>()
    private var region: String? = null
    private var storeId: String? = null
    private var employee: String? = null
    private var brandId: String? = null
    private var conceptId: String? = null
    private val employeeDetailList = mutableListOf<EmployeeDetail>()
    private val storeDetailList = mutableListOf<StoreList>()
    private var api: OJEApis? = null
    private val ojeQuesionList = mutableListOf<OJEList>()
    private var bmReview = "N"
    private var employeeAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_oje_score)

        initView()
        dataInitialization()
        brandList()
        getOJEList()
        getConceptList()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        api = OJEApis(activity)

        binding.toolbarLayout.tvToolBarTitle.text = "OJE Score"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE


        binding.swBMReview.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                bmReview = "Y"
            else
                bmReview = "N"
        }

        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun brandList() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                brands.brandName?.let { brandNameList.add(it) }
            }
        }
        brandNameList.add(0, "Choose Brand")
        //setDropdown(binding.spLmeb, brandNameList, "Brand", "Brand")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spLmeb.adapter = adapter
        binding.spLmeb.setPadding(0, 0, 0, 0)
        binding.spLmeb.setSelection(0, false)
        binding.spLmeb.onItemSelectedListener = activity
    }

    private fun dataInitialization() {
        clearData()
        //preparedDataList()

        //setDropdown(binding.spEmployeeList, employeeList, "Brand Staff", "Employee")
        employeeAdapter = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, employeeList)

        employeeAdapter?.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spEmployeeList.adapter = employeeAdapter
        binding.spEmployeeList.setPadding(0, 0, 0, 0)
        binding.spEmployeeList.setSelection(0, false)
        binding.spEmployeeList.onItemSelectedListener = activity
    }

    private fun getConceptList() {
        val conceptList: ArrayList<String> = arrayListOf()
        conceptList.addAll(resources.getStringArray(R.array.concept).toList())
        conceptList.add(0, "Choose Concept")

        //setDropdown(binding.spConcept, conceptList, "Choose Concept", "Concept")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, conceptList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spConcept.adapter = adapter
        binding.spConcept.setPadding(0, 0, 0, 0)
        binding.spConcept.setSelection(0, false)
        binding.spConcept.onItemSelectedListener = activity
    }


    private fun preparedDataList() {
        regionList.add("East")
        regionList.add("West")
        regionList.add("North")
        regionList.add("South")

        employeeList.add("1063807 - Krishna")
        employeeList.add("1063694 - Sajid")
        employeeList.add("1000002 - Madhvan")
    }

    private fun clearData() {
        regionList.clear()
        employeeList.clear()

        regionList.add(0, "Region")
        employeeList.add(0, "Choose Brand Staff")
    }

    private fun getStoreList() {
        storeDetailList.clear()
        storeList.clear()
        val request = StoreListRequest()
        request.ouCode = "3"
        val api = AttendanceApis(activity)
        api.getStoreList(activity, request)
    }

    override fun storeList(response: StoreListResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            if (response.regionList != null) {
                for (regions in response.regionList) {
                    if (regions.districtList != null) {
                        for (state in regions.districtList) {
                            if (state.storeList != null) {
                                for (store in state.storeList) {
                                    //store.region = regions.region
                                    //storeDetailList.add(store)

                                    if (store.storeName!!.contains("VENUE SALE",true))
                                        Log.d("Abrar","Found")
                                    else{
                                        store.region = regions.region
                                        storeDetailList.add(store)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            storeDetailList.sortBy { it.storeName }
            val st = StoreList()
            st.storeName = "Choose Store"
            st.storeId = "0"
            st.region = "0"
            storeDetailList.add(0, st)

            for (store in storeDetailList) {
                storeList.add("${store.storeName}")
            }

            //setDropdown(binding.spStoreList, storeList, "Store", "Store")

            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                    R.layout.spinner_simple_text_black, storeList)

            adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            binding.spStoreList.adapter = adapter
            binding.spStoreList.setPadding(0, 0, 0, 0)
            binding.spStoreList.setSelection(0, false)
            binding.spStoreList.onItemSelectedListener = activity

            binding.detailLayout.visibility = View.VISIBLE

        }
    }


    private fun getEmployeeList(brandId: String?) {
        if (brandId != null) {
            employeeList.clear()
            employeeDetailList.clear()
            val request = BrandPerformanceRequest()
            request.areas = "-1"
            request.region = "-1"
            request.store = "-1"
            request.bu = "LS"
            request.tsfEnty = "82"
            request.brandId = brandId
            val brandApi = AttendanceApis(activity)
            brandApi.getBrandUserDetail(activity, request)
        }
    }

    override fun employeeList(response: com.lifestyle.retail_dashboard.view.attendance.model.EmployeeListResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            if (response.brandUsrDetailsList.size > 0) {
                employeeDetailList.addAll(response.brandUsrDetailsList)
            }

            employeeDetailList.sortBy { it.fName }
            for (employee in employeeDetailList) {
                employeeList.add("${employee.usrId} - ${employee.fName} ${employee.lName}")
            }

            employeeList.add(0, "Choose Brand Staff")
            //setDropdown(binding.spEmployeeList, employeeList, "Brand Staff", "Employee")

           /* val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                    R.layout.spinner_simple_text_black, employeeList)

            adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            binding.spEmployeeList.adapter = adapter
            binding.spEmployeeList.setPadding(0, 0, 0, 0)
            binding.spEmployeeList.setSelection(0, false)
            binding.spEmployeeList.onItemSelectedListener = activity*/
            employeeAdapter?.notifyDataSetChanged()
        }
    }

    override fun onItemSelected(prnt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(prnt?.id == R.id.spLmeb) {
            val selectedItem = binding.spLmeb.selectedItem.toString()
            if (selectedItem.equals("Choose Brand")){
                brandId = null
                CommonUtility.showSnackBar(activity,"Choose Brand")
            }else {
                val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
                if (brandPerDetail != null) {
                    for (brands in brandPerDetail) {
                        if (brands.brandName.equals(selectedItem)) {
                            brands.brandId?.let { brandId = it }
                            break
                        }
                    }
                    getEmployeeList(brandId)
                }
            }
        }
        else if(prnt?.id == R.id.spConcept) {
            val selectedItem = binding.spConcept.selectedItem.toString()
            if (selectedItem.equals("Choose Concept")){
                conceptId = null
                CommonUtility.showSnackBar(activity,"Choose Concept")
            }else {
                if (selectedItem.contains(" : ")) {
                    val concepts = selectedItem.split(" : ")
                    conceptId = concepts[1]
                }
            }
            Log.d("Abrar", "concept Id: $conceptId")
        }else if(prnt?.id == R.id.spStoreList) {
            val selectedItem = binding.spStoreList.selectedItem.toString()
            for (store in storeDetailList) {
                if (store.storeName.equals(selectedItem, true)) {
                    storeId = store.storeId
                    break
                }
            }

            Log.d("Abrar", "Store Id: $storeId")
        }
        else if(prnt?.id == R.id.spEmployeeList) {
            val selectedItem = binding.spEmployeeList.selectedItem.toString()
            if (selectedItem.equals("Choose Brand Staff",true)){
                employee = null
            }else
                employee = selectedItem
            Log.d("Abrar", "Employee Selected: $employee")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    /*override fun callBack(view: View, position: Int, componentName: String, result: String) {
        if (componentName.equals("Region", true)) {
            if (position == 0)
                CommonUtility.showSnackBar(activity, "Choose Region")
            else
                region = result
        } else if (componentName.equals("Store", true)) {
            if (position == 0)
                CommonUtility.showSnackBar(activity, "Choose Store")
            else {
                for (store in storeDetailList) {
                    if (store.storeName.equals(result, true)) {
                        storeId = store.storeId
                        break
                    }
                }

                Log.d("Abrar", "Store Id: $storeId")
            }
        } else if (componentName.equals("Employee", true)) {
            if (position == 0)
                CommonUtility.showSnackBar(activity, "Choose Employee")
            else
                employee = result
        } else if (componentName.equals("Brand", true)) {
            if (position == 0)
                CommonUtility.showSnackBar(activity, "Choose Brand")
            else {
                val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
                if (brandPerDetail != null) {
                    for (brands in brandPerDetail) {
                        if (brands.brandName.equals(result)) {
                            brands.brandId?.let { brandId = it }
                            break
                        }
                    }
                }
                Log.d("Abrar", "Brand $result Brand Id: $brandId")
                getEmployeeList(brandId)
            }
        } else if (componentName.equals("Concept", true)) {
            if (position == 0)
                CommonUtility.showSnackBar(activity, "Choose Concept")
            else {
                if (result.contains(" : ")) {
                    val concepts = result.split(" : ")
                    conceptId = concepts[1]
                }
            }
            Log.d("Abrar", "concept Id: $conceptId")
        }
    }*/

    private fun submit() {
        val remark = binding.etRemark.text.toString()
        val conceptName = binding.etConcptName.text.toString()
        if (storeId == null)
            CommonUtility.showSnackBar(activity, "Choose Store")
        else if (conceptId == null)
            CommonUtility.showSnackBar(activity, "Choose Concept")
        else if (employee == null)
            CommonUtility.showSnackBar(activity, "Choose Brand Staff")
        else if (brandId == null)
            CommonUtility.showSnackBar(activity, "Choose Brand")
        else if (conceptName.isEmpty())
            CommonUtility.showSnackBar(activity, "Enter Concept Manager Name")
        else if (remark.isEmpty())
            CommonUtility.showSnackBar(activity, "Enter Remark")
        else {
            var userId = ""
            var userName = ""
            if (employee != null && employee!!.contains(" - ")) {
                val emplyeeDetail = employee!!.split(" - ")
                userId = emplyeeDetail[0]
                userName = emplyeeDetail[1]
            }

            Log.d("Abrar", "UserId:$userId UserName:$userName")

            val ojeScoreList = mutableListOf<OJEScore>()
            for (oje in ojeQuesionList) {
                val OjeScore = OJEScore()
                OjeScore.ojeId = oje.ojeId
                OjeScore.ojeScore = oje.ojeScore
                ojeScoreList.add(OjeScore)
            }

            val request = OJERequest()
            request.storeId = storeId
            request.createdUser = PreferenceUtils.getUserId()
            request.brndId = brandId
            request.concept = conceptId
            request.empId = userId
            request.lsConceptMgr = conceptName
            request.remarks = remark
            request.createdBy = "BRND"
            request.bmReview = bmReview
            request.ojeScoreList = ojeScoreList
            api?.updateOJEScore(activity, request)
        }
    }

    override fun ojeUpdate(response: OJEResponse) {
        if (response.statusMessage.equals("Y", true)) {
            CommonUtility.showSnackBar(activity, "Score Updated Successfully")
        } else
            CommonUtility.showSnackBar(activity, response.serverErrormsg)

        Handler().postDelayed({ activity.finish() }, 600)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getOJEList() {
        ojeQuesionList.clear()
        api?.getOJEList(activity)
    }

    override fun getOJEList(ojeScoreList: List<OJEList>?) {
        if (ojeScoreList != null && ojeScoreList.isNotEmpty()) {
            binding.ojectRecyclerview.layoutManager = LinearLayoutManager(activity)
            val adapter = OJEScoreListAdapter(ojeScoreList, activity)
            binding.ojectRecyclerview.adapter = adapter
            ojeQuesionList.addAll(ojeScoreList)
            getStoreList()
        }
    }

    override fun onItemCheck(position: Int, item: OJEList) {
        ojeQuesionList.set(position, item)
    }
}