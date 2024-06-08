package com.example.taskkttelematic

import android.app.Application
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration


class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(3)
            .migration(MyMigration())
            .allowWritesOnUiThread(true) // Enable writes on UI thread
            .build()
        Realm.setDefaultConfiguration(config)
    }

    inner class MyMigration : RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            val schema = realm.schema

            if (oldVersion < 1L) {
                schema.get("LocationData")!!
                    .addField("userId", String::class.java, FieldAttribute.REQUIRED)
            }
        }
    }
}