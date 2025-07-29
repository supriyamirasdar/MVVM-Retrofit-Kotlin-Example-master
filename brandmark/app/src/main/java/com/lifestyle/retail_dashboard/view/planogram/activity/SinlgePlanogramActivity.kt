package com.lifestyle.retail_dashboard.view.planogram.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivitySinglePlanogramBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant.dateFormat
import com.lifestyle.retail_dashboard.utils.Constant.inputDateFormat
import com.lifestyle.retail_dashboard.utils.DownloadTask
import com.lifestyle.retail_dashboard.view.planogram.adapter.ImageListAdapter
import com.lifestyle.retail_dashboard.view.planogram.model.Planogram
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramRequest
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramResponse
import com.lifestyle.retail_dashboard.view.planogram.model.api.PlanogramApis
import okhttp3.*
import java.io.File
import java.io.IOException


class SinlgePlanogramActivity : AppCompatActivity(), PlanogramApis.PlanogramDetailListner {
    private lateinit var binding: ActivitySinglePlanogramBinding
    private lateinit var activity: SinlgePlanogramActivity
    private var planogram: Planogram? = null
    private var imageUrlList = mutableListOf<Planogram>()
    private var mAdapter: ImageListAdapter? = null

    private var urlString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_planogram)

        intView()
        imageRecycler()
        getPlanogramDetail()
    }

    @SuppressLint("SetTextI18n")
    private fun intView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        planogram = intent.extras?.getParcelable<Planogram>("planogram")

        binding.toolbarLayout.tvToolBarTitle.text = "Planogram"
        if (planogram != null) {
            binding.toolbarLayout.tvToolBarSubtitle.text = "P:${planogram?.planogramId}"
            binding.planogram = planogram
            binding.inputDF = inputDateFormat
            binding.outputDF = dateFormat

            urlString = planogram?.filePath

            binding.tvBrand.setOnClickListener {
               // downlkoadFIle()

               // val URL = "https://121.244.54.173:9011/ImageViewer/T-BASE/153/153_Test.pdf";
                DownloadTask(activity, urlString)

                /* val path = Uri.parse(planogram?.filePath)
                 val intent = Intent(Intent.ACTION_VIEW, path)
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                 intent.setPackage("com.android.chrome")
                 try {
                     startActivity(intent)
                 } catch (e: ActivityNotFoundException) {
                     Log.d("Abrar","Exception: ${e.localizedMessage}")
                     intent.setPackage(null)
                     startActivity(intent)
                 }*/
            }
        }
    }


    private fun imageRecycler() {
        mAdapter = ImageListAdapter(activity, imageUrlList)
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.imageRecyclerView.adapter = mAdapter
    }

    private fun getPlanogramDetail() {
        imageUrlList.clear()
        val request = PlanogramRequest()
        request.planogramId = planogram?.planogramId
        //request.planogramId = "124"
        request.required = "IMG"
        val api = PlanogramApis(activity)
        api.getPlanogramList(activity, request)
    }

    override fun planogramDetail(response: PlanogramResponse?, reqCode: Int) {
        if (response != null) {
            if (response.serverErrormsg != null)
                CommonUtility.showSnackBar(activity, response.serverErrormsg)
            else {
                if (response.planogramList != null) {
                    if (response.planogramList.isNotEmpty())
                        imageUrlList.addAll(response.planogramList)
                }
            }
        }
        if (imageUrlList.size > 0) {
            binding.imageRecyclerView.visibility = View.VISIBLE
            binding.tvErrorMsg.visibility = View.GONE
        } else {
            binding.imageRecyclerView.visibility = View.GONE
            binding.tvErrorMsg.visibility = View.VISIBLE
        }
        mAdapter?.notifyDataSetChanged()
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

    @Suppress("DEPRECATION")
    fun downlkoadFIle() {
        try {
            var urlString: String = "http://148.66.133.56/res/bsvStock/ILETS//13012021_ielts_mocktest_7894564565.pdf";//"https://121.244.54.173:9011/ImageViewer/T-BASE/153/153_Test.pdf";
            var folder: String? = null
            val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(urlString)
            val fileName = urlString!!.substring(urlString!!.lastIndexOf('/') + 1, urlString!!.length)
            val appName = getString(R.string.app_name) + "_Download"
            folder = activity!!.getExternalFilesDir(null).toString() + File.separator + "" + appName + "/"

            //Create androiddeft folder if it does not exist
            val directory = File(folder!!)

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val request = DownloadManager.Request(uri)
            request.setTitle(getString(R.string.app_name))
            request.setDescription("Downloading")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setVisibleInDownloadsUi(false)
            request.setDestinationUri(Uri.parse("file://$folder/$fileName"))

            downloadmanager.enqueue(request)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun downlkoadFIleold() {
        var folder: String? = null
        val downloadmanager = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(urlString)
        val fileName = urlString!!.substring(urlString!!.lastIndexOf('/') + 1, urlString!!.length)
        val appName = activity!!.getString(R.string.app_name)
        folder = Environment.getExternalStorageDirectory().toString() + File.separator + "" + appName + "/"

        val dir = File(Environment.getExternalStorageDirectory(), "BrandMark")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val dir2 = File(Environment.getExternalStorageDirectory(), "BrandMark/Download")
        if (!dir2.exists()) {
            dir2.mkdirs()
        }

        val file = File(Environment.getExternalStorageDirectory(), "BrandMark/Download/$fileName")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //Create androiddeft folder if it does not exist
        val directory = File(folder!!)

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val request = DownloadManager.Request(uri)
        request.setTitle(getString(R.string.app_name))
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setVisibleInDownloadsUi(true)
        //request.setDestinationUri(Uri.parse("file://BrandMark/Download/$fileName"))
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"game-of-life");

//      request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS, "ALSupermarket");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        downloadmanager.enqueue(request)


    }

   /* private val client = OkHttpClient()
    fun run() {
        val request = Request.Builder()
                .url("https://121.244.54.173:9011/ImageViewer/T-BASE/153/153_Test.pdf")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    println(response.body!!.string())
                }
            }
        })
    }*/
}