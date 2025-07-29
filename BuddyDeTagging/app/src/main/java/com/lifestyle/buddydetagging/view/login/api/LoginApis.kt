package com.lifestyle.buddydetagging.view.login.api

import android.app.Activity
import android.os.Handler
import com.lifestyle.buddydetagging.network.ApiNetwork
import com.lifestyle.buddydetagging.utils.CommonUtility
import com.lifestyle.buddydetagging.utils.Constant
import com.lifestyle.buddydetagging.utils.Utility
import com.lifestyle.buddydetagging.view.login.dto.LoginResponseDTO
import com.lifestyle.buddydetagging.view.login.dto.UserLoginDTO
import com.lifestyle.buddydetagging.view.login.model.LoginResponse
import com.lifestyle.landmark_covid_care.view.homescreen.model.AppVersion
import com.lifestyle.landmark_covid_care.view.login.model.ForgotPasswordResponse
import com.lifestyle.landmark_covid_care.view.login.model.LoginRequest

class LoginApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var loginDetailListener: LoginDetailListener? = null
    private var appVersionListener: AppVersionListener? = null

    interface LoginDetailListener {
        fun loginDetail(response: LoginResponseDTO)
    }

    interface AppVersionListener {
        fun appVersion(response: AppVersion)
    }

    fun checkLogin(loginDetailListener: LoginDetailListener?, request: UserLoginDTO?) {
        this.loginDetailListener = loginDetailListener

        apiNetwork.network(apiNetwork.getApiInterface().getLoginDetail(request),
                Constant.LOGIN_DETAIL, this)
    }


    fun checkAppVersion(appVersionListener: AppVersionListener?) {
        this.appVersionListener = appVersionListener
        apiNetwork.network(apiNetwork.getApiInterface().checkAppVersion(),
                Constant.CHECK_APP_VERSION, this)
    }

    /*fun changePassword(loginDetailListener: LoginDetailListener?, request: LoginRequest?) {
        this.loginDetailListener = loginDetailListener
        apiNetwork.network(apiNetwork.getApiInterface().changePassword(request),
                Constant.CHANGE_PASSWORD, this)
    }*/

    /*fun forgotPassword(request: LoginRequest?) {
        apiNetwork.network(apiNetwork.getApiInterface().forgotPassword(request),
                Constant.FORGOT_PASSWORD, this)
    }*/

    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        if (requestCode == Constant.LOGIN_DETAIL) {
            if (response is LoginResponseDTO) {
                loginDetailListener?.loginDetail(response)
            }
        }/*else if (requestCode == Constant.CHANGE_PASSWORD) {
            if (response is LoginResponseDTO) {
                loginDetailListener?.loginDetail(response)
            }
        }*/else if (requestCode == Constant.CHECK_APP_VERSION) {
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