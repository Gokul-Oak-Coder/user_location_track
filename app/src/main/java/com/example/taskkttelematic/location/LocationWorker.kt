package com.example.taskkttelematic.location


import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.Worker
import com.example.taskkttelematic.model.LocationData
import io.realm.Realm


class LocationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val latitude = inputData.getDouble(KEY_LATITUDE, 0.0)
        val longitude = inputData.getDouble(KEY_LONGITUDE, 0.0)
        saveLocationDataToRealm(latitude, longitude)
        return Result.success()
    }

    private fun saveLocationDataToRealm(latitude: Double, longitude: Double) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->
            val locationData = realm.createObject(LocationData::class.java)
            locationData.latitude = latitude
            locationData.longitude = longitude
        }
        realm.close()
    }

    companion object {
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
    }
}


