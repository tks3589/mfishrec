package com.example.mfishrec.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Record(@NonNull var imguri:String,
             @NonNull var date:String,
             @NonNull var time:String){

    @PrimaryKey(autoGenerate = true)
    var id:Long = 0

}