package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.view.oje.model.OJEResponse
import com.lifestyle.retail_dashboard.view.oje.model.api.OJEApis
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.NewRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.UpdateRACRequest

class RACApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var ojeListListener :OJEApis.OJEUpdateListener? = null
    private var getRACListListener :GETRACListListener? = null


    interface GETRACListListener {
        fun getRACList(response: GetRACResponse)
    }

    fun createNewRetailAudit(ojeListListener: OJEApis.OJEUpdateListener?,request : NewRACRequest) {
        this.ojeListListener = ojeListListener
        apiNetwork.network(apiNetwork.getApiInterface().createRetailAudit(request),
                Constant.CREATE_NEW_REATIL_AUDIT, this)
    }

    fun updateRAC(ojeListListener: OJEApis.OJEUpdateListener?,request : UpdateRACRequest) {
        this.ojeListListener = ojeListListener
        apiNetwork.network(apiNetwork.getApiInterface().updateRACAudit(request),
                Constant.CREATE_NEW_REATIL_AUDIT, this)
    }


    fun getRACList(getRACListListener: GETRACListListener?, request : GetRACRequest) {
        this.getRACListListener = getRACListListener
        apiNetwork.network(apiNetwork.getApiInterface().getRACList(request),
                Constant.GET_RAC_LIST, this)
    }


    override fun onSuccess(response: Any?, requestCode: Int) {
        if (requestCode == Constant.CREATE_NEW_REATIL_AUDIT) {
            if (response is OJEResponse) {
                ojeListListener?.ojeUpdate(response)
            }
        }else if (requestCode == Constant.GET_RAC_LIST) {
            if (response is GetRACResponse)
                getRACListListener?.getRACList(response)
        }
    }
}