package com.pulsepath.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pulsepath.databinding.FragmentHistoryBinding
import com.pulsepath.databinding.ItemWorkoutSessionBinding
import com.pulsepath.models.WorkoutSession
import com.pulsepath.models.WorkoutType
import com.pulsepath.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!
    private val vm: WorkoutViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHistoryBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        val adapter = WorkoutAdapter()
        b.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        b.rvHistory.adapter = adapter
        vm.allWorkouts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

class WorkoutAdapter : ListAdapter<WorkoutSession, WorkoutAdapter.VH>(DIFF) {

    private val fmt = SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(
        ItemWorkoutSessionBinding.inflate(LayoutInflater.from(p.context), p, false)
    )

    override fun onBindViewHolder(h: VH, i: Int) = h.bind(getItem(i))

    inner class VH(private val b: ItemWorkoutSessionBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(s: WorkoutSession) {
            // Label — show the full activity name
            b.tvWorkoutType.text = s.type.label

            // Strip colour — lime for even index types, purple for odd
            val stripColor = when (s.type) {
                WorkoutType.RUNNING,
                WorkoutType.CYCLING,
                WorkoutType.SWIMMING,
                WorkoutType.JUMP_ROPE,
                WorkoutType.WEIGHTS   -> Color.parseColor("#C4FF00")

                WorkoutType.WALKING,
                WorkoutType.HIKING,
                WorkoutType.HIIT,
                WorkoutType.YOGA,
                WorkoutType.ROWING,
                WorkoutType.CUSTOM    -> Color.parseColor("#7B5CEA")
            }
            b.viewTypeStrip.setBackgroundColor(stripColor)

            b.tvWorkoutDate.text = fmt.format(Date(s.startTime))

            val mins = s.durationSeconds / 60
            b.tvDuration.text = "${mins}m"
            b.tvCaloriesItem.text = "${s.caloriesBurned} kcal"
            b.tvStepsItem.text = "${s.steps} steps"
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<WorkoutSession>() {
            override fun areItemsTheSame(a: WorkoutSession, b: WorkoutSession) = a.id == b.id
            override fun areContentsTheSame(a: WorkoutSession, b: WorkoutSession) = a == b
        }
    }
}
