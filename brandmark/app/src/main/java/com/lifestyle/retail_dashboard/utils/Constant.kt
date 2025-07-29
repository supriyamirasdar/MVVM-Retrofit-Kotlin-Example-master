package com.lifestyle.retail_dashboard.utils

import android.graphics.Rect
import android.view.View
import java.text.DecimalFormat
import java.text.SimpleDateFormat

object Constant {
    const val appType = "BRND"
    const val TIME_OUT = 120
    const val UPLODA_PLANOGRAM = 500
    const val GET_PLANOGRAM_LIST = 501
    const val DELETE_PLANOGRAM = 502
    const val GET_BRAND_PERFROMANCE = 503
    const val GET_BRAND_GRAPH = 504
    const val LOGIN_DETAIL = 505
    const val CHANGE_PASSWORD = 506
    const val FORGOT_PASSWORD = 512
    const val GET_EMPLOYEE_LIST = 507
    const val STORE_LIST = 508
    const val ATTENDANCE_HISTORY = 509
    const val ATTENDANCE_GRAPH = 510
    const val CHECK_APP_VERSION = 511
    const val GET_OJE_LIST = 512
    const val UPDATE_OJE_SCORE = 513
    const val CREATE_NEW_REATIL_AUDIT = 514
    const val GET_RAC_LIST = 515
    const val GET_BRAND_RANK = 516
    const val GET_INVENTORY_MANAGMENT = 517
    const val GET_RAC_EMP_LIST = 518
    const val MEGABYTE = 1024L * 1024L
    const val FIRST_ARGUMENT = "PassArgument1"
    const val SECOND_ARGUMENT = "PassArgument2"
    var floatingDecimalFormate2 = DecimalFormat("#,##,###.#")
    const val LACS = 100000.0
    const val CRORE = 10000000.0
    const val NO_OF_CELL = 3
    const val ALIGNMENT_LEFT = "left"
    const val ALIGNMENT_RIGHT = "right"
    const val ALIGNMENT_CENTER = "center"
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val inputDateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
    val timeFormat = SimpleDateFormat("HH:mm")
    val timeFormat1 = SimpleDateFormat("HH:mm:ss")

    const val RAC_STANDARD = "Standard"
    const val RAC_BUSINESS = "Business"
    const val RAC_BACKSTORE = "BackStore"

    fun getViewLocation(v: View?): Rect? {
        val loc_int = IntArray(2)
        if (v == null) return null
        try {
            v.getLocationOnScreen(loc_int)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }
        val location = Rect()
        location.left = loc_int[0]
        location.top = loc_int[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }

    fun getScreenHeightBelowView(v: View, screenHeight: Int): Int {
        val loc_int = IntArray(2)
        try {
            v.getLocationOnScreen(loc_int)
        } catch (npe: NullPointerException) {
        }
        val location = Rect()
        location.left = loc_int[0]
        location.top = loc_int[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return screenHeight - location.bottom
    }
}