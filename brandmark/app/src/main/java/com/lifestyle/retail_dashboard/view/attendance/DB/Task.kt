package com.lifestyle.retail_dashboard.view.attendance.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import androidx.room.Index as Index1

@Entity(indices = [Index1(value = ["itemId"], unique = true)])
class Task(itemId: String,itemName: String,mappedWith: String,type:String) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    @ColumnInfo(name = "itemId")
    var itemId: String? = null

    @ColumnInfo(name = "itemName")
    var itemName: String? = null

    @ColumnInfo(name = "mappedWith")
    var mappedWith: String? = null

    @ColumnInfo(name = "type")
    var type: String? = null

    init {
        this.itemId = itemId
        this.itemName = itemName
        this.mappedWith = mappedWith
        this.type = type
    }
}