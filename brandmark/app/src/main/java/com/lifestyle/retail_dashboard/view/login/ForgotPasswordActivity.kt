package com.lifestyle.retail_dashboard.view.login

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityForgotPasswordBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.view.login.model.LoginRequest
import com.lifestyle.retail_dashboard.view.login.model.api.LoginApis

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var activity: ForgotPasswordActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)

        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        binding.toolbarLayout.tvToolBarTitle.text = "Forgot Password"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        binding.btnSubmit.setComponentName("Submit")

        binding.btnSubmit.addSlideListener { slider, done ->
            if (done){
                forgotPassword()
            }
            slider.reset()
        }
    }

    private fun forgotPassword() {
        val empId = binding.userId.text.toString()
        val empEmail = binding.emailId.text.toString()

        if (empId.isNotEmpty() && empEmail.isNotEmpty()){
            val request = LoginRequest()
            request.userId = empId
            request.emailId = empEmail
            request.reqType = "BRND"
            val api = LoginApis(activity)
            api.forgotPassword(request)
        }else
            CommonUtility.showSnackBar(activity,"Enter User Id & Email Id")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}