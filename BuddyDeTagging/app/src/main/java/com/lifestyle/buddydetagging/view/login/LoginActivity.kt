package com.lifestyle.buddydetagging.view.login

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.lifestyle.buddydetagging.BuildConfig
import com.lifestyle.buddydetagging.R
import com.lifestyle.buddydetagging.constant.GeneralConstant
import com.lifestyle.buddydetagging.databinding.ActivityDtLoginBinding
import com.lifestyle.buddydetagging.utils.CommonUtility
import com.lifestyle.buddydetagging.utils.PreferenceUtils
import com.lifestyle.buddydetagging.utils.Utility
import com.lifestyle.buddydetagging.utils.Utility.goToNextScreen
import com.lifestyle.buddydetagging.utils.VersionHelper
import com.lifestyle.buddydetagging.view.base.BaseCompatActivity
import com.lifestyle.buddydetagging.view.detagging.DtHomeActivity
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppVrsnResponse
import com.lifestyle.buddydetagging.view.detagging.model.StylusAppsVrsnCheckRequest
import com.lifestyle.buddydetagging.view.login.api.LoginApis
import com.lifestyle.buddydetagging.view.login.dto.LoginResponseDTO
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO
import com.lifestyle.buddydetagging.view.login.dto.UserDTO
import com.lifestyle.buddydetagging.view.login.dto.UserLoginDTO
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class LoginActivity : AppCompatActivity(), LoginApis.LoginDetailListener {
    private lateinit var binding: ActivityDtLoginBinding
    public lateinit var activity: LoginActivity
    private var userId: String? = null
    private var password: String? = null

   // private var activity: LoginActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_login)

        initView()
        loginTextWatcher()
    }


    fun initView() {
        binding.toolbarTop.tvToolBarTitle.text = "LOGIN"
        binding.toolbarTop.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg)

        binding.btnForgotPassword.setOnClickListener {
            goToNextScreen(activity, DtForgotPasswordActivity::class.java)
        }

        binding.btnLogin.setOnClickListener {
            doLogin()
        }


        binding.password.setOnEditorActionListener(OnEditorActionListener { textView, i, keyEvent -> // If user press done key
            if (i == EditorInfo.IME_ACTION_DONE) {
                // Get the input method manager
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(binding.password.getWindowToken(), 0)
                val itemCode: String = binding.password.getText().toString().trim { it <= ' ' }
                if (validLogin()) {

                    binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg)
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg)
                }
                // if (PreferenceUtils.get_devicedata_type() == null) PreferenceUtils.set_devicedata_type("")
                doLogin()
                return@OnEditorActionListener true
            }
            false
        })
        binding.tvAppVersion.setText("Version " + Utility.appVersion())
    }

    private fun doLogin() {
        if (validLogin()) {
            val user = binding.userId.text.toString()
            val pass = binding.password.text.toString()

            when {
                user.isEmpty() -> {
                    //CommonUtility.showSnackBar(activity, "Enter User Id")
                    val titleST = "De-Tagging";
                    val messageST = "Enter User Id.";
                    showAlertDialog(activity, titleST, messageST);
                }
                user.length < 7 -> {
                    //CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                    val titleST = "De-Tagging";
                    val messageST = "Enter Valid User Id.";
                    showAlertDialog(activity, titleST, messageST);
                }
                pass.isEmpty() -> {
                    //CommonUtility.showSnackBar(activity, "Enter Password")
                    val titleST = "De-Tagging";
                    val messageST = "Enter Password.";
                    showAlertDialog(activity, titleST, messageST);
                }
                pass.length < 6 -> {
                    //CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                    val titleST = "De-Tagging";
                    val messageST = "Enter Valid password.";
                    showAlertDialog(activity, titleST, messageST);

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
                if (charSequence.length > 5) {
                    binding.correct.visibility = View.VISIBLE
                    binding.wrong.visibility = View.INVISIBLE
                } else {
                    binding.correct.visibility = View.GONE
                    binding.wrong.visibility = View.VISIBLE
                }
                if (validLogin()) {

                    binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg)
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        }

        val pwdWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (validLogin()) {
                    binding.btnLogin.setBackgroundResource(R.drawable.toolbar_bg)
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.button_disable_bg)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        }

        binding.userId.addTextChangedListener(commentWatcher)

        binding.password.addTextChangedListener(pwdWatcher)
    }

    private fun validLogin(): Boolean {
        val user = binding.userId.text.toString()
        val pass = binding.password.text.toString()

        when {
            user.isEmpty() -> {
                //CommonUtility.showSnackBar(activity, "Enter User Id")
                return false;
            }
            user.length < 7 -> {
                //CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                return false;
            }
            pass.isEmpty() -> {
                //CommonUtility.showSnackBar(activity, "Enter Password")
                return false;
            }
            pass.length < 6 -> {
                //CommonUtility.showSnackBar(activity, "Enter Valid User Id")
                return false;
            }
            else -> {
                return true;
            }
        }
    }

    override fun onResume() {
        super.onResume()
        autoLogin()
    }


    private fun autoLogin() {
        Log.d("Abrar", "User ${PreferenceUtils.getUserId()} pass ${PreferenceUtils.getUserPass()}")
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
        val request = UserLoginDTO()
        request.userId = userId
        request.pwd = password;// CryptoUtil.encrypt(password)
        request.role = 2;
        api.checkLogin(activity, request)
    }

    override fun loginDetail(response: LoginResponseDTO) {
        /* if (response.serverErrormsg != null) {
             //CommonUtility.showSnackBar(activity, response.serverErrormsg)
             val titleST = "De-Tagging";
             val messageST = "" + response.serverErrormsg;
             showAlertDialog(activity, titleST, messageST);
         } else */
        if (response.statusCode == 200) {

            var userDto = UserDTO()
            userDto = response.data;
            PreferenceUtils.setUserId(response.data.userId)
            PreferenceUtils.setUserPass(password)
            PreferenceUtils.setUserName("${response.data.getfName()} ${response.data.getlName()}")
            //PreferenceUtils.setAuthKey(response.authKey)
            PreferenceUtils.setMobileNo(response.data.contactNo)
            PreferenceUtils.setEmailId(response.data.email)
            PreferenceUtils.setStoreId("0" + response.data.stroreId)
            PreferenceUtils.setStoreName(response.data.stroreName)
            PreferenceUtils.setRole(response.data.role)
            PreferenceUtils.setUserStatus(response.data.status)
            PreferenceUtils.setOuCode(response.data.ouCode)
            PreferenceUtils.setPwdChangeReq(response.data.pwdChgReq)


            if (response.data.pwdChgReq.equals("Y")) {
                CommonUtility.showSnackBar(activity, response.statusMessage)
                Handler().postDelayed({
                    goToNextScreen(activity, DtChangePasswordActivity::class.java, userId)
                    finish()
                }, 400)
            } else {
                CommonUtility.showSnackBar(activity, "Login Successful.")
                Handler().postDelayed({
                    goToNextScreen(activity, DtHomeActivity::class.java)
                    finish()
                }, 400)
            }

        } else if (response.statusCode == 400) {
            val titleST = "De-Tagging";
            val messageST = "" + response.statusMessage;
            showAlertDialog(activity, titleST, messageST);
        }

    }


    fun showAlertDialog(context: Context?, title: String, message: String) {
        val factory = LayoutInflater.from(context)
        val deleteDialogView = factory.inflate(R.layout.layout_dt_alert_dialog, null)
        val alertDialog = AlertDialog.Builder(context).setInverseBackgroundForced(true).create()
        alertDialog.setCancelable(false)
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setView(deleteDialogView)
        val titleTV = deleteDialogView.findViewById<TextView>(R.id.titleTV)
        val alertMessageTV = deleteDialogView.findViewById<TextView>(R.id.alertMessageTV)
        val yesBt = deleteDialogView.findViewById<TextView>(R.id.yesBt)
        val noBt = deleteDialogView.findViewById<TextView>(R.id.noBt)

        noBt.visibility = View.GONE

        titleTV.text = "" + title
        alertMessageTV.text = "" + message
        yesBt.text = "Okay"
        yesBt.setOnClickListener {
            alertDialog.dismiss()
            // finish();
        }
        noBt.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    fun ShowHidePass(view: View) {
        if (view.id == R.id.show_pass_btn) {
            if (binding.password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.drawable.ic_eye_open)

                //Show Password
                binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                (view as ImageView).setImageResource(R.drawable.ic_eye_close)

                //Hide Password
                binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // check for app version..




}