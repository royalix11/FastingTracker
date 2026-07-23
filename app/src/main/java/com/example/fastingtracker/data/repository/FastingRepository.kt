package com.example.fastingtracker.data.repository

import com.example.fastingtracker.data.local.FastingDao
import com.example.fastingtracker.data.local.FastingLog
import com.example.fastingtracker.data.local.QualitativeLogDao
import com.example.fastingtracker.data.local.QualitativeLogEntity
import kotlinx.coroutines.flow.Flow

class FastingRepository(
    private val fastingDao: FastingDao,
    val qualitativeLogDao: QualitativeLogDao
) {
    val allLogs: Flow<List<FastingLog>> = fastingDao.getAllLogs()
    val allQualitativeLogs: Flow<List<QualitativeLogEntity>> = qualitativeLogDao.getAllLogs()

    suspend fun insertLog(log: FastingLog) {
        fastingDao.insertLog(log)
    }

    suspend fun insertQualitativeLog(log: QualitativeLogEntity) {
        qualitativeLogDao.insertLog(log)
    }

    suspend fun deleteLog(log: FastingLog) {
        fastingDao.deleteLog(log)
    }
}
