package com.lifestyle.retail_dashboard.view.planogram.activity.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram

class PlanogramHomeViewModel : ViewModel() {
    private var planogramList: MutableLiveData<List<Planogram>>? = null

    internal fun getPlanogramList(): MutableLiveData<List<Planogram>> {
        if (planogramList == null) {
            planogramList = MutableLiveData()
            loadData()
        }
        return planogramList as MutableLiveData<List<Planogram>>
    }

    private fun loadData() {
        val list = ArrayList<Planogram>()
        //list.add()
        planogramList?.postValue(list)

    }
}