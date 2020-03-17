package com.example.mfishrec

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_crop_result.*
import java.io.File
import java.lang.Exception

class CropResultActivity : AppCompatActivity() {

    companion object {
        fun startWithUri(context: Context,uri: Uri){
            var intent = Intent(context,CropResultActivity::class.java)
            intent.setData(uri)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_result)
        var uri = intent.getData()
        if(uri!=null){
            try{
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
            actionBar.setTitle(getString(R.string.format_crop_result_d_d,options.outWidth,options.outHeight))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_download){

        }else if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
