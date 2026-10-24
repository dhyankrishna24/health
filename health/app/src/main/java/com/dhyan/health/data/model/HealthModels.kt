package com.dhyan.health.data.model

/**
 * Data class holding Health Connect daily activity metrics (Steps & Active Minutes).
 */
data class DailyActivityData(
    val stepCount: Long = 0,
    val activeMinutes: Long = 0,
    val isHealthConnectAvailable: Boolean = true,
    val hasPermissions: Boolean = false
)
