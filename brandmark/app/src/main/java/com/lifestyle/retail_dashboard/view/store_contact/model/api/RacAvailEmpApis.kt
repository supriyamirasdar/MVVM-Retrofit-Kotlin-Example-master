package com.lifestyle.retail_dashboard.view.store_contact.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api.RACApis
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpRequest
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpResponse

class RacAvailEmpApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface  {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var getRacAvailEmpListener : GETRacAvailEmpListener? = null

    interface GETRacAvailEmpListener {
        fun getRacAvailEmpListResponse(response: GetRacAvailEmpResponse)
    }

    fun getRacAvailEmpList(getRacAvailEmpListener: GETRacAvailEmpListener?, request : GetRacAvailEmpRequest) {
        this.getRacAvailEmpListener = getRacAvailEmpListener
        apiNetwork.network(apiNetwork.getApiInterface().getRacAvailEmpList(request),
                Constant.GET_RAC_EMP_LIST, this)
    }


    override fun onSuccess(response: Any?, requestCode: Int) {
        if (requestCode == Constant.GET_RAC_EMP_LIST) {
            if (response is GetRacAvailEmpResponse)
                getRacAvailEmpListener?.getRacAvailEmpListResponse(response)
        }
    }
}