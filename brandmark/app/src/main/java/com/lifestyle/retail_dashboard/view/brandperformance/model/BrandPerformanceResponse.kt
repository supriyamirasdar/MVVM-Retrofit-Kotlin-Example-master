package com.lifestyle.retail_dashboard.view.brandperformance.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BrandPerformanceResponse : Serializable {
    @SerializedName("serverErrormsg")
    var serverErrormsg: String? = null

    @SerializedName("productDashboard")
    var productDashboard = mutableListOf<BrandPerformanceList>()

    var reqArea: String? = null
    var reqRegion: String? = null
    var reqStore: String? = null
}