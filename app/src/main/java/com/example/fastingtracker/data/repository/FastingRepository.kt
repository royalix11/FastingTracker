package com.example.fastingtracker.data.repository

import com.example.fastingtracker.data.local.FastingDao

import com.example.fastingtracker.data.local.FastingLog

import kotlinx.coroutines.flow.Flow

class FastingRepository(private val fastingDao: FastingDao) {
    val allLogs: Flow<List<FastingLog>> = fastingDao.getAllLogs()

    suspend fun insertLog(log: FastingLog) {
        fastingDao.insertLog(log)
    }

    suspend fun deleteLog(log: FastingLog) {
        fastingDao.deleteLog(log)
    }
}
