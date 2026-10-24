package com.dhyan.health.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.dhyan.health.data.model.DailyActivityData
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Manages integration with Android Health Connect SDK to read daily steps and active exercise minutes.
 */
class HealthConnectManager(private val context: Context) {

    private val healthConnectClient: HealthConnectClient? by lazy {
        if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        val granted = client.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    suspend fun fetchDailyActivity(): DailyActivityData {
        val client = healthConnectClient
            ?: return DailyActivityData(isHealthConnectAvailable = false)

        val hasPerms = hasAllPermissions()
        if (!hasPerms) {
            return DailyActivityData(
                isHealthConnectAvailable = true,
                hasPermissions = false
            )
        }

        val now = Instant.now()
        val startOfDay = now.atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return try {
            val stepsAggregate = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                )
            )
            val stepCount = stepsAggregate[StepsRecord.COUNT_TOTAL] ?: 0L

            val exerciseResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                )
            )

            val activeMinutes = exerciseResponse.records.sumOf { record ->
                ChronoUnit.MINUTES.between(record.startTime, record.endTime)
            }

            DailyActivityData(
                stepCount = stepCount,
                activeMinutes = activeMinutes,
                isHealthConnectAvailable = true,
                hasPermissions = true
            )
        } catch (e: Exception) {
            DailyActivityData(
                stepCount = 0,
                activeMinutes = 0,
                isHealthConnectAvailable = true,
                hasPermissions = true
            )
        }
    }
}
