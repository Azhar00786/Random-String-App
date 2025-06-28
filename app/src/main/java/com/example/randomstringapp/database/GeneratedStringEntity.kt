package com.example.randomstringapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_string_table")
data class GeneratedStringEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: String,
    val length: Int,
    val timestamp: Long
)