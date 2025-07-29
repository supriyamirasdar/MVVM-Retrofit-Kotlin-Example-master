package com.lifestyle.retail_dashboard.view.attendance.DB

import androidx.room.*

@Dao
interface TaskDao {
    @get:Query("SELECT * FROM task")
    val all: List<Task?>?

    @Query("SELECT * FROM task WHERE `mappedWith` in (:mappedWith)")
    fun getPerticularData(mappedWith: List<String>?): List<Task?>?

    @Query("SELECT * FROM task WHERE `type` = (:type)")
    fun getStoreList(type: String): List<Task?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task?)

    @Delete
    fun delete(task: Task?)

    @Update
    fun update(task: Task?)
}