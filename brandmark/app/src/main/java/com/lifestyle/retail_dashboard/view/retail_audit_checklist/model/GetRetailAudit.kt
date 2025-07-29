package com.lifestyle.retail_dashboard.view.retail_audit_checklist.model

import android.os.Parcel
import android.os.Parcelable

class GetRetailAudit() : Parcelable{
    var racNo: String? = null
    var region: String? = null
    var store: String? = null
    var status: String? = null
    var assignTo: String? = null
    var brand: String? = null
    var date: String? = null

    constructor(parcel: Parcel) : this() {
        racNo = parcel.readString()
        region = parcel.readString()
        store = parcel.readString()
        status = parcel.readString()
        assignTo = parcel.readString()
        brand = parcel.readString()
        date = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(racNo)
        parcel.writeString(region)
        parcel.writeString(store)
        parcel.writeString(status)
        parcel.writeString(assignTo)
        parcel.writeString(brand)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetRetailAudit> {
        override fun createFromParcel(parcel: Parcel): GetRetailAudit {
            return GetRetailAudit(parcel)
        }

        override fun newArray(size: Int): Array<GetRetailAudit?> {
            return arrayOfNulls(size)
        }
    }
}