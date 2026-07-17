package com.example.fastingtracker.service.tracker

import com.example.fastingtracker.MainActivity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat

class FastingService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
            return START_NOT_STICKY
        }

        val startTime = intent?.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis()) ?: System.currentTimeMillis()
        val targetDuration = intent?.getLongExtra(EXTRA_TARGET_DURATION, 168 * 60 * 60 * 1000L) ?: (168 * 60 * 60 * 1000L)

        createNotificationChannel()
        val notification = buildNotification(startTime, targetDuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    private fun buildNotification(startTime: Long, targetDuration: Long): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val endTime = startTime + targetDuration
        
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fasting is Active")
            .setContentText("Your 16:8 fast is currently in progress")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        // Use Android's built-in chronometer countdown feature in notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setWhen(endTime)
            builder.setUsesChronometer(true)
            builder.setChronometerCountDown(true)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Active Fasting Tracker",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the active countdown timer of your fast"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "fasting_active_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val EXTRA_START_TIME = "EXTRA_START_TIME"
        const val EXTRA_TARGET_DURATION = "EXTRA_TARGET_DURATION"
    }
}
