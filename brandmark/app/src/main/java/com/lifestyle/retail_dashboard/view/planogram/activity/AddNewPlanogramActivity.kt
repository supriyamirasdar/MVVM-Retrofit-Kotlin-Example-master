package com.lifestyle.retail_dashboard.view.planogram.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.ActivityAddNewPlanogramBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant.MEGABYTE
import com.lifestyle.retail_dashboard.utils.PathUtils
import com.lifestyle.retail_dashboard.utils.PreferenceUtils
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramRequest
import com.lifestyle.retail_dashboard.view.planogram.model.PlanogramResponse
import com.lifestyle.retail_dashboard.view.planogram.model.api.PlanogramApis
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


class AddNewPlanogramActivity : AppCompatActivity(), PlanogramApis.PlanogramDetailListner, AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityAddNewPlanogramBinding
    private val CHOOSE_FILE_REQUESTCODE = 123
    private var fileNameList: ArrayList<String> = ArrayList()
    private var fileByteList: ArrayList<String> = ArrayList()
    private lateinit var fileAdapter: ArrayAdapter<String>
    private lateinit var activity: AddNewPlanogramActivity
    private var mimeTypes = arrayOf("image/*", "application/pdf")
    //var mimeTypes = arrayOf("application/pdf")
    val brandList: ArrayList<String> = arrayListOf()
    private var isRefresh = false
    private var pType: String? = null
    private var brandId: String? = null
    private var conceptId: String? = null
    private val brandNameList =  mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_planogram)

        initView()
        getAccessData()
        seasonList()
        getWeekList()
        getConceptList()
        filesList()
    }

    private fun filesList() {
        fileAdapter = ArrayAdapter<String>(activity,
                R.layout.simple_list_item, android.R.id.text1, fileNameList)
        binding.fileList.adapter = fileAdapter
    }

    private fun initView() {
        setSupportActionBar(binding.toolbarLayout.toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        activity = this

        val plonogramType = intent.getStringExtra("itemSelected")

        binding.toolbarLayout.tvToolBarTitle.text = plonogramType
        binding.toolbarLayout.tvToolBarSubtitle.visibility = View.GONE

        when {
            plonogramType.equals("Planogram ",true) -> pType = "planogram"
            plonogramType.equals("Catalog & Content",true) -> pType = "catalog"
            plonogramType.equals("Training",true) -> pType = "training"
            else -> pType = "planogram"
        }

        binding.btnUpload.setOnClickListener { pickFiletoUpload() }

        binding.btnSubmit.setOnClickListener { uploadPlanogram() }
    }

    private fun getAccessData() {
        val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
        if (brandPerDetail != null) {
            for (brands in brandPerDetail) {
                brands.brandName?.let { brandNameList.add(it) }
            }
        }

        brandList()
    }


    private fun uploadPlanogram() {
        val brandName = binding.spBrand.selectedItem.toString()
        val season = binding.spSeason.selectedItem.toString()
        val week = binding.spWeek.selectedItem.toString()

        if (brandId == null)
            CommonUtility.showSnackBar(activity, "Choose Brand")
        else if (season.equals("Choose Season", true))
            CommonUtility.showSnackBar(activity, "Choose Season")
        else if (week.equals("Choose Hit", true))
            CommonUtility.showSnackBar(activity, "Choose Hit")
        else if (conceptId == null)
            CommonUtility.showSnackBar(activity, "Choose Concept")
        else if (fileNameList.size == 0)
            CommonUtility.showSnackBar(activity, "Upload File to submit")
        else if (fileNameList.size > 0 && fileByteList.size > 0){
            if (brandId != null) {
                val request = PlanogramRequest()
                request.file = fileByteList[fileByteList.size - 1]
                request.fileName = fileNameList[fileNameList.size - 1]
                request.fileType = "pdf"
                request.brandId = brandId
                request.pType = pType
                request.usrId = PreferenceUtils.getUserId()
                request.brandName = brandName
                request.season = season
                request.concept = conceptId
                request.week = week
                val api = PlanogramApis(activity)
                api.uploadPlanogram(this, request)
            }else
                CommonUtility.showSnackBar(activity,"Choose Brand")
        }
    }

    private fun pickFiletoUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type =
                    if (mimeTypes.size == 1)
                        mimeTypes[0]
                    else
                        "*/*"
            if (mimeTypes.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        } else {
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
        }
        startActivityForResult(Intent.createChooser(intent, "File"), CHOOSE_FILE_REQUESTCODE)
    }


    private fun brandList() {
        brandNameList.add(0,"Choose Brand")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, brandNameList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spBrand.adapter = adapter
        binding.spBrand.setPadding(0, 0, 0, 0)
        binding.spBrand.setSelection(0,false)
        binding.spBrand.onItemSelectedListener = activity
    }

    private fun seasonList() {
        val seasonList: ArrayList<String> = arrayListOf()
        seasonList.addAll(resources.getStringArray(R.array.season).toList())
        seasonList.add(0,"Choose Season")

        //setDropdown(binding.spSeason, seasonList, "Choose Season", "Season")

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, seasonList)

        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spSeason.adapter = adapter
        binding.spSeason.setPadding(0, 0, 0, 0)
        binding.spSeason.setSelection(0,false)
        binding.spSeason.onItemSelectedListener = activity
    }


    private fun getWeekList() {
        val  weekList:ArrayList<String> = arrayListOf()
        for (x in 1 until 11){
            weekList.add("Hit $x")
        }
        weekList.add(0,"Choose Hit")
        weekList.add(1,"Interim")
        //setDropdown(binding.spWeek, weekList, "Choose Hit", "Hit")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, weekList)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spWeek.adapter = adapter
        binding.spWeek.setPadding(0, 0, 0, 0)
        binding.spWeek.setSelection(0,false)
        binding.spWeek.onItemSelectedListener = activity
    }


    private fun getConceptList() {
        val  conceptList:ArrayList<String> = arrayListOf()
        conceptList.addAll(resources.getStringArray(R.array.concept).toList())
        conceptList.add(0,"Choose Concept")
        //setDropdown(binding.spConcept, conceptList, "Choose Concept", "Concept")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                R.layout.spinner_simple_text_black, conceptList)
        adapter.setDropDownViewResource(R.layout.spinner_simple_text_black)
        binding.spConcept.adapter = adapter
        binding.spConcept.setPadding(0, 0, 0, 0)
        binding.spConcept.setSelection(0,false)
        binding.spConcept.onItemSelectedListener = activity
    }

    override fun onItemSelected(prnt: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(prnt?.id == R.id.spBrand) {
            val selectedItem = binding.spBrand.selectedItem.toString()
            if (selectedItem.equals("Choose Brand")){
                brandId = null
                CommonUtility.showSnackBar(activity,"Choose Brand")
            }else {
                val brandPerDetail = PreferenceUtils.getBrandPerfDetail()
                if (brandPerDetail != null) {
                    for (brands in brandPerDetail) {
                        if (brands.brandName.equals(selectedItem)) {
                            brands.brandId?.let { brandId = it }
                            break
                        }
                    }
                }
            }
        }
        else if(prnt?.id == R.id.spConcept) {
            val selectedItem = binding.spConcept.selectedItem.toString()
            Log.d("Abrar","Concept $selectedItem")
            if (selectedItem.equals("Choose Concept")){
                conceptId = null
                CommonUtility.showSnackBar(activity,"Choose Concept")
            }else {
                if (selectedItem.contains(" : ")) {
                    val concepts = selectedItem.split(" : ")
                    conceptId = concepts[1]
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CHOOSE_FILE_REQUESTCODE && intent != null) {
            val uri: Uri? = intent.data
            fileByteList.clear()
            fileNameList.clear()

            try {

                val cursor: Cursor? = uri?.let { contentResolver.query(it, null, null, null, null) }
                if (cursor != null) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val fileSize = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    Log.d("Abrar", "File size ${cursor.getLong(fileSize)} fix ${5 * MEGABYTE}")
                    if (cursor.getLong(fileSize) < 5 * MEGABYTE) {
                        val name = cursor.getString(nameIndex)
                        cursor.close()
                        fileNameList.add(name)

                        Log.d("Abrar","Name: $name")

                        val fileBytes = getFileByteArray(uri)
                        if (fileBytes != null) {
                            fileByteList.add(fileBytes)
                        }

                        binding.tvFile.text = name
                        fileAdapter.notifyDataSetChanged()
                    } else {
                        CommonUtility.showSnackBar(this, "File size should not be more the 5MB")

                    }
                }
            }catch (e: Exception){
                Log.d("Abrar","Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun getFileByteArray(uri: Uri?): String? {
        val filePath = uri?.let { PathUtils.getPath(activity, it) }
        val pdfFile = File(filePath)

        Log.d("Abrar","filePath: $filePath")

        val bytes = ByteArray(pdfFile.length().toInt())
        try {
            val buf = BufferedInputStream(FileInputStream(pdfFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: Exception) {
            Log.d("Abrar", "Exception ${e.localizedMessage}")
        }

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


    private fun getFileName(uri: Uri?): String? {
        val cursor: Cursor? = uri?.let { contentResolver.query(it, null, null, null, null) }
        if (cursor != null) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileSize = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            if (cursor.getLong(fileSize) < 5) {
                val name = cursor.getString(nameIndex)
                cursor.close()
                return name
            }else
                return null
        }
        return null
    }

    override fun planogramDetail(response: PlanogramResponse?, reqCode: Int) {
        if (response!=null){
            if (response.serverErrormsg != null)
                CommonUtility.showSnackBar(activity, response.serverErrormsg)
            else if (response.statusMessage.equals("Y", true)){
                isRefresh = true
                CommonUtility.showSnackBar(activity, "Successfully uploaded")
                Handler().postDelayed({
                    activity.onBackPressed()
                }, 400)
            }
        }
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("isRefresh", isRefresh)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}