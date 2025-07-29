package com.lifestyle.retail_dashboard.view.attendance.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.SingleAttendanceItemBinding
import com.lifestyle.retail_dashboard.view.attendance.activity.AttendanceActivity
import com.lifestyle.retail_dashboard.view.attendance.fragment.AttendanceHistoryFragment
import com.lifestyle.retail_dashboard.view.attendance.model.EmployeeDetail


class AttendanceAdapter(val context: Context, var employeeList: List<EmployeeDetail>) : RecyclerView.Adapter<ViewHolder>(), Filterable {

    private var filterationList = employeeList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: SingleAttendanceItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.single_attendance_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bindItems(employee)

        holder.binding.btnVideoCall.setOnClickListener {
            // DialogBox.showSorryDialog(context as Activity)

            if (context is AttendanceActivity) {
                val activity = context as AttendanceActivity
                activity.showCallOptionDialog(employee.mobileNum);
            }
        }

        holder.binding.btnCall.setOnClickListener {
            try {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:${employee.mobileNum}")
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 123)
                } else
                    context.startActivity(callIntent)
            } catch (e: Exception) {
                Log.d("Abrar", "Exception: ${e.localizedMessage}")
            }

        }

        holder.binding.root.setOnClickListener {
            if (context is AppCompatActivity) {
                val activity = context as AppCompatActivity
                val fragmentTransaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                val fragment: Fragment? = activity.supportFragmentManager.findFragmentByTag("dialog")
                if (fragment != null) {
                    fragmentTransaction.remove(fragment)
                }
                fragmentTransaction.addToBackStack(null)
                val name: String
                if (employee.lName == null)
                    name = employee.fName
                else
                    name = employee.fName + employee.lName
                val dialogFragment: DialogFragment = AttendanceHistoryFragment(name, employee.usrId)
                dialogFragment.show(fragmentTransaction, "dialog")
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty())
                    employeeList = filterationList
                else {
                    val resultList: MutableList<EmployeeDetail> = arrayListOf()
                    for (myData in filterationList) {
                        val name = "${myData.fName} ${myData.lName}"
                        if (name.toLowerCase().contains(charSearch))
                            resultList.add(myData)
                    }
                    employeeList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = employeeList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                employeeList = results?.values as MutableList<EmployeeDetail>
                notifyDataSetChanged()
            }
        }
    }
}

class ViewHolder(val binding: SingleAttendanceItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(employee: EmployeeDetail) {
        binding.employee = employee

        if (employee.mobileNum == null) {
            binding.btnVideoCall.visibility = View.GONE
            binding.btnCall.visibility = View.GONE
            binding.tvDesig.visibility = View.GONE
        } else {
            binding.btnVideoCall.visibility = View.VISIBLE
            binding.btnCall.visibility = View.VISIBLE
            binding.tvDesig.visibility = View.VISIBLE
        }
    }
}