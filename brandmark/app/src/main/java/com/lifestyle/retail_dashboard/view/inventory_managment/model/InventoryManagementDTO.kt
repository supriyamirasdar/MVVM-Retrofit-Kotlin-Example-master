package com.lifestyle.retail_dashboard.view.inventory_managment.model

import java.io.Serializable

class InventoryManagementDTO : Serializable{
    var respType: String? = null
    var fillratePer = 0.0
    var brokenPer = 0.0
    var freshnessPer = 0.0
}