package com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityGetRetailAuditBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Constant.dateFormat
import com.lifestyle.retail_dashboard.utils.Constant.inputDateFormat
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.RecyclerTouchListener
import com.lifestyle.retail_dashboard.utils.Utility.parsingDateFormate
import com.lifestyle.retail_dashboard.view.attendance.model.StoreList
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListRequest
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.FilterByAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.GetRetailAuditAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.ItemListener
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACList
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api.RACApis
import java.util.*


class GetRACActivity : AppCompatActivity(), ItemListener, AdapterView.OnItemSelectedListener, AttendanceApis.StoreListListener, RACApis.GETRACListListener {
    private lateinit var binding: ActivityGetRetailAuditBinding
    private lateinit var activity: GetRACActivity
    private var mAdapter: GetRetailAuditAdapter? = null
    private var filterAdapter: FilterByAdapter? = null
    private val getRatailAuditList = mutableListOf<GetRACList>()
    private val filterRatailAuditList = mutableListOf<GetRACList>()
    private var behavior: BottomSheetBehavior<*>? = null
    private val filterByList = mutableListOf<String>()
    private var isFilterApply = false
    private var filterbyStatus = "All"
    private var filterbyAssignTo = "All"
    private val brandPerDetail = mutableListOf<BrandPerfUserDetail>()
    private val storeDetailList = mutableListOf<StoreList>()
    private val storeList = mutableListOf<String>()
    private var brandId: String? = null
    private var storeId: String? = null
    private val ACTIVITY_RESULT = 150
    private var isFirstTimeStore = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_retail_audit)

        initView()
        getBrandList()
        getStoreList()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        binding.toolbarLayout.tvToolBarTitle.text = "Retail Audit"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter = GetRetailAuditAdapter(getRatailAuditList)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, binding.recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                val retailAudit = getRatailAuditList[position]
                val intent = Intent(activity, SingleRACDetail::class.java)
                intent.putExtra("retailAudit", retailAudit)
                startActivityForResult(intent, ACTIVITY_RESULT)
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))


        binding.rvFilterByAssignTo.layoutManager = GridLayoutManager(activity, 3)
        binding.rvFilterByAssignTo.itemAnimator = DefaultItemAnimator()
        filterAdapter = FilterByAdapter(filterByList, activity)
        binding.rvFilterByAssignTo.adapter = filterAdapter


        binding.btnAdd.setOnClickListener {
            val intent = Intent(activity, CreateNewRACActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent, ACTIVITY_RESULT)
        }

        binding.rgStatus.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            filterbyStatus = rb.text.toString()
            filterRetailList()
        }

        /*binding.showStoreSpinnerView.setOnClickListener {
            binding.spStoreListView.performClick()
        }*/
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

            val storeListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity,
                    R.layout.spinner_simple_text_black, storeList)
            storeListAdapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            binding.spStoreList.adapter = storeListAdapter
            binding.spStoreList.setPadding(0, 0, 50, 0)
            binding.spStoreList.setSelection(0, false)
            binding.spStoreList.onItemSelectedListener = activity

            //binding.spStoreListView.adapter = storeListAdapter
            binding.spStoreListView.setTitle("" + resources.getString(R.string.choose_store));


            // Creating ArrayAdapter using the string array and default spinner layout
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, storeList)
            // Specify layout to be used when list of choices appears
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applying the adapter to our spinner
            //binding.spStoreList.setPadding(0, 20, 0, 0)
            binding.spStoreListView.setAdapter(arrayAdapter)
//            binding.spStoreListView.setSelected(false);  // must
//            binding.spStoreListView.setSelection(-1, false)
             binding.spStoreListView.setSelection(Adapter.NO_SELECTION, true);
            binding.spStoreListView.onItemSelectedListener = activity

// new code for Autocomplete textview

            //binding.storeListACTV.adapter = storeListAdapter
            //    binding.storeListACTV.setAdapter<ArrayAdapter<String>>(storeListAdapter)
            // binding.storeListACTV.setPadding(0, 0, 50, 0)
            // binding.storeListACTV.setSelection(0, false)
            //binding.storeListACTV.onItemSelectedListener = activity

            /*val arrayAdapterPricemrp: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, priceMrpList)
            arrayAdapterPricemrp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            priceMrpACTV.setAdapter<ArrayAdapter<String>>(arrayAdapterPricemrp)*/

            // binding.storeListACTV.setDropDownHorizontalOffset(100)

            /*    val pointSize = Point()

                windowManager.defaultDisplay.getSize(pointSize)

                binding.storeListACTV.setDropDownWidth(pointSize.x - 250)*/

            /* binding.storeListACTV.setOnTouchListener(OnTouchListener { v, event ->
                 binding.storeListACTV.showDropDown()
                 //binding.storeListACTV.focusable = 1
                 //binding.storeListACTV.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
                 false
             })*/
            /*  binding.storeListACTV.setOnClickListener(View.OnClickListener {
                  binding.storeListACTV.showDropDown()
                  //binding.storeListACTV.focusable = 1
                  //binding.storeListACTV.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
              })
              binding.storeListACTV.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                  // fetch the user selected value
                  val storeName = parent.getItemAtPosition(position).toString()
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
                  //binding.storeListACTV.setInputType(0)
                  //binding.storeListACTV.setSelection(1);
                  //binding.storeListACTV.focusable = 0
                  // binding.storeListACTV.isInEditMode = false
                  // binding.storeListACTV.isEnabled = false
                  //binding.storeListACTV.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE)
                  Log.d("Abrar", "Store Id: $storeId")
                  Utility.hideKeyboard(activity)


                  if (brandId != null && storeId != null)
                      getRACList()
              })*/
        }
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spStoreListView) {
           /* if (isFirstTimeStore == 1) {
                isFirstTimeStore = 0
            } else {*/

                //val storeName = binding.spStoreList.selectedItem.toString()
                val storeName = prt.getItemAtPosition(p2).toString();
                if (storeName == resources.getString(R.string.choose_store) || TextUtils.isEmpty(storeName)) {
                    storeId = null
                    //binding.showStoreSpinnerView.text = "" + resources.getString(R.string.choose_store)
                    CommonUtility.showSnackBar(activity, "Choose Store")
                } else {
                    //binding.spStoreList.setPadding(0, 0, 0, 0)
                    for (store in storeDetailList) {
                        if (store.storeName.equals(storeName, true)) {
                            storeId = store.storeId
                            //binding.showStoreSpinnerView.text = "" + storeName
                            break
                        }
                    }
                }

                Log.d("Abrar", "Store Id: $storeId")
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
            Log.d("Abrar", "Brand Id: $brandId")
        }

        if (brandId != null && storeId != null)
            getRACList()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getRACList() {
        if (storeId != null && brandId != null) {
            getRatailAuditList.clear()
            filterRatailAuditList.clear()
            val request = GetRACRequest()
            request.storeId = storeId
            request.brandId = brandId
            request.reqType = "RAC_HEADER"
            request.appType = Constant.appType
            val api = RACApis(activity)
            api.getRACList(activity, request)
        }
    }

    override fun getRACList(response: GetRACResponse) {
        if (response.serverErrormsg != null)
            showErrorMsg(response.serverErrormsg)
        else {
            if (response.brandStaffRacAuditList.size > 0) {
                binding.tvErrorMsg.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                filterByList.clear()
                for (ratail in response.brandStaffRacAuditList) {
                    if (ratail.appType.equals(Constant.appType)) {
                        ratail.auditDate = parsingDateFormate(ratail.auditDate, inputDateFormat, dateFormat)
                        getRatailAuditList.add(ratail)
                        filterRatailAuditList.add(ratail)
                    }
                }

                val auditByList = mutableSetOf<String>()
                for (retail in getRatailAuditList) {
                    retail.auditBy?.let { auditByList.add(it) }
                }
                val auditByHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap
                for (empData in auditByList) {
                    if (auditByHashMap.containsKey(empData.toLowerCase())) {
                        // data already exist..
                    } else {
                        auditByHashMap.put(empData.toLowerCase()!!, empData!!)
                    }
                }
                for (key in auditByHashMap.keys) {
                    filterByList.add(key)
                }
                //filterByList.addAll(auditByList)
                filterAdapter?.notifyDataSetChanged()

                mAdapter?.notifyDataSetChanged()
            } else
                showErrorMsg("No Data Found")
        }
    }

    private fun showErrorMsg(msg: String?) {
        CommonUtility.showSnackBar(activity, msg)
        binding.tvErrorMsg.text = msg
        binding.tvErrorMsg.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
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
                if (filterRatailAuditList.size > 0)
                    bottomSheet()
                else
                    CommonUtility.showSnackBar(activity, "No Data Available")
                return true

            }

            R.id.action_apply_filter -> {
                if (filterRatailAuditList.size > 0)
                    bottomSheet()
                else
                    CommonUtility.showSnackBar(activity, "No Data Available")
                return true

            }
            else -> super.onOptionsItemSelected(item)
        }
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
        Log.d("Abrar", "Filter By Status $filterbyStatus ")
        isFilterApply = !filterbyStatus.equals("All", true)
        isFilterApply = !filterbyAssignTo.equals("All", true)
        invalidateOptionsMenu()
        bottomSheet()
        getRatailAuditList.clear()
        if (filterbyStatus.equals("All", true) && filterbyAssignTo.equals("All", true))
            getRatailAuditList.addAll(filterRatailAuditList)
        else {
            for (retail in filterRatailAuditList) {
                if (filterbyAssignTo.equals("All", true)) {
                    if (retail.racStatus.equals(filterbyStatus, true)) {
                        getRatailAuditList.add(retail)
                    }
                } else if (filterbyStatus.equals("All", true)) {
                    if (retail.auditBy.equals(filterbyAssignTo, true)) {
                        getRatailAuditList.add(retail)
                    }
                } else {
                    if (retail.auditBy.equals(filterbyAssignTo, true)
                            && retail.racStatus.equals(filterbyStatus, true)) {
                        getRatailAuditList.add(retail)
                    }
                }
            }
        }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == ACTIVITY_RESULT && resultCode == RESULT_OK) {
            if (intent != null) {
                val isRefresh = intent.getBooleanExtra("isRefresh", false)
                if (isRefresh)
                    getRACList()

                Log.d("Abrar", "isRefershing $isRefresh")
            }
        }
    }
}
/*

http://android.devise.space/searchable-spinner-with-search-box-example-in-android/

 */
