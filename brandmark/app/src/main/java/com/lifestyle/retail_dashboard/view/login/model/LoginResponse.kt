package com.lifestyle.retail_dashboard.view.login.model

class LoginResponse {
    var serverErrormsg: String? = null
    var statusCode =0
    var statusMessage: String? = null
    var statusErrMessage: String? = null
    var authKey: String? = null
    var usrId: String? = null
    var fName: String? = null
    var lName: String? = null
    var mobileNum: String? = null
    var emailId: String? = null
    var isFirstLogin : Boolean = false
    var brandUsrDetailsList: List<BrandPerfUserDetail>? = null
}