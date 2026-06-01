package com.pulsepath.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val dateKey: String = "",   // format: "2026-05-14"
    val userId: String = "",
    val steps: Int = 0,
    val caloriesBurned: Int = 0,
    val activeMinutes: Int = 0,
    val workoutCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
