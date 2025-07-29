package com.lifestyle.retail_dashboard.view.homescreen.fragment

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
import com.lifestyle.retail_dashboard.utils.Constant.NO_OF_CELL
import com.lifestyle.retail_dashboard.view.homescreen.adapter.HomeAdapter
import com.lifestyle.retail_dashboard.view.homescreen.model.Usecase

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        intiView()
        return binding.root
    }

    private fun intiView() {
        binding.recyclerView.layoutManager= GridLayoutManager(activity,NO_OF_CELL)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        activity?.let {
            homeViewModel.getUseCaseList(it).observe(viewLifecycleOwner, { usecaseList ->
            val usecase= MutableLiveData<List<Usecase>>()
            usecase.value = usecaseList
            val adapter = HomeAdapter(usecase)
            binding.recyclerView.adapter= adapter
        })
        }
    }
}