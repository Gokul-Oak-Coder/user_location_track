package com.example.taskkttelematic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.example.taskkttelematic.databinding.ActivityMainBinding
import com.example.taskkttelematic.location.LocationAdapter
import com.example.taskkttelematic.repository.LocationDataRepository
import com.example.taskkttelematic.map.MapsActivity
import com.example.taskkttelematic.model.LocationData
import com.example.taskkttelematic.model.UserData
import com.example.taskkttelematic.repository.UserDataRepository
import com.example.taskkttelematic.ui.SwitchUserDialogFragment
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.kotlin.where

class MainActivity : AppCompatActivity(), LocationAdapter.OnItemClickListener {

    private lateinit var realm: Realm
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var workManager: WorkManager
    private lateinit var currentUser: UserData
    private lateinit var locationAdapter: LocationAdapter

    private lateinit var locationCallback: LocationCallback

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Permission denied. Location updates will not be available.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = Realm.getDefaultInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        workManager = WorkManager.getInstance(this)

        binding.delBtn.setOnClickListener {
            clearLocationData()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        locationAdapter = LocationAdapter(mutableListOf(), this)
        binding.recyclerView.adapter = locationAdapter

        val fullName = intent.getStringExtra("USER_FULL_NAME")
        val email = intent.getStringExtra("USER_EMAIL").toString()
        binding.fullNameText.text = fullName
        loginUser(email)

        checkLocationPermissionAndStart()

        binding.switchUserBtn.setOnClickListener {
            val switchUserDialog = SwitchUserDialogFragment()
            switchUserDialog.show(supportFragmentManager, "switch_user_dialog")
        }
    }
    private fun loginUser(email: String) {
        currentUser = UserDataRepository.getUserData(email)
            ?: throw IllegalArgumentException("User not found")


        displayLocationData()

    }


    private fun displayLocationData() {
        val locationData = LocationDataRepository.getLocationData(currentUser.email).toMutableList()
        locationAdapter.apply {
            locations.clear()
            locations.addAll(locationData)
            notifyDataSetChanged()
        }
    }

    private fun clearLocationData() {
        if (::currentUser.isInitialized) {
            val dataDeleted = LocationDataRepository.deleteLocationData(currentUser.email)
            if (dataDeleted) {
                Toast.makeText(this, "Location data cleared.", Toast.LENGTH_SHORT).show()
                displayLocationData()
            } else {
                Toast.makeText(this, "No location data found to delete.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No user logged in. Cannot delete location data.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkLocationPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationUpdates() {
        if (::currentUser.isInitialized) {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // 10 seconds interval
                fastestInterval = 5000 // 5 seconds fastest interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    saveLocationToRealm(location!!)
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                Toast.makeText(this, "Location permission is not granted.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No user logged in. Cannot start location updates.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }


    private fun loadUserData(email: String) {
        val user = realm.where<UserData>().equalTo("email", email).findFirst()
        user?.let {
            binding.fullNameText.text = "${it.firstName} ${it.lastName}"
        }
    }

    fun switchUser(email: String) {
        stopLocationUpdates() // Stop updates for the current user
        loadUserData(email)
        loginUser(email)
        startLocationUpdates() // Start updates for the new user
    }


    private fun saveLocationToRealm(location: Location) {
        if (::currentUser.isInitialized) {
            realm.executeTransaction { r ->
                val locationData = r.createObject(LocationData::class.java)
                locationData.latitude = location.latitude
                locationData.longitude = location.longitude
                locationData.timestamp = System.currentTimeMillis()
                locationData.userId = currentUser.email
                Toast.makeText(this@MainActivity, "Location Added to Db", Toast.LENGTH_SHORT).show()

                // Update the RecyclerView dynamically
                locationAdapter?.let { adapter ->
                    adapter.addLocation(locationData)
                }
            }
        } else {
            Toast.makeText(this, "No user logged in. Cannot save location.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        realm.close()
    }


    override fun onItemClick(location: LocationData) {
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
            putExtra("USER_EMAIL", location.userId)
        }
        startActivity(intent)
    }
}



