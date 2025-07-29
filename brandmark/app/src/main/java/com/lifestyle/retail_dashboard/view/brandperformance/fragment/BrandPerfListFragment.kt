package com.lifestyle.retail_dashboard.view.brandperformance.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.RecyclerviewBinding
import com.lifestyle.retail_dashboard.utils.Constant.FIRST_ARGUMENT
import com.lifestyle.retail_dashboard.utils.Constant.SECOND_ARGUMENT
import com.lifestyle.retail_dashboard.view.brandperformance.activity.BPListViewModel
import com.lifestyle.retail_dashboard.view.brandperformance.activity.SendDetailData
import com.lifestyle.retail_dashboard.view.brandperformance.adapter.BrandPerAdapter
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList
import java.io.Serializable

class BrandPerfListFragment : Fragment(), BrandPerformanceList.OnItemClickListener {
    private lateinit var viewModel: BPListViewModel
    private lateinit var binding: RecyclerviewBinding
    private var sendDetailData : SendDetailData? = null
    //private lateinit var brandTabViewModel: BrandTabViewModel

    companion object {
        @JvmStatic
        fun newInstance(brandType: String, brandPerformanceList: List<BrandPerformanceList>): BrandPerfListFragment {
            return BrandPerfListFragment().apply {
                arguments = Bundle().apply {
                    if (brandPerformanceList.isNotEmpty()) {
                        putString(FIRST_ARGUMENT, brandType)
                        putSerializable(SECOND_ARGUMENT, brandPerformanceList as Serializable)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.recyclerview, container, false)
        intiView()
        return binding.root
    }

    private fun intiView() {
        var brandPerformanceList= mutableListOf<BrandPerformanceList>()
        val brandType = arguments?.getString(FIRST_ARGUMENT)
        if (arguments?.getSerializable(SECOND_ARGUMENT) != null)
            brandPerformanceList = (arguments?.getSerializable(SECOND_ARGUMENT) as List<BrandPerformanceList>).toMutableList()

        /*brandTabViewModel = ViewModelProviders.of(this).get(BrandTabViewModel::class.java).apply {
            if (brandType != null) {
                setBrandData(brandType, brandPerformanceList)
            }
        }

        brandTabViewModel.types.observe(viewLifecycleOwner, {
            Log.d("Abrar","Brand $it")
        })*/

        binding.recyclerView.layoutManager= LinearLayoutManager(activity)
        if(brandPerformanceList.isNotEmpty() && brandType != null) {
            val adapter = BrandPerAdapter(brandType,brandPerformanceList, this)
            binding.recyclerView.adapter = adapter
        }

       /* viewModel = ViewModelProviders.of(this).get(BPListViewModel::class.java)
        viewModel.getBrandPerformanceList().observe(viewLifecycleOwner, { brandPerformanceList ->
            val brandPerformance = MutableLiveData<List<BrandPerformance>>()
            brandPerformance.value = brandPerformanceList

        })*/
    }

    override fun onItemClick(view: View, item: BrandPerformanceList) {
        sendDetailData?.sendData(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            sendDetailData = activity as SendDetailData?
        } catch (e: ClassCastException) {
            throw ClassCastException("Error in retrieving data. Please try again")
        }
    }
}