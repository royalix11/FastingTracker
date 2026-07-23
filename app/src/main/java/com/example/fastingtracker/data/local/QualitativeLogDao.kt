package com.example.fastingtracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QualitativeLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: QualitativeLogEntity): Long

    @Query("SELECT * FROM qualitative_logs ORDER BY timestampEpochMs DESC")
    fun getAllLogs(): Flow<List<QualitativeLogEntity>>

    @Query("SELECT * FROM qualitative_logs WHERE sessionId = :sessionId ORDER BY timestampEpochMs ASC")
    fun observeLogsForSession(sessionId: Long): Flow<List<QualitativeLogEntity>>

    @Query("DELETE FROM qualitative_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)
}
