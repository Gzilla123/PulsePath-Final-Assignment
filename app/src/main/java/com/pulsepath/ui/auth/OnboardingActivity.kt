package com.pulsepath.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pulsepath.databinding.ActivityOnboardingBinding
import com.pulsepath.ui.main.MainActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var b: ActivityOnboardingBinding
    private var step = 0
    private val total = 4

    private var goal     = "STAY_ACTIVE"
    private var activity = "MODERATE"
    private var age      = 22
    private var weight   = 70f
    private var height   = 170f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(b.root)
        showStep(0)

        b.btnNext.setOnClickListener {
            if (step < total - 1) {
                step++
                showStep(step)
            } else {
                saveAndContinue()
            }
        }

        b.btnBack.setOnClickListener {
            if (step > 0) {
                step--
                showStep(step)
            }
        }

        setupGoals()
        setupActivity()
        setupSliders()
    }

    private fun showStep(s: Int) {
        b.stepGoal.visibility      = if (s == 0) View.VISIBLE else View.GONE
        b.stepActivity.visibility  = if (s == 1) View.VISIBLE else View.GONE
        b.stepAge.visibility       = if (s == 2) View.VISIBLE else View.GONE
        b.stepBodyStats.visibility = if (s == 3) View.VISIBLE else View.GONE
        b.btnBack.visibility       = if (s == 0) View.GONE else View.VISIBLE
        b.btnNext.text             = if (s == total - 1) "Get Started" else "Continue"
        b.tvStepIndicator.text     = "${s + 1} of $total"
    }

    private fun setupGoals() {
        val btns = listOf(
            b.btnGoalLoseWeight to "LOSE_WEIGHT",
            b.btnGoalBuildMuscle to "BUILD_MUSCLE",
            b.btnGoalStayActive to "STAY_ACTIVE",
            b.btnGoalEndurance to "IMPROVE_ENDURANCE"
        )
        btns.forEach { (btn, g) ->
            btn.setOnClickListener {
                goal = g
                btns.forEach { (b2, _) -> b2.isSelected = false }
                btn.isSelected = true
            }
        }
        b.btnGoalStayActive.isSelected = true
    }

    private fun setupActivity() {
        val btns = listOf(
            b.btnActSedentary to "SEDENTARY",
            b.btnActLight to "LIGHT",
            b.btnActModerate to "MODERATE",
            b.btnActVeryActive to "VERY_ACTIVE"
        )
        btns.forEach { (btn, a) ->
            btn.setOnClickListener {
                activity = a
                btns.forEach { (b2, _) -> b2.isSelected = false }
                btn.isSelected = true
            }
        }
        b.btnActModerate.isSelected = true
    }

    private fun setupSliders() {
        b.seekAge.max = 65
        b.seekAge.progress = 7
        b.tvAgeValue.text = "22"
        b.seekAge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, u: Boolean) {
                age = p + 15
                b.tvAgeValue.text = age.toString()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        b.seekWeight.max = 110
        b.seekWeight.progress = 30
        b.tvWeightValue.text = "70 kg"
        b.seekWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, u: Boolean) {
                weight = (p + 40).toFloat()
                b.tvWeightValue.text = "${weight.toInt()} kg"
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        b.seekHeight.max = 80
        b.seekHeight.progress = 30
        b.tvHeightValue.text = "170 cm"
        b.seekHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, u: Boolean) {
                height = (p + 140).toFloat()
                b.tvHeightValue.text = "${height.toInt()} cm"
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
    }

    private fun saveAndContinue() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(mapOf(
                "age"                to age,
                "weightKg"           to weight,
                "heightCm"           to height,
                "fitnessGoal"        to goal,
                "activityLevel"      to activity,
                "onboardingComplete" to true
            ))
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
