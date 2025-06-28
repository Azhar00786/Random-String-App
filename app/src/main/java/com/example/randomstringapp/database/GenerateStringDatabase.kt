package com.example.randomstringapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.randomstringapp.utils.AppConstants

@Database(
    entities = [GeneratedStringEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GeneratedStringDatabase : RoomDatabase() {

    abstract fun generatedStringDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: GeneratedStringDatabase? = null

        /**
         * Returns the singleton instance of [GeneratedStringDatabase].
         * Uses double-checked locking to ensure only one instance exists.
         */
        fun getDatabase(context: Context): GeneratedStringDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GeneratedStringDatabase::class.java,
                    AppConstants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}