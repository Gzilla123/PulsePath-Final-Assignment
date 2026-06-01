package com.pulsepath.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.*
import com.pulsepath.models.WorkoutSession
import com.pulsepath.models.WorkoutType
import com.pulsepath.models.usesSteps
import com.pulsepath.repository.FitnessRepository
import com.pulsepath.utils.CalorieCalculator
import com.pulsepath.utils.FirebaseManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WorkoutViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FitnessRepository(app)
    private val uid  = FirebaseManager.userId

    val allWorkouts: LiveData<List<WorkoutSession>> = repo.getAllWorkouts(uid)

    private var timerJob: Job? = null
    private var startTimeMs = 0L
    private var pausedElapsedMs = 0L

    private val _isRunning      = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _isPaused       = MutableLiveData(false)
    val isPaused: LiveData<Boolean> = _isPaused

    private val _elapsedSeconds = MutableLiveData(0L)
    val elapsedSeconds: LiveData<Long> = _elapsedSeconds

    private val _liveSteps      = MutableLiveData(0)
    val liveSteps: LiveData<Int> = _liveSteps

    private val _liveCalories   = MutableLiveData(0)
    val liveCalories: LiveData<Int> = _liveCalories

    private val _selectedType   = MutableLiveData(WorkoutType.RUNNING)
    val selectedType: LiveData<WorkoutType> = _selectedType

    val currentTypeUsesSteps: LiveData<Boolean> = _selectedType.map { it.usesSteps() }

    private val _workoutSaved   = MutableLiveData(false)
    val workoutSaved: LiveData<Boolean> = _workoutSaved

    fun selectType(type: WorkoutType) {
        _selectedType.value = type
        _liveSteps.value    = 0
        _liveCalories.value = 0
    }

    fun startWorkout() {
        startTimeMs = SystemClock.elapsedRealtime()
        _isRunning.value      = true
        _isPaused.value       = false
        _liveSteps.value      = 0
        _elapsedSeconds.value = 0L
        _workoutSaved.value   = false
        startTimer()
    }

    fun pauseWorkout() {
        _isPaused.value = true
        pausedElapsedMs = SystemClock.elapsedRealtime() - startTimeMs
        timerJob?.cancel()
    }

    fun resumeWorkout() {
        _isPaused.value = false
        startTimeMs = SystemClock.elapsedRealtime() - pausedElapsedMs
        startTimer()
    }

    fun stopWorkout() {
        timerJob?.cancel()
        val durationSec = _elapsedSeconds.value ?: 0L
        val steps       = _liveSteps.value ?: 0
        val type        = _selectedType.value ?: WorkoutType.RUNNING
        val calories    = CalorieCalculator.calculate(type, durationSec)

        val session = WorkoutSession(
            userId          = uid,
            type            = type,
            durationSeconds = durationSec,
            steps           = if (type.usesSteps()) steps else 0,
            caloriesBurned  = calories,
            endTime         = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repo.saveWorkout(session)
            _workoutSaved.value = true
        }
        _isRunning.value = false
        _isPaused.value  = false
    }

    fun updateSteps(steps: Int) {
        if (_selectedType.value?.usesSteps() == true) {
            _liveSteps.value    = steps
            _liveCalories.value = CalorieCalculator.caloriesFromSteps(steps)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value = (SystemClock.elapsedRealtime() - startTimeMs) / 1000
                if (_selectedType.value?.usesSteps() == false) {
                    _liveCalories.value = CalorieCalculator.calculate(
                        _selectedType.value ?: WorkoutType.CUSTOM,
                        _elapsedSeconds.value ?: 0L
                    )
                }
            }
        }
    }
}
