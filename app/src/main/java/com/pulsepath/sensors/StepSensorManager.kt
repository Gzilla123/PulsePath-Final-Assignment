package com.pulsepath.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat


class StepSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val accelerometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var baselineSteps = -1
    private var sessionSteps = 0
    private var lastAccelTime = 0L
    private var lastMagnitude = 0.0
    private var stepThreshold = 12.5   // acceleration threshold for step detection

    var onStepsUpdated: ((steps: Int) -> Unit)? = null
    var onSensorStatus: ((status: String) -> Unit)? = null

    fun hasActivityPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required below Android 10
        }
    }

    fun start() {
        baselineSteps = -1
        sessionSteps = 0

        when {
            !hasActivityPermission() -> {
                // Still try accelerometer as fallback and tell user they need permission
                registerAccelerometer()
                onSensorStatus?.invoke("Step sensor: Grant Activity permission in Settings for accurate counts")
            }

            stepCounterSensor != null -> {
                sensorManager.registerListener(
                    this,
                    stepCounterSensor,
                    SensorManager.SENSOR_DELAY_FASTEST   // Fastest = most responsive
                )
                onSensorStatus?.invoke("Step sensor: Active — counting your steps")
            }

            accelerometerSensor != null -> {
                registerAccelerometer()
                onSensorStatus?.invoke("Step sensor: Using accelerometer (estimated)")
            }

            else -> {
                onSensorStatus?.invoke("Step sensor: Not available on this device")
            }
        }
    }

    private fun registerAccelerometer() {
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        onSensorStatus?.invoke("Step sensor: Stopped")
    }

    fun getSessionSteps() = sessionSteps

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {

            Sensor.TYPE_STEP_COUNTER -> {
                // Hardware step counter — most accurate
                val totalSteps = event.values[0].toInt()
                if (baselineSteps == -1) {
                    baselineSteps = totalSteps   // Record start baseline
                }
                sessionSteps = totalSteps - baselineSteps
                onStepsUpdated?.invoke(sessionSteps)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                // Software step estimation from accelerometer
                val x = event.values[0].toDouble()
                val y = event.values[1].toDouble()
                val z = event.values[2].toDouble()
                val magnitude = Math.sqrt(x * x + y * y + z * z)
                val now = System.currentTimeMillis()

                // Detect a step: magnitude crosses threshold, with minimum 300ms between steps
                if (magnitude > stepThreshold &&
                    lastMagnitude <= stepThreshold &&
                    now - lastStepTime > 300
                ) {
                    sessionSteps++
                    lastStepTime = now
                    onStepsUpdated?.invoke(sessionSteps)
                }
                lastMagnitude = magnitude
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        // Permission constant for use in Activity/Fragment
        const val ACTIVITY_RECOGNITION_PERMISSION = Manifest.permission.ACTIVITY_RECOGNITION
        const val PERMISSION_REQUEST_CODE = 1001

        private var lastStepTime = 0L
    }
}
