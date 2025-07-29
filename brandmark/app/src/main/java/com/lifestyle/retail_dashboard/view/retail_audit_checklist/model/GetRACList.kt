package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model

import android.os.Parcel
import android.os.Parcelable

class GetRACList() : Parcelable {
    var racNo: String? = null
    var storeId: String? = null
    var whStoreName: String? = null
    var cityName: String? = null
    var rgnName: String? = null
    var brandId: String? = null
    var brndDesc: String? = null
    var lsConceptMgrNm: String? = null
    var auditBy: String? = null
    var auditDate: String? = null
    var appType: String? = null
    var racStatus: String? = null
    val brandRacAuditList = mutableListOf<GetRACLineItem>()

    constructor(parcel: Parcel) : this() {
        racNo = parcel.readString()
        storeId = parcel.readString()
        whStoreName = parcel.readString()
        cityName = parcel.readString()
        rgnName = parcel.readString()
        brandId = parcel.readString()
        brndDesc = parcel.readString()
        lsConceptMgrNm = parcel.readString()
        auditBy = parcel.readString()
        auditDate = parcel.readString()
        appType = parcel.readString()
        racStatus = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(racNo)
        parcel.writeString(storeId)
        parcel.writeString(whStoreName)
        parcel.writeString(cityName)
        parcel.writeString(rgnName)
        parcel.writeString(brandId)
        parcel.writeString(brndDesc)
        parcel.writeString(lsConceptMgrNm)
        parcel.writeString(auditBy)
        parcel.writeString(auditDate)
        parcel.writeString(appType)
        parcel.writeString(racStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetRACList> {
        override fun createFromParcel(parcel: Parcel): GetRACList {
            return GetRACList(parcel)
        }

        override fun newArray(size: Int): Array<GetRACList?> {
            return arrayOfNulls(size)
        }
    }
}