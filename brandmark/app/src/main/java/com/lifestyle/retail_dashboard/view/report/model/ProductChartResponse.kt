package com.lifestyle.retail_dashboard.view.report.model

import java.io.Serializable

class ProductChartResponse : Serializable {
    var serverErrormsg: String? = null
    var productTrendsData: List<ProductTrendsData>? = null
}