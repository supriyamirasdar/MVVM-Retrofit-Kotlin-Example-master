package com.lifestyle.retail_dashboard.view.planogram.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramRequest
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramResponse

class PlanogramApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var planogramDetailListner: PlanogramDetailListner? = null

    interface PlanogramDetailListner {
        fun planogramDetail(response: PlanogramResponse?, reqCode: Int)
    }

    fun uploadPlanogram(planogramDetailListner: PlanogramDetailListner?, request: PlanogramRequest?) {
        this.planogramDetailListner = planogramDetailListner
        apiNetwork.network(apiNetwork.getApiInterface().uploadPlanogram(request),
                Constant.UPLODA_PLANOGRAM, this)
    }

    fun getPlanogramList(planogramDetailListner: PlanogramDetailListner?, request: PlanogramRequest?) {
        this.planogramDetailListner = planogramDetailListner
        apiNetwork.network(apiNetwork.getApiInterface().getPlanogramList(request),
                Constant.GET_PLANOGRAM_LIST, this)
    }


    fun getSinglePlanogramDetail(planogramDetailListner: PlanogramDetailListner?, request: PlanogramRequest?) {
        this.planogramDetailListner = planogramDetailListner
        apiNetwork.network(apiNetwork.getApiInterface().getPlanogramList(request),
                Constant.GET_PLANOGRAM_LIST, this)
    }

    fun deletePlanogram(planogramDetailListner: PlanogramDetailListner?, request: PlanogramRequest?) {
        this.planogramDetailListner = planogramDetailListner
        apiNetwork.network(apiNetwork.getApiInterface().deletePlanogram(request),
                Constant.DELETE_PLANOGRAM, this)
    }

    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        if (requestCode == Constant.UPLODA_PLANOGRAM
                ||requestCode == Constant.GET_PLANOGRAM_LIST
                ||requestCode == Constant.DELETE_PLANOGRAM) {
            if (response is PlanogramResponse) {
                planogramDetailListner?.planogramDetail(response, requestCode)
            }
        }
    }
}