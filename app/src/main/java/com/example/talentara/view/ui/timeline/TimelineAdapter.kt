package com.example.talentara.view.ui.timeline

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.timeline.TimelineProjectItem
import com.example.talentara.databinding.TimelineItemBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class TimelineAdapter(
    private var listTimeline: List<TimelineProjectItem>,
): RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    private var accessLevel: String = "NoAccess"
    private var onClientApproveClick: ((TimelineProjectItem) -> Unit)? = null
    private var onManagerApproveClick: ((TimelineProjectItem) -> Unit)? = null
    private var onEditClick: ((TimelineProjectItem) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setAccessLevel(access: String) {
        this.accessLevel = access
        notifyDataSetChanged()
    }
    fun setOnClientApproveClick(cb: (TimelineProjectItem) -> Unit) {
        this.onClientApproveClick = cb
    }
    fun setOnManagerApproveClick(cb: (TimelineProjectItem) -> Unit) {
        this.onManagerApproveClick = cb
    }
    fun setOnEditClick(callback: (TimelineProjectItem) -> Unit) {
        this.onEditClick = callback
    }

    fun getItemAt(position: Int): TimelineProjectItem =
        listTimeline[position]

    inner class ViewHolder(private val binding: TimelineItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: TimelineProjectItem) {
                val formattedDeadline = formatDeadline(item.startDate.toString(),
                    item.endDate.toString()
                )
                val deadline = formattedDeadline

                //Count Timeline Remaining Days
                val daysRemaining: Int =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // API 26+
                        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val today = LocalDate.now()
                        val end = item.endDate
                            ?.substring(0, 10)
                            ?.let { LocalDate.parse(it, fmt) }
                        end?.let { ChronoUnit.DAYS.between(today, it).toInt() } ?: 0
                    } else {
                        // API <26
                        val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val today = LegacyLocalDate.now()
                        val end = item.endDate
                            ?.substring(0, 10)
                            ?.let { LegacyLocalDate.parse(it, fmt) }
                        end?.let { LegacyChronosUnit.DAYS.between(today, it).toInt() } ?: 0
                    }

                //Count Timeline Completed Days
                val completedDays: Int =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val start = item.startDate
                            ?.substring(0, 10)
                            ?.let { LocalDate.parse(it, fmt) }
                        val completed = item.completedDate
                            ?.substring(0, 10)
                            ?.let { LocalDate.parse(it, fmt) }
                        if (start != null && completed != null) {
                            ChronoUnit.DAYS.between(start, completed).toInt()
                        } else 0
                    } else {
                        val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val start = item.startDate
                            ?.substring(0, 10)
                            ?.let { LegacyLocalDate.parse(it, fmt) }
                        val completed = item.completedDate
                            ?.substring(0, 10)
                            ?.let { LegacyLocalDate.parse(it, fmt) }
                        if (start != null && completed != null) {
                            LegacyChronosUnit.DAYS.between(start, completed).toInt()
                        } else 0
                    }


                binding.apply {
                    tvPhase.text = item.projectPhase
                    tvDeadline.text = itemView.context.getString(R.string.phase_deadline, deadline)
                    if (item.completedDate != null) {
                        binding.tvRemaining.visibility = ViewGroup.GONE
                        binding.tvComplete.visibility = ViewGroup.VISIBLE
                        binding.tvApprove.visibility = ViewGroup.GONE
                        tvComplete.text = itemView.context.getString(R.string.completed_in_d_days, completedDays)
                    } else {
                        binding.tvRemaining.visibility = ViewGroup.VISIBLE
                        binding.tvComplete.visibility = ViewGroup.GONE
                        tvRemaining.text = itemView.context.getString(R.string.remaining_days, daysRemaining)
                    }
                    tvEvidance.text = itemView.context.getString(R.string.evidence_s, item.evidance ?: "-")
                    if (item.clientApproved == 1 && item.leaderApproved == 1) {
                        cvCompleted.visibility = ViewGroup.VISIBLE
                        cvManagerApproved.visibility = ViewGroup.GONE
                        cvClientApproved.visibility = ViewGroup.GONE
                    } else {
                        cvCompleted.visibility = ViewGroup.GONE
                        cvManagerApproved.visibility = ViewGroup.VISIBLE
                        cvClientApproved.visibility = ViewGroup.VISIBLE
                    }
                }

                val isManager = accessLevel == "Project Manager"
                val isClient  = accessLevel == "Client"

                binding.cvManagerApproved.apply {
                    when (item.clientApproved) {
                        1 -> {
                            binding.cvClientApproved.setCardBackgroundColor(itemView.context.getColor(R.color.blue))
                            binding.cvClientApproved.isEnabled = true
                            setOnClickListener {
                                if (isManager) onManagerApproveClick?.invoke(item)
                            }
                        }
                        0 -> {
                            binding.cvClientApproved.setCardBackgroundColor(itemView.context.getColor(R.color.dark_gray))
                            isClickable = isManager
                            alpha = if (isManager) 1f else 0.5f
                            setOnClickListener {
                                if (isManager) onManagerApproveClick?.invoke(item)
                            }
                        }
                        else -> {}
                    }
                }
                binding.cvClientApproved.apply {
                    when (item.leaderApproved) {
                        1 -> {
                            binding.cvManagerApproved.setCardBackgroundColor(itemView.context.getColor(R.color.blue))
                            binding.cvClientApproved.isEnabled = true
                            setOnClickListener {
                                if (isClient) onClientApproveClick?.invoke(item)
                            }
                        }
                        0 -> {
                            binding.cvManagerApproved.setCardBackgroundColor(itemView.context.getColor(R.color.dark_gray))
                            isClickable = isClient
                            alpha = if (isClient) 1f else 0.5f
                            setOnClickListener {
                                if (isClient) onClientApproveClick?.invoke(item)
                            }
                        }
                        else -> {}
                    }
                }
                binding.btnEditTimeline.apply {
                    if (item.clientApproved == 1 && item.leaderApproved == 1) {
                        binding.btnEditTimeline.visibility = ViewGroup.GONE
                    } else {
                        binding.btnEditTimeline.visibility = ViewGroup.VISIBLE
                        setOnClickListener {
                            onEditClick?.invoke(item)
                        }
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
    }

    override fun getItemCount(): Int = listTimeline.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<TimelineProjectItem>) {
        listTimeline = newList
        notifyDataSetChanged()
    }
}