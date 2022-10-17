package com.aad.storyapp.view.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityCreateStoryBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.*
import com.aad.storyapp.model.Story
import com.aad.storyapp.model.User
import com.aad.storyapp.view.camera.CameraXActivity
import com.aad.storyapp.view.viewmodel.StoryViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private val storyViewModel: StoryViewModel by viewModel()

    private var currentPhotoPath: String? = null
    private var user: User? = null
    private var currentDate: String? = null
    private var getFile: File? = null

    private var lat: Double? = null
    private var lon: Double? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLocation()
            }
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLocation()
            }
            permission[Manifest.permission.CAMERA] ?: false -> {
                starCameraX()
            }
            else -> {
                // No access granted.
                Toast.makeText(this@CreateStoryActivity, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val resolutionLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
            RESULT_CANCELED -> Toast.makeText(this@CreateStoryActivity, "Anda harus mengaktifkan GPS untuk menggunakan membuka halaman ini!", Toast.LENGTH_SHORT).show()
        }
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

    companion object {
        private val TAG = this::class.java.simpleName
        const val CAMERA_X_RESULT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                        val lat = lat.toString().toRequestBody("text/plain".toMediaType())
                        val lon = lon.toString().toRequestBody("text/plain".toMediaType())

                        submitData(file, imageFile, description, lat, lon)
                    }
                }
            }
        }

        setupViewModel()
        createLocationRequest()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lat = location.latitude
                lon = location.longitude

                binding.tvStoryLocation.text = getAddressName(this@CreateStoryActivity, lat!!, lon!!)
                Log.d(TAG, "getMyLocation: Location found: $lat - $lon")
            } else {
                Toast.makeText(this@CreateStoryActivity, "Location is not found. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this@CreateStoryActivity, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun setupViewModel() {
//        storyViewModel = ViewModelProvider(this, ViewModelFactory())[StoryViewModel::class.java]
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
                        createdAt = currentDate!!,
                        lat = lat,
                        lon = lon
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

    private fun submitData(file: File, imageFile: RequestBody, description: RequestBody, lat: RequestBody, lon: RequestBody) {
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageFile
        )

        storyViewModel.create(imageMultipart, description, lat, lon)
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launchIntentGallery.launch(chooser)
    }

    private fun starCameraX() {
        if (!checkPermission(Manifest.permission.CAMERA)) {
            Toast.makeText(this@CreateStoryActivity, "Please give camera permission to use this feature.", Toast.LENGTH_SHORT).show()
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            return
        }

        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

}