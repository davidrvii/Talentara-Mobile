package com.example.talentara.view.ui.project.offer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.databinding.RoleOrderItemBinding

class ItemRoleOrderAdapter(
    private var roleOrder: List<String>
): RecyclerView.Adapter<ItemRoleOrderAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RoleOrderItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            val role = text.substringBefore("(").trim()
            val amount = text.substringAfter("(").substringBefore(")").trim()

            binding.tvRoleItem.text = role
            binding.tvRoleAmount.text = amount
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = RoleOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = roleOrder[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = roleOrder.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<String>) {
        roleOrder = newList
        notifyDataSetChanged()
    }
}