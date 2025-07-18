package com.example.talentara.view.ui.notifications

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.data.model.response.notification.NotificationHistoryItem
import com.example.talentara.databinding.NotificationItemBinding
import com.example.talentara.view.ui.project.offer.ProjectOfferActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NotificationAdapter(
    private var listNotification: MutableList<NotificationHistoryItem>,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    fun getItemAt(position: Int): NotificationHistoryItem =
        listNotification[position]

    fun removeAt(position: Int) {
        listNotification.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: NotificationHistoryItem, position: Int) {
        listNotification.add(position, item)
        notifyItemInserted(position)
    }

    private var onItemClick: ((NotificationHistoryItem) -> Unit)? = null

    fun setOnItemClickListener(callback: (NotificationHistoryItem) -> Unit) {
        this.onItemClick = callback
    }

    inner class ViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationHistoryItem) {
            // Set the notification title and description style based on the status
            val style = if (item.status == "unread") Typeface.BOLD else Typeface.NORMAL
            binding.tvNotificationTitle.setTypeface(null, style)
            binding.tvNotificationDesc.setTypeface(null, style)

            // Set the unread dot visibility based on the status
            binding.unreadDot.visibility = if (item.status == "unread") View.VISIBLE else View.GONE

            binding.apply {
                tvNotificationTitle.text = item.notificationTitle
                tvNotificationDesc.text = item.notificationDesc
                tvTime.text = formatedDate(item.createdAt ?: "")
            }

            binding.cvNotification.apply {
                setOnClickListener {
                    onItemClick?.invoke(item)

                    val context = it.context
                    when (item.clickAction) {
                        "OPEN_PROJECT_OFFER" -> {
                            val projectId = item.referenceId
                            val roleName = item.notificationDesc?.substringAfter("as a ")?.trim() ?: "Unknown Role"

                            val intent = Intent(context, ProjectOfferActivity::class.java).apply {
                                putExtra("project_id", projectId)
                                putExtra("role_name", roleName)
                            }
                            context.startActivity(intent)
                        }
                        else -> {}
                    }
                }
            }
        }

        fun formatedDate(isoDate: String): String {
            return try {
                val inputFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                val date = inputFormat.parse(isoDate)

                val outputFormat = SimpleDateFormat("d MMMM yy", Locale("id", "ID"))
                outputFormat.format(date ?: Date())
            } catch (_: Exception) {
                isoDate
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listNotification[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listNotification.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: MutableList<NotificationHistoryItem>) {
        listNotification = newList
        notifyDataSetChanged()
    }
}