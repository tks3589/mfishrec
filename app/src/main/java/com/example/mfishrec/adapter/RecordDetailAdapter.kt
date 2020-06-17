package com.example.mfishrec.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mfishrec.R
import com.example.mfishrec.page.main.RecordFragment
import com.example.mfishrec.data.RecDatabase
import com.example.mfishrec.data.Record
import com.example.mfishrec.model.ResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_record_detail.view.*

class RecordDetailAdapter(val context: Context, var record:Record) : RecyclerView.Adapter<RecordDetailAdapter.RecordDetailHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordDetailHolder {
        return RecordDetailHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record_detail,parent,false))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecordDetailHolder, position: Int) {
        holder.imageView.setImageURI(Uri.parse(record.imguri))
        val listType = object : TypeToken<ArrayList<ResponseModel>>(){}.type
        val responseDataList = Gson().fromJson<ArrayList<ResponseModel>>(record.result, listType)
        var result = ""
        for(i in 0 until responseDataList.size)
            result += "No:${responseDataList[i].rank}(${responseDataList[i].name}) Score:${responseDataList[i].score} \n\n "
        holder.dataText.text = record.date+" "+record.time+"\n\n"+result.trim()
        //holder.dataText.text = record.date+" "+record.time+"\n\n"+record.result
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("是否刪除這筆紀錄？")
                .setPositiveButton("刪除"){ _ , _ ->
                    deleteRecord(context,record)
                }
                .setNegativeButton("取消"){ dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    class RecordDetailHolder(view:View) : RecyclerView.ViewHolder(view){
        var imageView = view.record_detail_imageview
        var dataText = view.record_detail_text
        var deleteButton = view.record_detail_delete_button
    }

    private fun deleteRecord(context: Context,record: Record){
        Thread {
            val database = RecDatabase.getInstance(context)
            database?.recordDao()?.delete(record)
            RecordFragment.instance.loadDB()
            (context as Activity).finish()
        }.start()
    }

}