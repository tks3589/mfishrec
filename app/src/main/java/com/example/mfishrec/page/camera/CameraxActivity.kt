package com.example.mfishrec.page.camera

import android.app.Activity
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.example.mfishrec.R
import kotlinx.android.synthetic.main.activity_camerax.*
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executors

class CameraxActivity : AppCompatActivity() ,LifecycleOwner{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)
        try{
            viewFinder = findViewById(R.id.view_finder)
            viewFinder.post { startCamera() }
            viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }
        }catch (e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    private fun startCamera(){
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        capture_button.setOnClickListener {
//            val file = File(externalMediaDirs.first(),
//                "${System.currentTimeMillis()}.jpg")
            val file = File(cacheDir,"CameraxPicture.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            intent.data = Uri.fromFile(file)
                            setResult(Activity.RESULT_OK,intent)
                            finish()
                        }
                    }
                })
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview,imageCapture)
    }

    private fun updateTransform(){
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }


}
