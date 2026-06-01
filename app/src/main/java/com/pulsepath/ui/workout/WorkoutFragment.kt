package com.pulsepath.ui.workout

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pulsepath.databinding.FragmentWorkoutBinding
import com.pulsepath.models.ExerciseLibrary
import com.pulsepath.models.WorkoutType
import com.pulsepath.sensors.StepSensorManager
import com.pulsepath.viewmodel.WorkoutViewModel

class WorkoutFragment : Fragment() {

    private var _b: FragmentWorkoutBinding? = null
    private val b get() = _b!!
    private val vm: WorkoutViewModel by viewModels()
    private lateinit var stepSensor: StepSensorManager
    private var selectedType = WorkoutType.RUNNING

    private val LIME   = Color.parseColor("#C4FF00")
    private val PURPLE = Color.parseColor("#7B5CEA")
    private val CARD   = Color.parseColor("#1A1A1A")
    private val BLACK  = Color.parseColor("#0D0D0D")
    private val GRAY   = Color.parseColor("#888888")

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        stepSensor.start()
        if (!granted) Toast.makeText(requireContext(),
            "Step counting using accelerometer — grant Activity permission for higher accuracy",
            Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentWorkoutBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        stepSensor = StepSensorManager(requireContext())
        setupSensorCallbacks()
        setupActivityGrid()
        loadExercises(WorkoutType.RUNNING)
        observeViewModel()
    }

    private fun setupActivityGrid() {
        data class BtnType(val btn: LinearLayout, val type: WorkoutType)
        val items = listOf(
            BtnType(b.btnTypeRunning,  WorkoutType.RUNNING),
            BtnType(b.btnTypeWalking,  WorkoutType.WALKING),
            BtnType(b.btnTypeCycling,  WorkoutType.CYCLING),
            BtnType(b.btnTypeHiking,   WorkoutType.HIKING),
            BtnType(b.btnTypeSwimming, WorkoutType.SWIMMING),
            BtnType(b.btnTypeHiit,     WorkoutType.HIIT),
            BtnType(b.btnTypeYoga,     WorkoutType.YOGA),
            BtnType(b.btnTypeRowing,   WorkoutType.ROWING),
            BtnType(b.btnTypeJump,     WorkoutType.JUMP_ROPE),
            BtnType(b.btnTypeWeights,  WorkoutType.WEIGHTS)
        )
        items.forEach { (btn, type) ->
            btn.setOnClickListener {
                if (vm.isRunning.value == true) return@setOnClickListener
                selectedType = type
                vm.selectType(type)
                items.forEach { (b2, t2) ->
                    val selColor = if (t2.ordinal % 2 == 0) LIME else PURPLE
                    b2.setBackgroundColor(if (b2 == btn) selColor else CARD)
                    val textColor = if (b2 == btn) BLACK else GRAY
                    (b2.getChildAt(0) as? TextView)?.setTextColor(textColor)
                    (b2.getChildAt(1) as? TextView)?.setTextColor(textColor)
                }
                loadExercises(type)
            }
        }
        // default Running selected
        b.btnTypeRunning.setBackgroundColor(LIME)
        (b.btnTypeRunning.getChildAt(0) as? TextView)?.setTextColor(BLACK)
        (b.btnTypeRunning.getChildAt(1) as? TextView)?.setTextColor(BLACK)
    }

    private fun loadExercises(type: WorkoutType) {
        val exercises = ExerciseLibrary.getWarmupFor(type)
        b.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        b.rvExercises.adapter = ExerciseAdapter(exercises) {
            Toast.makeText(requireContext(), "Warmup complete! Ready to start.", Toast.LENGTH_SHORT).show()
        }
        b.tvExercisesLabel.text = "${type.label} Warmup"
    }

    private fun setupSensorCallbacks() {
        stepSensor.onStepsUpdated = { steps ->
            activity?.runOnUiThread {
                vm.updateSteps(steps)
                b.tvLiveSteps.text = steps.toString()
            }
        }
        stepSensor.onSensorStatus = { status ->
            activity?.runOnUiThread { b.tvSensorStatus.text = status }
        }
    }

    private fun observeViewModel() {
        vm.isRunning.observe(viewLifecycleOwner) { running ->
            b.btnStartStop.text = if (running) "End Session" else "Start Session"
            b.cardLiveStats.visibility = if (running) View.VISIBLE else View.GONE
            b.btnPauseResume.visibility = if (running) View.VISIBLE else View.GONE
            b.rvExercises.visibility = if (running) View.GONE else View.VISIBLE
            b.tvExercisesLabel.visibility = if (running) View.GONE else View.VISIBLE
        }
        vm.isPaused.observe(viewLifecycleOwner) { paused ->
            b.btnPauseResume.text = if (paused) "Resume" else "Pause"
        }
        vm.elapsedSeconds.observe(viewLifecycleOwner) { secs ->
            b.tvTimer.text = "%02d:%02d".format(secs / 60, secs % 60)
        }
        vm.liveCalories.observe(viewLifecycleOwner) { b.tvLiveCalories.text = it.toString() }
        vm.workoutSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "Session saved!", Toast.LENGTH_SHORT).show()
                b.cardLiveStats.visibility = View.GONE
                b.rvExercises.visibility = View.VISIBLE
                b.tvExercisesLabel.visibility = View.VISIBLE
                b.tvSensorStatus.text = "Step sensor: Waiting for movement"
                loadExercises(selectedType)
            }
        }
        b.btnStartStop.setOnClickListener {
            if (vm.isRunning.value == true) {
                stepSensor.stop(); vm.stopWorkout()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                } else {
                    stepSensor.start()
                }
                vm.startWorkout()
            }
        }
        b.btnPauseResume.setOnClickListener {
            if (vm.isPaused.value == true) vm.resumeWorkout() else vm.pauseWorkout()
        }
    }

    override fun onDestroyView() { stepSensor.stop(); super.onDestroyView(); _b = null }
}
