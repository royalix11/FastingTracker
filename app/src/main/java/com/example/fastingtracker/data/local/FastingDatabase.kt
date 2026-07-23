package com.example.fastingtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FastingLog::class, QualitativeLogEntity::class], version = 2, exportSchema = false)
abstract class FastingDatabase : RoomDatabase() {
    abstract fun fastingDao(): FastingDao
    abstract fun qualitativeLogDao(): QualitativeLogDao

    companion object {
        @Volatile
        private var INSTANCE: FastingDatabase? = null

        fun getDatabase(context: Context): FastingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FastingDatabase::class.java,
                    "fasting_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
