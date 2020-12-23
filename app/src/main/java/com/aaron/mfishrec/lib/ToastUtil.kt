package com.aaron.mfishrec.lib

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object{
        fun loadDataErrorToast(context:Context){
            Toast.makeText(context,"Loading Data Error !", Toast.LENGTH_SHORT).show()
        }
    }
}