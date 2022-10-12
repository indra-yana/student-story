package com.aad.storyapp.view.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aad.storyapp.R
import com.aad.storyapp.databinding.ActivityMapsStoryBinding
import com.aad.storyapp.datasource.remote.response.ResponseStatus
import com.aad.storyapp.helper.setupView
import com.aad.storyapp.helper.visible
import com.aad.storyapp.model.Story
import com.aad.storyapp.view.viewmodel.StoryViewModel
import com.aad.storyapp.view.viewmodel.ViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import java.util.concurrent.TimeUnit


class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var stories = arrayListOf<Story>()
    private lateinit var storyViewModel: StoryViewModel
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
            else -> {
                // No location access granted.
                Toast.makeText(this@MapsStoryActivity, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val resolutionLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
            RESULT_CANCELED -> Toast.makeText(this@MapsStoryActivity, "Anda harus mengaktifkan GPS untuk menggunakan membuka halaman ini!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setupView(supportActionBar)
        binding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // getMyLocation()
        setMapStyle()
        setupVieModel()
        createLocationRequest()
    }

    private fun setupVieModel() {
        storyViewModel = ViewModelProvider(this, ViewModelFactory())[StoryViewModel::class.java]
        storyViewModel.storiesResponse.observe(this) {

            binding.pbLoading.visible(it is ResponseStatus.Loading)

            when (it) {
                is ResponseStatus.Loading -> {
                    // TODO: Handle loading state
                }
                is ResponseStatus.Success -> {
                    stories = it.value.listStory
                    createLocationRequest()
                    addManyMarker()
                }
                is ResponseStatus.Failure -> {
                    Toast.makeText(this@MapsStoryActivity, getString(R.string.fetch_data_failed, it.value?.message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(TAG, "setupVieModel: Unknown ResponseStatus")
                }
            }
        }

        storyViewModel.stories(size = 20, location = 1)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    markMyLocation(location)
                } else {
                    Toast.makeText(this@MapsStoryActivity, "Location is not found. Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
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
                        Toast.makeText(this@MapsStoryActivity, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun markMyLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val addressName = getAddressName(latitude, longitude)
        val myPoint = LatLng(latitude, longitude)

        mMap.addMarker(
            MarkerOptions()
                .position(myPoint)
                .title(getString(R.string.start_point))
                .snippet("My Location: $addressName")
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPoint, 5f))
    }

    private fun addManyMarker() {
        stories.forEach { story ->
            val latitude = story.lat
            val longitude = story.lon

            if (latitude != null && longitude != null) {
                val latLng = LatLng(latitude, longitude)
                val addressName = getAddressName(latitude, longitude)

                if (addressName != null) {
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("${story.name}: ${story.description}")
                            .snippet(addressName)
                    )

                    boundsBuilder.include(latLng)
                }

            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        // Use real device
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsStoryActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addressName
    }

}