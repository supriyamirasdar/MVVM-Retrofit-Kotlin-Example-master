package com.lifestyle.retail_dashboard.view.planogram.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityPlanogramBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.attendance.model.FilterItemSelected
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase
import com.lifestyle.retail_dashboard.view.planogram.adapter.PlanogramAdapter
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramRequest
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramResponse
import com.lifestyle.retail_dashboard.view.planogram.model.api.PlanogramApis
import java.util.*
import kotlin.collections.ArrayList


class PlanogramActivity : AppCompatActivity(), PlanogramApis.PlanogramDetailListner,
        Planogram.DeletePlanogramListner, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityPlanogramBinding
    private val planogramList = mutableListOf<Planogram>()
    private val filterPlanogramList = mutableListOf<Planogram>()
    private lateinit var api: PlanogramApis
    private lateinit var adapter: PlanogramAdapter
    private lateinit var activity: PlanogramActivity
    private val ACTIVITY_RESULT = 150
    private var pType: String? = null
    private var brandId: String? = null
    private var conceptId: String? = null
    private val brandNameList = mutableListOf<String>()
    private val seasonList = ArrayList<String>()
    private val hitList = ArrayList<String>()
    private val filteredBrandList = ArrayList<Usecase>()
    private var isFilterApply = false
    private val myOriginal = mutableListOf<Planogram>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_planogram)

        initView()
        getAccessData()
        recyclerView()
        conceptList()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        val plonogramType = intent.getStringExtra("itemSelected")
        if (plonogramType != null) {
            binding.toolbarLayout.tvToolBarTitle.text = plonogramType
            binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

            pType = when {
                plonogramType.equals("Planogram ", true) -> "planogram"
                plonogramType.equals("Catalog & Content", true) -> "catalog"
                plonogramType.equals("Training", true) -> "training"
                else -> "planogram"
            }

        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(activity, AddNewPlanogramActivity::class.java)
            intent.putExtra("itemSelected", plonogramType)
            startActivityForResult(intent, ACTIVITY_RESULT)
        }

        binding.tabs.addTab(binding.tabs.newTab().setText("Upload"))
        binding.tabs.addTab(binding.tabs.newTab().setText("Download"))


        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.etSearch.setText("")

                tabSelectedData(tab.text.toString(), myOriginal)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun getAccessData() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                brands.brandName?.let { brandNameList.add(it) }
            }
        }
        brandNameList.add(0, "Choose Brand")
        initDropDown()
    }

    private fun initDropDown() {
        Log.d("Abrar", "DropDown $brandNameList")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spBrand.adapter = adapter
        binding.spBrand.setPadding(0, 0, 0, 0)
        binding.spBrand.setSelection(0, false)
        binding.spBrand.onItemSelectedListener = activity
    }

    private fun recyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = PlanogramAdapter(activity, planogramList, activity)
        binding.recyclerView.adapter = adapter
    }

    private fun getPlanogramList() {
        Log.d("Abrar", "Brand: $brandId Type: $pType")
        planogramList.clear()
        filterPlanogramList.clear()
        if (brandId != null) {
            api = PlanogramApis(activity)
            val request = PlanogramRequest()
            request.brandId = brandId
            request.concept = conceptId
            request.pType = pType
            api.getPlanogramList(activity, request)
        }
    }

    override fun deletePlanogram(planogram: Planogram) {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage("Do you want to Delete this Planogram?")
                .setCancelable(false)
                .setPositiveButton("YES") { dialog, id ->
                    deletePlanogramApi(planogram)
                }
                .setNegativeButton("NO") { dialog, id ->
                    dialog.cancel()
                }

        val alert = dialogBuilder.create()
        alert.setTitle("Alert")
        alert.show()
    }

    private fun deletePlanogramApi(planogram: Planogram) {
        val request = PlanogramRequest()
        request.brandId = planogram.brandId.toString()
        request.usrId = planogram.usrId
        request.planogramId = planogram.planogramId
        api.deletePlanogram(activity, request)
    }

    override fun planogramDetail(response: PlanogramResponse?, reqCode: Int) {
        if (reqCode == Constant.GET_PLANOGRAM_LIST) {
            if (response != null) {
                if (response.serverErrormsg != null)
                    CommonUtility.showSnackBar(activity, response.serverErrormsg)
                else if (response.planogramList != null &&
                        response.planogramList.isNotEmpty()) {
                    binding.tvErrorMsg.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tabs.visibility = View.VISIBLE
                    //PreferenceUtils.setPlanogramData(response.planogramList)
                    displayPlanogramList(response.planogramList)
                } else {
                    binding.tvErrorMsg.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.tabs.visibility = View.GONE
                }
            }
        } else if (reqCode == Constant.DELETE_PLANOGRAM) {
            if (response != null) {
                if (response.serverErrormsg != null)
                    CommonUtility.showSnackBar(activity, response.serverErrormsg)
                else if (response.statusMessage != null
                        && response.statusMessage.equals("Y")) {
                    CommonUtility.showSnackBar(activity, "Planogram Deleted Successfully.")
                    getPlanogramList()
                } else {
                    CommonUtility.showSnackBar(activity, "Planogram Not Deleted.")
                }
            }
        }
    }

    private fun displayPlanogramList(responsePlanogramList: List<Planogram>) {
        Log.d("Abrar", "Res Size ${responsePlanogramList.size}")
        myOriginal.clear()
        myOriginal.addAll(responsePlanogramList)
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString().toLowerCase())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val selectedTab = binding.tabs.getTabAt(binding.tabs.selectedTabPosition)?.text.toString()
        tabSelectedData(selectedTab, myOriginal)
    }

    private fun tabSelectedData(selectedTab: String, myOriginal: List<Planogram>) {
        Log.d("Abrar", "Original ${myOriginal.size}")

        val brandIdList = mutableSetOf<Int>()
        val uploadList = mutableListOf<Planogram>()
        val downloadList = mutableListOf<Planogram>()
        val userId = PreferenceUtils.getUserId()
        val seasonSetList = mutableSetOf<String>()
        val hitSetList = mutableSetOf<String>()
        seasonList.clear()
        hitList.clear()

        for (planogram in myOriginal) {

            if (planogram.usrId.equals(userId, true)) {
                brandIdList.add(planogram.brandId)
                seasonSetList.add("${planogram.season}")
                hitSetList.add("${planogram.week}")
                uploadList.add(planogram)
            } else {
                brandIdList.add(planogram.brandId)
                seasonSetList.add("${planogram.season}")
                hitSetList.add("${planogram.week}")
                downloadList.add(planogram)
            }
        }

        seasonList.addAll(seasonSetList)
        hitList.addAll(hitSetList)

        for (bId in brandIdList) {
            for (planogram in myOriginal) {
                if (bId == planogram.brandId) {
                    filteredBrandList.add(Usecase(planogram.brandName!!, planogram.brandId))
                    break
                }
            }
        }
        filteredBrandList.add(0, Usecase("All", 0))

        planogramList.clear()
        filterPlanogramList.clear()
        if (selectedTab.equals("Download", true)) {
            planogramList.addAll(downloadList)
            filterPlanogramList.addAll(downloadList)
        } else {
            planogramList.addAll(uploadList)
            filterPlanogramList.addAll(uploadList)
        }

        if (planogramList.size > 0) {
            val filterList = PreferenceUtils.getSavedFilter()
            if (filterList != null) {
                isFilterApply = true
                invalidateOptionsMenu()
                filterPlanogramList(filterList)
            } else
                adapter.notifyDataSetChanged()
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvErrorMsg.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.tvErrorMsg.text = "No Data Found!!!"
            binding.tvErrorMsg.visibility = View.VISIBLE
        }
    }

    private fun conceptList() {
        val conceptList: ArrayList<String> = arrayListOf()
        conceptList.addAll(resources.getStringArray(R.array.concept).toList())
        conceptList.add(0, "Choose Concept")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, conceptList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spConcept.adapter = adapter
        binding.spConcept.setPadding(0, 0, 0, 0)
        binding.spConcept.setSelection(0, false)
        binding.spConcept.onItemSelectedListener = activity
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spBrand) {
            val selectedItem = binding.spBrand.selectedItem.toString()
            if (selectedItem.equals("Choose Brand")) {
                brandId = null
                CommonUtility.showSnackBar(activity, "Choose Brand")
            } else {
                val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
                if (brandPerDetail != null) {
                    for (brands in brandPerDetail) {
                        if (brands.brandName.equals(selectedItem)) {
                            brands.brandId?.let { brandId = it }
                            break
                        }
                    }
                }
            }
        } else if (prt?.id == R.id.spConcept) {
            val selectedItem = binding.spConcept.selectedItem.toString()
            Log.d("Abrar", "Concept $selectedItem")
            if (selectedItem.equals("Choose Concept")) {
                conceptId = null
                CommonUtility.showSnackBar(activity, "Choose Concept")
            } else {
                if (selectedItem.contains(" : ")) {
                    val concepts = selectedItem.split(" : ")
                    conceptId = concepts[1]
                }
            }
        }

        if (brandId != null && conceptId != null)
            getPlanogramList()
        Log.d("Abrar", "Brand $brandId concept $conceptId")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
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
                if (filterPlanogramList.size > 0) {
                    callFilterActivity()
                } else
                    CommonUtility.showSnackBar(activity, "No Filter Available")
                return true
            }
            R.id.action_apply_filter -> {
                if (filterPlanogramList.size > 0) {
                    callFilterActivity()
                } else
                    CommonUtility.showSnackBar(activity, "No Filter Available")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun callFilterActivity() {
        val intent = Intent(activity, FilterPlanogamActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList("brandList", filteredBrandList)
        bundle.putStringArrayList("seasonList", seasonList)
        bundle.putStringArrayList("hitList", hitList)
        intent.putExtras(bundle)
        startActivityForResult(intent, ACTIVITY_RESULT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == ACTIVITY_RESULT && resultCode == RESULT_OK) {
            if (intent != null) {
                val isRefresh = intent.getBooleanExtra("isRefresh", false)
                if (isRefresh)
                    getPlanogramList()

                val filter: MutableList<FilterItemSelected>? = intent.extras?.getParcelableArrayList("planogramFilter")
                Log.d("Abrar", "Planogram Filter ${Gson().toJson(filter)}")
                if (filter != null && filter.size > 0) {
                    isFilterApply = true
                    invalidateOptionsMenu()
                    filterPlanogramList(filter)
                }
            }
        }
    }


    private fun filterPlanogramList(filterList: MutableList<FilterItemSelected>) {
        val filterSeasonList = mutableListOf<String>()
        val filterWeekList = mutableListOf<String>()
        for (filter in filterList) {
            if (filter.selectedType.equals("Season", true))
                filterSeasonList.add(filter.itemId)

            if (filter.selectedType.equals("Week", true))
                filterWeekList.add(filter.itemId)
        }
        Log.d("Abrar", "Original ${planogramList.size} F: ${filterPlanogramList.size}")
        Log.d("Abrar", "Original ${Gson().toJson(planogramList)}")
        planogramList.clear()

        val resultList: MutableList<Planogram> = arrayListOf()
        for (planogram in filterPlanogramList) {
            if (filterSeasonList.size > 0 && filterWeekList.size > 0) {
                if (filterSeasonList.contains(planogram.season) && filterWeekList.contains(planogram.week))
                    resultList.add(planogram)
            } else if (filterSeasonList.size > 0) {
                if (filterSeasonList.contains(planogram.season))
                    resultList.add(planogram)
            } else if (filterWeekList.size > 0) {
                if (filterWeekList.contains(planogram.week))
                    resultList.add(planogram)
            }
        }

        planogramList.addAll(resultList)

        Log.d("Abrar", "Filtered ${planogramList.size} F: ${filterPlanogramList.size}")
        Log.d("Abrar", "Filtered List ${Gson().toJson(planogramList)}")
        if (planogramList.size == 0) {
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvErrorMsg.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        PreferenceUtils.setSavedFilter(null)
        // PreferenceUtils.setPlanogramData(null)
        super.onDestroy()
    }
}