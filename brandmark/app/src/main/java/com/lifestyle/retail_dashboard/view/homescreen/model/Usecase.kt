package com.lifestyle.retail_dashboard.view.homescreen.model

import android.os.Parcel
import android.os.Parcelable
import android.view.View


class Usecase(val name: String, val icon: Int): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt()) {
    }

    interface ItemClickListner{
        fun onItemClick(view: View, item: String)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usecase> {
        override fun createFromParcel(parcel: Parcel): Usecase {
            return Usecase(parcel)
        }

        override fun newArray(size: Int): Array<Usecase?> {
            return arrayOfNulls(size)
        }
    }
}