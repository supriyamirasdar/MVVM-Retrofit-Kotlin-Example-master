package com.lifestyle.retail_dashboard.view.homescreen.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifestyle.retail_dashboard.BuildConfig
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityHomeScreenActivtyBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil
import com.lifestyle.retail_dashboard.utils.ExternalStorageUtil.*
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.VersionHelper.versionCompare
import com.lifestyle.retail_dashboard.view.attendance.DB.DatabaseClient
import com.lifestyle.retail_dashboard.view.attendance.DB.Task
import com.lifestyle.retail_dashboard.view.homescreen.adapter.NavigationAdapter
import com.lifestyle.retail_dashboard.view.homescreen.fragment.HomeViewModel
import com.lifestyle.retail_dashboard.view.homescreen.model.AppVersion
import com.lifestyle.retail_dashboard.view.login.model.api.LoginApis
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class HomeScreenActivty : AppCompatActivity(), LoginApis.AppVersionListener {
    private lateinit var binding: ActivityHomeScreenActivtyBinding
    private lateinit var activity: HomeScreenActivty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen_activty)

        initView()
        deleteCrashFile()
        deleteLogFile()
        navigationDrawer()
        checkAppVersion()
        //installAPK()
        getTasks("store")
    }

    private fun initView() {
        setSupportActionBar(binding.appbar.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_drawer)

        activity = this

        binding.appbar.toolbarLayout.tvToolBarTitle.text = resources.getString(R.string.app_name)
        binding.appbar.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE
    }

    //Navigation
    private fun navigationDrawer() {
        binding.navig.usernameTv.text = PreferenceUtils.getUserName()

        binding.navig.navigRecyclerview.setHasFixedSize(true)
        binding.navig.navigRecyclerview.layoutManager = LinearLayoutManager(this)

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.getNavigationList().observe(this, { navigationList ->
            val adapter = NavigationAdapter(this, navigationList)
            binding.navig.navigRecyclerview.adapter = adapter
        })
    }


    private fun getTasks(condition: String) {
        class GetTasks : AsyncTask<Void?, Void?, List<Task?>?>() {
            override fun doInBackground(vararg voids: Void?): List<Task?>? {
                return DatabaseClient.getInstance(activity)
                        ?.appDatabase
                        ?.taskDao()
                        ?.getStoreList(condition)
            }

            override fun onPostExecute(dataList: List<Task?>?) {
                super.onPostExecute(dataList)
                Log.d("Abrar", "Size ${dataList?.size}")
                if (dataList != null && dataList.isNotEmpty()) {
                    PreferenceUtils.setIsStoreListFound(true)
                }
            }
        }

        val gt = GetTasks()
        gt.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_test, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                else
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        else
            binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed()
    }

    private fun checkAppVersion() {
        val api = LoginApis(activity)
        api.checkAppVersion(activity)
    }

    override fun appVersion(response: AppVersion) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else {
            if (response.mobAppsVersion != null) {
                try {
                    val curVerTes = packageManager.getPackageInfo(packageName, 0).versionName
                    if (versionCompare(curVerTes, response.mobAppsVersion) < 0) {
                        displayFragment(response.mobAppsVersion, response.ftpHostName, response.ftpPortNum, response.ftpUsr, response.ftpPwd)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun displayFragment(sysVerTes: String, hostName: String?, portNum: String?, ftpUser: String?, ftpPassword: String?) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Download APK")
        builder.setCancelable(false)
        builder.setMessage("BrandMark App version has been upgraded to $sysVerTes version. Please update.")
        builder.setPositiveButton("Update") { dialog: DialogInterface?, which: Int ->
            DownloadFile(activity).execute(hostName, portNum, ftpUser, ftpPassword)
        }
        builder.show()
    }

    private class DownloadFile(val activity: Activity) : AsyncTask<String?, Int?, Void?>() {
        var status = 0
        var notificationID = 1
        var manager: NotificationManager? = null
        var builder: NotificationCompat.Builder? = null
        private var dialog: AlertDialog? = null
        private var mProgressBar: ProgressBar? = null
        private var tvdownload: TextView? = null
        private var tvPercentage: TextView? = null
        override fun onPreExecute() {
            super.onPreExecute()
            manager = activity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            builder = NotificationCompat.Builder(activity)
            builder?.setContentTitle("BrandMark")
            builder?.setContentText("Download in progress")
            builder?.setSmallIcon(R.mipmap.ic_launcher)
            manager?.notify(notificationID, builder?.build())
            val dialogView = View.inflate(activity, R.layout.apk_download_layout, null)
            val builder = AlertDialog.Builder(activity)
            builder.setView(dialogView)
            builder.setTitle("Downloading")
            builder.setCancelable(false)
            dialog = builder.create()
            //assert(dialog.getWindow() != null)
            dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            mProgressBar = dialogView.findViewById(R.id.progressBar)
            tvdownload = dialogView.findViewById(R.id.tvdownload)
            tvPercentage = dialogView.findViewById(R.id.tvPercentage)
            dialog?.show()
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            values[0]?.let { builder?.setProgress(100, it, true) }
            manager?.notify(notificationID, builder?.build())
            if (values[0] != null) {
                mProgressBar?.progress = values[0]!!
                tvdownload?.text = "${values[0]}/100"
                tvPercentage?.text = "${values[0]}%"
            }
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            activity.runOnUiThread { publishProgress(100) }
            if (dialog?.isShowing!!) {
                mProgressBar?.progress = 100
                tvPercentage?.text = "100%"
                tvdownload?.text = "100/100"
                dialog?.dismiss()
            }
            builder?.setContentText("Download Complete")
            builder?.setProgress(0, 0, false)
            manager?.notify(notificationID, builder?.build())
            Handler().postDelayed({
                manager?.cancel(notificationID)
                val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                activity.sendBroadcast(intent)
            }, 2000)
            if (status == 1) CommonUtility.showSnackBar(activity, "Download Failed. Please Check Network Connection.") else {
                CommonUtility.showSnackBar(activity, "File downloaded")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!activity.packageManager.canRequestPackageInstalls())
                        activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(java.lang.String.format("package:%s", activity.getPackageName()))), 1234)
                    else installAPK()
                } else
                    installAPK()
            }
        }

        private fun installAPK() {
            val pathname = Environment.getExternalStorageDirectory().toString() + "/BrandMark.apk"
            val file = File(pathname)
            if (file.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                intent.setDataAndType(uriFromFile(File(pathname)), "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    activity.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    Log.d("Abrar", "Exception:  " + e.localizedMessage)
                    writeToFile("Install APK:  ${e.localizedMessage}".trimIndent())
                }
            }
        }

        private fun uriFromFile(file: File): Uri? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file)
            } else {
                Uri.fromFile(file)
            }
        }

        override fun doInBackground(vararg params: String?): Void? {
            val host = params[0]
            val user = params[2]
            val pass = params[3]
            val filePath = "/BrandMark.apk"
            var ftpUrl = "ftp://$user:$pass@$host$filePath"

            Log.d("Abrar", "Url $ftpUrl")
            try {
                val apkFile = File(Environment.getExternalStorageDirectory().absoluteFile, "BrandMark.apk")
                if (apkFile.exists()) {
                    apkFile.delete()
                    apkFile.createNewFile()
                }
                ftpUrl = String.format(ftpUrl, user, pass, host)
                val url = URL(ftpUrl)
                val conn = url.openConnection()
                val totalLength = 10000000
                activity.runOnUiThread {
                    CommonUtility.showSnackBar(activity, "Downloading started..")
                }
                val inputStream = conn.getInputStream()
                val outputStream = FileOutputStream(apkFile)
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead = -1
                var total = 0
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    total += bytesRead
                    publishProgress(total * 100 / totalLength)
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()

            } catch (e: Exception) {
                Log.d("Abrar", "Exception : ${e.localizedMessage}")
                writeToFile("Download APK:  ${e.localizedMessage}".trimIndent())
                status = 1
            }
            return null
        }

        companion object {
            private const val BUFFER_SIZE = 4096
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (packageManager.canRequestPackageInstalls()) {
                    installAPK()
                }
            }
        }
    }


    fun installAPK() {
        val pathname = Environment.getExternalStorageDirectory().toString() + "/BrandMark.apk"
        Log.d("Abrar", "Path $pathname")
        val file = File(pathname)
        if (file.exists()) {
            Log.d("Abrar", "Exist")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            intent.setDataAndType(uriFromFile(File(pathname)), "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                applicationContext.startActivity(intent)
            } catch (e: Exception) {
                Log.d("Abrar", "Exception:  " + e.localizedMessage)
                writeToFile("Install APK:  ${e.localizedMessage}".trimIndent())
            }
        } else
            Log.d("Abrar", "Not Exist")
    }

    private fun uriFromFile(file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            file?.let { FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", it) }
        } else {
            Uri.fromFile(file)
        }
    }
}