package com.lifestyle.retail_dashboard.view.brandperformance.model

import com.lifestyle.retail_dashboard.view.report.model.ProductTrendsData
import java.io.Serializable

class BrandChartResponse : Serializable {
    var serverErrormsg: String? = null
    var productTrendsData: List<ProductTrendsData>? = null
}