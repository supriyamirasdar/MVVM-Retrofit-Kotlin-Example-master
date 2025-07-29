package com.lifestyle.retail_dashboard.view.planogram.model

import android.os.Parcel
import android.os.Parcelable

class Planogram() : Parcelable{
    var brandId = 0
    var brandName: String? = null
    var season: String? = null
    var filePath: String? = null
    var usrId: String? = null
    var planogramId: String? = null
    var fileName: String? = null
    var uploadDTTM: String? = null
    var concept: String? = null
    var week: String? = null
    var conceptNm:String? = null
    var storeId:String? = null
    var storeName:String? = null
    var plgImgId:String? = null

    constructor(parcel: Parcel) : this() {
        brandId = parcel.readInt()
        brandName = parcel.readString()
        season = parcel.readString()
        filePath = parcel.readString()
        usrId = parcel.readString()
        planogramId = parcel.readString()
        fileName = parcel.readString()
        uploadDTTM = parcel.readString()
        concept = parcel.readString()
        week = parcel.readString()
        conceptNm = parcel.readString()
        storeId = parcel.readString()
        storeName = parcel.readString()
        plgImgId = parcel.readString()
    }


    interface DeletePlanogramListner{
        fun deletePlanogram(planogram: Planogram)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel?, p1: Int) {
        parcel?.writeInt(brandId)
        parcel?.writeString("$brandName")
        parcel?.writeString("$season")
        parcel?.writeString("$filePath")
        parcel?.writeString("$usrId")
        parcel?.writeString("$planogramId")
        parcel?.writeString("$fileName")
        parcel?.writeString("$uploadDTTM")
        parcel?.writeString("$concept")
        parcel?.writeString("$week")
        parcel?.writeString("$conceptNm")
        parcel?.writeString("$storeId")
        parcel?.writeString("$storeName")
        parcel?.writeString("$plgImgId")
    }

    companion object CREATOR : Parcelable.Creator<Planogram> {
        override fun createFromParcel(parcel: Parcel): Planogram {
            return Planogram(parcel)
        }

        override fun newArray(size: Int): Array<Planogram?> {
            return arrayOfNulls(size)
        }
    }
}