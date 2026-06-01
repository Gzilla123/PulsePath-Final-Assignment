package com.pulsepath.utils

import com.pulsepath.models.WorkoutType


object CalorieCalculator {

    private const val AVG_WEIGHT_KG = 70.0

    fun calculate(type: WorkoutType, durationSeconds: Long): Int {
        val hours = durationSeconds / 3600.0
        val met = when (type) {
            WorkoutType.RUNNING   -> 9.8
            WorkoutType.WALKING   -> 3.5
            WorkoutType.CYCLING   -> 7.5
            WorkoutType.HIKING    -> 6.0
            WorkoutType.SWIMMING  -> 8.0
            WorkoutType.HIIT      -> 10.0
            WorkoutType.YOGA      -> 3.0
            WorkoutType.ROWING    -> 7.0
            WorkoutType.JUMP_ROPE -> 11.0
            WorkoutType.WEIGHTS   -> 5.0
            WorkoutType.CUSTOM    -> 5.0
        }
        return (met * AVG_WEIGHT_KG * hours).toInt()
    }

    fun caloriesFromSteps(steps: Int): Int = (steps * 0.04).toInt()

    fun stepsToKm(steps: Int): Double = steps * 0.00078
}
