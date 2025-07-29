package com.lifestyle.retail_dashboard.view.login.model.api

import android.app.Activity
import android.os.Handler
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.homescreen.model.AppVersion
import com.lifestyle.retail_dashboard.view.login.model.ForgotPasswordResponse
import com.lifestyle.retail_dashboard.view.login.model.LoginRequest
import com.lifestyle.retail_dashboard.view.login.model.LoginResponse

class LoginApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var loginDetailListener: LoginDetailListener? = null
    private var appVersionListener: AppVersionListener? = null

    interface LoginDetailListener {
        fun loginDetail(response: LoginResponse)
    }

    interface AppVersionListener {
        fun appVersion(response: AppVersion)
    }

    fun checkLogin(loginDetailListener: LoginDetailListener?, request: LoginRequest?) {
        this.loginDetailListener = loginDetailListener
        apiNetwork.network(apiNetwork.getApiInterface().getLoginDetail(request),
                Constant.LOGIN_DETAIL, this)
    }


    fun checkAppVersion(appVersionListener: AppVersionListener?) {
        this.appVersionListener = appVersionListener
        apiNetwork.network(apiNetwork.getApiInterface().checkAppVersion(),
                Constant.CHECK_APP_VERSION, this)
    }

    fun changePassword(loginDetailListener: LoginDetailListener?, request: LoginRequest?) {
        this.loginDetailListener = loginDetailListener
        apiNetwork.network(apiNetwork.getApiInterface().changePassword(request),
                Constant.CHANGE_PASSWORD, this)
    }

    fun forgotPassword(request: LoginRequest?) {
        apiNetwork.network(apiNetwork.getApiInterface().forgotPassword(request),
                Constant.FORGOT_PASSWORD, this)
    }

    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        if (requestCode == Constant.LOGIN_DETAIL) {
            if (response is LoginResponse) {
                loginDetailListener?.loginDetail(response)
            }
        }else if (requestCode == Constant.CHANGE_PASSWORD) {
            if (response is LoginResponse) {
                loginDetailListener?.loginDetail(response)
            }
        }else if (requestCode == Constant.CHECK_APP_VERSION) {
            if (response is AppVersion) {
                appVersionListener?.appVersion(response)
            }
        }else if (requestCode == Constant.FORGOT_PASSWORD) {
            if (response is ForgotPasswordResponse) {
                if (response.serverErrormsg != null)
                    CommonUtility.showSnackBar(activity,response.serverErrormsg)
                else{
                    if (response.successIndi){
                        CommonUtility.showSnackBar(activity,"Password send to your email Id. Please check the mail.")
                        Handler().postDelayed({
                            activity.finish()
                        },400)
                    }else
                        CommonUtility.showSnackBar(activity,"Failed!!! Please enter valid User Id & Email Id.")
                }
            }
        }
    }
}