package com.aaron.mfishrec.page.crop

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.aaron.mfishrec.R
import com.aaron.mfishrec.data.RecDatabase
import com.aaron.mfishrec.data.Record
import com.aaron.mfishrec.lite.ClassifierModel
import com.aaron.mfishrec.lite.ImageClassification
import com.aaron.mfishrec.model.ResponseModel
import com.aaron.mfishrec.page.container.ShowActivity
import com.aaron.mfishrec.page.main.RecordFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_crop_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
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
    private var imageClassification: ImageClassification? = null
    private val MODEL_PATH = "mfish.tflite"
    private val LABEL_PATH = "mfish_labels.txt"

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
        BitmapFactory.decodeFile(File(intent.data!!.path).absolutePath,options)

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
            /*if(checkNetworkState())
                callbackUri?.let { Thread { uploadImg(it) }.start() }
            else*/
            callbackUri?.let { loadModule(it) }
        }else if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkNetworkState():Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true
        return isConnected
    }

    private fun loadModule(uri:Uri){
        CoroutineScope(Dispatchers.IO).launch {
            if(imageClassification == null) {
                imageClassification = ImageClassification.create(
                    classifierModel = ClassifierModel.FLOAT,
                    assetManager = assets,
                    modelPath = MODEL_PATH,
                    labelPath = LABEL_PATH
                )
                Log.d("results","init")
                detectObject(uri)
            }else{
                detectObject(uri)
            }
        }
    }

    private fun detectObject(uri:Uri){
        CoroutineScope(Dispatchers.IO).launch {
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            bitmap = resizeBitmap(bitmap,299,299)
            val results = async { imageClassification?.classifyImage(bitmap) }
            //Log.d("results",results.await().toString())
            CoroutineScope(Dispatchers.Main).launch {
                var result = results.await().toString()
                result = result.substring(1,result.length-2)
                val splitList = result.split("@").toTypedArray()

                val alert = AlertDialog.Builder(this@CropResultActivity)
                    .setItems(splitList,null)
                    .create()

                var rootArray = JSONArray()
                for(i in splitList.indices){
                    var splitList2 = splitList[i].split(",")
                    var rootObject= JSONObject()
                    if(i != 0) {
                        rootObject.put("rank", splitList2[1].split("=")[1].trim().toInt())
                        rootObject.put("name", splitList2[2].split("=")[1].trim())
                        rootObject.put("score", splitList2[3].split("=")[1].trim())
                    }else{
                        rootObject.put("rank", splitList2[0].split("=")[1].trim().toInt())
                        rootObject.put("name", splitList2[1].split("=")[1].trim())
                        rootObject.put("score", splitList2[2].split("=")[1].trim())
                    }
                    rootArray.put(rootObject)
                }
                val listType = object : TypeToken<ArrayList<ResponseModel>>(){}.type
                val responseData = rootArray.toString()
                val responseDataList = Gson().fromJson<ArrayList<ResponseModel>>(responseData , listType)
                insertRecord(responseData)

                alert.listView.setOnItemClickListener { _, _, i, _ ->
                    val model = responseDataList[i]
                    if(model.rank!=99) {
                        var intent = Intent(
                            this@CropResultActivity,
                            ShowActivity::class.java
                        )
                        var bundle = Bundle()
                        bundle.putString("type", "crop_result")
                        bundle.putInt("rank", model.rank)
                        bundle.putString("name", model.name)
                        bundle.putFloat("score", model.score)
                        intent.putExtra("bundle", bundle)
                        startActivity(intent)
                    }
                }

                alert.show()
                dialog?.dismiss()
            }
        }
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
                        if(model.rank!=99) {
                            var intent = Intent(
                                this@CropResultActivity,
                                ShowActivity::class.java
                            )
                            var bundle = Bundle()
                            bundle.putString("type", "crop_result")
                            bundle.putInt("rank", model.rank)
                            bundle.putString("name", model.name)
                            bundle.putFloat("score", model.score)
                            intent.putExtra("bundle", bundle)
                            startActivity(intent)
                        }
                    }

                    alert.show()

                }
            }
        })
    }

    private fun insertRecord(result:String){
        CoroutineScope(Dispatchers.IO).launch {
            val database = RecDatabase.getInstance(this@CropResultActivity)
            val sdf_date = SimpleDateFormat("yyyy/MM/dd")
            val sdf_time = SimpleDateFormat("HH:mm:ss")
            val record = Record(
                callbackUri!!.toString(),
                sdf_date.format(Date()),
                sdf_time.format(Date()),
                result
            )
            database?.recordDao()?.insert(record)
            RecordFragment.instance.loadDB()
        }
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
