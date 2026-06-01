package com.pulsepath.models

data class Exercise(
    val name: String,
    val muscles: String,          // Muscles worked
    val sets: String,             // e.g. "3 x 15 reps" or "30 sec"
    val bodyRegion: BodyRegion,   // For highlighting body diagram
    val description: String       // Short why-it-helps text
)

enum class BodyRegion {
    FULL_BODY,
    UPPER_BODY,
    CORE,
    LOWER_BODY,
    BACK,
    SHOULDERS,
    CARDIO
}

object ExerciseLibrary {

    fun getWarmupFor(type: WorkoutType): List<Exercise> = when (type) {

        WorkoutType.RUNNING -> listOf(
            Exercise("Leg Swings",       "Hip Flexors, Hamstrings", "20 swings each leg", BodyRegion.LOWER_BODY,  "Loosens hip joints for your stride"),
            Exercise("High Knees",       "Quads, Core, Cardio",     "30 seconds",          BodyRegion.CARDIO,     "Warms up legs and raises heart rate"),
            Exercise("Dynamic Lunges",   "Quads, Glutes, Calves",   "10 each leg",         BodyRegion.LOWER_BODY, "Activates all major running muscles"),
            Exercise("Ankle Rolls",      "Ankles, Calves",          "10 each direction",   BodyRegion.LOWER_BODY, "Prevents ankle injuries on uneven ground"),
            Exercise("Calf Raises",      "Calves, Achilles Tendon", "20 reps",             BodyRegion.LOWER_BODY, "Protects your Achilles during long runs")
        )

        WorkoutType.CYCLING -> listOf(
            Exercise("Hip Flexor Stretch", "Hip Flexors, Psoas",    "30 sec each side",    BodyRegion.LOWER_BODY,  "Critical for cyclists — prevents lower back pain"),
            Exercise("Quad Stretch",       "Quadriceps",            "30 sec each side",    BodyRegion.LOWER_BODY,  "Loosens the main pedalling muscle"),
            Exercise("Shoulder Rolls",     "Shoulders, Upper Back", "20 reps",             BodyRegion.SHOULDERS,   "Reduces neck tension from riding position"),
            Exercise("Calf Raises",        "Calves, Ankles",        "20 reps",             BodyRegion.LOWER_BODY,  "Activates the pedal stroke push muscle"),
            Exercise("Torso Rotation",     "Core, Lower Back",      "15 each side",        BodyRegion.CORE,        "Stabilises your body on the bike")
        )

        WorkoutType.WALKING -> listOf(
            Exercise("Neck Rolls",         "Neck, Upper Shoulders", "10 each direction",   BodyRegion.SHOULDERS,  "Releases tension from posture"),
            Exercise("Arm Swings",         "Shoulders, Chest",      "20 reps",             BodyRegion.UPPER_BODY, "Prepares upper body for walking rhythm"),
            Exercise("Hip Circles",        "Hip Flexors, Lower Back","10 each direction",  BodyRegion.LOWER_BODY, "Improves hip mobility for longer walks"),
            Exercise("Calf Stretch",       "Calves, Achilles",      "30 sec each side",    BodyRegion.LOWER_BODY, "Prevents shin splints and calf tightness"),
            Exercise("Standing Knee Raise","Hip Flexors, Core",     "15 each side",        BodyRegion.CORE,       "Warms up the walking motion pattern")
        )

        WorkoutType.HIIT -> listOf(
            Exercise("Jumping Jacks",      "Full Body, Cardio",     "30 reps",             BodyRegion.FULL_BODY,  "Fast full-body warm-up to raise heart rate"),
            Exercise("Arm Circles",        "Shoulders, Arms",       "20 each direction",   BodyRegion.SHOULDERS,  "Lubricates shoulder joint for arm-heavy moves"),
            Exercise("Hip Rotations",      "Hips, Lower Back",      "10 each direction",   BodyRegion.LOWER_BODY, "Prepares hips for explosive movements"),
            Exercise("Leg Swings",         "Hamstrings, Hip Flexors","15 each leg",        BodyRegion.LOWER_BODY, "Dynamic stretch for burpees and jumps"),
            Exercise("Torso Twists",       "Core, Obliques, Spine", "20 reps",             BodyRegion.CORE,       "Activates rotational core stability")
        )

        WorkoutType.YOGA -> listOf(
            Exercise("Cat-Cow",            "Spine, Core",           "10 slow reps",        BodyRegion.BACK,       "Warms up the entire spine gently"),
            Exercise("Child's Pose",       "Back, Hips, Shoulders", "30 seconds",          BodyRegion.FULL_BODY,  "Releases tension before deeper poses"),
            Exercise("Downward Dog",       "Hamstrings, Calves, Shoulders","30 seconds",   BodyRegion.FULL_BODY,  "Full-body stretch to open the body"),
            Exercise("Low Lunge",          "Hip Flexors, Quads",    "30 sec each side",    BodyRegion.LOWER_BODY, "Opens hips for standing and balance poses"),
            Exercise("Standing Side Bend", "Obliques, Intercostals","10 each side",        BodyRegion.CORE,       "Lengthens the side body before twists")
        )

        WorkoutType.HIKING -> listOf(
            Exercise("Ankle Circles",      "Ankles, Lower Leg",     "10 each direction",   BodyRegion.LOWER_BODY, "Essential for uneven terrain stability"),
            Exercise("Quad Stretch",       "Quadriceps",            "30 sec each side",    BodyRegion.LOWER_BODY, "Prepares the main uphill climbing muscle"),
            Exercise("Glute Bridges",      "Glutes, Hamstrings",    "15 reps",             BodyRegion.LOWER_BODY, "Activates glutes to protect your knees on hills"),
            Exercise("Calf Stretch",       "Calves, Achilles",      "30 sec each side",    BodyRegion.LOWER_BODY, "Prevents calf cramps on long ascents"),
            Exercise("Hip Flexor Stretch", "Hip Flexors, Psoas",    "30 sec each side",    BodyRegion.LOWER_BODY, "Prevents lower back pain when climbing")
        )

        WorkoutType.SWIMMING -> listOf(
            Exercise("Shoulder Rotations", "Shoulders, Rotator Cuff","20 each direction",  BodyRegion.SHOULDERS,  "Critical — prevents shoulder injury in water"),
            Exercise("Arm Circles",        "Shoulders, Biceps, Triceps","20 each direction",BodyRegion.UPPER_BODY,"Mimics the swim stroke motion"),
            Exercise("Hip Stretch",        "Hip Flexors, Glutes",   "30 sec each side",    BodyRegion.LOWER_BODY, "Improves kick range of motion"),
            Exercise("Torso Twists",       "Core, Obliques",        "20 reps",             BodyRegion.CORE,       "Activates rotational power for freestyle"),
            Exercise("Neck Side Stretch",  "Neck, Traps",           "30 sec each side",    BodyRegion.SHOULDERS,  "Prevents neck strain from breathing technique")
        )

        WorkoutType.ROWING -> listOf(
            Exercise("Hip Hinge",          "Hamstrings, Glutes, Lower Back","15 reps",     BodyRegion.BACK,       "Mimics the rowing drive movement"),
            Exercise("Shoulder Rolls",     "Shoulders, Upper Back", "20 reps",             BodyRegion.SHOULDERS,  "Prepares shoulders for the catch and drive"),
            Exercise("Seated Torso Twist", "Core, Obliques",        "15 each side",        BodyRegion.CORE,       "Activates rotational power for rowing"),
            Exercise("Quad Stretch",       "Quadriceps",            "30 sec each side",    BodyRegion.LOWER_BODY, "Loosens legs for the leg drive phase"),
            Exercise("Wrist Circles",      "Wrists, Forearms",      "10 each direction",   BodyRegion.UPPER_BODY, "Protects wrists gripping the handle")
        )

        WorkoutType.JUMP_ROPE -> listOf(
            Exercise("Ankle Jumps",        "Calves, Ankles",        "20 reps",             BodyRegion.LOWER_BODY, "Conditions landing joints for rope impact"),
            Exercise("Wrist Circles",      "Wrists, Forearms",      "10 each direction",   BodyRegion.UPPER_BODY, "Loosens rope rotation joints"),
            Exercise("Calf Raises",        "Calves, Achilles",      "20 reps",             BodyRegion.LOWER_BODY, "Warms up the main landing muscle"),
            Exercise("Shoulder Rolls",     "Shoulders, Arms",       "20 reps",             BodyRegion.SHOULDERS,  "Prepares the rope swing motion"),
            Exercise("Light Jogging",      "Full Body, Cardio",     "30 seconds",          BodyRegion.CARDIO,     "Raises heart rate before intense skipping")
        )

        WorkoutType.WEIGHTS -> listOf(
            Exercise("Shoulder Rolls",     "Shoulders, Traps",      "20 reps",             BodyRegion.SHOULDERS,  "Critical before any pressing movements"),
            Exercise("Hip Circles",        "Hips, Lower Back",      "10 each direction",   BodyRegion.LOWER_BODY, "Protects lower back during squats/deadlifts"),
            Exercise("Wrist Rotations",    "Wrists, Forearms",      "10 each direction",   BodyRegion.UPPER_BODY, "Prepares wrists for gripping weight"),
            Exercise("Leg Swings",         "Hamstrings, Hip Flexors","15 each leg",        BodyRegion.LOWER_BODY, "Dynamic warm-up for leg day movements"),
            Exercise("Band Pull-Aparts",   "Upper Back, Rear Delts","20 reps",             BodyRegion.BACK,       "Activates stabiliser muscles before lifting")
        )

        else -> listOf(
            Exercise("Jumping Jacks",   "Full Body",  "30 reps",   BodyRegion.FULL_BODY, "General full-body warm-up"),
            Exercise("Arm Circles",     "Shoulders",  "20 reps",   BodyRegion.SHOULDERS, "Upper body activation"),
            Exercise("Hip Circles",     "Hips",       "10 reps",   BodyRegion.LOWER_BODY,"Hip joint mobility"),
            Exercise("High Knees",      "Legs, Core", "30 seconds",BodyRegion.CARDIO,    "Raise heart rate"),
            Exercise("Torso Twists",    "Core",       "20 reps",   BodyRegion.CORE,      "Core activation")
        )
    }
}
