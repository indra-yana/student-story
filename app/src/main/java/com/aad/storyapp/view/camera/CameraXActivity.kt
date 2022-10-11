package com.aad.storyapp.view.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.aad.storyapp.databinding.ActivityCameraXBinding
import com.aad.storyapp.helper.createFile
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.view.story.CreateStoryActivity

class CameraXActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraXBinding

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            captureImage.setOnClickListener {
                takePhoto()
            }

            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (ex: Exception) {
                Toast.makeText(this@CameraXActivity, "Failed to launch camera!", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val imageCaptureCallback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(this@CameraXActivity, "The image has been successfully saved", Toast.LENGTH_SHORT).show()

                val intent = Intent()
                intent.putExtra("picture", photoFile)
                intent.putExtra("isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                setResult(CreateStoryActivity.CAMERA_X_RESULT, intent)
                finish()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@CameraXActivity, "Failed to take picture", Toast.LENGTH_SHORT).show()
            }
        }

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            imageCaptureCallback
        )
    }
}