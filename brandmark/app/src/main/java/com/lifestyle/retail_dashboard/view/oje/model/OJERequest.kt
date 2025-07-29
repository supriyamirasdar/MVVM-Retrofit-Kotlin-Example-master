package com.lifestyle.retail_dashboard.view.oje.model

class OJERequest {
    var storeId: String? = null
    var empId: String? = null
    var bmReview: String? = null
    var brndId: String? = null
    var concept: String? = null
    var remarks: String? = null
    var createdUser: String? = null
    var createdBy: String? = null
    var lsConceptMgr: String? = null
    var ojeScoreList = mutableListOf<OJEScore>()
}