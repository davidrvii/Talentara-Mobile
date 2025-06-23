package com.example.talentara.view.ui.portfolio.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.databinding.ItemsItemBinding

class ItemsAdapter(
    private var items: List<String>
): RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemsItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.tvItem.text = text
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<String>) {
        items = newList
        notifyDataSetChanged()
    }
}