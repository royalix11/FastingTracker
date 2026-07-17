package com.example.fastingtracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingDao {
    @Query("SELECT * FROM fasting_logs ORDER BY startTimeMillis DESC")
    fun getAllLogs(): Flow<List<FastingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FastingLog)

    @Delete
    suspend fun deleteLog(log: FastingLog)

    @Query("DELETE FROM fasting_logs")
    suspend fun deleteAllLogs()
}
