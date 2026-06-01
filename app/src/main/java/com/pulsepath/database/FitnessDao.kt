package com.pulsepath.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pulsepath.models.DailyStats
import com.pulsepath.models.WorkoutSession

@Dao
interface FitnessDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(session: WorkoutSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(sessions: List<WorkoutSession>)

    @Delete
    suspend fun deleteWorkout(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE userId = :uid ORDER BY startTime DESC")
    fun getAllWorkouts(uid: String): LiveData<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :uid ORDER BY startTime DESC LIMIT 5")
    fun getRecentWorkouts(uid: String): LiveData<List<WorkoutSession>>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :uid AND startTime >= :weekStart")
    fun getWorkoutsThisWeek(uid: String, weekStart: Long): LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions WHERE userId = :uid AND startTime >= :weekStart")
    fun getCaloriesThisWeek(uid: String, weekStart: Long): LiveData<Int?>

    @Query("SELECT * FROM workout_sessions WHERE userId = :uid AND syncedToCloud = 0")
    suspend fun getUnsyncedWorkouts(uid: String): List<WorkoutSession>

    @Query("SELECT SUM(steps) FROM workout_sessions WHERE userId = :uid")
    fun getTotalSteps(uid: String): LiveData<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions WHERE userId = :uid")
    fun getTotalCalories(uid: String): LiveData<Int?>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :uid")
    fun getTotalWorkoutCount(uid: String): LiveData<Int>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: DailyStats)

    @Query("SELECT * FROM daily_stats WHERE userId = :uid AND dateKey = :dateKey LIMIT 1")
    suspend fun getStatsForDate(uid: String, dateKey: String): DailyStats?

    @Query("SELECT * FROM daily_stats WHERE userId = :uid ORDER BY dateKey DESC LIMIT 7")
    fun getLast7DaysStats(uid: String): LiveData<List<DailyStats>>
}
