package com.pulsepath.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val type: WorkoutType = WorkoutType.RUNNING,
    val durationSeconds: Long = 0L,
    val steps: Int = 0,
    val caloriesBurned: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = 0L,
    val syncedToCloud: Boolean = false
)

enum class WorkoutType(
    val label: String,
    val icon: String,
    val metValue: Double       // MET for calorie calculation
) {
    RUNNING(    "Running",      "Run",   9.8),
    WALKING(    "Walking",      "Walk",  3.5),
    CYCLING(    "Cycling",      "Cycle", 7.5),
    HIKING(     "Hiking",       "Hike",  6.0),
    SWIMMING(   "Swimming",     "Swim",  8.0),
    HIIT(       "HIIT",         "HIIT",  10.0),
    YOGA(       "Yoga",         "Yoga",  3.0),
    ROWING(     "Rowing",       "Row",   7.0),
    JUMP_ROPE(  "Jump Rope",    "Jump",  11.0),
    WEIGHTS(    "Weights",      "Lift",  5.0),
    CUSTOM(     "Custom",       "Other", 5.0)
}
