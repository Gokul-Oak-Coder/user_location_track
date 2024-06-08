package com.example.taskkttelematic.repository

import com.example.taskkttelematic.model.LocationData
import com.example.taskkttelematic.model.UserData
import io.realm.Realm
import io.realm.kotlin.where

object LocationDataRepository {
    private val realm : Realm = Realm.getDefaultInstance()
    fun saveLocationData(latitude: Double, longitude: Double) {
        realm.use { realm ->
            realm.executeTransactionAsync { realm ->
                val locationData = realm.createObject(LocationData::class.java)
                locationData.latitude = latitude
                locationData.longitude = longitude
            }
        }
    }

    fun getLocationData(userId:String): List<LocationData> {
        realm.use { realm ->
            return realm.where<LocationData>().equalTo("userId", userId).findAll()
        }
    }
    fun deleteLocationData(userId: String): Boolean {
        var isDeleted = false // Initialize the boolean variable
        realm.use { realm ->
            realm.executeTransaction { realm ->
                val results = realm.where<LocationData>().equalTo("userId", userId).findAll()
                if (results.isNotEmpty()) {
                    results.deleteAllFromRealm()
                    isDeleted = true // Set the variable to true if deletion is successful
                }
            }
        }
        return isDeleted // Return the boolean value after the transaction
    }

}