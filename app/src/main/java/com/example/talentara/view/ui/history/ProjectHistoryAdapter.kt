package com.example.talentara.view.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.project.HistoryProjectItem
import com.example.talentara.databinding.ProjectItemBinding
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class ProjectHistoryAdapter(
    private var listProjectHistory: List<HistoryProjectItem>
): RecyclerView.Adapter<ProjectHistoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ProjectItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryProjectItem) {
            //Get First Item of Product Type and Platform
            val firstProduct = item.productTypes?.split("|")?.firstOrNull() ?: "-"
            val firstPlatform = item.platforms?.split("|")?.firstOrNull() ?: "-"

            //Count Project Worked Days
            val daysWorked: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+
                val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(item.startDate, fmt)
                val completed = item.completedDate?.let { LocalDate.parse(it, fmt) }
                completed?.let { ChronoUnit.DAYS.between(start, it).toInt() } ?: 0
            } else {
                // API <26
                val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LegacyLocalDate.parse(item.startDate, fmt)
                val completed = item.completedDate?.let { LegacyLocalDate.parse(it, fmt) }
                completed?.let { LegacyChronosUnit.DAYS.between(start, it).toInt() } ?: 0
            }

            binding.apply {
                tvStatus.text = item.statusName
                tvProject.text = item.projectName
                tvClient.text = item.clientName
                tvProduct.text = itemView.context.getString(R.string.project_product, firstProduct, firstPlatform)
                if (item.statusName == "Completed"){
                    tvCompleted.text = itemView.context.getString(R.string.complete_in_d_days, daysWorked)
                } else {
                    tvCompleted.text = itemView.context.getString(R.string.project_is_not_completed_yet)
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, ProjectDetailActivity::class.java).apply {
                    putExtra(ProjectDetailActivity.PROJECT_ID, item.projectId)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listProjectHistory[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listProjectHistory.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<HistoryProjectItem>) {
        listProjectHistory = newList
        notifyDataSetChanged()
    }
}