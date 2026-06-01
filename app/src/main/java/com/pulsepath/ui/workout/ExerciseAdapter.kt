package com.pulsepath.ui.workout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.pulsepath.databinding.ItemExerciseBinding
import com.pulsepath.models.BodyRegion
import com.pulsepath.models.Exercise

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onFinished: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val LIME   = Color.parseColor("#C4FF00")
    private val PURPLE = Color.parseColor("#7B5CEA")
    private val BLACK  = Color.parseColor("#0D0D0D")
    private val WHITE  = Color.parseColor("#FFFFFF")

    companion object {
        private const val VIEW_EXERCISE = 0
        private const val VIEW_FINISH   = 1
    }

    override fun getItemViewType(pos: Int) = if (pos < exercises.size) VIEW_EXERCISE else VIEW_FINISH
    override fun getItemCount() = exercises.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_EXERCISE) {
            ExVH(ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            val frame = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                setPadding(0, 32, 0, 0)
            }
            FinVH(frame)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ExVH) holder.bind(exercises[position], position)
        else if (holder is FinVH) holder.bind()
    }

    inner class ExVH(private val b: ItemExerciseBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(e: Exercise, pos: Int) {
            b.tvExerciseName.text = e.name
            b.tvMuscles.text = e.muscles
            b.tvDescription.text = e.description
            b.tvSets.text = e.sets
            b.tvNumber.text = (pos + 1).toString()
            b.tvBodyRegion.text = e.bodyRegion.name.replace("_", " ")

            val isEven = pos % 2 == 0
            b.tvNumber.setBackgroundColor(if (isEven) LIME else PURPLE)
            b.tvNumber.setTextColor(if (isEven) BLACK else WHITE)

            val dotColor = when (e.bodyRegion) {
                BodyRegion.LOWER_BODY  -> LIME
                BodyRegion.UPPER_BODY  -> Color.parseColor("#A78BFA")
                BodyRegion.CORE        -> Color.parseColor("#F59E0B")
                BodyRegion.BACK        -> Color.parseColor("#60A5FA")
                BodyRegion.SHOULDERS   -> PURPLE
                BodyRegion.CARDIO      -> Color.parseColor("#F87171")
                BodyRegion.FULL_BODY   -> Color.parseColor("#34D399")
            }
            b.vBodyRegionDot.setBackgroundColor(dotColor)
            b.tvBodyRegion.setTextColor(dotColor)
        }
    }

    inner class FinVH(frame: FrameLayout) : RecyclerView.ViewHolder(frame) {
        private val btn = Button(frame.context).apply {
            text = "Finished Warmup"
            textSize = 15f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(BLACK)
            setBackgroundColor(LIME)
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 160)
            lp.setMargins(0, 0, 0, 0)
            layoutParams = lp
        }
        init { (itemView as FrameLayout).addView(btn) }
        fun bind() { btn.setOnClickListener { onFinished() } }
    }
}
