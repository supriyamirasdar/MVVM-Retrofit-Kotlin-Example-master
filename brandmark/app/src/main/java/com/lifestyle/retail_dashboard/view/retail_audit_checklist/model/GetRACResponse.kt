package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model

class GetRACResponse {
    var serverErrormsg: String? = null
    val brandStaffRacAuditList =  mutableListOf<GetRACList>()
    val brandRacAuditList =  mutableListOf<GetRACLineItem>()
}