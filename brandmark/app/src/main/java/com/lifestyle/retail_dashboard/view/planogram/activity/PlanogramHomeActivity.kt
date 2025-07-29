package com.lifestyle.retail_dashboard.view.planogram.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityPalindromHomeBinding
import com.lifestyle.retail_dashboard.view.planogram.fragment.PlanogramHomeFragment

class PlanogramHomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPalindromHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_palindrom_home)

        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        val plonogramType = intent.getStringExtra("itemSelected")

        binding.toolbarLayout.tvToolBarTitle.text = plonogramType
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        replaceFragment(PlanogramHomeFragment.newInstance(plonogramType!!))

    }

    private fun replaceFragment(fragment: PlanogramHomeFragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss()
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