package com.lifestyle.retail_dashboard.view.planogram.activity

import android.app.Activity
import android.content.Intent
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
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityFilterAttendanceListBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.RecyclerTouchListener
import com.lifestyle.retail_dashboard.utils.RecyclerTouchListener.ClickListener
import com.lifestyle.retail_dashboard.view.attendance.adapter.LeftFilterAdapter
import com.lifestyle.retail_dashboard.view.attendance.adapter.RightFilterAdapter
import com.lifestyle.retail_dashboard.view.attendance.model.FilterItemSelected
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase
import java.util.*


class FilterPlanogamActivity : AppCompatActivity(), FilterItemSelected.OnItemCheckListener {
    private lateinit var binding: ActivityFilterAttendanceListBinding
    private lateinit var activity: FilterPlanogamActivity
    private var mLeftAdapter: LeftFilterAdapter? = null
    private val leftItemList = mutableListOf<String>()
    private var mRightAdapter: RightFilterAdapter? = null
    private val rightItemList = mutableListOf<FilterItemSelected>()
    private val itemSelectedList = ArrayList<FilterItemSelected>()
    private var selectedType: String = "Concept"
    private val weekList = mutableListOf<String>()
    private val seasonList = mutableListOf<String>()
    private val brandList = mutableListOf<Usecase>()
    private val conceptList = mutableListOf<Usecase>()
    private var sortedWeekList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter_attendance_list)

        initView()
        leftRecyclerView()
        rightRecyclerView()
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

        if (PreferenceUtils.getSavedFilter() != null) {
            itemSelectedList.addAll(PreferenceUtils.getSavedFilter())
        }

        val bundle = intent.extras
        val brands: ArrayList<Usecase>? = bundle?.getParcelableArrayList("brandList")
        val seasons = bundle?.getStringArrayList("seasonList")
        val hits = bundle?.getStringArrayList("hitList")

        if (seasons != null)
            seasonList.addAll(seasons)
        else
            seasonList.addAll(resources.getStringArray(R.array.season).toList())

        if (hits != null)
            weekList.addAll(hits)
        else {
            for (x in 1 until 11) {
                weekList.add("Hit $x")
            }
        }


        if (brands != null)
            brandList.addAll(brands.toMutableList())

        binding.toolbarLayout.tvToolBarTitle.text = "Filter By"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE
        binding.btnClear.setOnClickListener {
            itemSelectedList.clear()
            //Handler().postDelayed({
            mRightAdapter?.notifyDataSetChanged()
            //},50)
        }


        /*val oddNo = mutableListOf<String>()
        val evenNo = mutableListOf<String>()
        for (x in 1 until 10){
            if (x % 2 == 0)
                evenNo.add("Hit $x")
            else
                oddNo.add("Hit $x")
        }
        Log.d("Abrar","Odd $oddNo \n even: $evenNo")
        weekList.addAll(oddNo)
        weekList.addAll(evenNo)*/

        conceptList.add(Usecase("Mens Wear", 2321))
        conceptList.add(Usecase("Womens Wear", 2323))

        binding.btnApply.setOnClickListener {
            if (itemSelectedList.size >= 1) {

                var isState = false
                for (itemSelected in itemSelectedList) {
                    if (itemSelected.selectedType.equals("Brand", true))
                        isState = true

                }
                /*if (isState) {
                    for (brandselected in itemSelectedList) {
                        if (brandselected.selectedType.equals("Brand", true))
                            count++
                    }
                    if (count == 1) {

                    } else
                        CommonUtility.showSnackBar(activity, "Please Choose Single Brand")
                } else
                    CommonUtility.showSnackBar(activity, "Select Filter Properly")*/

                PreferenceUtils.setSavedFilter(itemSelectedList)
                Handler().postDelayed({
                    //Utility.goToNextScreen(activity, AttendanceActivity::class.java)
                    //finish()
                    onBackPressed()
                }, 400)
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

    private fun rightRecyclerView() {
        binding.rightRecyclerview.visibility = View.VISIBLE
        binding.tvNoData.visibility = View.GONE
        mRightAdapter = RightFilterAdapter(activity, rightItemList, this, itemSelectedList)
        binding.rightRecyclerview.layoutManager = GridLayoutManager(activity, 2)
        binding.rightRecyclerview.adapter = mRightAdapter
        mRightAdapter?.rightSelect(selectedType)
    }

    private fun preparedLeftDataList() {
        //leftItemList.add("Concept")
        //leftItemList.add("Brand")
        leftItemList.add("Season")
        leftItemList.add("Hit")
        mLeftAdapter?.notifyDataSetChanged()

        binding.tvLbl.text = "Choose ${leftItemList.get(0)}"
    }

    private fun preparedRightDataList(position: Int) {
        rightItemList.clear()
        /*if (position == 0) {
            selectedType = "Concept"
            for (brand in conceptList) {
                rightItemList.add(FilterItemSelected(brand.icon.toString(), brand.name, selectedType))
            }
            rightRecyclerView(2)
        } else if (position == 1) {
            selectedType = "Brand"
            for (brand in brandList) {
                rightItemList.add(FilterItemSelected(brand.icon.toString(), brand.name, selectedType))
            }
            rightRecyclerView(2)
        } else*/ if (position == 0) {
            selectedType = "Season"
            /*val planogramData = PreferenceUtils.getPlanogramData()
            val stateHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap

            for (empData in planogramData) {
                stateHashMap.put(empData.season!!, empData.season!!)

            }
            for (season in seasonList) {
                for (key in stateHashMap.keys) {
                    if (season.equals(key, true)) {
                        rightItemList.add(FilterItemSelected(season, season, selectedType))
                    }
                }
            }*/

            for (season in seasonList) {
                rightItemList.add(FilterItemSelected(season, season, selectedType))
            }
            rightRecyclerView()
        } else if (position == 1) {
            selectedType = "Week"
            weekList.sort()
            sortedWeekList = weekList.sortedBy { it.length }.toMutableList()

            /*val planogramData = PreferenceUtils.getPlanogramData()
            val weekHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap

            for (empData in planogramData) {
                weekHashMap.put(empData.week!!, empData.week!!)

            }
            for (week in sortedWeekList) {
                for (key in weekHashMap.keys) {
                    if (week.equals(key, true)) {
                        rightItemList.add(FilterItemSelected(week, week, selectedType))
                    }
                }
            }*/

            for (week in sortedWeekList) {
                rightItemList.add(FilterItemSelected(week, week, selectedType))
            }
            rightRecyclerView()
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
                //finish()
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


    override fun onBackPressed() {
        val returnIntent = Intent()
        val bundle = Bundle()
        bundle.putParcelableArrayList("planogramFilter", itemSelectedList)
        returnIntent.putExtras(bundle)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}