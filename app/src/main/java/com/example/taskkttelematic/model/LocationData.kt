package com.example.taskkttelematic.model

import io.realm.RealmObject

open class LocationData(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var timestamp: Long = 0L,
    var userId: String = ""
) : RealmObject()