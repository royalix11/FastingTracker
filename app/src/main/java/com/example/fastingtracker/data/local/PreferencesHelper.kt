package com.example.fastingtracker.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "fasting_prefs"
        
        const val KEY_ACTIVE_TAB = "active_tab"
        const val KEY_IS_FASTING = "is_fasting"
        const val KEY_IS_FASTING_SCHEDULED = "is_fasting_scheduled"
        const val KEY_START_TIME_MILLIS = "start_time_millis"
        const val KEY_TARGET_DURATION_MILLIS = "target_duration_millis"
        const val KEY_HAS_EXPLICITLY_SELECTED_PLAN = "has_explicitly_selected_plan"
        const val KEY_IS_COUNTDOWN = "is_countdown"
        const val KEY_THEME_PREFERENCE = "theme_preference"
    }
    
    fun getSharedPreferences(): SharedPreferences = prefs

    var activeTab: String
        get() = prefs.getString(KEY_ACTIVE_TAB, "HOME") ?: "HOME"
        set(value) = prefs.edit().putString(KEY_ACTIVE_TAB, value).apply()

    var isFasting: Boolean
        get() = prefs.getBoolean(KEY_IS_FASTING, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_FASTING, value).apply()

    var isFastingScheduled: Boolean
        get() = prefs.getBoolean(KEY_IS_FASTING_SCHEDULED, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_FASTING_SCHEDULED, value).apply()

    var startTimeMillis: Long
        get() = prefs.getLong(KEY_START_TIME_MILLIS, 0L)
        set(value) = prefs.edit().putLong(KEY_START_TIME_MILLIS, value).apply()

    var hasExplicitlySelectedPlan: Boolean
        get() = prefs.getBoolean(KEY_HAS_EXPLICITLY_SELECTED_PLAN, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_EXPLICITLY_SELECTED_PLAN, value).apply()

    var targetDurationMillis: Long
        get() = if (hasExplicitlySelectedPlan) prefs.getLong(KEY_TARGET_DURATION_MILLIS, 168 * 60 * 60 * 1000L) else 168 * 60 * 60 * 1000L
        set(value) = prefs.edit().putLong(KEY_TARGET_DURATION_MILLIS, value).apply()

    var isCountdown: Boolean
        get() = prefs.getBoolean(KEY_IS_COUNTDOWN, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_COUNTDOWN, value).apply()

    var themePreference: String
        get() = prefs.getString(KEY_THEME_PREFERENCE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_PREFERENCE, value).apply()
}
