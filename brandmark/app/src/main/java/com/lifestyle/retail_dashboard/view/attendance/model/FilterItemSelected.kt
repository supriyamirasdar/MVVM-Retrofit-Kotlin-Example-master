package com.lifestyle.retail_dashboard.view.attendance.model

import android.os.Parcel
import android.os.Parcelable


class FilterItemSelected(val itemId: String,val itemName: String,val selectedType: String) : Parcelable{
    /*val itemName: String? = null
    val itemId: String? = null
    val mapedWith: String? = null*/
    var itemSelected: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
        itemSelected = parcel.readByte() != 0.toByte()
    }

    interface OnItemCheckListener {
        fun onItemCheck(item: FilterItemSelected)
        fun onItemUncheck(item: FilterItemSelected)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemId)
        parcel.writeString(itemName)
        parcel.writeString(selectedType)
        parcel.writeByte(if (itemSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterItemSelected> {
        override fun createFromParcel(parcel: Parcel): FilterItemSelected {
            return FilterItemSelected(parcel)
        }

        override fun newArray(size: Int): Array<FilterItemSelected?> {
            return arrayOfNulls(size)
        }
    }
}