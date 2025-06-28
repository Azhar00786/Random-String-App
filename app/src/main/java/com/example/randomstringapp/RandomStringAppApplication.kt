package com.example.randomstringapp

import android.app.Application
import com.example.randomstringapp.database.GeneratedStringDatabase
import com.example.randomstringapp.repository.RandomStringAppRepository

/**
 * [RandomStringAppApplication] provides application-wide singletons,
 * including the Room database and the repository instance.
 */
class RandomStringAppApplication : Application() {
    private val database by lazy { GeneratedStringDatabase.getDatabase(this) }
    val repository by lazy {
        RandomStringAppRepository(
            database.generatedStringDao(),
            contentResolver
        )
    }
}