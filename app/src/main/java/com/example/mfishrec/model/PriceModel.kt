package com.example.mfishrec.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceModel(
    val date: String,
    val market: String,
    val name: String,
    val id: Long,
    val up: Double,
    val mid: Double,
    val down: Double,
    val avg: Double,
    val count: Double
) : Parcelable