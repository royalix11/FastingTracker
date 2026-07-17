package com.example.fastingtracker.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey
@Serializable data object ThemeSettings : NavKey
@Serializable data class FastingStagesTimeline(val initialStageIndex: Int? = null) : NavKey

@Serializable data class CustomFastingPlan(val editStartTimeMillis: Long? = null, val lockedDurationMillis: Long? = null) : NavKey
