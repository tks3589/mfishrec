package com.example.mfishrec.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Record::class], version = 1)
abstract class RecDatabase : RoomDatabase() {
    abstract fun recordDao():RecordDao
    companion object{
        private var instance : RecDatabase? = null
        fun getInstance(context: Context) : RecDatabase? {
            if(instance == null)
                instance = Room.databaseBuilder(context,RecDatabase::class.java,"record.db").build()

            return instance
        }
    }
}