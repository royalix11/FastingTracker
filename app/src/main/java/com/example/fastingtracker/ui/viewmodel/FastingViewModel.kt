package com.example.fastingtracker.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fastingtracker.data.local.FastingDatabase
import com.example.fastingtracker.data.local.FastingLog
import com.example.fastingtracker.data.repository.FastingRepository
import com.example.fastingtracker.service.receiver.FastingAlarmReceiver
import com.example.fastingtracker.service.tracker.FastingService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.fastingtracker.domain.model.FastingStage

class FastingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FastingRepository
    private val prefs = application.getSharedPreferences("fasting_prefs", Context.MODE_PRIVATE)

    val historyLogs: StateFlow<List<FastingLog>>

    private val _isFasting = MutableStateFlow(false)
    val isFasting: StateFlow<Boolean> = _isFasting.asStateFlow()

    private val _isFastingScheduled = MutableStateFlow(false)
    val isFastingScheduled: StateFlow<Boolean> = _isFastingScheduled.asStateFlow()

    private val _startTimeMillis = MutableStateFlow(0L)
    val startTimeMillis: StateFlow<Long> = _startTimeMillis.asStateFlow()

    private val _elapsedTimeMillis = MutableStateFlow(0L)
    val elapsedTimeMillis: StateFlow<Long> = _elapsedTimeMillis.asStateFlow()

    private val initialTargetDuration = if (prefs.getBoolean("has_explicitly_selected_plan", false)) {
        prefs.getLong("target_duration_millis", 168 * 60 * 60 * 1000L)
    } else {
        168 * 60 * 60 * 1000L
    }
    private val _targetDurationMillis = MutableStateFlow(initialTargetDuration)
    val targetDurationMillis: StateFlow<Long> = _targetDurationMillis.asStateFlow()

    val currentFastingStage: StateFlow<FastingStage> = _elapsedTimeMillis
        .map { FastingStage.getStageForElapsed(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FastingStage.BloodSugarIncreases
        )

    private var timerJob: Job? = null

    init {
        val database = FastingDatabase.getDatabase(application)
        repository = FastingRepository(
            fastingDao = database.fastingDao(),
            qualitativeLogDao = database.qualitativeLogDao()
        )
        historyLogs = repository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Load active fasting state
        val savedIsFasting = prefs.getBoolean("is_fasting", false)
        val savedIsScheduled = prefs.getBoolean("is_fasting_scheduled", false)
        val savedStartTime = prefs.getLong("start_time_millis", 0L)
        
        _isFasting.value = savedIsFasting
        _isFastingScheduled.value = savedIsScheduled
        _startTimeMillis.value = savedStartTime

        if (savedIsFasting || savedIsScheduled) {
            startTimerUpdates()
        }
    }

    fun startFast() {
        val now = System.currentTimeMillis()
        _isFasting.value = true
        _isFastingScheduled.value = false
        _startTimeMillis.value = now

        prefs.edit().apply {
            putBoolean("is_fasting", true)
            putBoolean("is_fasting_scheduled", false)
            putLong("start_time_millis", now)
            putLong("target_duration_millis", targetDurationMillis.value)
            apply()
        }

        // 1. Start foreground service
        startFastingService(now)

        // 2. Schedule completion alarm
        scheduleCompletionAlarm(now + targetDurationMillis.value)

        // 3. Start updates in app UI
        startTimerUpdates()
    }

    fun startCustomFast(customStartTimeMillis: Long, customDurationMillis: Long) {
        _isFasting.value = true
        _isFastingScheduled.value = false
        _startTimeMillis.value = customStartTimeMillis
        _targetDurationMillis.value = customDurationMillis

        prefs.edit().apply {
            putBoolean("is_fasting", true)
            putBoolean("is_fasting_scheduled", false)
            putBoolean("has_explicitly_selected_plan", true)
            putLong("start_time_millis", customStartTimeMillis)
            putLong("target_duration_millis", customDurationMillis)
            apply()
        }

        startFastingService(customStartTimeMillis)
        scheduleCompletionAlarm(customStartTimeMillis + customDurationMillis)
        startTimerUpdates()
    }

    fun scheduleCustomFast(customStartTimeMillis: Long, customDurationMillis: Long) {
        _isFasting.value = false
        _isFastingScheduled.value = true
        _startTimeMillis.value = customStartTimeMillis
        _targetDurationMillis.value = customDurationMillis

        prefs.edit().apply {
            putBoolean("is_fasting", false)
            putBoolean("is_fasting_scheduled", true)
            putBoolean("has_explicitly_selected_plan", true)
            putLong("start_time_millis", customStartTimeMillis)
            putLong("target_duration_millis", customDurationMillis)
            apply()
        }
        
        // Stop active timers and alarms if any were running
        stopFastingService()
        cancelCompletionAlarm()
        startTimerUpdates()
    }

    fun endFast() {
        val now = System.currentTimeMillis()
        val start = _startTimeMillis.value
        val duration = now - start

        // Save log to DB
        viewModelScope.launch {
            repository.insertLog(
                FastingLog(
                    startTimeMillis = start,
                    endTimeMillis = now,
                    durationMillis = duration,
                    targetDurationMillis = targetDurationMillis.value,
                    isCompleted = duration >= targetDurationMillis.value
                )
            )
        }

        // Reset state
        _isFasting.value = false
        _isFastingScheduled.value = false
        _startTimeMillis.value = 0L
        _elapsedTimeMillis.value = 0L
        timerJob?.cancel()

        prefs.edit().apply {
            putBoolean("is_fasting", false)
            putBoolean("is_fasting_scheduled", false)
            putLong("start_time_millis", 0L)
            apply()
        }

        // 1. Stop service
        stopFastingService()

        // 2. Cancel alarm
        cancelCompletionAlarm()
    }

    private fun startTimerUpdates() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isFasting.value || _isFastingScheduled.value) {
                if (_isFasting.value) {
                    val elapsed = System.currentTimeMillis() - _startTimeMillis.value
                    _elapsedTimeMillis.value = if (elapsed > 0) elapsed else 0L
                } else if (_isFastingScheduled.value) {
                    val now = System.currentTimeMillis()
                    if (_startTimeMillis.value <= now) {
                        _isFasting.value = true
                        _isFastingScheduled.value = false
                        prefs.edit().apply {
                            putBoolean("is_fasting", true)
                            putBoolean("is_fasting_scheduled", false)
                            apply()
                        }
                        startFastingService(_startTimeMillis.value)
                        scheduleCompletionAlarm(_startTimeMillis.value + _targetDurationMillis.value)
                    }
                }
                delay(1000)
            }
        }
    }

    private fun startFastingService(startTime: Long) {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, FastingService::class.java).apply {
            putExtra(FastingService.EXTRA_START_TIME, startTime)
            putExtra(FastingService.EXTRA_TARGET_DURATION, targetDurationMillis.value)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun updateTargetDuration(durationMillis: Long) {
        _targetDurationMillis.value = durationMillis
        prefs.edit()
            .putLong("target_duration_millis", durationMillis)
            .putBoolean("has_explicitly_selected_plan", true)
            .apply()
        if (_isFasting.value) {
            startFastingService(_startTimeMillis.value)
            scheduleCompletionAlarm(_startTimeMillis.value + durationMillis)
        }
    }

    fun editActiveStartTime(newStartTimeMillis: Long) {
        val now = System.currentTimeMillis()
        if (newStartTimeMillis > now) {
            scheduleCustomFast(newStartTimeMillis, _targetDurationMillis.value)
        } else {
            _startTimeMillis.value = newStartTimeMillis
            prefs.edit().putLong("start_time_millis", newStartTimeMillis).apply()

            val elapsed = now - newStartTimeMillis
            _elapsedTimeMillis.value = if (elapsed > 0) elapsed else 0L

            if (_isFastingScheduled.value) {
                _isFastingScheduled.value = false
                _isFasting.value = true
                prefs.edit().apply {
                    putBoolean("is_fasting", true)
                    putBoolean("is_fasting_scheduled", false)
                    apply()
                }
            }

            if (_isFasting.value) {
                startFastingService(newStartTimeMillis)
                scheduleCompletionAlarm(newStartTimeMillis + _targetDurationMillis.value)
            }
            startTimerUpdates()
        }
    }

    private fun stopFastingService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, FastingService::class.java).apply {
            action = FastingService.ACTION_STOP_SERVICE
        }
        context.startService(intent)
    }

    private fun scheduleCompletionAlarm(triggerTime: Long) {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, FastingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun cancelCompletionAlarm() {
        val context = getApplication<Application>().applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, FastingAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
