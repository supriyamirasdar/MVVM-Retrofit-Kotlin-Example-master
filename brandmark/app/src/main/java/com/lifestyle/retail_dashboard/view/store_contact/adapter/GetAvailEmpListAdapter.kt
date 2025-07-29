package com.lifestyle.retail_dashboard.view.store_contact.adapter

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleContactNumberBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.view.attendance.activity.AttendanceActivity
import com.lifestyle.retail_dashboard.view.store_contact.activity.StoreContactNumberActivity
import com.lifestyle.retail_dashboard.view.store_contact.model.GetBrandRacAvailEmpList


class GetAvailEmpListAdapter(val context: Context, var brandRacAvailEmpList: MutableList<GetBrandRacAvailEmpList>) : RecyclerView.Adapter<GetAvailEmpListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GetAvailEmpListViewHolder {
        val binding: SingleContactNumberBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_contact_number, parent, false)
        return GetAvailEmpListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return brandRacAvailEmpList.size
    }

    override fun onBindViewHolder(holder: GetAvailEmpListViewHolder, position: Int) {
        val brandRacAvailEmpList = brandRacAvailEmpList[position]

        holder.binding.btnCall.setOnClickListener {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:${brandRacAvailEmpList.mobile}")
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 123)
                } else
                    context.startActivity(callIntent)
            } catch (e: Exception) {
                Log.d("Abrar", "Exception: ${e.localizedMessage}")
            }
        }

        holder.binding.btnVideoCall.setOnClickListener {
            // DialogBox.showSorryDialog(context as Activity)

            if (context is StoreContactNumberActivity) {
                val activity = context as StoreContactNumberActivity
                activity.showCallOptionDialog(brandRacAvailEmpList.mobile);
            }
        }

        holder.binding.btnEmail.setOnClickListener {
            try {
                val emailto = brandRacAvailEmpList.email
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailto))
                intent.putExtra(Intent.EXTRA_SUBJECT, "")
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                CommonUtility.showSnackBar(context as Activity?, "There are no email client installed on your device.")
            }
        }
        holder.bindItems(brandRacAvailEmpList)
    }
}

class GetAvailEmpListViewHolder(val binding: SingleContactNumberBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(brandRacAvailEmpList: GetBrandRacAvailEmpList) {
        binding.availEmpList = brandRacAvailEmpList
        binding.tvEmailId.isSelected = true
    }
}