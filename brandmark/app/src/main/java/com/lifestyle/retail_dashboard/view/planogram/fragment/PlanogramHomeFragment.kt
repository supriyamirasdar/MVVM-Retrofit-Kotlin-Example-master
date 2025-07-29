package com.lifestyle.retail_dashboard.view.planogram.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.FragmentHomeBinding
import com.lifestyle.retail_dashboard.utils.Constant.FIRST_ARGUMENT
import com.lifestyle.retail_dashboard.utils.Constant.NO_OF_CELL
import com.lifestyle.retail_dashboard.view.homescreen.adapter.HomeAdapter
import com.lifestyle.retail_dashboard.view.homescreen.fragment.HomeViewModel
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase

class PlanogramHomeFragment  : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    companion object {
        @JvmStatic
        fun newInstance(itemType: String): PlanogramHomeFragment {
            return PlanogramHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(FIRST_ARGUMENT, itemType)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        intiView()
        return binding.root
    }

    private fun intiView() {
        binding.recyclerView.layoutManager= GridLayoutManager(activity, NO_OF_CELL)

        val planogramType = arguments?.getString(FIRST_ARGUMENT)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        if (planogramType != null) {
            homeViewModel.getPlanogramTypeList(planogramType).observe(viewLifecycleOwner, { planogramList ->
                val planogram = MutableLiveData<List<Usecase>>()
                planogram.value = planogramList
                val adapter = HomeAdapter(planogram)
                binding.recyclerView.adapter = adapter
            })
        }

    }
}