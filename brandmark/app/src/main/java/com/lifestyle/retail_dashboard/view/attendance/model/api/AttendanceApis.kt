package com.lifestyle.retail_dashboard.view.attendance.model.api

import android.app.Activity
import com.lifestyle.retail_dashboard.network.ApiNetwork
import com.lifestyle.retail_dashboard.utils.Constant
import com.lifestyle.retail_dashboard.utils.Utility
import com.lifestyle.retail_dashboard.view.attendance.model.*
import com.lifestyle.retail_dashboard.view.brandperformance.model.BrandPerformanceRequest

class AttendanceApis(val activity: Activity) : ApiNetwork.SuccessResponseInterface {
    private val apiNetwork: ApiNetwork = ApiNetwork(activity)
    private var employeeListListener: BrandEmployeeListListener? = null
    private var attendanceHistoryListener: AttendanceHistoryListener? = null
    private var attendanceGraphListener: AttendanceGraphListener? = null
    private var storeListListener: StoreListListener? = null

    interface BrandEmployeeListListener {
        fun employeeList(response: EmployeeListResponse)
    }

    interface AttendanceHistoryListener {
        fun attendanceHistory(response: AttendanceResponse)
    }

    interface AttendanceGraphListener {
        fun attendanceGraph(response: AttendanceGraphResponse)
    }

    interface StoreListListener {
        fun storeList(response: StoreListResponse)
    }

    fun getAttendanceGraphData(attendanceGraphListener: AttendanceGraphListener?, request: AttendanceRequest?) {
        this.attendanceGraphListener = attendanceGraphListener
        apiNetwork.network(apiNetwork.getApiInterface().getAttendanceGraph(request),
                Constant.ATTENDANCE_GRAPH, this)
    }

    fun getAttendanceHistory(attendanceHistoryListener: AttendanceHistoryListener?, request: AttendanceRequest?) {
        this.attendanceHistoryListener = attendanceHistoryListener
        apiNetwork.network(apiNetwork.getApiInterface().getAttendanceHistory(request),
                Constant.ATTENDANCE_HISTORY, this)
    }

    fun getBrandUserDetail(employeeListListener: BrandEmployeeListListener?, request: BrandPerformanceRequest?) {
        this.employeeListListener = employeeListListener
        apiNetwork.network(apiNetwork.getApiInterface().getBrandUserDetail(request),
                Constant.GET_EMPLOYEE_LIST, this)
    }

    fun getStoreList(storeListListener: StoreListListener?, request: StoreListRequest?) {
        this.storeListListener = storeListListener
        apiNetwork.network(apiNetwork.getApiInterface().getStoreList(request),
                Constant.STORE_LIST, this)
    }

    override fun onSuccess(response: Any?, requestCode: Int) {
        Utility.hideKeyboard(activity)
        apiNetwork.hideProgressDialog()
        if (requestCode == Constant.GET_EMPLOYEE_LIST) {
            if (response is EmployeeListResponse) {
                employeeListListener?.employeeList(response)

            }
        }else if (requestCode == Constant.STORE_LIST) {
            if (response is StoreListResponse) {
                storeListListener?.storeList(response)

            }
        }else if (requestCode == Constant.ATTENDANCE_HISTORY) {
            if (response is AttendanceResponse) {
                attendanceHistoryListener?.attendanceHistory(response)

            }
        }else if (requestCode == Constant.ATTENDANCE_GRAPH) {
            if (response is AttendanceGraphResponse) {
                attendanceGraphListener?.attendanceGraph(response)

            }
        }
    }
}