package com.lifestyle.retail_dashboard.view.brandperformance.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceList

class BPListViewModel : ViewModel() {
    private var brandPerformanceList: MutableLiveData<List<BrandPerformanceList>>? = null

    internal fun getBrandPerformanceList(): MutableLiveData<List<BrandPerformanceList>> {
        if (brandPerformanceList == null) {
            brandPerformanceList = MutableLiveData()
            //brandPerformanceList()
        }
        return brandPerformanceList as MutableLiveData<List<BrandPerformanceList>>
    }
}