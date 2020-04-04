package com.example.mfishrec.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record:Record)

    @Query("select * from record order by id desc")
    suspend fun getAll():List<Record>

}