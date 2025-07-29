package com.lifestyle.retail_dashboard.view.attendance.DB

import android.content.Context
import android.util.Log
import androidx.room.Room

class DatabaseClient private constructor(private val mCtx: Context) {
    val appDatabase: AppDatabase = Room.databaseBuilder(mCtx, AppDatabase::class.java, "MyToDos").build()

    companion object {
        private var mInstance: DatabaseClient? = null
        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance
        }
    }

    init {
        //creating the app database with Room database builder
        //MyToDos is the name of the database
        setupingData()
    }

    private fun setupingData() {
        try {
            //Region List
            val region1 = Task("R1", "South", "Region","Region")
            val region2 = Task("R2", "North", "Region","Region")
            val region3 = Task("R3", "West", "Region","Region")
            val region4 = Task("R4", "East", "Region","Region")

            appDatabase.taskDao().insert(region1)
            appDatabase.taskDao().insert(region2)
            appDatabase.taskDao().insert(region3)
            appDatabase.taskDao().insert(region4)

            //State List
            val state1 = Task("13", "HARYANA", "R2","State")
            val state2 = Task("10", "DELHI", "R2","State")
            val state3 = Task("34", "UTTAR PRADESH", "R2","State")
            val state4 = Task("29", "RAJASTHAN", "R2","State")
            val state5 = Task("28", "PUNJAB", "R2","State")
            val state6 = Task("6", "CHANDIGARH", "R2","State")
            val state7 = Task("33", "UTTARANCHAL", "R2","State")
            val state8 = Task("15", "JAMUU AND KASHMIR", "R2","State")
            val state9 = Task("17", "KARNATAKA", "R1","State")
            val state10 = Task("31", "TAMIL NADU", "R1","State")
            val state11 = Task("36", "TELANGANA", "R1","State")
            val state12 = Task("2", "ANDHRA PRADESH", "R1","State")
            val state13 = Task("18", "KERALA", "R1","State")
            val state14 = Task("21", "MAHARASHTRA", "R3","State")
            val state15 = Task("12", "GUJARAT", "R3","State")
            val state16 = Task("7", "CHHATTISGARH", "R3","State")
            val state17 = Task("35", "WEST BENGAL", "R4","State")
            val state18 = Task("26", "ORISSA", "R4","State")
            val state19 = Task("998", "MADHYAPRADESH", "R3","State")
            appDatabase.taskDao().insert(state1)
            appDatabase.taskDao().insert(state2)
            appDatabase.taskDao().insert(state3)
            appDatabase.taskDao().insert(state4)
            appDatabase.taskDao().insert(state5)
            appDatabase.taskDao().insert(state6)
            appDatabase.taskDao().insert(state7)
            appDatabase.taskDao().insert(state8)
            appDatabase.taskDao().insert(state9)
            appDatabase.taskDao().insert(state10)
            appDatabase.taskDao().insert(state11)
            appDatabase.taskDao().insert(state12)
            appDatabase.taskDao().insert(state13)
            appDatabase.taskDao().insert(state14)
            appDatabase.taskDao().insert(state15)
            appDatabase.taskDao().insert(state16)
            appDatabase.taskDao().insert(state17)
            appDatabase.taskDao().insert(state18)
            appDatabase.taskDao().insert(state19)
        }catch (e : Exception){
            Log.d("Abrar","Exception: ${e.localizedMessage}")
        }
    }
}