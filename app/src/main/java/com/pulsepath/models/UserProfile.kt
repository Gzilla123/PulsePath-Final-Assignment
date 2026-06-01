package com.pulsepath.models


data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    val fitnessGoal: FitnessGoal = FitnessGoal.STAY_ACTIVE,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val onboardingComplete: Boolean = false
)

enum class FitnessGoal(val label: String, val description: String) {
    LOSE_WEIGHT("Lose Weight", "Burn calories and reduce body fat"),
    BUILD_MUSCLE("Build Muscle", "Increase strength and muscle mass"),
    STAY_ACTIVE("Stay Active", "Maintain a healthy, active lifestyle"),
    IMPROVE_ENDURANCE("Improve Endurance", "Build stamina and cardiovascular fitness")
}

enum class ActivityLevel(val label: String, val description: String) {
    SEDENTARY("Not Active", "Little to no exercise"),
    LIGHT("Lightly Active", "Exercise 1-2 days per week"),
    MODERATE("Moderately Active", "Exercise 3-4 days per week"),
    VERY_ACTIVE("Very Active", "Exercise 5+ days per week")
}
