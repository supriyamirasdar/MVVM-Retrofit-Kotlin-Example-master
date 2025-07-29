package com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivitySingleRACDetailBinding
import com.lifestyle.retail_dashboard.databinding.ActivitySingleRACDetailNewBinding
import com.lifestyle.retail_dashboard.databinding.SingleUpdateAuditDetailBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil.writeToFile
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.oje.model.api.OJEApis
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.*
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.*
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api.RACApis

class SingleRACDetail : AppCompatActivity(), RACApis.GETRACListListener, OJEApis.OJEUpdateListener, ItemListener {
    private lateinit var binding: ActivitySingleRACDetailNewBinding
    private lateinit var activity: SingleRACDetail
    private val racListItemList = mutableListOf<GetRACLineItem>()
    private val filterRacListItemList = mutableListOf<GetRACLineItem>()
    private var retailAudit: GetRACList? = null
    private var storeId: String? = null
    private var racNo: String? = null
    private var brandId: String? = null
    private var racLineNo: String? = null
    private var api: RACApis? = null
    private var isRefresh = false
    private var filterAdapter: FilterByAdapter? = null
    private var behavior: BottomSheetBehavior<*>? = null
    private val filterByList = mutableListOf<String>()
    private var isFilterApply = false
    private var filterbyAssignTo = "All"
    private var filterbyStatus = "All"
    private var isAllClose = true

    private val displayStandardList = mutableListOf<GetRACLineItem>()
    private val businessCMList = mutableListOf<GetRACLineItem>()
    private val backStoreList = mutableListOf<GetRACLineItem>()

    private val filterdisplayStandardList = mutableListOf<GetRACLineItem>()
    private val filterbusinessCMList = mutableListOf<GetRACLineItem>()
    private val filterbackStoreList = mutableListOf<GetRACLineItem>()

    private lateinit var racStandardAdapter: SingleRacStandardAdapter
    private lateinit var racBusinessAdapter: SingleRacBusinessAdapter
    private lateinit var racBackstoreAdapter: SingleRacBackstoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_r_a_c_detail_new)

        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        binding.toolbarLayout.tvToolBarTitle.text = "RAC"

        activity = this

        api = RACApis(activity)

        retailAudit = intent.getParcelableExtra<GetRACList>("retailAudit")
        if (retailAudit != null) {
            binding.retailAudit = retailAudit
            racNo = retailAudit?.racNo
            brandId = retailAudit?.brandId
            storeId = retailAudit?.storeId
            binding.toolbarLayout.tvToolBarSubtitle.text = racNo
            getRACList()
        }

        val drawable = ProgressBar(activity).indeterminateDrawable.mutate()
        drawable.setColorFilter(Color.parseColor("#ffcf00"), PorterDuff.Mode.SRC_IN)
        binding.progressBar.indeterminateDrawable = drawable

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
            updateData()
        }


        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.rgStatus.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            filterbyStatus = rb.text.toString()
            filterRetailList()
        }

        binding.lblFilterbyAssign.visibility = View.VISIBLE
        binding.rvFilterByAssignTo.visibility = View.VISIBLE
        binding.rvFilterByAssignTo.layoutManager = GridLayoutManager(activity, 3)
        binding.rvFilterByAssignTo.itemAnimator = DefaultItemAnimator()
        filterAdapter = FilterByAdapter(filterByList, activity)
        binding.rvFilterByAssignTo.adapter = filterAdapter

    }

    private fun getRACList() {
        isAllClose = true
        racListItemList.clear()
        filterRacListItemList.clear()
        val request = GetRACRequest()
        request.racNo = racNo
        request.storeId = storeId
        request.brandId = brandId
        request.reqType = "RAC_LINE"
        request.appType = Constant.appType
        api?.getRACList(activity, request)
    }

    override fun getRACList(response: GetRACResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            racListItemList.addAll(response.brandRacAuditList)
            filterByList.clear()
            val assignToList = mutableSetOf<String>()
            assignToList.add("All")
            for (retail in racListItemList) {
                retail.taskAssigned?.let { assignToList.add(it) }
                if (retail.lineStatus.equals("Open", true))
                    isAllClose = false
            }

            filterByList.addAll(assignToList)
            //filterByList.add(0,"All")
            filterAdapter?.notifyDataSetChanged()

            if (racListItemList.size > 0) {
                binding.btnSubmit.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                filterRacListItemList.addAll(racListItemList)
                Thread {
                    runOnUiThread {
                        displayStandardList.clear()
                        businessCMList.clear()
                        backStoreList.clear()

                        filterdisplayStandardList.clear()
                        filterbusinessCMList.clear()
                        filterbackStoreList.clear()


                        for (i in racListItemList.indices) {
                            val getRACLineItem = racListItemList[i]
                            if (i <= 3) {
                                displayStandardList.add(getRACLineItem)

                            } else if (i >= 4 && i <= 8) {
                                businessCMList.add(getRACLineItem)
                            } else {
                                backStoreList.add(getRACLineItem)
                            }
                        }
                        filterdisplayStandardList.addAll(displayStandardList)
                        filterbusinessCMList.addAll(businessCMList)
                        filterbackStoreList.addAll(backStoreList)
                        addLineItem()
                        //addLineItem(racListItemList);
                    }
                }.start()
            } else
                binding.btnSubmit.visibility = View.VISIBLE
        }
    }

    private fun gettingRACDetail(getRAC: HashMap<Int, String>) {
        filterByList.clear()
        val assignToList = mutableSetOf<String>()
        assignToList.add("All")
        for (line in racListItemList) {
            //line.racDescTitle = getRAC[line.racDesc?.toInt()]
            line.taskAssigned?.let { assignToList.add(it) }
        }

        filterByList.addAll(assignToList)
        //filterByList.add(0,"All")
        filterAdapter?.notifyDataSetChanged()
    }

    /*private fun getRACDetailList(racList: HashMap<Int, String>) {
        try {
            for (data in 1 until 12) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "${data}"
                retailAudit.title = racList[data]
                displayStandardList.add(retailAudit)
            }

            for (data in 12 until 25) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "${data}"
                retailAudit.title = racList[data]
                businessCMList.add(retailAudit)
            }

            for (data in 25 until racList.size + 1) {
                val retailAudit = RetailAudit()
                retailAudit.workDesc = "${data}"
                retailAudit.title = racList[data]
                backStoreList.add(retailAudit)
            }

            Thread {
                runOnUiThread {
                    addCustomView(displayStandardList, "DisplayStandard")
                    addCustomView(businessCMList, "BusinessCM")
                    //addCustomView(backStoreList, "BackStore")
                }
            }.start()
        }catch (e: Exception){
            Log.d("Abrar","Exception: ${e.localizedMessage}")
        }
    }

    private fun addCustomView(retailAuditList: MutableList<RetailAudit>, auditType:String) {
        val assignToList: ArrayList<String> = arrayListOf()
        assignToList.add("BM")
        assignToList.add("CM")
        assignToList.add("Other")
        var container: LinearLayout? = null
        if (auditType.equals("DisplayStandard",true)){
            container = binding.rvDisplayStandart
        }else if (auditType.equals("BusinessCM",true)){
            container = binding.rvBusinessCM
        }else if (auditType.equals("BackStore",true)){
            container = binding.rvBackStore
        }
        for (retailAudit in retailAuditList) {
            val viewBinding: SingleAuditDetailBinding = DataBindingUtil.inflate(layoutInflater, R.layout.single_audit_detail, container, true)
            viewBinding.retailAudit = retailAudit

            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, assignToList)
            adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            viewBinding.spName.adapter = adapter
            viewBinding.spName.setPadding(0, 0, 0, 0)
            viewBinding.spName.setSelection(0, false)

            viewBinding.radioGroupBT.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
                if (checkedId == R.id.rbClosed) {
                    viewBinding.openIssueLayout.visibility = View.GONE
                    retailAudit.status = "Close"
                    //retailAudit.action = "No"
                    retailAudit.taskAssigned = null
                    CommonUtility.showSnackBar(activity,"Submit this line item")
                } else if (checkedId == R.id.rbOpen) {
                    viewBinding.openIssueLayout.visibility = View.VISIBLE
                    retailAudit.status = "Open"
                    //retailAudit.action = "Yes"
                    retailAudit.taskAssigned = "BM"
                }
            }

            viewBinding.btnRemark.setOnClickListener {
                if (viewBinding.etRemark.visibility == View.VISIBLE) {
                    val remark = viewBinding.etRemark.text.toString()
                    if (remark.isNotEmpty()) {
                        retailAudit.brndFreeText = remark
                    }
                    Utility.hideKeyboard(activity)
                    //viewBinding.etRemark.clearFocus()
                    viewBinding.etRemark.visibility = View.GONE
                }else
                    viewBinding.etRemark.visibility = View.VISIBLE
            }

            viewBinding.btnComment.setOnClickListener {
                if (viewBinding.etComment.visibility == View.VISIBLE) {
                    val comment = viewBinding.etComment.text.toString()
                    if (comment.isNotEmpty()) {
                        retailAudit.lsFreeText = comment
                    }
                    Utility.hideKeyboard(activity)
                    //Utility.hideKeyboard(activity) v
                    viewBinding.etComment.visibility = View.GONE
                }
                else
                    viewBinding.etComment.visibility = View.VISIBLE
            }

            viewBinding.spName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(prt: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                    if (prt?.id == R.id.spName) {
                        val  assignTo = viewBinding.spName.selectedItem.toString()
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

            viewBinding.btnSaveUserId.setOnClickListener {
                val userId = viewBinding.etUserId.text.toString()
                if (userId.length >= 7){
                    retailAudit.taskAssigned = userId
                    Utility.hideKeyboard(activity)
                    viewBinding.etUserId.clearFocus()
                }else
                    CommonUtility.showSnackBar(activity, "Enter Valid User Id")
            }
        }
    }*/

    private fun addLineItem() {
        //clearAllView()
        try {


            /*var container: LinearLayout
            for (index in 0 until  retailAuditList.size) {
                val retailAudit = retailAuditList[index]
                container = when (index) {
                    in 0..3 -> binding.rvDisplayStandart
                    in 4..8 -> binding.rvBusinessCM
                    else -> binding.rvBackStore
                }

                val viewBinding: SingleUpdateAuditDetailBinding = DataBindingUtil.inflate(layoutInflater, R.layout.single_update_audit_detail, container, true)
                viewBinding.retailAudit = retailAudit

                if (retailAudit.lineStatus.equals("Open", true)) {
                    viewBinding.openIssueLayout.visibility = View.VISIBLE
                    viewBinding.radioGroupBT.check(R.id.rbOpen)
                }
                else {
                    viewBinding.openIssueLayout.visibility = View.GONE
                    viewBinding.radioGroupBT.check(R.id.rbClosed)
                    viewBinding.rbClosed.isEnabled = false
                    viewBinding.rbOpen.isEnabled = false
                    viewBinding.root.isEnabled = false
                }

                if (retailAudit.lineStatus.equals("Open", true)) {
                    viewBinding.radioGroupBT.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
                        if (checkedId == R.id.rbClosed) {
                            viewBinding.openIssueLayout.visibility = View.GONE
                            val remarks = viewBinding.etComment.text.toString().trim()
                            racLineNo = retailAudit.racLineNo
                            if (remarks.isNotEmpty())
                                updateLineItem(remarks)
                            else {
                                CommonUtility.showSnackBar(activity, "Enter Response Remark")
                                viewBinding.radioGroupBT.check(R.id.rbOpen)
                                viewBinding.etComment.visibility = View.VISIBLE
                            }
                        } else if (checkedId == R.id.rbOpen) {
                            viewBinding.openIssueLayout.visibility = View.VISIBLE
                            retailAudit.lineStatus = "OPEN"
                        }
                    }
                }

                viewBinding.btnRemark.setOnClickListener {
                    if (viewBinding.etRemark.visibility == View.VISIBLE) {
                        Utility.hideKeyboard(activity)
                        viewBinding.etRemark.visibility = View.GONE
                        Handler().postDelayed({
                            val remark = viewBinding.etRemark.text.toString()
                            if (remark.isNotEmpty()) {
                                retailAudit.auditRemarks = remark
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
                                retailAudit.responseRemarks = comment
                            }
                        }, 10)
                    } else
                        viewBinding.etComment.visibility = View.VISIBLE
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
            }*/


            binding.displayStandardRV.layoutManager = LinearLayoutManager(activity)
            racStandardAdapter = SingleRacStandardAdapter(activity, displayStandardList, object : SingleRacStandardAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeResponseComments")) {
                            saveComments(Constant.RAC_STANDARD, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveAssigneName(Constant.RAC_STANDARD, position, changedValue!!);
                        }
                    }
                }
            })
            binding.displayStandardRV.adapter = racStandardAdapter

            binding.businessCMRV.layoutManager = LinearLayoutManager(activity)
            racBusinessAdapter = SingleRacBusinessAdapter(activity, businessCMList, object : SingleRacBusinessAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeResponseComments")) {
                            saveComments(Constant.RAC_BUSINESS, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveAssigneName(Constant.RAC_BUSINESS, position, changedValue!!);
                        }
                    }
                }
            })
            binding.businessCMRV.adapter = racBusinessAdapter

            binding.backstoreRV.layoutManager = LinearLayoutManager(activity)
            racBackstoreAdapter = SingleRacBackstoreAdapter(activity, backStoreList, object : SingleRacBackstoreAdapter.DataChangeListener {
                override fun onValueChanged(actionType: String?, changedValue: String?, position: Int) {
                    if (TextUtils.isEmpty(actionType)) {
                        // no action performed..
                    } else {
                        if (actionType.equals("ChangeResponseComments")) {
                            saveComments(Constant.RAC_BACKSTORE, position, changedValue!!);
                        } else if (actionType.equals("SaveUserID")) {
                            saveAssigneName(Constant.RAC_BACKSTORE, position, changedValue!!);
                        }
                    }
                }
            })
            binding.backstoreRV.adapter = racBackstoreAdapter


            if (binding.rvDisplayStandart.childCount > 0) {
                binding.rvDisplayStandart.visibility = View.VISIBLE
                binding.LblDisplayStand.visibility = View.VISIBLE
            } else {
                binding.rvDisplayStandart.visibility = View.GONE
                binding.LblDisplayStand.visibility = View.GONE
            }

            if (binding.rvBusinessCM.childCount > 0) {
                binding.rvBusinessCM.visibility = View.VISIBLE
                binding.LblBusinessCM.visibility = View.VISIBLE
            } else {
                binding.rvBusinessCM.visibility = View.GONE
                binding.LblBusinessCM.visibility = View.GONE
            }

            if (binding.rvBackStore.childCount > 0) {
                binding.rvBackStore.visibility = View.VISIBLE
                binding.LblStaff.visibility = View.VISIBLE
            } else {
                binding.rvBackStore.visibility = View.GONE
                binding.LblStaff.visibility = View.GONE
            }

            Log.d("Abrar", "IsAllClose $isAllClose")

            if (isAllClose)
                binding.btnSubmit.visibility = View.VISIBLE
            else
                binding.btnSubmit.visibility = View.GONE


            binding.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
            writeToFile("Displaying Exception: ${e.localizedMessage}")
        }
    }

    public fun saveRemarks(type: String, position: Int, remarks: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).auditRemarks = remarks;
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).auditRemarks = remarks;
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).auditRemarks = remarks;
        }
    }

    public fun saveComments(type: String, position: Int, comment: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).responseRemarks = comment
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).responseRemarks = comment
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).responseRemarks = comment
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

    public fun updateLineStatus(type: String, position: Int, status: String) {
        if (type.equals(Constant.RAC_STANDARD, true)) {
            displayStandardList.get(position).lineStatus = status
        } else if (type.equals(Constant.RAC_BUSINESS, true)) {
            businessCMList.get(position).lineStatus = status
        } else if (type.equals(Constant.RAC_BACKSTORE, true)) {
            backStoreList.get(position).lineStatus = status
        }
    }

    public fun updateRacLineNo(racLineNum: String?) {
        racLineNo = racLineNum;
    }

    public fun updateLineItemRemarks(remark: String) {
        updateLineItem(remark);
    }

    private fun addingsavedRAC() {
        var count = 0
        val standardList = resources.getStringArray(R.array.display_standard).toList() as List<String>
        val business_CM = resources.getStringArray(R.array.business_CM).toList() as List<String>
        val staff = resources.getStringArray(R.array.staff).toList() as List<String>

        val savedRAC = HashMap<Int, String>()
        for (std in standardList) {
            count++
            savedRAC[count] = std
        }

        for (bus in business_CM) {
            count++
            savedRAC[count] = bus
        }


        for (back in staff) {
            count++
            savedRAC[count] = back
        }

        PreferenceUtils.setRACDetail(savedRAC)
        gettingRACDetail(savedRAC)
    }

    private fun updateData() {
        if (retailAudit != null) {
            isRefresh = true
            val request = UpdateRACRequest()
            request.racNo = retailAudit?.racNo
            request.storeId = storeId
            request.usrId = PreferenceUtils.getUserId()
            request.status = "CLOSED"
            request.racLineNo = racLineNo
            request.remarks = "All Line Item Close"
            request.reqType = "RAC_HEADER"
            api?.updateRAC(activity, request)
        }
    }


    private fun updateLineItem(remark: String) {
        if (racLineNo != null) {
            if (retailAudit != null) {
                val request = UpdateRACRequest()
                request.racNo = retailAudit?.racNo
                request.storeId = storeId
                request.usrId = PreferenceUtils.getUserId()
                request.status = "CLOSED"
                request.racLineNo = racLineNo
                request.remarks = remark
                request.reqType = "RAC_LINE"
                api?.updateRAC(activity, request)
            }
        } else
            CommonUtility.showSnackBar(activity, "Status should be Close")
    }

    override fun ojeUpdate(response: OJEResponse) {
        if (response.statusMessage.equals("Y", true)) {
            CommonUtility.showSnackBar(activity, "Update Successfully")
        } else
            CommonUtility.showSnackBar(activity, response.serverErrormsg)

        if (isRefresh)
            Handler().postDelayed({ onBackPressed() }, 600)
        else
            Handler().postDelayed({ getRACList() }, 600)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val filterMenu = menu?.findItem(R.id.action_filter)
        val filterApplyMenu = menu?.findItem(R.id.action_apply_filter)
        if (isFilterApply) {
            filterMenu?.isVisible = false
            filterApplyMenu?.isVisible = true
        } else {
            filterMenu?.isVisible = true
            filterApplyMenu?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
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
                if (filterRacListItemList.size > 0)
                    bottomSheet()
                else
                    CommonUtility.showSnackBar(activity, "No Data Available")
                return true

            }

            R.id.action_apply_filter -> {
                if (filterRacListItemList.size > 0)
                    bottomSheet()
                else
                    CommonUtility.showSnackBar(activity, "No Data Available")
                return true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("isRefresh", isRefresh)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


    private fun bottomSheet() {
        if (behavior?.state == BottomSheetBehavior.STATE_EXPANDED)
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (behavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                binding.bottomSheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                    behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onItemClick(assignTo: String) {
        filterbyAssignTo = assignTo
        filterAdapter?.filterBy(filterbyAssignTo)

        filterRetailList()
    }

    private fun filterRetailList() {
        // filterdisplayStandardList, filterbusinessCMList, filterbackStoreList

        bottomSheet()
        displayStandardList.clear()
        businessCMList.clear()
        backStoreList.clear()
        if (filterbyStatus.equals("All", true)
                && filterbyAssignTo.equals("All", true)) {
            isFilterApply = false
            displayStandardList.addAll(filterdisplayStandardList)
            businessCMList.addAll(filterbusinessCMList)
            backStoreList.addAll(filterbackStoreList)
        } else {
            isFilterApply = true
            for (retail in filterdisplayStandardList) {
                if (filterbyAssignTo.equals("All", true)) {
                    if (retail.lineStatus.equals(filterbyStatus, true)) {
                        displayStandardList.add(retail)
                    }
                } else if (filterbyStatus.equals("All", true)) {
                    if (retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        displayStandardList.add(retail)
                    }
                } else {
                    if (retail.lineStatus.equals(filterbyStatus, true)
                            && retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        displayStandardList.add(retail)
                    }
                }
            }

            for (retail in filterbusinessCMList) {
                if (filterbyAssignTo.equals("All", true)) {
                    if (retail.lineStatus.equals(filterbyStatus, true)) {
                        businessCMList.add(retail)
                    }
                } else if (filterbyStatus.equals("All", true)) {
                    if (retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        businessCMList.add(retail)
                    }
                } else {
                    if (retail.lineStatus.equals(filterbyStatus, true)
                            && retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        businessCMList.add(retail)
                    }
                }
            }


            for (retail in filterbackStoreList) {
                if (filterbyAssignTo.equals("All", true)) {
                    if (retail.lineStatus.equals(filterbyStatus, true)) {
                        backStoreList.add(retail)
                    }
                } else if (filterbyStatus.equals("All", true)) {
                    if (retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        backStoreList.add(retail)
                    }
                } else {
                    if (retail.lineStatus.equals(filterbyStatus, true)
                            && retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        backStoreList.add(retail)
                    }
                }
            }
        }

        // old code..
        /*racListItemList.clear()
        if (filterbyStatus.equals("All", true)
                && filterbyAssignTo.equals("All", true)) {
            isFilterApply = false
            racListItemList.addAll(filterRacListItemList)
        } else {
            isFilterApply = true
            for (retail in filterRacListItemList) {
                if (filterbyAssignTo.equals("All", true)) {
                    if (retail.lineStatus.equals(filterbyStatus, true)) {
                        racListItemList.add(retail)
                    }
                } else if (filterbyStatus.equals("All", true)) {
                    if (retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        racListItemList.add(retail)
                    }
                } else {
                    if (retail.lineStatus.equals(filterbyStatus, true)
                            && retail.taskAssigned.equals(filterbyAssignTo, true)) {
                        racListItemList.add(retail)
                    }
                }
            }
        }*/
        invalidateOptionsMenu()
        Thread {
            runOnUiThread {
                addLineItem()
                //addLineItem(racListItemList)
            }
        }.start()
    }

    private fun clearAllView() {
        binding.LblBusinessCM.visibility = View.GONE
        binding.LblDisplayStand.visibility = View.GONE
        binding.LblStaff.visibility = View.GONE
        binding.rvDisplayStandart.removeAllViews()
        binding.rvBusinessCM.removeAllViews()
        binding.rvBackStore.removeAllViews()
    }

}