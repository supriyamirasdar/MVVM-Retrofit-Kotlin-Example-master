package com.lifestyle.retail_dashboard.view.attendance.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityAttendanceBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.attendance.adapter.AttendanceAdapter
import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeDetail
import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeListResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest

@SuppressLint("SetTextI18n")
class AttendanceActivity : AppCompatActivity(), AttendanceApis.BrandEmployeeListListener {
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var activity: AttendanceActivity
    private var mAdapter: AttendanceAdapter? = null
    private val employeeDetailList = mutableListOf<EmployeeDetail>()
    private lateinit var api: AttendanceApis
    private var area: String = "-1"
    private var region: String = "-1"
    private var store: String = "-1"
    private var bu: String? = null
    private var tsfEnty: String? = null
    private var subTitle = ""
    private var brandId: String = "-1"
    private var brandName: String? = null
    private var isFilterApply = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance)

        initView()
        getAccessData()
        getFilerList()
    }

    private fun getFilerList() {
        Log.d("Abrar", "Retrieve ${Gson().toJson(PreferenceUtils.getSavedFilter())}")

        if (PreferenceUtils.getSavedFilter() != null) {
            try {
                val selectedFilterList = PreferenceUtils.getSavedFilter()
                val brndFiltrList = StringBuffer()
                val regFiltrList = StringBuffer()
                val stateFiltrList = StringBuffer()
                val storeFiltrList = StringBuffer()
                val brndFiltrIdList = StringBuffer()
                val regFiltrIdList = StringBuffer()
                val stateFiltrIdList = StringBuffer()
                val storeFiltrIdList = StringBuffer()
                val filteredString = StringBuffer()
                if (selectedFilterList != null && selectedFilterList.size >= 1) {
                    isFilterApply = true
                    invalidateOptionsMenu()
                    for (itemSelected in selectedFilterList) {
                        when (itemSelected.selectedType) {
                            "Brand" -> {
                                brndFiltrList.append("${itemSelected.itemName},")
                                brndFiltrIdList.append("${itemSelected.itemId},")
                            }
                            "Region" -> {
                                regFiltrList.append("${itemSelected.itemName},")
                                regFiltrIdList.append("${itemSelected.itemId},")
                            }
                            "State" -> {
                                stateFiltrList.append("${itemSelected.itemName},")
                                stateFiltrIdList.append("${itemSelected.itemId},")
                            }
                            "Store" -> {
                                storeFiltrList.append("${itemSelected.itemName},")
                                storeFiltrIdList.append("${itemSelected.itemId},")
                            }
                        }
                    }

                    if (brndFiltrList.isNotEmpty() && brndFiltrIdList.isNotEmpty()) {
                        brndFiltrList.deleteCharAt(brndFiltrList.length - 1)
                        brndFiltrIdList.deleteCharAt(brndFiltrIdList.length - 1)
                        brandId = brndFiltrIdList.toString()
                        filteredString.append("$brndFiltrList")
                    }



                    if (regFiltrList.isNotEmpty() && regFiltrIdList.isNotEmpty()) {
                        regFiltrList.deleteCharAt(regFiltrList.length - 1)
                        regFiltrIdList.deleteCharAt(regFiltrIdList.length - 1)
                        area = regFiltrIdList.toString().replace("R", "")
                        filteredString.append("\n$regFiltrList")
                    }

                    if (stateFiltrList.isNotEmpty() && stateFiltrIdList.isNotEmpty()) {
                        stateFiltrList.deleteCharAt(stateFiltrList.length - 1)
                        stateFiltrIdList.deleteCharAt(stateFiltrIdList.length - 1)
                        region = stateFiltrIdList.toString()
                        filteredString.append("\n$stateFiltrList")
                    }

                    if (storeFiltrList.isNotEmpty() && storeFiltrIdList.isNotEmpty()) {
                        storeFiltrList.deleteCharAt(storeFiltrList.length - 1)
                        storeFiltrIdList.deleteCharAt(storeFiltrIdList.length - 1)
                        store = storeFiltrIdList.toString()
                        subTitle = storeFiltrList.toString()
                        filteredString.append("\n$storeFiltrList")
                    }

                    binding.toolbarLayout.tvToolBarSubtitle.text = subTitle
                    binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE
                    binding.tvFilterSelected.text = filteredString.toString()
                    getEmployeeList()
                } else
                    CommonUtility.showSnackBar(activity, "Select Filter Properly")
            } catch (e: Exception) {
                Log.d("Abrar", "Exception: ${e.localizedMessage}")
            }
        } else {
            Utility.goToNextScreen(activity, FilterAttendanceListActivity::class.java)
            finish()
        }
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        api = AttendanceApis(activity)

        binding.toolbarLayout.tvToolBarTitle.text = "Attendance"
        binding.toolbarLayout.tvToolBarSubtitle.text = "PAN India"

        mAdapter = AttendanceAdapter(activity, employeeDetailList)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = mAdapter

        binding.tvFilterSelected.setOnClickListener {
            Utility.goToNextScreen(activity, FilterAttendanceListActivity::class.java)
            finish()
        }

    }

    private fun getAccessData() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                bu = brands.bu
                tsfEnty = brands.tsfEnt
                break
            }
        }
    }

    private fun getEmployeeList() {
        employeeDetailList.clear()
        val request = BrandPerformanceRequest()
        request.areas = area
        request.region = region
        request.store = store
        request.bu = bu
        request.tsfEnty = tsfEnty
        request.brandId = brandId
        api.getBrandUserDetail(activity, request)
    }

    override fun employeeList(response: EmployeeListResponse) {
        if (response.serverErrormsg != null) {
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.tvErrorMsg.text = response.serverErrormsg
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        } else {
            response.brandUsrDetailsList.let { employeeDetailList.addAll(it) }
            if (employeeDetailList.size > 0) {
                binding.tvErrorMsg.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                mAdapter?.notifyDataSetChanged()
            } else {
                binding.tvErrorMsg.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_filter -> {
                Utility.goToNextScreen(activity, FilterAttendanceListActivity::class.java)
                finish()
                return true
            }
            R.id.action_apply_filter -> {
                Utility.goToNextScreen(activity, FilterAttendanceListActivity::class.java)
                finish()
                return true
            }
            /*R.id.action_csr -> {
                DialogBox.showSimpleDialog(activity)
                return true
            }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val filterMenu = menu?.findItem(R.id.action_filter)
        val filterApplyMenu = menu?.findItem(R.id.action_apply_filter)
        if (isFilterApply) {
            filterMenu?.isVisible = false
            filterApplyMenu?.isVisible = true
        } else {
            filterMenu?.isVisible = true
            filterApplyMenu?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    public fun showCallOptionDialog(mobileNum: String?) {

        val inflater = activity.layoutInflater
        @SuppressLint("InflateParams") val dialogView: View = inflater.inflate(R.layout.video_call_option_dialog, null)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
        val duoCallBT = dialogView.findViewById<LinearLayout>(R.id.duoCallBT)
        val whatsAppCallBT = dialogView.findViewById<LinearLayout>(R.id.whatsAppCallBT)
        val cancelDialogTv = dialogView.findViewById<TextView>(R.id.cancelDialogTv)
        dialog!!.show()
        duoCallBT.setOnClickListener {
            callGoogleDuo(mobileNum);
            dialog.dismiss()
        }
        whatsAppCallBT.setOnClickListener {
            callWhatsApp(mobileNum)
//            callWhatsApp("2255663377")
            dialog.dismiss()
        }
        cancelDialogTv.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun callGoogleDuo(mobileNum: String?) {
        val isAppInstalled: Boolean = Utility.appInstalledOrNot("com.google.android.apps.tachyon")

        if (isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            val intent = Intent()
            intent.setPackage("com.google.android.apps.tachyon")
            intent.action = "com.google.android.apps.tachyon.action.CALL"
            intent.data = Uri.parse("tel:${mobileNum}")
            startActivity(intent)
        } else {
            // Do whatever we want to do if application not installed
            CommonUtility.showSnackBar(this, "To make video call install Google Duo Application.")
        }
    }

    private fun callWhatsApp(mobileNum: String?) {

        val isAppInstalled: Boolean = Utility.appInstalledOrNot("com.whatsapp")
        if (isAppInstalled) {
            val contactNumber = mobileNum// to change with real value
            val cursor: Cursor? = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI, arrayOf(ContactsContract.Data._ID),
                    ContactsContract.RawContacts.ACCOUNT_TYPE.toString() + " = 'com.whatsapp' " +
                            "AND " + ContactsContract.Data.MIMETYPE + " = 'vnd.android.cursor.item/vnd.com.whatsapp.video.call' " +
                            "AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + contactNumber + "%'",
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME
            )

            var id: Long = -1
            if (cursor == null) {
                // throw an exception
                CommonUtility.showSnackBar(this, "Not a valid number.")
            } else {
                while (cursor!!.moveToNext()) {
                    id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))
                }

                if (!cursor!!.isClosed()) {
                    cursor.close()
                }
            }

            if (id >= 0) {
                Log.e("Attendance", "Call id ::" + id);
                try {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(Uri.parse("content://com.android.contacts/data/$id"), "vnd.android.cursor.item/vnd.com.whatsapp.voip.call")
                    intent.setPackage("com.whatsapp")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.d("Abrar", "Exception: ${e.localizedMessage}")
                }
            } else {
                //CommonUtility.showSnackBar(this, "Number is not registered with WhatsApp.")
                var smsNumber = "";
                Log.e("Attendance", "mobileNum ::" + mobileNum);
                if (mobileNum!!.length == 10) {
                    smsNumber = "91" + mobileNum!!
                } else {
                    smsNumber = mobileNum!!
                }
                Log.e("Attendance", "smsNumber ::" + smsNumber);
                var smsText = ""
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$smsNumber&text=$smsText")
                startActivity(intent)
            }
        } else {
            // Do whatever we want to do if application not installed
            CommonUtility.showSnackBar(this, "WhatsApp not installed.")
        }
    }
}