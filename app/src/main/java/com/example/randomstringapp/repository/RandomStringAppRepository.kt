package com.example.randomstringapp.repository

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import com.example.randomstringapp.database.AppDao
import com.example.randomstringapp.database.GeneratedStringEntity
import com.example.randomstringapp.utils.AppConstants
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class RandomStringAppRepository(
    private val dao: AppDao,
    private val contentResolver: ContentResolver
) {
    val generatedStrings: Flow<List<GeneratedStringEntity>> = dao.getAllGeneratedStrings()

    /**
     * Queries the external ContentProvider for a random string of the given length
     * and saves the result in the local Room database.
     *
     * @param length The length of the random string to request.
     * @return [Result.success] if the operation succeeds, or [Result.failure] with an exception.
     */
    suspend fun queryAndSaveGeneratedString(length: Int): Result<Unit> {
        return try {
            val uri = Uri.parse(AppConstants.URI_STRING)
            val bundle = Bundle().apply {
                putInt("android:query-arg-limit", length)
            }

            val cursor = contentResolver.query(uri, arrayOf("data"), bundle, null)
            val entity = cursor?.use {
                if (it.moveToFirst()) {
                    val jsonStr = it.getString(it.getColumnIndexOrThrow("data"))
                    val jsonObj = JSONObject(jsonStr).getJSONObject("randomText")
                    val value = jsonObj.getString("value")
                    val len = jsonObj.getInt("length")
                    val createdStr = jsonObj.getString("created")
                    val timestamp =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                            .apply { timeZone = TimeZone.getTimeZone("UTC") }
                            .parse(createdStr)?.time ?: System.currentTimeMillis()

                    GeneratedStringEntity(value = value, length = len, timestamp = timestamp)
                } else null
            }

            if (entity != null) {
                dao.insert(entity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No data returned from ContentProvider"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a single record from the local Room database.
     *
     * @param record The [GeneratedStringEntity] to delete.
     */
    suspend fun deleteSingleRecord(record: GeneratedStringEntity) {
        dao.deleteRecord(record)
    }

    /**
     * Deletes all generated string records from the local Room database.
     */
    suspend fun clearAllRecords() {
        dao.deleteAllRecords()
    }
}