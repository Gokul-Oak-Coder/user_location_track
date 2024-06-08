package com.example.taskkttelematic.viewModel

import androidx.lifecycle.ViewModel
import com.example.taskkttelematic.model.UserData
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.UUID

class UserViewModel : ViewModel() {
    private val realm: Realm = Realm.getDefaultInstance()

    fun signup(email: String, password: String, firstName: String, lastName: String?, onResult: (Boolean, String) -> Unit) {

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
            onResult(false, "Please fill in all required fields")
            return
        }

        realm.executeTransaction { transactionRealm ->
            val existingUser = transactionRealm.where<UserData>().equalTo("email", email).findFirst()
            if (existingUser != null) {
                onResult(false, "Email or phone number already exists")
                return@executeTransaction
            }

            val user = transactionRealm.createObject<UserData>(UUID.randomUUID().toString())
            user.email = email
            user.password = password
            user.firstName = firstName
            user.lastName = lastName
            onResult(true, "Signup successful")
        }

    }

    fun login(email: String, password: String, onResult: (Boolean, String, String?) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Please fill in all required fields", null)
            return
        }

        val user = realm.where<UserData>().equalTo("email", email).equalTo("password", password).findFirst()
        if (user != null) {
            val fullName = "${user.firstName} ${user.lastName.orEmpty()}"
            onResult(true, "Login successful", fullName)
        } else {
            onResult(false, "Invalid email or password", null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}
