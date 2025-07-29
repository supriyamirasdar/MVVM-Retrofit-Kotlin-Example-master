package com.lifestyle.retail_dashboard.view.brandperformance.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.FragmentBrandPerformanceBinding
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Constant.FIRST_ARGUMENT
import com.lifestyle.retail_dashboard.utils.Constant.SECOND_ARGUMENT
import com.lifestyle.retail_dashboard.utils.DashboardUtils.*
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class BrandPerfDetailFragment : Fragment() {
    private lateinit var binding: FragmentBrandPerformanceBinding
    private val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.US)
    private val monthFormat = SimpleDateFormat("MMM-yy", Locale.US)
    private val yearFormat = SimpleDateFormat("yy", Locale.US)
    private var todayDate: String? = null

    companion object {
        @JvmStatic
        fun newInstance(brandType: String, brandPerformanceList: List<BrandPerformanceList>): BrandPerfDetailFragment {
            return BrandPerfDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(FIRST_ARGUMENT, brandType)
                    putSerializable(SECOND_ARGUMENT, brandPerformanceList as Serializable)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_performance, container, false)
        intiView()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun intiView() {
        val brandType = arguments?.getString(FIRST_ARGUMENT)
        val brandPerformanceList = arguments?.getSerializable(Constant.SECOND_ARGUMENT) as List<BrandPerformanceList>?

        val selectedCal = PreferenceUtils.getBPCalender()
        todayDate = dateFormat.format(selectedCal.time)

        if (selectedCal != null) {
            when {
                brandType.equals("WTD", true) -> {
                    val monday = dateFormat.format(getWeekStartDate(selectedCal))
                    binding.tvMsg.visibility = View.VISIBLE
                    binding.tvMsg.text = "${resources.getString(R.string.bp_text)} $monday to $todayDate"
                }
                brandType.equals("MTD", true) -> {
                    val month = monthFormat.format(selectedCal.time)
                    binding.tvMsg.visibility = View.VISIBLE
                    binding.tvMsg.text = "${resources.getString(R.string.bp_text)} 01-$month to $todayDate"
                }
                brandType.equals("YTD", true) -> {
                    val year = yearFormat.format(selectedCal.time)
                    binding.tvMsg.visibility = View.VISIBLE
                    binding.tvMsg.text = "${resources.getString(R.string.bp_text)} 01-Apr-$year to $todayDate"
                }
            }
        }


        if (brandPerformanceList != null) {
            binding.saleType = brandType
            binding.store = "1"
            binding.lacs = LACS
            binding.crore = CRORE
            binding.noDecimal = decimalFormat
            binding.oneDecimal = floatingDecimalFormate1
            binding.twoDecimal = floatingDecimalFormate5
            binding.onItemClick = this

            if(brandPerformanceList.isNotEmpty()) {
                binding.brandName = brandPerformanceList[brandPerformanceList.size - 1].brandNm

                val salePerformanceList = brandPerformanceList[brandPerformanceList.size - 1].productTimeIntervalList

                if (salePerformanceList != null) {
                    for (salePerformance in salePerformanceList) {
                        if (salePerformance.respType.equals(brandType, ignoreCase = true)) {
                            binding.brandPerformance = salePerformance
                            break
                        }
                    }
                }
            }

            /*binding.tvBrandName.setOnClickListener {
                Utility.goToNextScreen(activity, PlotingGraphActivity::class.java)
            }*/
        }
    }

    fun updateUi(view: View?, itemClicked: String?){
        Log.d("Abrar", "Clicked $itemClicked")
        binding.itemClicked = itemClicked
    }


    private fun getWeekStartDate(cal:Calendar): Date {
        while (cal.get(Calendar.DAY_OF_WEEK) !== Calendar.MONDAY) {
            cal.add(Calendar.DATE, -1)
        }
        return cal.time
    }

    interface OnItemClickToUpdate {
        fun updateUi(view: View?, itemClicked: String?)
    }
}
