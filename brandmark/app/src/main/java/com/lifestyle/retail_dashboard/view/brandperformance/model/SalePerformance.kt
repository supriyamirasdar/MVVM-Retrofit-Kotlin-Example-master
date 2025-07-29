package com.lifestyle.retail_dashboard.view.brandperformance.model

import java.io.Serializable

class SalePerformance : Serializable {
    var respType: String? = null
    var sales = 0.0
    var lflSales = 0.0
    var salesBudgt = 0.0
    var lflSalesBudgt = 0.0
    var achievePerc = 0.0
    var lflAchievePerc = 0.0
    var slsGrowthPerc = 0.0
    var slsLflGrowthPerc = 0.0
    var contributePerc = 0.0
    var slsQty = 0.0
    var slsQtyPerc = 0.0
    var lflSlsQty = 0.0
    var lflSlsQtyPerc = 0.0
    var disc = 0.0
    var discPerc = 0.0
    var mrgn = 0.0
    var mrgnPerc = 0.0
}