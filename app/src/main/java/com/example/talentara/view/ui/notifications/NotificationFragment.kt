package com.example.talentara.view.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentNotificationBinding
import com.example.talentara.view.utils.FactoryViewModel


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
        updateNotification()
        deleteNotification()
    }

    private fun updateNotification() {
        notificationAdapter.setOnItemClickListener { item ->
            notificationViewModel.updateNotification(
                status = "read",
                notificationId = item.notificationId ?: 0
            )
        }

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
                        "Failed to read notification",
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
                        result.data.notificationHistory?.filterNotNull() ?: emptyList()
                    )
                }

                is Results.Error -> {
                    showLoading(false)
                    if (result.error.contains("HTTP 404")) {
                        Toast.makeText(
                            requireContext(),
                            "There is no notification yet",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.cvNoNotification.visibility = View.VISIBLE
                    } else {
                        binding.cvNoNotification.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_to_get_notification),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "NotificationFragment",
                            "Error getting notification history: ${result.error}"
                        )
                    }
                }
            }
        }
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        Log.d("HistoryFragment", "Setup RecyclerView")
        notificationAdapter = NotificationAdapter(emptyList())
        binding.rvNotification.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(context)
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

                val builder = AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.remove_notification))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_remove_this_notification))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        notificationViewModel.deleteNotification(
                            item.notificationId ?: return@setPositiveButton
                        )
                        notificationObserver()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        notificationAdapter.notifyItemChanged(pos)
                        dialog.dismiss()
                    }
                    .setOnCancelListener {
                        notificationAdapter.notifyItemChanged(pos)
                    }

                val dialog = builder.create()

                dialog.setOnShowListener {
                    dialog.window
                        ?.setBackgroundDrawable(Color.WHITE.toDrawable())

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    }

                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                        setBackgroundColor(Color.WHITE)
                        setTextColor(ContextCompat.getColor(context, R.color.blue))
                    }
                }

                dialog.show()
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                actionState: Int, isCurrentlyActive: Boolean,
            ) {
                val itemView = viewHolder.itemView
                val background = Color.RED.toDrawable()
                if (dX < 0) {
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                } else {
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                }
                background.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
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