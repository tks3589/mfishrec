package com.example.mfishrec

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_function.*

class FunctionFragment : Fragment(){

    companion object {
        private const val REQUEST_READ= 5566
        private const val GET_GALLERY= 7788
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

        }
        button_gallery.setOnClickListener {
            pickFromGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            if(requestCode == GET_GALLERY){
                val selectedUri = data?.data
                if(selectedUri!=null){
                    Toast.makeText(context,selectedUri.toString(),Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Cannot retrieve selected image",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun pickFromGallery(){
        if(context!!.hasPermission(READ_EXTERNAL_STORAGE)){
            var intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }

            startActivityForResult(Intent.createChooser(intent,"Select Picture"),GET_GALLERY)

        }else{
            requestPermissions(
                arrayOf(
                    READ_EXTERNAL_STORAGE
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
        if(requestCode == REQUEST_READ && context!!.hasPermission(READ_EXTERNAL_STORAGE)){
            pickFromGallery()
        }else{

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