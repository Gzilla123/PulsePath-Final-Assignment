package com.pulsepath.models

fun WorkoutType.usesSteps(): Boolean = when (this) {
    WorkoutType.RUNNING,
    WorkoutType.WALKING,
    WorkoutType.HIKING,
    WorkoutType.HIIT,
    WorkoutType.JUMP_ROPE -> true
    else -> false
}

fun WorkoutType.activeMetricLabel(): String = when (this) {
    WorkoutType.CYCLING  -> "CADENCE"
    WorkoutType.SWIMMING -> "LAPS"
    WorkoutType.ROWING   -> "STROKES"
    WorkoutType.YOGA     -> "MIN"
    WorkoutType.WEIGHTS  -> "SETS"
    else                 -> "STEPS"
}
