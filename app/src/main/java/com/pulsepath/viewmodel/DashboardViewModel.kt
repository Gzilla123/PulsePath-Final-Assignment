package com.pulsepath.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsepath.models.DailyStats
import com.pulsepath.repository.FitnessRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FitnessRepository(application)
    private val uid: String get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName


    private val _todaySteps    = MutableLiveData(0)
    val todaySteps: LiveData<Int> = _todaySteps

    private val _activeMinutes = MutableLiveData(0)
    val activeMinutes: LiveData<Int> = _activeMinutes


    val workoutsThisWeek: LiveData<Int>  = repo.getWorkoutsThisWeek(uid)
    val caloriesThisWeek: LiveData<Int?> = repo.getCaloriesThisWeek(uid)

    private val _workoutsThisMonth   = MutableLiveData(0)
    val workoutsThisMonth: LiveData<Int> = _workoutsThisMonth

    private val _daysSinceLastWorkout = MutableLiveData(0)
    val daysSinceLastWorkout: LiveData<Int> = _daysSinceLastWorkout


    val totalWorkouts: LiveData<Int>  = repo.getTotalWorkoutCount(uid)
    val totalCalories: LiveData<Int?> = repo.getTotalCalories(uid)
    val totalSteps:    LiveData<Int?> = repo.getTotalSteps(uid)


    val last7DaysStats: LiveData<List<DailyStats>> = repo.getLast7DaysStats(uid)


    val allWorkouts: LiveData<List<com.pulsepath.models.WorkoutSession>> = repo.getAllWorkouts(uid)

    init {
        loadUserName()
        loadTodayStats()
    }

    fun loadTodayStats() {
        viewModelScope.launch {
            try {
                val todayKey = todayKey()

                // Today's steps from DailyStats table
                val todayStats = repo.getStatsForDate(uid, todayKey)
                _todaySteps.postValue(todayStats?.steps ?: 0)
                _activeMinutes.postValue(todayStats?.activeMinutes ?: 0)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _userName.postValue("Athlete")
            return
        }
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                _userName.postValue(
                    doc.getString("name")
                        ?: currentUser.displayName
                        ?: "Athlete"
                )
            }
            .addOnFailureListener {
                _userName.postValue(currentUser.displayName ?: "Athlete")
            }
    }

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
