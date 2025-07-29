package com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityRetailAuditCheckListNewBinding
import com.lifestyle.retail_dashboard.databinding.SingleAuditDetailBinding
import com.lifestyle.retail_dashboard.utils.*
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil.writeToFile
import com.lifestyle.retail_dashboard.view.attendance.model.StoreList
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListRequest
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.oje.model.api.OJEApis
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.RacBackstoreAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.RacBusinessAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.RacStandardAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.NewRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.RetailAudit
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api.RACApis
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CreateNewRACActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, AttendanceApis.StoreListListener,
        OJEApis.OJEUpdateListener {
    private lateinit var binding: ActivityRetailAuditCheckListNewBinding
    private lateinit var activity: CreateNewRACActivity
    private val brandPerDetail = mutableListOf<BrandPerfUserDetail>()
    private val storeDetailList = mutableListOf<StoreList>()
    private val storeList = mutableListOf<String>()
    private var brandId: String? = null
    private var storeId: String? = null
    private var retailAudit: RetailAudit? = null
    private var imageFileName: String? = null
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val displayStandardList = mutableListOf<RetailAudit>()
    private val businessCMList = mutableListOf<RetailAudit>()
    private val backStoreList = mutableListOf<RetailAudit>()
    private var isRefresh = false
    private var isFirstTimeStore = 1
    private lateinit var racStandardAdapter: RacStandardAdapter
    private lateinit var racBusinessAdapter: RacBusinessAdapter
    private lateinit var racBackstoreAdapter: RacBackstoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_retail_audit_check_list_new)

        initView()
        getStoreList()
        getBrandList()
        val getRAC = PreferenceUtils.getRACDetail()
        if (getRAC != null && getRAC.size > 0)
            getRACDetailList(getRAC)
        else
            addingsavedRAC()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        binding.toolbarLayout.tvToolBarTitle.text = "New Retail Audit"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE


        binding.LblDisplayStand.setOnClickListener {
            if (binding.rvDisplayStandart.visibility == View.VISIBLE) {
                binding.rvDisplayStandart.visibility = View.GONE
                binding.LblDisplayStand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black, 0)
            } else {
                binding.rvDisplayStandart.visibility = View.VISIBLE
                binding.LblDisplayStand.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0)
            }
        }

        binding.LblBusinessCM.setOnClickListener {
            if (binding.rvBusinessCM.visibility == View.VISIBLE) {
                binding.rvBusinessCM.visibility = View.GONE
                binding.LblBusinessCM.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black, 0)
            } else {
                binding.rvBusinessCM.visibility = View.VISIBLE
                binding.LblBusinessCM.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0)
            }
        }


        binding.LblStaff.setOnClickListener {
            if (binding.rvBackStore.visibility == View.VISIBLE) {
                binding.rvBackStore.visibility = View.GONE
                binding.LblStaff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_black, 0)
            } else {
                binding.rvBackStore.visibility = View.VISIBLE
                binding.LblStaff.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0)
            }
        }

        binding.btnSubmit.setOnClickListener {
            submitRetailAudit()
        }
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
        if (response.serverErrormsg != null) {
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
            binding.tvErrorMsg.text = response.serverErrormsg
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.dataLayout.visibility = View.GONE
        } else {
            binding.tvErrorMsg.visibility = View.GONE
            binding.dataLayout.visibility = View.VISIBLE
            if (response.regionList != null) {
                for (regions in response.regionList) {
                    if (regions.districtList != null) {
                        for (state in regions.districtList) {
                            if (state.storeList != null) {
                                for (store in state.storeList) {
                                    //store.region = regions.region
                                    //storeDetailList.add(store)
                                    if (store.storeName!!.contains("VENUE SALE", true))
                                        Log.d("Abrar", "Found")
                                    else {
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
            st.storeName = resources.getString(R.string.choose_store)
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
            binding.spStoreName.adapter = adapter
            binding.spStoreName.setPadding(0, 0, 50, 0)
            binding.spStoreName.setSelection(0, false)
            binding.spStoreName.onItemSelectedListener = activity

            //binding.spStoreListView.adapter = storeListAdapter
            binding.spStoreListView.setTitle("" + resources.getString(R.string.choose_store));


            // Creating ArrayAdapter using the string array and default spinner layout
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, storeList)
            // Specify layout to be used when list of choices appears
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applying the adapter to our spinner
            binding.spStoreListView.setAdapter(arrayAdapter)
            //binding.spStoreListView.setSelected(false);  // must
            //binding.spStoreListView.setSelection(0, true)
            binding.spStoreListView.setSelection(Adapter.NO_SELECTION, true);
            binding.spStoreListView.onItemSelectedListener = activity

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

    private fun addingsavedRAC() {
        var count = 0
        val standardList = resources.getStringArray(R.array.display_standard).toList() as List<String>
        val staff = resources.getStringArray(R.array.staff).toList() as List<String>
        val business_CM = resources.getStringArray(R.array.business_CM).toList() as List<String>

        val savedRAC = HashMap<Int, String>()
        for (std in standardList) {
            count++
            savedRAC[count] = std
        }

        for (bus in staff) {
            count++
            savedRAC[count] = bus
        }


        for (back in business_CM) {
            count++
            savedRAC[count] = back
        }

        PreferenceUtils.setRACDetail(savedRAC)
        getRACDetailList(savedRAC)
    }

    private fun getRACDetailList(racList: HashMap<Int, String>) {
        try {
            for (data in 1..4) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "B${data}"
                retailAudit.title = racList[data]
                displayStandardList.add(retailAudit)
            }

            for (data in 5..9) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "B${data}"
                retailAudit.title = racList[data]
                businessCMList.add(retailAudit)
            }

            for (data in 10 until racList.size + 1) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "B${data}"
                retailAudit.title = racList[data]
                backStoreList.add(retailAudit)
            }

            /*Thread {
                runOnUiThread {
                    addCustomView(displayStandardList, "DisplayStandard")
                    addCustomView(businessCMList, "BusinessCM")
                    addCustomView(backStoreList, "BackStore")
                }
            }.start()*/

            // set data in adapter..

            binding.displayStandardRV.layoutManager = LinearLayoutManager(activity)
            racStandardAdapter = RacStandardAdapter(activity, displayStandardList, object : RacStandardAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeRemarks")) {
                            saveRemarks(Constant.RAC_STANDARD, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveUserIds(Constant.RAC_STANDARD, position, changedValue!!);
                        }
                    }
                }
            })
            binding.displayStandardRV.adapter = racStandardAdapter
            /* purchaseGrnListAdapter = PurchaseGrnListAdapter(this@PurchaseActivity, grnSkuDtoList, this, object : QtyChangeListener() {
                 fun onValueChanged(actionType: String?, newValue: String?, position: Int) {}
             })*/
            binding.businessCMRV.layoutManager = LinearLayoutManager(activity)
            racBusinessAdapter = RacBusinessAdapter(activity, businessCMList, object : RacBusinessAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeRemarks")) {
                            saveRemarks(Constant.RAC_BUSINESS, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveUserIds(Constant.RAC_BUSINESS, position, changedValue!!);
                        }
                    }
                }
            })
            binding.businessCMRV.adapter = racBusinessAdapter

            binding.backstoreRV.layoutManager = LinearLayoutManager(activity)
            racBackstoreAdapter = RacBackstoreAdapter(activity, backStoreList, object : RacBackstoreAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeRemarks")) {
                            saveRemarks(Constant.RAC_BACKSTORE, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveUserIds(Constant.RAC_BACKSTORE, position, changedValue!!);
                        }
                    }
                }
            })
            binding.backstoreRV.adapter = racBackstoreAdapter


        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
    }

    /*private fun prepareDataList() {
        var count = 0
        val standardList = mutableListOf<String>()
        standardList.addAll(resources.getStringArray(R.array.display_standard).toList())
        for (index in 0 until  standardList.size){
            count++
            val retailAudit = RetailAudit()
            retailAudit.workDesc = "$count"
            retailAudit.title = standardList[index]
            displayStandardList.add(retailAudit)
        }

        addCustomView(displayStandardList, "DisplayStandard")

        val businessCM = mutableListOf<String>()
        businessCM.addAll(resources.getStringArray(R.array.business_CM).toList())
        for (index in 0 until  businessCM.size){
            count++
            val retailAudit = RetailAudit()
            retailAudit.workDesc = "$count"
            retailAudit.title = businessCM[index]
            businessCMList.add(retailAudit)
        }

        Handler().postDelayed({
            addCustomView(businessCMList, "BusinessCM")
        }, 100)


        val backStore = mutableListOf<String>()
        backStore.addAll(resources.getStringArray(R.array.backstore).toList())
        for (index in 0 until backStore.size){
            count++
            val retailAudit = RetailAudit()
            retailAudit.workDesc = "$count"
            retailAudit.title = backStore[index]
            backStoreList.add(retailAudit)
        }

        Handler().postDelayed({
            addCustomView(backStoreList, "BackStore")
        }, 100)
    }*/

    private fun addCustomView(retailAuditList: MutableList<RetailAudit>, auditType: String) {
        try {
            val assignToList: ArrayList<String> = arrayListOf()
            val ratingList: ArrayList<String> = arrayListOf()
            ratingList.addAll(resources.getStringArray(R.array.number_1_to_10).toList())
            assignToList.add("BM")
            assignToList.add("CM")
            assignToList.add("Other")
            var container: LinearLayout? = null
            when {
                auditType.equals("DisplayStandard", true) -> {
                    container = binding.rvDisplayStandart
                }
                auditType.equals("BusinessCM", true) -> {
                    container = binding.rvBusinessCM
                }
                auditType.equals("BackStore", true) -> {
                    container = binding.rvBackStore
                }
            }
            for (retailAudit in retailAuditList) {
                val viewBinding: SingleAuditDetailBinding = DataBindingUtil.inflate(layoutInflater, R.layout.single_audit_detail, container, true)
                viewBinding.retailAudit = retailAudit

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, assignToList)
                adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
                viewBinding.spName.adapter = adapter
                viewBinding.spName.setPadding(0, 0, 0, 0)
                viewBinding.spName.setSelection(0, false)

                if (auditType.equals("BackStore", true)) {
                    viewBinding.lbl1.visibility = View.GONE
                    viewBinding.spRating.visibility = View.GONE
                } else {
                    viewBinding.lbl1.visibility = View.VISIBLE
                    viewBinding.spRating.visibility = View.VISIBLE

                    val numAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, ratingList)
                    numAdapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
                    viewBinding.spRating.adapter = numAdapter
                    viewBinding.spRating.setPadding(0, 0, 0, 0)

                    val selectedRating = viewBinding.spRating.selectedItem.toString()
                    retailAudit.rating = selectedRating
                }

                viewBinding.radioGroupBT.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
                    if (checkedId == R.id.rbClosed) {
                        viewBinding.openIssueLayout.visibility = View.GONE
                        retailAudit.status = "CLOSED"
                        //retailAudit.action = "No"
                        retailAudit.taskAssigned = null
                    } else if (checkedId == R.id.rbOpen) {
                        viewBinding.openIssueLayout.visibility = View.VISIBLE
                        retailAudit.status = "OPEN"
                        //retailAudit.action = "Yes"
                        retailAudit.taskAssigned = "BM"
                    }
                }

                viewBinding.btnRemark.setOnClickListener {
                    if (viewBinding.etRemark.visibility == View.VISIBLE) {
                        Utility.hideKeyboard(activity)
                        viewBinding.etRemark.visibility = View.GONE
                        Handler().postDelayed({
                            val remark = viewBinding.etRemark.text.toString()
                            if (remark.isNotEmpty()) {
                                retailAudit.brndFreeText = remark
                            }
                        }, 10)
                    } else
                        viewBinding.etRemark.visibility = View.VISIBLE
                }

                viewBinding.btnComment.setOnClickListener {
                    if (viewBinding.etComment.visibility == View.VISIBLE) {
                        Utility.hideKeyboard(activity)
                        viewBinding.etComment.visibility = View.GONE

                        Handler().postDelayed({
                            val comment = viewBinding.etComment.text.toString()
                            if (comment.isNotEmpty()) {
                                retailAudit.lsFreeText = comment
                            }
                        }, 10)
                    } else
                        viewBinding.etComment.visibility = View.VISIBLE
                }

                viewBinding.spName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(prt: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                        if (prt?.id == R.id.spName) {
                            val assignTo = viewBinding.spName.selectedItem.toString()
                            retailAudit.taskAssigned = assignTo
                            if (viewBinding.spName.selectedItem.toString().equals("Other", true))
                                viewBinding.userIdLayout.visibility = View.VISIBLE
                            else
                                viewBinding.userIdLayout.visibility = View.GONE
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                    }
                }


                viewBinding.spRating.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(prt: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                        if (prt?.id == R.id.spRating) {
                            val rating = viewBinding.spRating.selectedItem.toString()
                            retailAudit.rating = rating
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                    }
                }

                viewBinding.btnSaveUserId.setOnClickListener {
                    val userId = viewBinding.etUserId.text.toString()
                    if (userId.length >= 7) {
                        retailAudit.taskAssigned = userId
                        Utility.hideKeyboard(activity)
                        viewBinding.etUserId.clearFocus()
                    } else
                        CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                }
            }
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
            writeToFile("Displaying Exception: ${e.localizedMessage}")
        }
    }

    public fun radioCheckListener(type: String, position: Int, status: String, taskAssigned: String?) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).status = status;
            displayStandardList.get(position).taskAssigned = taskAssigned;
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).status = status;
            businessCMList.get(position).taskAssigned = taskAssigned;
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).status = status;
            backStoreList.get(position).taskAssigned = taskAssigned;
        }

    }

    public fun saveRemarks(type: String, position: Int, remarks: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).brndFreeText = remarks;
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).brndFreeText = remarks;
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).brndFreeText = remarks;
        }
    }

    public fun saveComments(type: String, position: Int, comment: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).lsFreeText = comment
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).lsFreeText = comment
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).lsFreeText = comment
        }
    }

    public fun saveAssigneName(type: String, position: Int, assignTo: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).taskAssigned = assignTo
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).taskAssigned = assignTo
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).taskAssigned = assignTo
        }
    }

    public fun saveRatings(type: String, position: Int, rating: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).rating = rating
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).rating = rating
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            //backStoreList.get(position).rating = rating
        }
    }

    public fun saveUserIds(type: String, position: Int, userId: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).taskAssigned = userId
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).taskAssigned = userId
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).taskAssigned = userId
        }
    }

    private fun submitRetailAudit() {
        try {
            val retailAuditList = mutableListOf<RetailAudit>()
            val auditBy = binding.etAuditBy.text.toString()
            val managerName = binding.etManagerName.text.toString()
            var isRemarkDone = false
            //var isFreeTextDone = false
            var isUserIdAvailable = false
            var isOpen = false

            for (index in 0 until displayStandardList.size) {
                val display = displayStandardList[index]
                val v = binding.rvDisplayStandart.getChildAt(index)
                //val etRemark = v.findViewById<EditText>(R.id.etRemark)
                //val etComment = v.findViewById<EditText>(R.id.etComment)
                // val etUserID = v.findViewById<EditText>(R.id.etUserId)
                if (display.status.equals("Open", true)
                        && display.brndFreeText == null) {
                    // etRemark.visibility = View.VISIBLE
                    isRemarkDone = true
                    isOpen = true
                }
                /*if (display.status.equals("Open",true)
                    && display.lsFreeText == null){
                etComment.visibility= View.VISIBLE
                isFreeTextDone = true
            }*/

                if (display.taskAssigned.equals("Other", true)) {
                    // etUserID.visibility = View.VISIBLE
                    isUserIdAvailable = true
                }
            }

            for (index in 0 until businessCMList.size) {
                val business = businessCMList[index]
                val v = binding.rvBusinessCM.getChildAt(index)
                // val etRemark = v.findViewById<EditText>(R.id.etRemark)
                // val etComment = v.findViewById<EditText>(R.id.etComment)
                // val etUserID = v.findViewById<EditText>(R.id.etUserId)
                if (business.status.equals("Open", true)
                        && business.brndFreeText == null) {
                    //etRemark.visibility = View.VISIBLE
                    isRemarkDone = true
                    isOpen = true
                }
                /*if (business.status.equals("Open",true)
                    && business.lsFreeText == null){
                etComment.visibility= View.VISIBLE
                isFreeTextDone = true
            }*/

                if (business.taskAssigned.equals("Other", true)) {
                    // etUserID.visibility = View.VISIBLE
                    isUserIdAvailable = true
                }
            }

            for (index in 0 until backStoreList.size) {
                val backstore = backStoreList[index]
                val v = binding.rvBackStore.getChildAt(index)
                // val etRemark = v.findViewById<EditText>(R.id.etRemark)
                // val etComment = v.findViewById<EditText>(R.id.etComment)
                //  val etUserID = v.findViewById<EditText>(R.id.etUserId)
                if (backstore.status.equals("Open", true)
                        && backstore.brndFreeText == null) {
                    // etRemark.visibility = View.VISIBLE
                    isRemarkDone = true
                    isOpen = true
                }
                /*if (backstore.status.equals("Open", true)
                    && backstore.lsFreeText == null) {
                etComment.visibility = View.VISIBLE
                isFreeTextDone = true
            }*/

                if (backstore.taskAssigned.equals("Other", true)) {
                    // etUserID.visibility = View.VISIBLE
                    isUserIdAvailable = true
                }
            }

            when {
                storeId == null -> CommonUtility.showSnackBar(activity, "Choose Store")
                brandId == null -> CommonUtility.showSnackBar(activity, "Choose Brand")
                /*conceptId == null -> CommonUtility.showSnackBar(activity, "Choose Concept")*/
                managerName.isEmpty() -> CommonUtility.showSnackBar(activity, "Enter Concept Manager Name")
                auditBy.isEmpty() -> CommonUtility.showSnackBar(activity, "Enter Audit By Name")
                isRemarkDone -> CommonUtility.showSnackBar(activity, "Enter Remark For all Open status")
                /*isFreeTextDone -> CommonUtility.showSnackBar(activity,"Enter Free Text for all Open Status")*/
                isUserIdAvailable -> CommonUtility.showSnackBar(activity, "Enter valid User Id")
                else -> {
                    for (displayStd in displayStandardList) {
                        val retail = RetailAudit()
                        if (displayStd.status.equals("Open", true))
                            isOpen = true
                        retail.workDesc = displayStd.workDesc
                        retail.status = displayStd.status
                        retail.lsFreeText = displayStd.lsFreeText
                        retail.taskAssigned = displayStd.taskAssigned
                        retail.brndFreeText = displayStd.brndFreeText
                        retail.rating = displayStd.rating
                        retailAuditList.add(retail)
                    }

                    for (business in businessCMList) {
                        val retail = RetailAudit()
                        if (business.status.equals("Open", true))
                            isOpen = true
                        retail.workDesc = business.workDesc
                        retail.status = business.status
                        retail.lsFreeText = business.lsFreeText
                        retail.taskAssigned = business.taskAssigned
                        retail.brndFreeText = business.brndFreeText
                        retail.rating = business.rating
                        retailAuditList.add(retail)
                    }
                    //retailAuditList.addAll(displayStandardList)
                    //retailAuditList.addAll(businessCMList)

                    for (back in backStoreList) {
                        val retail = RetailAudit()
                        if (back.status.equals("Open", true))
                            isOpen = true
                        retail.workDesc = back.workDesc
                        retail.status = back.status
                        retail.lsFreeText = back.lsFreeText
                        retail.taskAssigned = back.taskAssigned
                        retail.brndFreeText = back.brndFreeText
                        retail.rating = back.rating
                        retailAuditList.add(retail)
                    }

                    val request = NewRACRequest()
                    request.brndId = brandId
                    request.appType = Constant.appType
                    request.auditBy = auditBy
                    if (isOpen)
                        request.racStatus = "OPEN"
                    else
                        request.racStatus = "CLOSED"
                    request.lsConceptMgr = managerName
                    request.usrId = PreferenceUtils.getUserId()
                    request.storeId = storeId
                    request.racAuditList.addAll(retailAuditList)
                    val api = RACApis(activity)
                    api.createNewRetailAudit(activity, request)
                }
            }
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.stackTrace}")
        }
    }

    override fun ojeUpdate(response: OJEResponse) {
        if (response.statusMessage.equals("Y", true)) {
            isRefresh = true
            CommonUtility.showSnackBar(activity, "Retail Audit Created Successfully")
        } else
            CommonUtility.showSnackBar(activity, response.serverErrormsg)

        Handler().postDelayed({
            val returnIntent = Intent()
            returnIntent.putExtra("isRefresh", isRefresh)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }, 600)
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spStoreListView) {
            /*if (isFirstTimeStore == 1) {
                isFirstTimeStore = 0
            } else {*/
                //val storeName = binding.spStoreName.selectedItem.toString()
                val storeName = prt.getItemAtPosition(p2).toString();
                if (storeName == resources.getString(R.string.choose_store)) {
                    storeId = null
                    CommonUtility.showSnackBar(activity, "Choose Store")
                } else {
                    for (store in storeDetailList) {
                        if (store.storeName.equals(storeName, true)) {
                            storeId = store.storeId
                            break
                        }
                    }
                }
            //}
        } else if (prt?.id == R.id.spBrand) {
            val brandName = binding.spBrand.selectedItem.toString()
            if (brandName.equals("Choose Brand", true)) {
                brandId = null
                CommonUtility.showSnackBar(activity, "Choose Brand")
            } else {
                for (brands in brandPerDetail) {
                    if (brands.brandName.equals(brandName, true)) {
                        brands.brandId?.let { brandId = it }
                        break
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun catupreImage(retailAudit: RetailAudit) {
        this.retailAudit = retailAudit
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = "IMG_$timeStamp.png"
        val file = File(Environment.getExternalStorageDirectory().path, "$imageFileName")
        val photo = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(activity, applicationContext.packageName + ".provider", file)
        photo.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        photo.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photo, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            //fileByteList.clear()
            //fileNameList.clear()

            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, "$imageFileName")
            val path = file.absolutePath
            val filename = path.substring(path.lastIndexOf("/") + 1)
            //binding.tvFile.text = filename
            //fileNameList.add(filename)
            val uri = Uri.fromFile(file)
            var bitmap: Bitmap?
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                var bitmapNew = rotateImageIfRequired(bitmap, uri);
                val timeStamp = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                //val bitmapAsString = getBitmapCompressString(bitmapNew!!)
                bitmapNew = ImageUtil.getScaledDownBitmap(bitmapNew!!, 780, false);
                val bitmapAsString = getBitmapCompressString(bitmapNew!!)
                bitmapNew = addWatermark(bitmapNew!!, timeStamp)
                saveImage(bitmapNew!!);

                //fileByteList.add(bitmapAsString)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Abrar", "Exception ${e.localizedMessage}")
            }
        }
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

    override fun onBackPressed() {
        showAlertDialog(this, "Do you need to cancel this request")
    }

    private fun showAlertDialog(activity: Activity, msg: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialog.setTitle("Alert")
        alertDialog.setMessage(msg)
        alertDialog.setPositiveButton(
                "yes"
        ) { _, _ ->
            val returnIntent = Intent()
            returnIntent.putExtra("isRefresh", isRefresh)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        alertDialog.setNegativeButton(
                "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    // image capture code..
    //private var imageFileName: String? = null
    private val dir = File(Environment.getExternalStorageDirectory(), "BrandMark/RAC_Images")

    fun captureImage(racStandard: String, position: Int) {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = "IMG_$timeStamp.png"
        val file = File(dir, "$imageFileName")
        val photo = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(activity, applicationContext.packageName + ".provider", file)
        photo.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        photo.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photo, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val input: InputStream = activity.getContentResolver().openInputStream(selectedImage)!!
        val ei: ExifInterface
        ei = if (Build.VERSION.SDK_INT > 23) ExifInterface(input) else ExifInterface(selectedImage.path!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun getBitmapCompressString(bitmap: Bitmap): String {
        val oByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, oByteArrayOutputStream)
        val byteArray = oByteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun addWatermark(bitmap: Bitmap, watermarkText: String, options: WatermarkOptions = WatermarkOptions()): Bitmap {
        val result = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paint.textAlign = when (options.corner) {
            Corner.TOP_LEFT, Corner.BOTTOM_LEFT -> Paint.Align.LEFT
            Corner.TOP_RIGHT, Corner.BOTTOM_RIGHT -> Paint.Align.RIGHT
        }

        val textSize = result.width * options.textSizeToWidthRatio
        paint.textSize = textSize
        paint.color = options.textColor
        if (options.shadowColor != null) {
            paint.setShadowLayer(textSize / 2, 0f, 0f, options.shadowColor)
        }
        if (options.typeface != null) {
            paint.typeface = options.typeface
        }
        val padding = result.width * options.paddingToWidthRatio
        val coordinates = calculateCoordinates(watermarkText, paint, options, canvas.width, canvas.height, padding)
        canvas.drawText(watermarkText, coordinates.x, coordinates.y, paint)
        return result
    }

    private fun calculateCoordinates(watermarkText: String, paint: Paint, options: WatermarkOptions, width: Int, height: Int, padding: Float): PointF {
        val x = when (options.corner) {
            Corner.TOP_LEFT,
            Corner.BOTTOM_LEFT -> {
                padding
            }
            Corner.TOP_RIGHT,
            Corner.BOTTOM_RIGHT -> {
                width - padding
            }
        }
        val y = when (options.corner) {
            Corner.BOTTOM_LEFT,
            Corner.BOTTOM_RIGHT -> {
                height - padding
            }
            Corner.TOP_LEFT,
            Corner.TOP_RIGHT -> {
                val bounds = Rect()
                paint.getTextBounds(watermarkText, 0, watermarkText.length, bounds)
                val textHeight = bounds.height()
                textHeight + padding
            }
        }
        return PointF(x, y)
    }

    data class WatermarkOptions(
            val corner: Corner = Corner.BOTTOM_RIGHT,
            val textSizeToWidthRatio: Float = 0.04f,
            val paddingToWidthRatio: Float = 0.03f,
            @ColorInt val textColor: Int = Color.WHITE,
            @ColorInt val shadowColor: Int? = Color.BLACK,
            val typeface: Typeface? = null
    )

    enum class Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
    }

    private fun saveImage(finalBitmap: Bitmap) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/Brandmark_Compressed_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Test_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}