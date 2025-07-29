package com.lifestyle.retail_dashboard.view.brandperformance.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandChartResponse
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceResponse
import com.lifestyle.retail_dashboard.view.inventory_managment.model.InventoryManagementResponse

class BrandPerformanceApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var brandPerfomanceListner: BrandPerformanceDetailListener? = null
    private var brandGraphListener: BrandGraphListener? = null
    private var brandRankResponseListener: BrandRankListener? = null
    private var inventoryMangementListener: InventoryManagementListener? = null

    interface BrandPerformanceDetailListener {
        fun brandPerformance(response: BrandPerformanceResponse)
    }

    interface BrandGraphListener {
        fun brandPerfGraph(response: BrandChartResponse)
    }

    interface BrandRankListener {
        fun brandRank(response: BrandRankResponse)
    }

    interface InventoryManagementListener {
        fun inventoryManagement(response: InventoryManagementResponse)
    }

    fun getInventoryManagement(inventoryMangementListener: InventoryManagementListener?, request: BrandPerformanceRequest?) {
        this.inventoryMangementListener = inventoryMangementListener
        apiNetwork.network(apiNetwork.getApiInterface().getInventoryManagement(request),
                Constant.GET_INVENTORY_MANAGMENT, this)
    }

    fun getBrandRankList(brandRankResponseListener: BrandRankListener?, request: BrandPerformanceRequest?) {
        this.brandRankResponseListener = brandRankResponseListener
        apiNetwork.network(apiNetwork.getApiInterface().getBrandRankList(request),
                Constant.GET_BRAND_RANK, this)
    }

    fun getBrandPerformance(brandPerfomanceListner: BrandPerformanceDetailListener?, request: BrandPerformanceRequest?) {
        this.brandPerfomanceListner = brandPerfomanceListner
        apiNetwork.network(apiNetwork.getApiInterface().getBrandPerformance(request),
                Constant.GET_BRAND_PERFROMANCE, this)
    }

    fun getBrandPerfGraph(brandGraphListener: BrandGraphListener?, request: BrandPerformanceRequest?) {
        this.brandGraphListener = brandGraphListener
        apiNetwork.network(apiNetwork.getApiInterface().getBrandPerfGraph(request),
                Constant.GET_BRAND_GRAPH, this)
    }

    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        if (requestCode == Constant.GET_BRAND_PERFROMANCE) {
            if (response is BrandPerformanceResponse)
                brandPerfomanceListner?.brandPerformance(response)
        }else if (requestCode == Constant.GET_BRAND_GRAPH) {
            if (response is BrandChartResponse)
                brandGraphListener?.brandPerfGraph(response)
        }else if (requestCode == Constant.GET_BRAND_RANK) {
            if (response is BrandRankResponse)
                brandRankResponseListener?.brandRank(response)
        }else if (requestCode == Constant.GET_INVENTORY_MANAGMENT) {
            if (response is InventoryManagementResponse)
                inventoryMangementListener?.inventoryManagement(response)
        }
    }
}