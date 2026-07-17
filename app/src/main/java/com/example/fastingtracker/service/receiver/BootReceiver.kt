package com.example.fastingtracker.service.receiver

import com.example.fastingtracker.service.tracker.FastingService

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE)
            val isFasting = prefs.getBoolean("is_fasting", false)
            if (isFasting) {
                val startTime = prefs.getLong("start_time_millis", 0L)
                val hasSelectedPlan = prefs.getBoolean("has_explicitly_selected_plan", false)
                val duration = if (hasSelectedPlan) prefs.getLong("target_duration_millis", 168 * 60 * 60 * 1000L) else 168 * 60 * 60 * 1000L
                val endTime = startTime + duration
                val currentTime = System.currentTimeMillis()

                if (endTime > currentTime) {
                    // Reschedule completion alarm
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, FastingAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, 0, alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                endTime,
                                pendingIntent
                            )
                        } else {
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                endTime,
                                pendingIntent
                            )
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            endTime,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            endTime,
                            pendingIntent
                        )
                    }

                    // Restart Foreground Service to restore persistent notification
                    val serviceIntent = Intent(context, FastingService::class.java).apply {
                        putExtra(FastingService.EXTRA_START_TIME, startTime)
                        putExtra(FastingService.EXTRA_TARGET_DURATION, duration)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    // Fast should have completed during device shutdown, trigger completion immediately
                    val alarmIntent = Intent(context, FastingAlarmReceiver::class.java)
                    context.sendBroadcast(alarmIntent)
                }
            }
        }
    }
}
