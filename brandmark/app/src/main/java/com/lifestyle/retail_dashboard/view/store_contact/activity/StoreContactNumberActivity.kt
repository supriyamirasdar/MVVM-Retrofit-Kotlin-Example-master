package com.lifestyle.retail_dashboard.view.store_contact.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityGetRetailAuditBinding
import com.lifestyle.retail_dashboard.databinding.ActivityStoreContactNumberBinding
import com.lifestyle.retail_dashboard.utils.*
import com.lifestyle.retail_dashboard.view.attendance.model.StoreList
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListRequest
import com.lifestyle.retail_dashboard.view.attendance.model.StoreListResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import com.lifestyle.retail_dashboard.view.login.model.BrandPerfUserDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.CreateNewRACActivity
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.GetRACActivity
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.activity.SingleRACDetail
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.FilterByAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.GetRetailAuditAdapter
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.adapter.ItemListener
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACList
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACRequest
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.GetRACResponse
import com.lifestyle.retail_dashboard.view.retail_audit_checklist.model.api.RACApis
import com.lifestyle.retail_dashboard.view.store_contact.adapter.GetAvailEmpListAdapter
import com.lifestyle.retail_dashboard.view.store_contact.model.GetBrandRacAvailEmpList
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpRequest
import com.lifestyle.retail_dashboard.view.store_contact.model.GetRacAvailEmpResponse
import com.lifestyle.retail_dashboard.view.store_contact.model.api.RacAvailEmpApis
import java.util.HashMap

class StoreContactNumberActivity : AppCompatActivity(), ItemListener, AdapterView.OnItemSelectedListener, AttendanceApis.StoreListListener, RacAvailEmpApis.GETRacAvailEmpListener {

    private lateinit var binding: ActivityStoreContactNumberBinding
    private lateinit var activity: StoreContactNumberActivity


    private var mAdapter: GetAvailEmpListAdapter? = null
    private val getBrandRacAvailEmpList = mutableListOf<GetBrandRacAvailEmpList>()
    private val allBrandRacAvailEmpList = mutableListOf<GetBrandRacAvailEmpList>()
    private val brandPerDetail = mutableListOf<BrandPerfUserDetail>()
    private val storeDetailList = mutableListOf<StoreList>()
    private val storeList = mutableListOf<String>()
    private var brandId: String? = null
    private var storeId: String? = null
    private var conceptId: String? = null
    private var conceptName: String? = null
    private val ACTIVITY_RESULT = 150
    private var isFirstTime = 1
    private var isFirstTimeStore = 1
    private val filterByList = mutableListOf<String>()
    private var isFilterApply = false
    private var filterbyStatus = "All"
    private var filterbyAssignTo = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_contact_number)

        initView()
        //getBrandList()
        getStoreList()
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        binding.toolbarLayout.tvToolBarTitle.text = "Store Contact Number"
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        activity = this

        /*behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            }
        })*/

        // setDummyData in list
        /*val st = GetBrandRacAvailEmpList()
        st.spoc = "Gurpreet Singh"//resources.getString(R.string.choose_store)
        st.mobile = "9041118892"
        st.email = "gurpreet.singh@infodartmail.com"
        st.roleNm = "BM"
        st.cmName = "Lifestyle Concept"
        getBrandRacAvailEmpList.add(st)

        val st2 = GetBrandRacAvailEmpList()
        st2.spoc = "krishna Reddy"//resources.getString(R.string.choose_store)
        st2.mobile = "7349299282"
        st2.email = "krishna.reddy@landmarkgroup.in"
        st2.roleNm = "BM"
        st2.cmName = "Apparel Children"
        getBrandRacAvailEmpList.add(st2)*/


        // @@@@@@@@@@@
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter = GetAvailEmpListAdapter(activity, getBrandRacAvailEmpList)
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, binding.recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                val brandRacAvailEmpList = getBrandRacAvailEmpList[position]
                /*val intent = Intent(activity, SingleRACDetail::class.java)
                intent.putExtra("StoreContact", brandRacAvailEmpList)
                startActivityForResult(intent, ACTIVITY_RESULT)*/
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))


        /*binding.rvFilterByAssignTo.layoutManager = GridLayoutManager(activity, 3)
        binding.rvFilterByAssignTo.itemAnimator = DefaultItemAnimator()
        filterAdapter = FilterByAdapter(filterByList, activity)
        binding.rvFilterByAssignTo.adapter = filterAdapter




        binding.rgStatus.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            filterbyStatus = rb.text.toString()
            filterRetailList()
        }
*/

    }


    private fun getStoreList() {
        storeDetailList.clear()
        storeList.clear()
        val request = StoreListRequest()
        request.ouCode = "3"
        val api = AttendanceApis(activity)
        api.getStoreList(activity, request)
    }

    override fun storeList(response: StoreListResponse) {
        if (response.serverErrormsg != null) {
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
            binding.tvErrorMsg.text = response.serverErrormsg
            binding.tvErrorMsg.visibility = View.VISIBLE
            binding.dataLayout.visibility = View.GONE
        } else {
            binding.tvErrorMsg.visibility = View.GONE
            binding.dataLayout.visibility = View.VISIBLE
            binding.spConcept.visibility = View.VISIBLE

            if (response.regionList != null) {
                for (regions in response.regionList) {
                    if (regions.districtList != null) {
                        for (state in regions.districtList) {
                            if (state.storeList != null) {
                                for (store in state.storeList) {
                                    if (store.storeName!!.contains("VENUE SALE", true))
                                        Log.d("Abrar", "Found")
                                    else {
                                        store.region = regions.region
                                        storeDetailList.add(store)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            storeDetailList.sortBy { it.storeName }
            val st = StoreList()
            st.storeName = resources.getString(R.string.choose_store)
            st.storeId = "0"
            st.region = "0"
            storeDetailList.add(0, st)

            for (store in storeDetailList) {
                storeList.add("${store.storeName}")
            }

            val storeListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity,
                    R.layout.spinner_simple_text_black, storeList)
            storeListAdapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            binding.spStoreList.adapter = storeListAdapter
            binding.spStoreList.setPadding(0, 0, 50, 0)
            binding.spStoreList.setSelection(0, false)
            binding.spStoreList.onItemSelectedListener = activity

            //binding.spStoreListView.adapter = storeListAdapter
            binding.spStoreListView.setTitle("" + resources.getString(R.string.choose_store));


            // Creating ArrayAdapter using the string array and default spinner layout
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(activity, R.layout.spinner_simple_text_black, storeList)
            // Specify layout to be used when list of choices appears
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applying the adapter to our spinner
            //binding.spStoreList.setPadding(0, 20, 0, 0)
            binding.spStoreListView.setAdapter(arrayAdapter)
//            binding.spStoreListView.setSelected(false);  // must
//            binding.spStoreListView.setSelection(-1, false)
            // binding.spStoreListView.setSelection(Adapter.NO_SELECTION, true);
            binding.spStoreListView.onItemSelectedListener = activity


            // set data in concept

            val conceptList: ArrayList<String> = arrayListOf()
            conceptList.addAll(resources.getStringArray(R.array.concept).toList())
            conceptList.add(0, "Choose Concept")

            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                    R.layout.spinner_simple_text_black, conceptList)

            adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
            binding.spConcept.adapter = adapter
            binding.spConcept.setPadding(0, 0, 0, 0)
            binding.spConcept.onItemSelectedListener = activity
        }
    }

    override fun onItemSelected(prt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (prt?.id == R.id.spStoreListView) {
            if (isFirstTimeStore == 1) {
                isFirstTimeStore = 0
            } else {

                //val storeName = binding.spStoreList.selectedItem.toString()
                val storeName = prt.getItemAtPosition(p2).toString();
                if (storeName == resources.getString(R.string.choose_store) || TextUtils.isEmpty(storeName)) {
                    storeId = null
                    //binding.showStoreSpinnerView.text = "" + resources.getString(R.string.choose_store)
                    CommonUtility.showSnackBar(activity, "Choose Store")
                } else {
                    //binding.spStoreList.setPadding(0, 0, 0, 0)
                    for (store in storeDetailList) {
                        if (store.storeName.equals(storeName, true)) {
                            storeId = store.storeId
                            //binding.showStoreSpinnerView.text = "" + storeName
                            break
                        }
                    }
                }
            }

            Log.d("Abrar", "Store Id: $storeId")
            if (storeId != null)
                getRACEmpList()

        } /*else if (prt?.id == R.id.spBrand) {
            val brandName = binding.spBrand.selectedItem.toString()
            if (brandName.equals("Choose Brand", true)) {
                brandId = null
                CommonUtility.showSnackBar(activity, "Choose Brand")
            } else {
                for (brands in brandPerDetail) {
                    if (brands.brandName.equals(brandName, true)) {
                        brands.brandId?.let { brandId = it }
                        break
                    }
                }
            }
            Log.d("Abrar", "Brand Id: $brandId")
        }*/ else if (prt?.id == R.id.spConcept) {
            if (isFirstTime == 1) {
                isFirstTime = 0
            } else {
                val selectedItem = binding.spConcept.selectedItem.toString()
                Log.d("Abrar", "Concept $selectedItem")
                if (selectedItem.equals("Choose Concept")) {
                    conceptId = null
                    CommonUtility.showSnackBar(activity, "Choose Concept")
                } else {
                    if (selectedItem.contains(" : ")) {
                        val concepts = selectedItem.split(" : ")
                        conceptId = concepts[1]
                        conceptName = concepts[0]
                        Log.d("Abrar", "conceptId $conceptId")
                    }
                }
            }
            // get filtered list..
            if (conceptId != null)
                getFilteredRACEmpList()
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getRACEmpList() {
        if (storeId != null) {
            getBrandRacAvailEmpList.clear()
            allBrandRacAvailEmpList.clear()
            val request = GetRacAvailEmpRequest()
            request.storeId = storeId
            //request.brandId = brandId
            //request.reqType = "RAC_HEADER"
            //request.appType = Constant.appType
            val api = RacAvailEmpApis(activity)
            api.getRacAvailEmpList(activity, request)
        }
    }

    override fun getRacAvailEmpListResponse(response: GetRacAvailEmpResponse) {
        if (response.serverErrormsg != null)
            showErrorMsg(response.serverErrormsg)
        else {
            if (response.brandRacAvailEmpList.size > 0) {
                binding.tvErrorMsg.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                //filterByList.clear()
                getBrandRacAvailEmpList.clear()
                allBrandRacAvailEmpList.clear()
                for (ratail in response.brandRacAvailEmpList) {

                    getBrandRacAvailEmpList.add(ratail)
                    allBrandRacAvailEmpList.add(ratail)

                }

                /*val auditByList = mutableSetOf<String>()
                for (retail in getRatailAuditList) {
                    retail.auditBy?.let { auditByList.add(it) }
                }
                val auditByHashMap: HashMap<String, String> = HashMap<String, String>()  //define empty hashmap
                for (empData in auditByList) {
                    if (auditByHashMap.containsKey(empData.toLowerCase())) {
                        // data already exist..
                    } else {
                        auditByHashMap.put(empData.toLowerCase()!!, empData!!)
                    }
                }
                for (key in auditByHashMap.keys) {
                    filterByList.add(key)
                }
                //filterByList.addAll(auditByList)
                filterAdapter?.notifyDataSetChanged()*/

                mAdapter?.notifyDataSetChanged()
            } else
                showErrorMsg("No Data Found")
        }
    }

    private fun showErrorMsg(msg: String?) {
        CommonUtility.showSnackBar(activity, msg)
        binding.tvErrorMsg.text = msg
        binding.tvErrorMsg.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun getFilteredRACEmpList() {


        getBrandRacAvailEmpList.clear()
        for (ratail in allBrandRacAvailEmpList) {
            if (ratail.cmName.equals(conceptName, true)) {
                getBrandRacAvailEmpList.add(ratail)
                //filterRatailAuditList.add(ratail)
            }
        }


        if (getBrandRacAvailEmpList.size > 0) {
            binding.tvErrorMsg.visibility = View.GONE
            binding.dataLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE
            mAdapter?.notifyDataSetChanged()
        } else {

            showErrorMsg("No Data Found")
        }
    }

    override fun onItemClick(assignTo: String) {
        /*filterbyAssignTo = assignTo
        filterAdapter?.filterBy(filterbyAssignTo)

        filterRetailList()*/
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        /*val filterMenu = menu?.findItem(R.id.action_filter)
        val filterApplyMenu = menu?.findItem(R.id.action_apply_filter)
        if (isFilterApply) {
            filterMenu?.isVisible = false
            filterApplyMenu?.isVisible = true
        } else {
            filterMenu?.isVisible = true
            filterApplyMenu?.isVisible = false
        }*/
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            /* R.id.action_filter -> {
                 if (filterRatailAuditList.size > 0)
                     bottomSheet()
                 else
                     CommonUtility.showSnackBar(activity, "No Data Available")
                 return true

             }

             R.id.action_apply_filter -> {
                 if (filterRatailAuditList.size > 0)
                     bottomSheet()
                 else
                     CommonUtility.showSnackBar(activity, "No Data Available")
                 return true

             }*/
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* private fun bottomSheet() {
         if (behavior?.state == BottomSheetBehavior.STATE_EXPANDED)
             behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
         else
             behavior?.state = BottomSheetBehavior.STATE_EXPANDED
     }

     override fun dispatchTouchEvent(event: MotionEvent): Boolean {
         if (event.action == MotionEvent.ACTION_DOWN) {
             if (behavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                 val outRect = Rect()
                 binding.bottomSheet.getGlobalVisibleRect(outRect)
                 if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                     behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
             }
         }
         return super.dispatchTouchEvent(event)
     }

     override fun onItemClick(assignTo: String) {
         filterbyAssignTo = assignTo
         filterAdapter?.filterBy(filterbyAssignTo)

         filterRetailList()
     }

     private fun filterRetailList() {
         Log.d("Abrar", "Filter By Status $filterbyStatus ")
         isFilterApply = !filterbyStatus.equals("All", true)
         isFilterApply = !filterbyAssignTo.equals("All", true)
         invalidateOptionsMenu()
         bottomSheet()
         getRatailAuditList.clear()
         if (filterbyStatus.equals("All", true) && filterbyAssignTo.equals("All", true))
             getRatailAuditList.addAll(filterRatailAuditList)
         else {
             for (retail in filterRatailAuditList) {
                 if (filterbyAssignTo.equals("All", true)) {
                     if (retail.racStatus.equals(filterbyStatus, true)) {
                         getRatailAuditList.add(retail)
                     }
                 } else if (filterbyStatus.equals("All", true)) {
                     if (retail.auditBy.equals(filterbyAssignTo, true)) {
                         getRatailAuditList.add(retail)
                     }
                 } else {
                     if (retail.auditBy.equals(filterbyAssignTo, true)
                             && retail.racStatus.equals(filterbyStatus, true)) {
                         getRatailAuditList.add(retail)
                     }
                 }
             }
         }
         mAdapter?.notifyDataSetChanged()
     }*/

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