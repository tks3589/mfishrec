package com.aaron.mfishrec.data

import androidx.room.*

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(record:Record)

    @Query("select * from record order by id desc")
    suspend fun getAll():List<Record>

    @Delete
    fun delete(record: Record)


}