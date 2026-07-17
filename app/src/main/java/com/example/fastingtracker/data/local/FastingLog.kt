package com.example.fastingtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_logs")
data class FastingLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMillis: Long,
    val targetDurationMillis: Long,
    val isCompleted: Boolean
)
