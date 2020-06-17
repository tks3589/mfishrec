package com.example.mfishrec.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MenuModel (
    val name:String,
    val imgurl:String,
    val materials:ArrayList<String>,
    val steps:ArrayList<String>
) : Parcelable