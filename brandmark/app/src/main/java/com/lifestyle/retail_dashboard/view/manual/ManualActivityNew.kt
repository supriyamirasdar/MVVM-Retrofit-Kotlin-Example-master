package com.lifestyle.retail_dashboard.view.manual

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityManualBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.OpenLocalPDF
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ManualActivityNew : AppCompatActivity() {

    private lateinit var binding: ActivityManualBinding
    private lateinit var activity: ManualActivityNew

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manual)

        initView()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        binding.toolbarLayout.tvToolBarTitle.text = "Manual"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        //showPdfFromAssets(getPdfNameFromAssets())

        //CopyReadAssets();

        OpenLocalPDF(activity, getPdfNameFromAssets()).execute()

        /*Handler().postDelayed({
            //val file: File = File(Environment.getExternalStorageDirectory().toString() + "/test.pdf")
            val url = File(Environment.getExternalStorageDirectory().toString() + "/BrandMark_Download/" + getPdfNameFromAssets()) // -> filename = maven.pdf


            //webview = findViewById<View>(R.id.webview) as WebView
            val settings: WebSettings = binding.pdfWebView.getSettings()
            settings.javaScriptEnabled = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.builtInZoomControls = true
            binding.pdfWebView.setWebChromeClient(WebChromeClient())
            binding.pdfWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + url.getAbsolutePath())
            // ContinueApp();
        }, 1000)*/


    }

    /*private fun showPdfFromAssets(pdfName: String) {
        binding.pdfView.fromAsset(pdfName)
                .password(null)
                .defaultPage(0)
                .onPageError { page, _ ->
                    CommonUtility.showSnackBar(this, "Error at page. $page")

                }
                .load()
    }*/

    fun getPdfNameFromAssets(): String {
        return "brandmark_user_manual.pdf"
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

    private fun CopyReadAssets() {
        val assetManager = assets
        var inst: InputStream? = null
        var out: OutputStream? = null
        val file = File(filesDir, getPdfNameFromAssets())
        try {
            inst = assetManager.open(getPdfNameFromAssets())
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE)
            copyFile(inst, out)
            inst.close()
            inst = null
            out.flush()
            out.close()
            out = null
        } catch (e: Exception) {
            Log.e("tag", e.message!!)
        }
        val intent = Intent(Intent.ACTION_VIEW)
        Log.e("tag PATH", "file://$filesDir/brandmark_user_manual.pdf")
        intent.setDataAndType(Uri.parse("file://$filesDir/brandmark_user_manual.pdf"), "application/pdf")
        startActivity(intent)
    }

    @Throws(IOException::class)
    private fun copyFile(inst: InputStream?, out: OutputStream?) {
        val buffer = ByteArray(1024)
        var read: Int
        while (inst?.read(buffer).also { read = it!! } != -1) {
            if (out != null) {
                out.write(buffer, 0, read)
            }
        }
    }
}