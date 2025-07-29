package com.lifestyle.retail_dashboard.view.inventory_managment.model

import java.io.Serializable

class InventoryManagement : Serializable{
    var areaNum = 0
    var rgnNum = 0
    var locNum = 0
    var brandId = 0
    var areaNm: String? = null
    var rgnNm: String? = null
    var locNm: String? = null
    var brandNm: String? = null
    var reqType: String? = null
    var soh = 0.0
    var dc = 0.0
    var fillratePer = 0.0
    var brokenPer = 0.0
    var freshnessPer = 0.0
    var inventoryList: List<InventoryManagementDTO>? = null

}