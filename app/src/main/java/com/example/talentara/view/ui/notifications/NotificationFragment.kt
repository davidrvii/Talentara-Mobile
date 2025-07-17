package com.example.talentara.view.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentNotificationBinding
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.snackbar.Snackbar


class NotificationFragment : Fragment() {

    private val notificationViewModel: NotificationsViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private lateinit var notificationAdapter: NotificationAdapter
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationList()
        deleteNotification()
    }

    private fun updateNotification() {
        notificationViewModel.updateNotification.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    setupNotificationList()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Failed to mark as read notification",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "NotificationFragment",
                        "Error updating notification: ${result.error}"
                    )
                }
            }
        }
    }

    private fun setupNotificationList() {
        setupRecyclerView()

        notificationViewModel.getNotificationHistory()
        notificationViewModel.notificationHistory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                    binding.cvNoNotification.visibility = View.GONE
                }

                is Results.Success -> {
                    showLoading(false)
                    binding.cvNoNotification.visibility = View.GONE
                    notificationAdapter.updateData(
                        result.data.notificationHistory?.filterNotNull()?.toMutableList() ?: mutableListOf()
                    )
                    Log.d("NotificationFragment", "Loaded notifications: ${result.data.notificationHistory?.size}")
                }

                is Results.Error -> {
                    showLoading(false)
                    if (result.error.contains("HTTP 404")) {
                        binding.cvNoNotification.visibility = View.VISIBLE
                    } else {
                        binding.cvNoNotification.visibility = View.VISIBLE
                        Log.e(
                            "NotificationFragment",
                            "Error getting notification history: ${result.error}"
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        Log.d("HistoryFragment", "Setup RecyclerView")
        notificationAdapter = NotificationAdapter(mutableListOf())
        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
        }

        notificationAdapter.setOnItemClickListener { item ->
            if (item.status == "unread") {
                notificationViewModel.updateNotification(
                    status = "read",
                    notificationId = item.notificationId ?: 0
                )
                updateNotification()
            }
        }
    }

    private fun deleteNotification() {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val item = notificationAdapter.getItemAt(pos)

                notificationAdapter.removeAt(pos)

                Snackbar.make(requireView(), "Notification removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        notificationAdapter.restoreItem(item, pos)
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event != DISMISS_EVENT_ACTION) {
                                notificationViewModel.deleteNotification(
                                    item.notificationId ?: return
                                )
                                notificationObserver()
                            }
                        }
                    })
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.blue))
                    .setTextColor(Color.WHITE)
                    .setActionTextColor(Color.YELLOW)
                    .show()
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.rvNotification)
    }

    private fun notificationObserver() {
        notificationViewModel.deleteNotification.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    setupNotificationList()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_to_delete_notification),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "NotificationFragment",
                        "Error deleting notification: ${result.error}"
                    )
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}