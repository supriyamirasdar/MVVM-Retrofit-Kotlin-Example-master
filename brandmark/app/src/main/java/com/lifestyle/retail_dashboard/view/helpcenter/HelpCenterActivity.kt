package com.lifestyle.retail_dashboard.view.helpcenter

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityHelpCenterBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Utility

class HelpCenterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpCenterBinding
    private lateinit var activity: HelpCenterActivity
    private val REQUEST_PHONE_CALL = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help_center)

        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        binding.toolbarLayout.tvToolBarTitle.text = "Help Center"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE


        val fade_in = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        fade_in.duration = 800
        fade_in.fillAfter = true
        binding.logo.startAnimation(fade_in)


        binding.tvAppVersion.text = "Version: ${Utility.appVersion()}  Dt: ${resources.getString(R.string.releaseDate)}"


        binding.tvSupportMail.setOnClickListener { v: View? ->
            val email = binding.tvSupportMail.text.toString()
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
            /*i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT   , "body of email");*/
            try {
            startActivity(Intent.createChooser(i, "Sending mail..."))
            } catch (ex: ActivityNotFoundException) {
                CommonUtility.showSnackBar(this, "There are no email clients installed.")
            }
        }

        binding.tvSupportcall.setOnClickListener { v: View? ->
            try {
                val mobileNo = binding.tvSupportcall.text.toString()
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$mobileNo")
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
                } else {
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.d("Abrar", "Exception: ${e.localizedMessage}")
            }
        }
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