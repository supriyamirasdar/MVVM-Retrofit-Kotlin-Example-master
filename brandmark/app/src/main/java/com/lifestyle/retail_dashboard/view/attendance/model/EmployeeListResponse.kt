package com.lifestyle.retail_dashboard.view.attendance.model

class EmployeeListResponse {
    var serverErrormsg: String? = null
    var brandId: String? = null
    var brandName: String? = null
    var brandUsrDetailsList = mutableListOf<EmployeeDetail>()
}