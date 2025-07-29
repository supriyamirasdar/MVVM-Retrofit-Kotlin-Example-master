package com.lifestyle.retail_dashboard.view.attendance.activity

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityFilterAttendanceListBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.RecyclerTouchListener
import com.lifestyle.retail_dashboard.utils.RecyclerTouchListener.ClickListener
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.attendance.DB.DatabaseClient
import com.lifestyle.retail_dashboard.view.attendance.DB.Task
import com.lifestyle.retail_dashboard.view.attendance.adapter.LeftFilterAdapter
import com.lifestyle.retail_dashboard.view.attendance.adapter.RightFilterAdapter
import com.lifestyle.retail_dashboard.view.attendance.model.FilterItemSelected
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import java.util.*


class FilterAttendanceListActivity : AppCompatActivity(), FilterItemSelected.OnItemCheckListener {
    private lateinit var binding: ActivityFilterAttendanceListBinding
    private lateinit var activity: FilterAttendanceListActivity
    private var mLeftAdapter: LeftFilterAdapter? = null
    private val leftItemList = mutableListOf<String>()
    private var mRightAdapter: RightFilterAdapter? = null
    private val rightItemList = mutableListOf<FilterItemSelected>()
    private val itemSelectedList = mutableListOf<FilterItemSelected>()
    private val brandDetailList = mutableListOf<BrandPerfUserDetail>()
    private var selectedType: String = "Brand"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_attendance_list)

        initView()
        leftRecyclerView()
        rightRecyclerView(2)
        preparedLeftDataList()
        preparedRightDataList(0)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_clear_white)

        activity = this

        if (PreferenceUtils.getBrandPerfDetail() != null) {
            brandDetailList.addAll(PreferenceUtils.getBrandPerfDetail())
        }

        if (PreferenceUtils.getSavedFilter() != null) {
            itemSelectedList.addAll(PreferenceUtils.getSavedFilter())
        }

        binding.toolbarLayout.tvToolBarTitle.text = "Filter By"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE
        binding.btnClear.setOnClickListener {
            itemSelectedList.clear()
            //Handler().postDelayed({
            mRightAdapter?.notifyDataSetChanged()
            //},50)
        }

        binding.btnApply.setOnClickListener {
            if (itemSelectedList.size >= 1) {

                var isState = false
                Log.d("Abrar", "Selected Filter: ${Gson().toJson(itemSelectedList)}")
                for (itemSelected in itemSelectedList) {
                    if (itemSelected.selectedType.equals("Brand", true))
                        isState = true

                }

                if (isState) {
                    var count = 0
                    for (brandselected in itemSelectedList) {
                        if (brandselected.selectedType.equals("Brand", true))
                            count++
                    }
                    if (count == 1) {
                        PreferenceUtils.setSavedFilter(itemSelectedList)
                        Handler().postDelayed({
                            Utility.goToNextScreen(activity, AttendanceActivity::class.java)
                            finish()
                        }, 400)
                    } else
                        CommonUtility.showSnackBar(activity, "Please Choose Single Brand")
                } else
                    CommonUtility.showSnackBar(activity, "Select Filter Properly")
            } else
                CommonUtility.showSnackBar(activity, "Select Filter Properly")
        }
    }

    private fun leftRecyclerView() {
        mLeftAdapter = LeftFilterAdapter(activity, leftItemList)
        binding.leftRecyclerview.layoutManager = LinearLayoutManager(activity)
        binding.leftRecyclerview.addItemDecoration(DividerItemDecoration(binding.leftRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        binding.leftRecyclerview.adapter = mLeftAdapter

        binding.leftRecyclerview.addOnItemTouchListener(RecyclerTouchListener(activity, binding.leftRecyclerview, object : ClickListener {
            override fun onClick(view: View?, position: Int) {
                binding.tvLbl.text = "Choose ${leftItemList.get(position)}"
                preparedRightDataList(position)
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))
    }

    private fun rightRecyclerView(gridColomn: Int) {
        binding.rightRecyclerview.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
        mRightAdapter = RightFilterAdapter(activity, rightItemList, this, itemSelectedList)
        binding.rightRecyclerview.layoutManager = GridLayoutManager(activity, gridColomn)
        binding.rightRecyclerview.adapter = mRightAdapter
        mRightAdapter?.rightSelect(selectedType)
    }

    private fun preparedLeftDataList() {
        leftItemList.add("Brand")
        leftItemList.add("Region")
        leftItemList.add("State")
        leftItemList.add("Store")
        mLeftAdapter?.notifyDataSetChanged()
    }

    private fun preparedRightDataList(position: Int) {
        if (PreferenceUtils.getEmpAttendanceDetail() != null) {
            val empAttendanceDetailData = PreferenceUtils.getEmpAttendanceDetail()



            rightItemList.clear()
            if (position == 0) {
                selectedType = "Brand"
                val brandId = PreferenceUtils.getAttendanceBrandId();
                for (brand in brandDetailList) {
                    if (brandId.equals(brand.brandId!!, true)) {
                        rightItemList.add(FilterItemSelected(brand.brandId!!, brand.brandName!!, selectedType))
                    }
                }
                rightRecyclerView(2)
            } else if (position == 1) {
                selectedType = "Region"
                val regionHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap
                for (empData in empAttendanceDetailData) {
                    regionHashMap.put(empData.areaNm!!, empData.areaNm!!)
                }
                if (regionHashMap.size > 0) {
                    for (key in regionHashMap.keys) {
                        //println("Element at key $key = ${hashMap[key]}")
                        if (key.equals("South", true) || key.equals("South1", true)) {
                            rightItemList.add(FilterItemSelected("R1", "South", selectedType))
                        } else if (key.equals("North", true)) {
                            rightItemList.add(FilterItemSelected("R2", "North", selectedType))
                        } else if (key.equals("West", true)) {
                            rightItemList.add(FilterItemSelected("R3", "West", selectedType))
                        } else if (key.equals("East", true)) {
                            rightItemList.add(FilterItemSelected("R4", "East", selectedType))
                        }
                    }
                    rightRecyclerView(2)
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rightRecyclerview.visibility = View.GONE
                    binding.tvNoData.text = "No $selectedType List Found. Please Check the Filter"
                }
                /* rightItemList.add(FilterItemSelected("R1", "South", selectedType))
                 rightItemList.add(FilterItemSelected("R2", "North", selectedType))
                 rightItemList.add(FilterItemSelected("R3", "West", selectedType))
                 rightItemList.add(FilterItemSelected("R4", "East", selectedType))*/

            } else if (position == 2) {
                selectedType = "State"
                val condition = mutableListOf<String>()
                if (itemSelectedList.size > 0) {
                    for (items in itemSelectedList) {
                        if (items.selectedType.equals("Region", true))
                            condition.add(items.itemId)
                    }
                } else {
                    condition.add("R1")
                }
                rightRecyclerView(2)
                getTasks(condition)
            } else if (position == 3) {
                selectedType = "Store"
                rightRecyclerView(1)
                val condition = mutableListOf<String>()
                if (itemSelectedList.size > 0) {
                    for (items in itemSelectedList) {
                        if (items.selectedType.equals("State", true))
                            condition.add(items.itemId)
                    }
                    getTasks(condition)
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rightRecyclerview.visibility = View.GONE
                    binding.tvNoData.text = "No $selectedType List Found. Please Check the Filter"
                }
            }
        }
        //itemSelectedList.clear()
        // mRightAdapter?.notifyDataSetChanged()
    }


    private fun getTasks(condition: List<String>) {
        class GetTasks : AsyncTask<Void?, Void?, List<Task?>?>() {
            override fun doInBackground(vararg voids: Void?): List<Task?>? {
                return DatabaseClient.getInstance(activity)
                        ?.appDatabase
                        ?.taskDao()
                        ?.getPerticularData(condition)
            }

            override fun onPostExecute(dataList: List<Task?>?) {
                super.onPostExecute(dataList)
                Log.d("Abrar", "Received: ${Gson().toJson(dataList)}")
                rightItemList.clear()
                if (dataList != null) {
                    Collections.sort(dataList, kotlin.Comparator { o1, o2 ->
                        o2?.itemName?.let { o1?.itemName?.compareTo(it, true) }!!
                    })
                    dataList.sortedWith(kotlin.Comparator { t, t2 -> t2?.itemId?.let { t?.itemId?.compareTo(it) }!! })
                    for (task in dataList) {
                        if (task != null) {
                            if (selectedType.equals("State")) {
                                val empAttendanceDetailData = PreferenceUtils.getEmpAttendanceDetail()
                                val stateHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap

                                for (empData in empAttendanceDetailData) {
                                    stateHashMap.put(empData.rgnNm!!, empData.rgnNm!!)

                                }
                                for (key in stateHashMap.keys) {
                                    if (task.itemName.equals(key, true)) {
                                        rightItemList.add(FilterItemSelected(task.itemId!!, task.itemName!!, selectedType))
                                    }
                                }

                            } else if (selectedType.equals("Store")) {
                                val empAttendanceDetailData = PreferenceUtils.getEmpAttendanceDetail()
                                val storeHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap

                                for (empData in empAttendanceDetailData) {
                                    storeHashMap.put(empData.locNm!!, empData.locNm!!)
                                    /*if (task.itemName.equals(empData.locNm)) {
                                        rightItemList.add(FilterItemSelected(task.itemId!!, task.itemName!!, selectedType))
                                    }*/
                                }

                                for (key in storeHashMap.keys) {
                                    if (task.itemName.equals(key, true)) {
                                        rightItemList.add(FilterItemSelected(task.itemId!!, task.itemName!!, selectedType))
                                    }
                                }
                            }
                            // rightItemList.add(FilterItemSelected(task.itemId!!, task.itemName!!, selectedType))
                        }
                    }
                }
                if (rightItemList.size > 0) {
                    binding.tvNoData.visibility = View.GONE
                    binding.rightRecyclerview.visibility = View.VISIBLE
                } else {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.rightRecyclerview.visibility = View.GONE
                    binding.tvNoData.text = "No $selectedType List Found. Please Check the Filter"
                }
                mRightAdapter?.notifyDataSetChanged()
            }
        }

        val gt = GetTasks()
        gt.execute()
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

    override fun onItemCheck(item: FilterItemSelected) {
        itemSelectedList.add(item)
    }

    override fun onItemUncheck(item: FilterItemSelected) {
        try {
            for (selected in itemSelectedList) {
                if (selected.itemId.equals(item.itemId, true))
                    itemSelectedList.remove(selected)
            }
        } catch (e: Exception) {
            Log.d("Abrar", "Exception: ${e.localizedMessage}")
        }
    }
}