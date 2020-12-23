package com.aaron.mfishrec.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.aaron.mfishrec.R
import com.aaron.mfishrec.page.main.RecordFragment
import com.aaron.mfishrec.data.RecDatabase
import com.aaron.mfishrec.data.Record
import com.aaron.mfishrec.model.ResponseModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_record_detail.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordDetailAdapter(val context: Context, var record:Record) : RecyclerView.Adapter<RecordDetailAdapter.RecordDetailHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordDetailHolder {
        return RecordDetailHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_record_detail,parent,false))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecordDetailHolder, position: Int) {
        val uri = Uri.parse(record.imguri)
        if(uri.toString().indexOf("/cache/") == -1) { //相簿照片的uri會有存取權限問題
            val contentResolver = context.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
        }
       // holder.imageView.setImageURI(uri)
        Glide.with(context).load(Uri.parse(uri.toString())).into(holder.imageView)
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
        CoroutineScope(Dispatchers.IO).launch {
            val database = RecDatabase.getInstance(context)
            database?.recordDao()?.delete(record)
            RecordFragment.instance.loadDB()
            (context as Activity).finish()
        }
    }

}