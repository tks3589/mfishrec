package com.example.mfishrec.page.main

import android.Manifest.permission.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.mfishrec.page.camera.CameraxActivity
import com.example.mfishrec.page.crop.CropResultActivity
import com.example.mfishrec.R
import com.example.mfishrec.data.Memory
import com.example.mfishrec.page.container.ShowActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_function.*
import java.io.File

class FunctionFragment : Fragment(){

    companion object {
        private const val REQUEST_READ= 5566
        private const val GET_GALLERY= 7788
        private const val REQUEST_CAMERA = 4455
        private const val GET_PHOTO = 3344
        val instance : FunctionFragment by lazy {
            FunctionFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_function, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_camera.setOnClickListener {
            openCameraX()
        }
        button_gallery.setOnClickListener {
            pickFromGallery()
        }
        button_guide.setOnClickListener {
            openGuide()
        }
        val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName,0)
        version_textview.text = pInfo.versionName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            val settings = Memory.getData(context!!,"settings")
            if(requestCode == GET_GALLERY){
                val selectedUri = data?.data
                if(selectedUri!=null){
                    if(settings == 0)
                        startCrop(selectedUri)
                    else
                        CropResultActivity.startWithUri(context!!, selectedUri)
                }else{
                    Toast.makeText(context,"Cannot retrieve selected image",Toast.LENGTH_SHORT).show()
                }
            }else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            }else if(requestCode == GET_PHOTO){
                val photoUri = data?.data
                if(photoUri!=null){
                    if(settings == 0)
                        startCrop(photoUri)
                    else
                        CropResultActivity.startWithUri(context!!, photoUri)
                }else{
                    Toast.makeText(context,"Cannot retrieve camera photo image",Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data!!)
        }
    }

    fun handleCropError(result: Intent){
        var cropError = UCrop.getError(result)
        if(cropError!=null){
            Toast.makeText(context,cropError.message,Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context,"Unexpected error",Toast.LENGTH_SHORT).show()
        }
    }

    fun handleCropResult(result:Intent){
        var resultUri = UCrop.getOutput(result)
        if(resultUri!=null){
            CropResultActivity.startWithUri(context!!, resultUri)
        }else{
            Toast.makeText(context,"Cannot retrieve cropped image",Toast.LENGTH_SHORT).show()
        }
    }

    fun startCrop(uri:Uri){
        var ucrop = UCrop.of(uri,Uri.fromFile(File(context!!.cacheDir,"Crop_${System.currentTimeMillis()}.jpg")))
        var options = UCrop.Options()
        options.setFreeStyleCropEnabled(true)
        ucrop.withOptions(options)
        ucrop.start(activity!!)
    }

    fun openGuide(){
        var bundle = Bundle()
        bundle.putString("type","guide")
        var intent = Intent(context, ShowActivity::class.java)
        intent.putExtra("bundle",bundle)
        startActivity(intent)
    }

    fun openCameraX(){
        if(context!!.hasPermission(CAMERA, ACCESS_FINE_LOCATION)){
            startActivityForResult(Intent(context, CameraxActivity::class.java),
                GET_PHOTO
            )
        }else{
            requestPermissions(
                arrayOf(
                    CAMERA,
                    ACCESS_FINE_LOCATION
                )
                ,
                REQUEST_CAMERA
            )
        }
    }

    fun pickFromGallery(){
        if(context!!.hasPermission(READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)){
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }

            startActivityForResult(Intent.createChooser(intent,"Select Picture"),
                GET_GALLERY
            )

        }else{
            requestPermissions(
                arrayOf(
                    READ_EXTERNAL_STORAGE,
                    ACCESS_FINE_LOCATION
                )
                ,
                REQUEST_READ
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_READ && context!!.hasPermission(READ_EXTERNAL_STORAGE,
                ACCESS_FINE_LOCATION)){
            pickFromGallery()
        }else if(requestCode == REQUEST_CAMERA && context!!.hasPermission(CAMERA,
                ACCESS_FINE_LOCATION)){
            openCameraX()
        }
    }
}

fun Context.hasPermission(vararg permission: String): Boolean {
    return permission.all {
        ActivityCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}