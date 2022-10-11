package com.aad.storyapp.view.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private var stories = arrayListOf<Story>()
    private lateinit var storyViewModel: StoryViewModel
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getMyLocation()
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

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        setupVieModel()
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

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addManyMarker() {
        stories.forEach { story ->
            val latitude = story.lat
            val longitude = story.lon

            if (latitude != null && longitude != null) {
                val latLng = LatLng(latitude, longitude)
                val addressName = getAddressName(latitude, longitude)

                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("${story.name}: ${story.description}")
                        .snippet(addressName)
                )

                boundsBuilder.include(latLng)
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
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
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
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

}