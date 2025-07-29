package com.lifestyle.retail_dashboard.view.attendance.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.lifestyle.retail_dashboard.R
import com.lifestyle.retail_dashboard.databinding.FragmentAttendanceHistoryBinding
import com.lifestyle.retail_dashboard.utils.CommonUtility
import com.lifestyle.retail_dashboard.utils.Constant.dateFormat
import com.lifestyle.retail_dashboard.utils.Constant.inputDateFormat
import com.lifestyle.retail_dashboard.utils.Constant.timeFormat
import com.lifestyle.retail_dashboard.utils.Constant.timeFormat1
import com.lifestyle.retail_dashboard.utils.Utility.parsingDateFormate
import com.lifestyle.retail_dashboard.view.attendance.adapter.GridCellAdapter
import com.lifestyle.retail_dashboard.view.attendance.adapter.GridCellAdapterNew
import com.lifestyle.retail_dashboard.view.attendance.adapter.OnCalenderListener
import com.lifestyle.retail_dashboard.view.attendance.model.AttendanceDetail
import com.lifestyle.retail_dashboard.view.attendance.model.AttendanceRequest
import com.lifestyle.retail_dashboard.view.attendance.model.AttendanceResponse
import com.lifestyle.retail_dashboard.view.attendance.model.api.AttendanceApis
import java.text.SimpleDateFormat
import java.util.*


class AttendanceHistoryFragment(val name: String, val usrId: String?) : DialogFragment(), View.OnClickListener, OnCalenderListener, AttendanceApis.AttendanceHistoryListener {
    private var adapter: GridCellAdapter? = null
    //private var adapter: GridCellAdapterNew? = null
    private var mCalendar: Calendar? = null
    private var numDays = 30
    private lateinit var binding: FragmentAttendanceHistoryBinding
    private var attendanceList = mutableListOf<AttendanceDetail>()
    private var month = 0
    private var year = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendance_history, container, false)
        intiView()
        getAttendanceHistory()
        return binding.root
    }

    private fun intiView() {
        mCalendar = Calendar.getInstance(Locale.getDefault())
        month = mCalendar!!.get(Calendar.MONTH) + 1
        year = mCalendar!!.get(Calendar.YEAR)

        binding.prevMonth.setOnClickListener(this)
        binding.currentMonth.text = DateFormat.format(dateTemplate, mCalendar!!.time)
        binding.nextMonth.setOnClickListener(this)

        binding.tvSun.isSelected = true
        binding.tvMon.isSelected = true
        binding.tvTue.isSelected = true
        binding.tvWed.isSelected = true
        binding.tvThu.isSelected = true
        binding.tvFri.isSelected = true
        binding.tvSat.isSelected = true
        binding.tvDetail.isSelected = true

        binding.tvName.text = name

        binding.tvMobile.setOnClickListener {
            val dialog = activity?.let { it1 -> Dialog(it1) }
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.attendance_info)
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            val btn: Button = dialog?.findViewById<View>(R.id.btnCancel) as Button
            btn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

        //binding.calendar.setLayoutManager(GridLayoutManager(activity, 7))
        adapter = GridCellAdapter(activity, month, year, this, attendanceList)
        adapter?.notifyDataSetChanged()
        binding.calendar.adapter = adapter
    }

    private fun getAttendanceHistory() {
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMM-yyyy")
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month - 1
        val month_name = month_date.format(cal.time)
        numDays = cal.getActualMaximum(Calendar.DATE)

        val request = AttendanceRequest()
        request.userId = usrId
        request.toDate = "01-$month_name"
        request.fromDate = "$numDays-$month_name"
        val api = activity?.let { AttendanceApis(it) }
        api?.getAttendanceHistory(this, request)

    }

    override fun attendanceHistory(response: AttendanceResponse) {
        if (response.serverErrormsg != null)
            CommonUtility.showSnackBar(activity, response.serverErrormsg)
        else{
            attendanceList.clear()
            val attendanceHistory = response.brandStaffAttList
            if (attendanceHistory != null){
                for (detail in attendanceHistory){
                    detail.outTime = parsingDateFormate(detail.outTime, timeFormat1, timeFormat)
                    detail.inTime = parsingDateFormate(detail.inTime, timeFormat1, timeFormat)
                    val date = parsingDateFormate(detail.attnDate, inputDateFormat, dateFormat)
                    detail.attnDate = date
                    attendanceList.add(detail)
                }
            }

            updateUI(attendanceList)
            setGridCellAdapterToDate(month, year)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(attendanceList: MutableList<AttendanceDetail>) {
        var present = 0
        var holiday = 0
        var weekOff = 0
        var compoff = 0
        var optionHoliday = 0
        var leave = 0
        var absent = 0
        var halfDayLeave = 0
        var halfDayAbsent = 0
        for (attendance in attendanceList){
            if (attendance.status != null) {
                if (attendance.status.equals("P", true))
                    present++
                if (attendance.status.equals("H", true))
                    holiday++
                if (attendance.status.equals("CO", true))
                    compoff++
                if (attendance.status.equals("OH", true))
                    optionHoliday++
                if (attendance.status.equals("A", true))
                    absent++
                if (attendance.status.equals("L", true))
                    leave++
                if (attendance.status.equals("HL", true))
                    halfDayLeave++
                if (attendance.status.equals("WO", true))
                    weekOff++
                if (attendance.status.equals("HA", true))
                    halfDayAbsent++
            }
        }

        binding.tvPresent.text = ": $present"
        binding.tvAbsent.text = ": $absent"
        binding.tvComoff.text = ": $compoff"
        binding.tvLeave.text = ": $leave"
        binding.tvWO.text = ": $weekOff"
        binding.tvHA.text = ": $halfDayAbsent"
        binding.tvHL.text = ": $halfDayLeave"
        binding.tvHoliday.text = ": $holiday"
        binding.tvOptionHoliday.text = ": $optionHoliday"
    }

    private fun setGridCellAdapterToDate(month: Int, year: Int) {
        //binding.calendar.setLayoutManager(GridLayoutManager(activity, 7))
        adapter = GridCellAdapter(activity, month, year, this, attendanceList)
        mCalendar!![year, month - 1] = mCalendar!![Calendar.DAY_OF_MONTH]
        binding.currentMonth.text = DateFormat.format(dateTemplate, mCalendar!!.time)
        adapter!!.notifyDataSetChanged()
        binding.calendar.adapter = adapter
    }

    override fun onClick(v: View) {
        if (v === binding.prevMonth) {
            binding.tvDetail.text = ""
            if (month <= 1) {
                month = 12
                year--
            } else {
                month--
            }
            getAttendanceHistory()
            //setGridCellAdapterToDate(month, year)
        }
        if (v === binding.nextMonth) {
            binding.tvDetail.text = ""
            if (month > 11) {
                month = 1
                year++
            } else {
                month++
            }
            getAttendanceHistory()
            //setGridCellAdapterToDate(month, year)
        }
    }

    override fun onDateClicked(date: String) {
        Log.d("Abrar", "Selected $date")
        binding.tvDetail.text = date
    }

    companion object {
        @SuppressLint("NewApi", "NewApi", "NewApi", "NewApi")
        private val dateTemplate = "MMM yyyy"
    }
}