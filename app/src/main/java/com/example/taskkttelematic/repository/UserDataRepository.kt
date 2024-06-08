package com.example.taskkttelematic.repository

import com.example.taskkttelematic.model.UserData
import io.realm.Realm
import io.realm.kotlin.where

object UserDataRepository {
   private val realm : Realm = Realm.getDefaultInstance()

    fun getUserData(email: String): UserData? {
        return realm.where<UserData>().equalTo("email", email).findFirst()
    }
}
