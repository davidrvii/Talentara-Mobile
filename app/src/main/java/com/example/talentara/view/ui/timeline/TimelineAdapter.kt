package com.example.talentara.view.ui.timeline

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.timeline.TimelineProjectItem
import com.example.talentara.databinding.TimelineItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TimelineAdapter(
    private var listTimeline: List<TimelineProjectItem>
): RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    fun getItemAt(position: Int): TimelineProjectItem =
        listTimeline[position]

    private var onItemClick: ((TimelineProjectItem) -> Unit)? = null

    fun setOnItemClickListener(callback: (TimelineProjectItem) -> Unit) {
        this.onItemClick = callback
    }

    class ViewHolder(private val binding: TimelineItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: TimelineProjectItem) {
                val formattedDeadline = formatDeadline(item.startDate.toString(),
                    item.endDate.toString()
                )
                val deadline = formattedDeadline
                binding.apply {
                    tvPhase.text = item.projectPhase
                    tvDeadline.text = itemView.context.getString(R.string.phase_deadline, deadline)
                    tvEvidance.text = itemView.context.getString(R.string.evidence_s, item.evidance)
                    when (item.clientApproved) {
                        true -> cvClientApproved.setCardBackgroundColor(itemView.context.getColor(R.color.blue))
                        false -> cvClientApproved.setCardBackgroundColor(itemView.context.getColor(R.color.dark_gray))
                        else -> {}
                    }
                    when (item.leaderApproved) {
                        true -> cvManagerApproved.setCardBackgroundColor(itemView.context.getColor(R.color.blue))
                        false -> cvManagerApproved.setCardBackgroundColor(itemView.context.getColor(R.color.dark_gray))
                        else -> {}
                    }
                }
            }

        private fun formatDeadline(start: String, end: String): String {
            // Input and output patterns
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "dd MMM yyyy"
            val locale = Locale.getDefault()

            return try {
                val sdfIn = SimpleDateFormat(inputPattern, Locale.US)
                val sdfOut = SimpleDateFormat(outputPattern, locale)

                val sDate = sdfIn.parse(start)
                val eDate = sdfIn.parse(end)

                if (sDate != null && eDate != null) {
                    "${sdfOut.format(sDate)} - ${sdfOut.format(eDate)}"
                } else {
                    // fallback to raw strings
                    "$start - $end"
                }
            } catch (e: Exception) {
                // If parsing fails, show raw
                e.printStackTrace()
                "$start - $end"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = TimelineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listTimeline[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data)
        }
    }

    override fun getItemCount(): Int = listTimeline.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<TimelineProjectItem>) {
        listTimeline = newList
        notifyDataSetChanged()
    }
}