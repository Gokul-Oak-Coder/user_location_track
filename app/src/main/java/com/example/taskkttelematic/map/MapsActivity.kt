package com.example.taskkttelematic.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.taskkttelematic.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taskkttelematic.databinding.ActivityMapsBinding
import com.example.taskkttelematic.model.LocationData
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import io.realm.Realm
import io.realm.kotlin.where

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var playbackButton: Button

    private lateinit var locationHistory: List<LocationData>
    private var playbackHandler: Handler? = null
    private var playbackIndex = 0
    private lateinit var polyline: Polyline
    private lateinit var flightMarker: Marker

    private lateinit var binding: ActivityMapsBinding

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var timestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the location data from the intent
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        timestamp = intent.getLongExtra("timestamp", 0)
        val userEmail = intent.getStringExtra("USER_EMAIL").toString()

        locationHistory = getLocationHistory(userEmail)

        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        playbackButton = binding.playBack
        playbackButton.setOnClickListener {
            togglePlayback()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker at the received location and move the camera
        val initialLocation = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(initialLocation)
            .title("Location at $timestamp")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            .snippet("$latitude,$longitude,$timestamp")

        val initialMarker = mMap.addMarker(markerOptions)
        initialMarker?.showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 18f)) // Set initial zoom level to 18f

        // Set custom info window adapter for the map
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(inflater))

        // Initialize the polyline
        polyline = mMap.addPolyline(PolylineOptions().color(Color.BLUE).width(5f))

        // Initialize the flight marker with the vector drawable icon
        val flightMarkerOptions = MarkerOptions()
            .position(initialLocation)
            .icon(bitmapDescriptorFromVector(this, R.drawable.ic_flight)) // Use vector drawable
            .anchor(0.5f, 0.5f)

        flightMarker = mMap.addMarker(flightMarkerOptions)!!
    }

    private fun getLocationHistory(userId: String): List<LocationData> {
        val realm = Realm.getDefaultInstance()
        locationHistory = realm.where<LocationData>().equalTo("userId", userId).findAll()
        return realm.copyFromRealm(locationHistory)
    }

    private fun togglePlayback() {
        if (playbackHandler == null) {
            // Start playback
            playbackIndex = 0
            playbackHandler = Handler(Looper.getMainLooper())
            playbackHandler?.postDelayed(playbackRunnable, 1000) // Start playback with 1 second delay between locations

            // Clear polyline
            polyline.points = mutableListOf()
        } else {
            // Stop playback
            playbackHandler?.removeCallbacks(playbackRunnable)
            playbackHandler = null

            // Clear polyline
            mMap.clear()
            // Add the initial marker back
            val initialLocation = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
                .position(initialLocation)
                .title("Location at $timestamp")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet("$latitude,$longitude,$timestamp")

            mMap.addMarker(markerOptions)?.showInfoWindow()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 18f)) // Set initial zoom level to 18f

            // Initialize the polyline again
            polyline = mMap.addPolyline(PolylineOptions().color(Color.BLUE).width(5f))

            // Re-add the flight marker
            val flightMarkerOptions = MarkerOptions()
                .position(initialLocation)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_flight)) // Use vector drawable
                .anchor(0.5f, 0.5f)

            flightMarker = mMap.addMarker(flightMarkerOptions)!!
        }
    }

    private val playbackRunnable = object : Runnable {
        override fun run() {
            if (playbackIndex < locationHistory.size) {
                val location = locationHistory[playbackIndex]
                val latLng = LatLng(location.latitude, location.longitude)

                // Move camera to the current location with zoom level 18f
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        18f
                    )
                )

                // Update polyline
                val points = polyline.points
                points.add(latLng)
                polyline.points = points

                // Update flight marker
                flightMarker.position = latLng

                // Show custom info window
                flightMarker.title = "Location at ${location.timestamp}"
                flightMarker.snippet = "${location.latitude}, ${location.longitude}, ${location.timestamp}"
                flightMarker.showInfoWindow()

                // Add a marker at the current location
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Location at ${location.timestamp}")
                    .snippet("${location.latitude}, ${location.longitude}, ${location.timestamp}")

                mMap.addMarker(markerOptions)

                playbackIndex++
                playbackHandler?.postDelayed(this, 1000) // Continue playback with 1 second delay
            } else {
                playbackHandler = null
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}