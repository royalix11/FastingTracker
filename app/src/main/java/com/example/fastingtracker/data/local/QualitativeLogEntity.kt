package com.example.fastingtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

object SymptomBitmask {
    const val NONE = 0L
    const val HUNGER_SPIKE = 1L shl 0
    const val HEADACHE = 1L shl 1
    const val FATIGUE = 1L shl 2
    const val HIGH_FOCUS = 1L shl 3
    const val CHILLS = 1L shl 4
    const val EUPHORIA = 1L shl 5
    const val DIZZINESS = 1L shl 6
    const val CRAVINGS = 1L shl 7

    fun hasSymptom(mask: Long, symptom: Long): Boolean {
        return (mask and symptom) == symptom
    }

    fun addSymptom(mask: Long, symptom: Long): Long {
        return mask or symptom
    }

    fun removeSymptom(mask: Long, symptom: Long): Long {
        return mask and symptom.inv()
    }
}

@Entity(tableName = "qualitative_logs")
data class QualitativeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: Long = 0L,
    val timestampEpochMs: Long,
    val elapsedMinutesAtLog: Int,
    val metabolicStageId: Int, // 1..5
    val energyScore: Int, // 1..10
    val symptomBitmask: Long,
    val userNotes: String? = null
)
