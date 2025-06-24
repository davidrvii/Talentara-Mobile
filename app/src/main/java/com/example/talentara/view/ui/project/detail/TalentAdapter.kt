package com.example.talentara.view.ui.project.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.databinding.TalentItemBinding

class TalentAdapter(
    private var talents: List<String>,
): RecyclerView.Adapter<TalentAdapter.ViewHolder>() {

    private var onItemClick: ((String) -> Unit)? = null

    fun setOnItemClickListener(callback: (String) -> Unit) {
        this.onItemClick = callback
    }

    class ViewHolder(private val binding: TalentItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(raw: String) {
            // raw = "6:Davie (Frontend Developer)"
            // Get After ":" → "Davie (Frontend Developer)"
            val afterColon = raw.substringAfter(":", "")

            // 2) name = before " ("
            val name = afterColon.substringBefore(" (").trim()

            // 3) Role = between "(" and ")"
            val role = afterColon.substringAfter("(", "").substringBefore(")").trim()

            binding.tvTalentName.text = name
            binding.tvTalentRole.text = role
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = TalentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = talents[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data)
        }
    }

    override fun getItemCount(): Int = talents.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<String>) {
        talents = newList
        notifyDataSetChanged()
    }
}