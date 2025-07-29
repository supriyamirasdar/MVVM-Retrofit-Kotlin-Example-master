package com.lifestyle.retail_dashboard.view.report.model

import java.io.Serializable

class ProductTrendsData : Serializable {
    var divNum: String? = null
    var grpNum: String? = null
    var dptNum: String? = null
    var productTrendsList: List<ProductTrends>? = null
}