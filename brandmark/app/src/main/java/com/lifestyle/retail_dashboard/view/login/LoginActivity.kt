package com.lifestyle.retail_dashboard.view.login

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityLoginBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.CryptoUtil
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility.goToNextScreen
import com.lifestyle.retail_dashboard.view.homescreen.activity.HomeScreenActivty
import com.lifestyle.retail_dashboard.view.login.model.LoginRequest
import com.lifestyle.retail_dashboard.view.login.model.LoginResponse
import com.lifestyle.retail_dashboard.view.login.model.api.LoginApis

class LoginActivity : AppCompatActivity(), LoginApis.LoginDetailListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var activity: LoginActivity
    private var userId : String? = null
    private  var password : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initView()
        loginTextWatcher()
    }


    fun initView() {
        binding.toolbarTop.tvToolBarTitle.text = "LOGIN"
        binding.toolbarTop.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        binding.btnForgotPassword.setOnClickListener {
            goToNextScreen(activity, ForgotPasswordActivity::class.java)
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.userId.text.toString()
            val pass = binding.password.text.toString()

            when {
                user.isEmpty() -> {
                    CommonUtility.showSnackBar(activity, "Enter User Id")
                }
                user.length < 4 -> {
                    CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                }
                pass.isEmpty() -> {
                    CommonUtility.showSnackBar(activity, "Enter Password")
                }
                else -> {
                    userId = user
                    password = pass
                    checkLogin()
                }
            }
        }
    }


    private fun loginTextWatcher() {
        val commentWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 3) {
                    binding.correct.visibility = View.VISIBLE
                    binding.wrong.visibility = View.INVISIBLE
                } else {
                    binding.correct.visibility = View.GONE
                    binding.wrong.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        }
        binding.userId.addTextChangedListener(commentWatcher)
    }

    override fun onResume() {
        super.onResume()
        autoLogin()
    }

    private fun autoLogin() {
        Log.d("Abrar","User ${PreferenceUtils.getUserId()} pass ${PreferenceUtils.getUserPass()}")
        if (PreferenceUtils.getUserId() != null
                && PreferenceUtils.getUserPass() != null) {

            userId = PreferenceUtils.getUserId()
            password = PreferenceUtils.getUserPass()
            binding.userId.setText(userId)
            checkLogin()
        }
    }

    private fun checkLogin() {
        val api = LoginApis(activity)
        val  request = LoginRequest()
        request.userId = userId
        request.password = CryptoUtil.encrypt(password)
        api.checkLogin(activity, request)
    }

    override fun loginDetail(response: LoginResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else if (response.statusCode == 200 && response.usrId != null){
            PreferenceUtils.setUserId(userId)
            PreferenceUtils.setUserPass(password)
            PreferenceUtils.setUserName("${response.fName} ${response.lName}")
            PreferenceUtils.setAuthKey(response.authKey)
            PreferenceUtils.setMobileNo(response.mobileNum)
            PreferenceUtils.setEmailId(response.emailId)
            PreferenceUtils.setBrandPerfDetail(response.brandUsrDetailsList)

            CommonUtility.showSnackBar(activity, "Login Successful.")
            Handler().postDelayed({
                goToNextScreen(activity, HomeScreenActivty::class.java)
                finish()
            }, 400)
        }else if (response.isFirstLogin){
            CommonUtility.showSnackBar(activity, response.statusErrMessage)
            Handler().postDelayed({
                goToNextScreen(activity, ChangePasswordActivity::class.java, userId)
                finish()
            }, 400)
        }
        else if (response.statusErrMessage != null)
            CommonUtility.showSnackBar(activity, response.statusErrMessage)
        else
            CommonUtility.showSnackBar(activity, "No User Detail Found.Contact Admin.")

    }
}