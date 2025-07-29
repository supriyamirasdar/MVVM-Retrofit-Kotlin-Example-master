package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model

class NewRACRequest {
    var storeId: String? = null
    var brndId: String? = null
    var lsConceptMgr: String? = null
    var auditBy: String? = null
    var racStatus: String? = null
    var usrId: String? = null
    var appType: String? = null
    var remarks: String? = null
    val racAuditList = mutableListOf<RetailAudit>()
}