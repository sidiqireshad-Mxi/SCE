package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_history")
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceCode: String,
    val sourceName: String,
    val destinationCode: String,
    val destinationName: String,
    val amount: Double,
    val result: Double,
    val isOnline: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
