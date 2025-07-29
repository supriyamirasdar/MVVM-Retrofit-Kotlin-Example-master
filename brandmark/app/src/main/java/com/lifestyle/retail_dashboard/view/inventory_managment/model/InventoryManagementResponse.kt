package com.lifestyle.retail_dashboard.view.inventory_managment.model

import com.lifestyle.retail_dashboard.view.brand_ranking.model.BrandRankData
import java.io.Serializable

class InventoryManagementResponse : Serializable {
    var serverErrormsg: String? = null
    val brandAppInvntData : MutableList<InventoryManagement>?= null
}