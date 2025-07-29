package com.lifestyle.retail_dashboard.view.brandperformance.model

import android.view.View
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BrandPerformanceList : Serializable {
    var areaNm: String? = null
    var rgnNm: String? = null
    var locNm: String? = null
    var areaNum: String? = null
    var rgnNum: String? = null
    var locNum: String? = null
    var brandId: String? = null
    var brandNm: String? = null

    @SerializedName("productTimeIntervalList")
    var productTimeIntervalList: List<SalePerformance>? = null


    interface OnItemClickListener{
        fun onItemClick(view: View, item: BrandPerformanceList)
    }
}