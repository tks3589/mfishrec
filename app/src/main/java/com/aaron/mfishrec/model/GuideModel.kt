package com.aaron.mfishrec.model

data class GuideModel(
    val id: Long,
    val name: String,
    val imgurl: String,
    val description: String
) {
    constructor() : this(0,"","","")
}