package com.lifestyle.retail_dashboard.view.oje.model


class OJEList {
    val ojeId = 0
    val ojeDesc: String? = null
    val ojeShrtDesc: String? = null
    val ojeSeq = 0
    val createDttm: String? = null
    var ojeScore = 0


    interface OnOJEReviewListener {
        fun onItemCheck(position:Int, item: OJEList)
    }
}