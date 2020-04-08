package com.example.mfishrec.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Record(@NonNull var imguri:String,
             @NonNull var date:String,
             @NonNull var time:String,
             @NonNull var result:String) : Serializable{

    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

}