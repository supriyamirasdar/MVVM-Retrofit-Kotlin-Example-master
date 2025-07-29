package com.lifestyle.retail_dashboard.view.store_contact.model

import android.os.Parcel
import android.os.Parcelable

class GetBrandRacAvailEmpList() : Parcelable {

    var storeId: String? = null
    var cmName: String? = null
    var brndId: String? = null
    var email: String? = null
    var mobile: String? = null
    var roleNm: String? = null
    var spoc: String? = null

    constructor(parcel: Parcel) : this() {
        storeId = parcel.readString()
        cmName = parcel.readString()
        brndId = parcel.readString()
        email = parcel.readString()
        mobile = parcel.readString()
        roleNm = parcel.readString()
        spoc = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storeId)
        parcel.writeString(cmName)
        parcel.writeString(brndId)
        parcel.writeString(email)
        parcel.writeString(mobile)
        parcel.writeString(roleNm)
        parcel.writeString(spoc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetBrandRacAvailEmpList> {
        override fun createFromParcel(parcel: Parcel): GetBrandRacAvailEmpList {
            return GetBrandRacAvailEmpList(parcel)
        }

        override fun newArray(size: Int): Array<GetBrandRacAvailEmpList?> {
            return arrayOfNulls(size)
        }
    }


}