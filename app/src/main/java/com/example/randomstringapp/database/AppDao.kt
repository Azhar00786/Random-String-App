package com.example.randomstringapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(generatedString: GeneratedStringEntity)

    @Query("SELECT * FROM generated_string_table ORDER BY timestamp DESC")
    fun getAllGeneratedStrings(): Flow<List<GeneratedStringEntity>>

    @Delete
    suspend fun deleteRecord(generatedString: GeneratedStringEntity)

    @Query("DELETE FROM generated_string_table")
    suspend fun deleteAllRecords()
}