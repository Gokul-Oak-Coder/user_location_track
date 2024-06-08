package com.example.taskkttelematic.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserData(
    @PrimaryKey var id: String ="",
    var email:String ="",
    var password: String = "",
    var firstName : String = "",
    var lastName : String? = null

):RealmObject()