package com.aad.storyapp.view.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityCreateStoryBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.*
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import com.aad.storyapp.view.camera.CameraXActivity
import com.aad.storyapp.view.viewmodel.StoryViewModel
import com.aad.storyapp.view.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var storyViewModel: StoryViewModel

    private var currentPhotoPath: String? = null
    private var user: User? = null
    private var currentDate: String? = null
    private var getFile: File? = null

    companion object {
        private val TAG = CreateStoryActivity::class.java.simpleName
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this@CreateStoryActivity, "Please give permission to this app", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
        }

        user = intent.getParcelableExtra("user")
        currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(System.currentTimeMillis())

        with(binding) {
            tvStoryTitle.text = user?.name ?: ""
            tvStoryDate.text = getString(R.string.createdAtFormat, currentDate?.withDateFormat())

            btnUploadCamera.setOnClickListener { starCameraX() }
            btnUploadGallery.setOnClickListener { startGallery() }
            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnSave.setOnClickListener {

                val edStoryDescription = edStoryDescription.text.toString().trim()

                when {
                    edStoryDescription.isEmpty() -> {
                        edStoryDescriptionLayout.error = getString(R.string.validation_description_required)
                    }
                    getFile == null -> {
                        Toast.makeText(this@CreateStoryActivity, getString(R.string.validation_image_required), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        edStoryDescriptionLayout.error = ""

                        val file = reduceFileImage(getFile as File)
                        val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val description = edStoryDescription.toRequestBody("text/plain".toMediaType())

                        submitData(file, imageFile, description)
                    }
                }
            }
        }

        setupViewModel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(this, ViewModelFactory())[StoryViewModel::class.java]
        storyViewModel.storyCreateResponse.observe(this) {

            with(binding) {
                pbLoading.visible(it is ResponseStatus.Loading)
                btnSave.enable(it !is ResponseStatus.Loading)
            }

            when (it) {
                is ResponseStatus.Loading -> {
                    // TODO: Do something when loading
                }
                is ResponseStatus.Success -> {
                    val story = Story(
                        id = "story-${System.currentTimeMillis()}",
                        name = user?.name!!,
                        photoUrl = currentPhotoPath!!,
                        description = binding.edStoryDescription.text.toString().trim(),
                        createdAt = currentDate!!
                    )

                    val intent = Intent()
                    intent.putExtra("story", story)
                    setResult(ListStoryActivity.CREATE_STORY_RESULT, intent)
                    finish()

                    Toast.makeText(this@CreateStoryActivity, it.value.message, Toast.LENGTH_SHORT).show()
                }
                is ResponseStatus.Failure -> {
                    Toast.makeText(this@CreateStoryActivity, getString(R.string.create_story_failed, it.value?.message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(TAG, "setupViewModel: Unknown ResponseStatus")
                }
            }
        }
    }

    private fun submitData(file: File, imageFile: RequestBody, description: RequestBody) {
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageFile
        )

        storyViewModel.create(imageMultipart, description)
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchIntentGallery.launch(chooser)
    }

    private fun starCameraX() {
        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            currentPhotoPath = Uri.fromFile(myFile).toString()
            getFile = myFile

            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)
            binding.ivStoryProfile.setImageBitmap(result)
        }
    }

    private val launchIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImage, this@CreateStoryActivity)

            currentPhotoPath = Uri.fromFile(myFile).toString()
            getFile = myFile

            binding.ivStoryProfile.setImageURI(selectedImage)
        }
    }


}