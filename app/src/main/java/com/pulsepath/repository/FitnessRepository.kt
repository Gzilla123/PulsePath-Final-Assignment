package com.pulsepath.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.pulsepath.database.FitnessDatabase
import com.pulsepath.models.DailyStats
import com.pulsepath.models.WorkoutSession
import com.pulsepath.utils.FirebaseManager
import java.text.SimpleDateFormat
import java.util.*

class FitnessRepository(context: Context) {

    private val dao = FitnessDatabase.getDatabase(context).fitnessDao()

    fun getAllWorkouts(uid: String): LiveData<List<WorkoutSession>>   = dao.getAllWorkouts(uid)
    fun getRecentWorkouts(uid: String): LiveData<List<WorkoutSession>> = dao.getRecentWorkouts(uid)
    fun getWorkoutsThisWeek(uid: String): LiveData<Int>               = dao.getWorkoutsThisWeek(uid, weekStartMillis())
    fun getCaloriesThisWeek(uid: String): LiveData<Int?>              = dao.getCaloriesThisWeek(uid, weekStartMillis())
    fun getLast7DaysStats(uid: String): LiveData<List<DailyStats>>    = dao.getLast7DaysStats(uid)
    fun getTotalSteps(uid: String): LiveData<Int?>                    = dao.getTotalSteps(uid)
    fun getTotalCalories(uid: String): LiveData<Int?>                 = dao.getTotalCalories(uid)
    fun getTotalWorkoutCount(uid: String): LiveData<Int>              = dao.getTotalWorkoutCount(uid)


    suspend fun getStatsForDate(uid: String, dateKey: String): DailyStats? =
        dao.getStatsForDate(uid, dateKey)

    suspend fun saveWorkout(session: WorkoutSession) {
        dao.insertWorkout(session)

        // Update today's DailyStats in Room
        val today    = todayKey()
        val existing = dao.getStatsForDate(session.userId, today)
            ?: DailyStats(today, session.userId)

        dao.insertOrUpdateStats(existing.copy(
            steps          = existing.steps + session.steps,
            caloriesBurned = existing.caloriesBurned + session.caloriesBurned,
            activeMinutes  = existing.activeMinutes + (session.durationSeconds / 60).toInt(),
            workoutCount   = existing.workoutCount + 1
        ))

        // Sync to Firebase
        val result = FirebaseManager.saveWorkout(session)
        if (result.isSuccess) dao.insertWorkout(session.copy(syncedToCloud = true))
    }

    suspend fun syncFromCloud(userId: String) {
        val result = FirebaseManager.fetchWorkoutsFromCloud(userId)
        if (result.isSuccess) dao.insertAllWorkouts(result.getOrNull() ?: return)
    }

    suspend fun syncPending(userId: String) {
        dao.getUnsyncedWorkouts(userId).forEach { session ->
            if (FirebaseManager.saveWorkout(session).isSuccess)
                dao.insertWorkout(session.copy(syncedToCloud = true))
        }
    }

    private fun weekStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        return cal.timeInMillis
    }

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
