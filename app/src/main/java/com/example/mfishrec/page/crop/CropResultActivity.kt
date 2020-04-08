package com.example.mfishrec.page.crop

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mfishrec.R
import com.example.mfishrec.data.RecDatabase
import com.example.mfishrec.data.Record
import com.example.mfishrec.model.ResponseModel
import com.example.mfishrec.page.container.ShowActivity
import com.example.mfishrec.page.main.RecordFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_crop_result.*
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CropResultActivity : AppCompatActivity() {

    private var callbackUri: Uri? = null
    private var dialog: ProgressDialog? = null

    companion object {
        fun startWithUri(context: Context,uri: Uri){
            var intent = Intent(context, CropResultActivity::class.java)
            intent.data = uri
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_result)

        var uri = intent.data
        if(uri!=null){
            try{
                callbackUri = uri
                ucrop.cropImageView.setImageUri(uri,null)
                ucrop.overlayView.setShowCropFrame(false)
                ucrop.overlayView.setShowCropGrid(false)
                ucrop.overlayView.setDimmedColor(Color.TRANSPARENT)
            }catch (e:Exception){
                Log.e("Error:", "setImageUri", e)
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(File(intent.data.path).absolutePath,options)

        setSupportActionBar(toolbar)
        var actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.format_crop_result_d_d,options.outWidth,options.outHeight)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_upload){
            dialog = ProgressDialog.show(this@CropResultActivity,"辨識中","請稍候...")
            callbackUri?.let { Thread { uploadImg(it) }.start() }
        }else if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun uploadImg(uri: Uri){
        var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
        bitmap = resizeBitmap(bitmap,299,299)
        var buf = ByteArrayOutputStream(bitmap.width * bitmap.height)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,buf)
        var encodeImage = String(Base64.encode(buf.toByteArray(),Base64.DEFAULT))
        //Log.d("OK:",encodeImage.trim())
        var data = FormBody.Builder().add("img",encodeImage).build()
        var request = Request.Builder()
                        .url("http://163.18.42.141:5000/api/recognize")
                        .method("POST",data)
                        .build()
        var client = OkHttpClient.Builder()
            .connectTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .build()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    dialog?.dismiss()
                    Log.d("Error:",e.toString())
                    Toast.makeText(this@CropResultActivity,e.toString(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    dialog?.dismiss()

                    var responseData = response.body?.string()
                    Log.d("OK:",responseData)
                    val listType = object : TypeToken<ArrayList<ResponseModel>>(){}.type
                    val responseDataList = Gson().fromJson<ArrayList<ResponseModel>>(responseData, listType)
                    responseData?.let { insertRecord(it) }

                    var alertBuilder = AlertDialog.Builder(this@CropResultActivity)
                    alertBuilder.setCancelable(false)
                    alertBuilder.setPositiveButton("OK") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    var dataArr = arrayOfNulls<String>(responseDataList.size)
                    for(i in dataArr.indices){
                        var model = responseDataList[i]
                        dataArr[i] = "No: ${model.rank} (${model.name}) \n Score: ${model.score} \n"
                    }
                    alertBuilder.setItems(dataArr,null)

                    var alert = alertBuilder.create()
                    alert.listView.setOnItemClickListener { _, _, i, _ ->
                        var model = responseDataList[i]
                        Log.d("alert",dataArr[i])
                        var intent = Intent(this@CropResultActivity,
                            ShowActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("type","crop_result")
                        bundle.putInt("rank",model.rank)
                        bundle.putString("name",model.name)
                        bundle.putFloat("score",model.score)
                        intent.putExtra("bundle",bundle)
                        startActivity(intent)
                    }

                    alert.show()

                }
            }
        })
    }

    private fun insertRecord(result:String){
        Thread {
            val database = RecDatabase.getInstance(this)
            val sdf_date = SimpleDateFormat("yyyy/MM/dd")
            val sdf_time = SimpleDateFormat("HH:mm:ss")
            val record = Record(callbackUri!!.toString(), sdf_date.format(Date()), sdf_time.format(Date()),result)
            database?.recordDao()?.insert(record)
            RecordFragment.instance.loadDB()
        }.start()
    }

    private fun resizeBitmap(bitmap:Bitmap, rwidth:Int, rheight:Int) : Bitmap{
        val bwidth = bitmap.width
        val bheight = bitmap.height
        var scaleWidth = rwidth.toFloat() / bwidth
        var scaleHeight = rheight.toFloat() / bheight
        var matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        //Log.d("OK: ","$bwidth , $bheight")
        return Bitmap.createBitmap(bitmap,0,0,bwidth,bheight,matrix,true)
    }


}
