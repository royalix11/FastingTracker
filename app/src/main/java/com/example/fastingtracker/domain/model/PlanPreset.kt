package com.example.fastingtracker.domain.model

data class PlanPreset(
    val hours: Int,
    val minutes: Int,
    val title: String,
    val description: String,
    val isPopular: Boolean = false
)
