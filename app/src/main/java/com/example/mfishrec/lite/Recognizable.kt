package com.example.mfishrec.lite

data class Recognizable(
    var id: String = "",
    var name: String = "",
    var confidence: Float = 0F
) {
    override fun toString(): String {
        return "Label Id = $id, Name = $name, Confidence = $confidence \n\n"
    }
}