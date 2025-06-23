package com.example.talentara.view.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.data.model.response.notification.NotificationHistoryItem
import com.example.talentara.databinding.NotificationItemBinding

class NotificationAdapter(
    private var listNotification: List<NotificationHistoryItem>
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun getItemAt(position: Int): NotificationHistoryItem =
        listNotification[position]

    private var onItemClick: ((NotificationHistoryItem) -> Unit)? = null

    fun setOnItemClickListener(callback: (NotificationHistoryItem) -> Unit) {
        this.onItemClick = callback
    }

    class ViewHolder(private val binding: NotificationItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: NotificationHistoryItem) {
                // Set the notification title and description style based on the status
                val style = if (item.status == "unread") Typeface.BOLD else Typeface.NORMAL
                binding.tvNotificationTitle.setTypeface(null, style)
                binding.tvNotificationDesc .setTypeface(null, style)

                // Set the unread dot visibility based on the status
                binding.unreadDot.visibility = if (item.status == "unread") View.VISIBLE else View.GONE

                binding.apply {
                    tvNotificationTitle.text = item.notificationTitle
                    tvNotificationDesc.text = item.notificationDesc
                    tvTime.text = item.createdAt
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listNotification[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data)
        }
    }

    override fun getItemCount(): Int = listNotification.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<NotificationHistoryItem>) {
        listNotification = newList
        notifyDataSetChanged()
    }
}