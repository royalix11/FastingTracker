package com.example.fastingtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FastingLog::class], version = 1, exportSchema = false)
abstract class FastingDatabase : RoomDatabase() {
    abstract fun fastingDao(): FastingDao

    companion object {
        @Volatile
        private var INSTANCE: FastingDatabase? = null

        fun getDatabase(context: Context): FastingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FastingDatabase::class.java,
                    "fasting_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
