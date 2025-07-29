package com.lifestyle.retail_dashboard.view.brandperformance.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList

class BrandTabViewModel : ViewModel() {

    private val brandType = MutableLiveData<String>()
    private val brandPerfList = MutableLiveData<List<BrandPerformanceList>>()
    val types: LiveData<String> = Transformations.map(brandType) {
        it
    }

    val brandPerformanceList: LiveData<List<BrandPerformanceList>> = Transformations.map(brandPerfList) {
        it
    }

    fun setBrandData(type: String, brandPerformanceList: List<BrandPerformanceList>) {
        brandType.value = type
        brandPerfList.value = brandPerformanceList
    }
}