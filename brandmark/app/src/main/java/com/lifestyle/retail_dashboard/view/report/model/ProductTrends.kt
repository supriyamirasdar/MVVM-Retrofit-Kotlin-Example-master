package com.lifestyle.retail_dashboard.view.report.model

import java.io.Serializable

class ProductTrends : Serializable {
    var respType: String? = null
    var busDate: String? = null
    var sales = 0.0
    var slsGrowthPerc = 0.0
    var slsQty = 0.0
    var budgt = 0.0
    var budgtAchvPerc = 0.0
    var slsQtyGrowthPerc = 0.0
    var slsDscPerc = 0.0
    var slsDscGrowthPerc = 0.0
    var slsMrgPerc = 0.0
    var slsMrgGrowthPerc = 0.0
}