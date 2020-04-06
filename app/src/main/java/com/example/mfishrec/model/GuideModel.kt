package com.example.mfishrec.model

data class GuideModel(
    val id: Int,
    val name: String,
    val imgurl: String,
    val description: String
) {
    constructor() : this(0,"","","")
}