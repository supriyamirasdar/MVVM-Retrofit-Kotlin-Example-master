package com.lifestyle.retail_dashboard.view.brand_ranking.model

import java.io.Serializable

class BrandRankData: Serializable{
    var areaNum = 0
    var rgnNum = 0
    var locNum = 0
    var brandId = 0
    var areaNm: String? = null
    var rgnNm: String? = null
    var locNm: String? = null
    var brandNm: String? = null
    var grpNum = 0
    var grpNm: String? = null
    var brandRankList = mutableListOf<BrandRankDTO>()
}