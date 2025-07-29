package com.lifestyle.retail_dashboard.view.inventory_managment.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityNormsBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.PreferenceUtils

class NormsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityNormsBinding
    private lateinit var activity: NormsActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_norms)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_norms)

        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        binding.toolbarLayout.tvToolBarTitle.text = "Norms"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        binding.normsOverallFillrateHeaderLL.visibility=View.GONE
        binding.normsFreshnessHeaderLL.visibility= View.GONE
        binding.normsBrokenFreshnessHeaderLL.visibility= View.GONE

        binding.normsOverallFillrateLL.visibility = View.GONE
        binding.normsFreshnessLL.visibility = View.GONE
        binding.normsBrokenFreshnessLL.visibility = View.GONE

        getNormsList();
    }

    private fun getNormsList() {
        val brandNameList = mutableListOf<String>()

        brandNameList.add(0, "Over All Fill Rate")
        brandNameList.add(1, "Freshness (Season+Core)")
        brandNameList.add(2, "Broken of Fresh/Non Discounted Stocks")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spNorms.adapter = adapter
        binding.spNorms.setPadding(0, 0, 0, 0)
       // binding.spNorms.setSelection(0, false)
        binding.spNorms.onItemSelectedListener = activity
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spNorms) {
            itemSelected()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun itemSelected() {
        val normsType = binding.spNorms.selectedItem.toString()
        if (normsType.equals("Over All Fill Rate", true)) {
            binding.normsOverallFillrateHeaderLL.visibility=View.VISIBLE
            binding.normsFreshnessHeaderLL.visibility= View.GONE
            binding.normsBrokenFreshnessHeaderLL.visibility= View.GONE
            binding.normsOverallFillrateLL.visibility = View.VISIBLE
            binding.normsFreshnessLL.visibility = View.GONE
            binding.normsBrokenFreshnessLL.visibility = View.GONE
        } else if (normsType.equals("Freshness (Season+Core)", true)) {
            binding.normsOverallFillrateHeaderLL.visibility=View.GONE
            binding.normsFreshnessHeaderLL.visibility= View.VISIBLE
            binding.normsBrokenFreshnessHeaderLL.visibility= View.GONE

            binding.normsOverallFillrateLL.visibility = View.GONE
            binding.normsFreshnessLL.visibility = View.VISIBLE
            binding.normsBrokenFreshnessLL.visibility = View.GONE
        } else if (normsType.equals("Broken of Fresh/Non Discounted Stocks", true)) {
            binding.normsOverallFillrateHeaderLL.visibility=View.GONE
            binding.normsFreshnessHeaderLL.visibility= View.GONE
            binding.normsBrokenFreshnessHeaderLL.visibility= View.VISIBLE

            binding.normsOverallFillrateLL.visibility = View.GONE
            binding.normsFreshnessLL.visibility = View.GONE
            binding.normsBrokenFreshnessLL.visibility = View.VISIBLE
        } else {
            binding.normsOverallFillrateHeaderLL.visibility=View.GONE
            binding.normsFreshnessHeaderLL.visibility= View.GONE
            binding.normsBrokenFreshnessHeaderLL.visibility= View.GONE

            binding.normsOverallFillrateLL.visibility = View.GONE
            binding.normsFreshnessLL.visibility = View.GONE
            binding.normsBrokenFreshnessLL.visibility = View.GONE
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
}