package com.aaron.mfishrec.data

import android.content.Context


class Memory {
    companion object{
        fun setData(context: Context,key: String,data: Int){
            val sp = context.getSharedPreferences(key,Context.MODE_PRIVATE)
            sp.edit().putInt(key,data).commit()
        }

        fun getData(context: Context,key: String) : Int{
            val sp = context.getSharedPreferences(key,Context.MODE_PRIVATE)
            return sp.getInt(key,0)
        }
    }
}