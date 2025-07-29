package com.lifestyle.retail_dashboard.view.brand_ranking.model

import java.io.Serializable

class BrandRankResponse : Serializable{
    var serverErrormsg: String? = null
    val brandRankData : MutableList<BrandRankData>?= null
}