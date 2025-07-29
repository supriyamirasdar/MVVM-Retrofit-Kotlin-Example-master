package com.lifestyle.retail_dashboard.view.login

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityChangePasswordBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.CryptoUtil
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.homescreen.activity.HomeScreenActivty
import com.lifestyle.retail_dashboard.view.login.model.LoginRequest
import com.lifestyle.retail_dashboard.view.login.model.LoginResponse
import com.lifestyle.retail_dashboard.view.login.model.api.LoginApis

class ChangePasswordActivity : AppCompatActivity(), LoginApis.LoginDetailListener {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var activity : ChangePasswordActivity
    private var userId: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        initView()
        clickListener()
    }

    private fun initView() {
        binding.toolbarLayout.tvToolBarTitle.text = "Change Password"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        userId = intent.getStringExtra("itemSelected")

        binding.btnSubmit.setComponentName("Update")
    }


    private fun clickListener() {
        binding.btnSubmit.addSlideListener { slider, done ->
            if (done){
                updatePassword()
            }
            slider.reset()
        }
    }

    private fun updatePassword() {
        val oldPass = binding.etOldPassword.text.toString()
        val newPass = binding.etNewPassword.text.toString()
        val confirmPass = binding.etConfirmPass.text.toString()

        if (newPass.isEmpty())
            CommonUtility.showSnackBar(activity, "Enter New Password")
        else if (confirmPass.isEmpty())
            CommonUtility.showSnackBar(activity, "Enter Confirm Password")
        else if (!newPass.equals(confirmPass,true))
            CommonUtility.showSnackBar(activity, "New & Confirm Password does not match")
        else
            changePassword(oldPass,newPass)
    }

    private fun changePassword(oldPass: String, newPass: String) {
        val api = LoginApis(activity)
        val request = LoginRequest()
        request.userId = userId
        request.password = CryptoUtil.encrypt(newPass)
        request.oldPassword = CryptoUtil.encrypt(oldPass)
        password = newPass
        api.changePassword(activity,request)
    }

    override fun loginDetail(response: LoginResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else if (response.statusCode == 200 && response.statusMessage?.equals("Y")!!){
            PreferenceUtils.setUserId(userId)
            PreferenceUtils.setUserPass(password)
            CommonUtility.showSnackBar(activity, response.statusErrMessage)
            Handler().postDelayed({
                Utility.goToNextScreen(activity, LoginActivity::class.java)
                finish()
            }, 400)
        }else
            CommonUtility.showSnackBar(activity, response.statusErrMessage)
    }
}