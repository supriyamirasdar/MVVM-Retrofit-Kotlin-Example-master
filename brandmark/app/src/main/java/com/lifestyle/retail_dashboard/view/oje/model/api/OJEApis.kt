package com.lifestyle.retail_dashboard.view.oje.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.oje.model.*


class OJEApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var ojeListListener: OJEListListener? = null
    private var ojeUpdateListener: OJEUpdateListener? = null

    interface BrandEmployeeListListener {
        fun employeeList(response: EmployeeListResponse)
    }

    interface OJEUpdateListener {
        fun ojeUpdate(response: OJEResponse)
    }

    interface OJEListListener {
        fun getOJEList(response: List<OJEList>?)
    }

    fun getOJEList(ojeListListener: OJEListListener?) {
        this.ojeListListener = ojeListListener
        apiNetwork.network(apiNetwork.getApiInterface().getOJEList(),
                Constant.GET_OJE_LIST, this)
    }

    fun updateOJEScore(ojeUpdateListener: OJEUpdateListener, request: OJERequest) {
        this.ojeUpdateListener = ojeUpdateListener
        apiNetwork.network(apiNetwork.getApiInterface().updOjeScore(request), Constant.UPDATE_OJE_SCORE, this)
    }


    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        if (requestCode == Constant.GET_OJE_LIST) {
            if (response is OJEListResponse) {
                if (response.serverErrormsg != null)
                    CommonUtility.showSnackBar(activity,response.serverErrormsg)
                else
                    ojeListListener?.getOJEList(response.ojeList)
            }
        }else if (requestCode == Constant.UPDATE_OJE_SCORE) {
            if (response is OJEResponse) {
                ojeUpdateListener?.ojeUpdate(response)
            }
        }
    }
}