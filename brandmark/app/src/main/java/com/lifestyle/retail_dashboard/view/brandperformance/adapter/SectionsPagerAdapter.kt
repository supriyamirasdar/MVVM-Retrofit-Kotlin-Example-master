package com.lifestyle.retail_dashboard.view.brandperformance.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.view.brandperformance.fragment.BrandPerfDetailFragment
import com.lifestyle.retail_dashboard.view.brandperformance.fragment.BrandPerfListFragment
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList

private val TAB_TITLES = arrayOf(
        R.string.ftd,
        R.string.wtd,
        R.string.mtd,
        R.string.std,
        R.string.ytd
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, var displayType: String, var brandPerformanceList: List<BrandPerformanceList>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
       return if (displayType.equals("detail", ignoreCase = true))
            BrandPerfDetailFragment.newInstance(context.resources.getString(TAB_TITLES[position]),brandPerformanceList)
       else
            BrandPerfListFragment.newInstance(context.resources.getString(TAB_TITLES[position]),brandPerformanceList)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun updateBrandPerfList(type: String, brand: List<BrandPerformanceList>) {
        displayType = type
        brandPerformanceList = brand
        notifyDataSetChanged()
    }

    /*fun displayBrandList(type: String,brand: List<BrandPerformanceList>) {
        displayType = type
        brandPerformanceList = brand
        notifyDataSetChanged()
    }*/
}