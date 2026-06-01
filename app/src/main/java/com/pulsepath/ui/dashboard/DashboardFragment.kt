package com.pulsepath.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.pulsepath.R
import com.pulsepath.databinding.FragmentDashboardBinding
import com.pulsepath.viewmodel.DashboardViewModel
import java.util.Calendar

class DashboardFragment : Fragment() {

    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!
    private val vm: DashboardViewModel by viewModels()
    private val DAY_LABELS = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentDashboardBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
        observeData()
        b.btnStartWorkout.setOnClickListener {
            findNavController().navigate(R.id.workoutFragment)
        }
    }

    private fun observeData() {

        vm.userName.observe(viewLifecycleOwner) { name ->
            b.tvUserName.text = name ?: "Athlete"
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            b.tvGreeting.text = when {
                hour < 12 -> "Good morning"
                hour < 17 -> "Good afternoon"
                else      -> "Good evening"
            }
        }

        vm.workoutsThisWeek.observe(viewLifecycleOwner) { count ->
            b.tvWorkoutsCount.text = "${count ?: 0} this week"
        }

        vm.caloriesThisWeek.observe(viewLifecycleOwner) { cal ->
            b.tvCalories.text = (cal ?: 0).toString()
        }

        vm.activeMinutes.observe(viewLifecycleOwner) { minutes ->
            b.tvActiveMinutes.text = "${minutes ?: 0} min"
        }

        vm.todaySteps.observe(viewLifecycleOwner) { steps ->
            val s = steps ?: 0
            b.tvSteps.text = s.toString()
            val pct = ((s.toFloat() / 10000f) * 100).toInt().coerceAtMost(100)
            b.tvGoalPercent.text = "$pct%"
            b.tvTodayProgress.text = "$s / 10,000 steps"
            b.progressGoal.progress = pct
        }

        vm.last7DaysStats.observe(viewLifecycleOwner) { stats ->
            if (stats.isNullOrEmpty()) return@observe
            try {
                val entries = stats.takeLast(7).mapIndexed { i, stat ->
                    BarEntry(i.toFloat(), stat.steps.toFloat())
                }
                val dataSet = BarDataSet(entries, "Steps").apply {
                    color = Color.parseColor("#C4FF00")
                    valueTextColor = Color.parseColor("#888888")
                    valueTextSize = 9f
                }
                b.weeklyChart.data = BarData(dataSet).also { it.barWidth = 0.5f }
                b.weeklyChart.invalidate()
            } catch (e: Exception) {
                // chart not ready yet
            }
        }

        vm.allWorkouts.observe(viewLifecycleOwner) { _ ->
            // observed to keep ViewModel alive
        }
    }

    private fun setupChart() {
        try {
            b.weeklyChart.apply {
                setBackgroundColor(Color.parseColor("#1A1A1A"))
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                axisRight.isEnabled = false
                axisLeft.apply {
                    textColor = Color.parseColor("#888888")
                    gridColor = Color.parseColor("#2A2A2A")
                    axisLineColor = Color.TRANSPARENT
                    textSize = 10f
                }
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(DAY_LABELS)
                    textColor = Color.parseColor("#888888")
                    gridColor = Color.TRANSPARENT
                    granularity = 1f
                    isGranularityEnabled = true
                    textSize = 10f
                }
                animateY(600)
            }
        } catch (e: Exception) {
            // chart setup failed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}