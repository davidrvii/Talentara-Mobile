package com.example.talentara.view.ui.timeline

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityTimelineBinding
import com.example.talentara.databinding.CustomCreateTimelineDialogBinding
import com.example.talentara.databinding.CustomDeleteTimelineDialogBinding
import com.example.talentara.databinding.CustomUpdateTimelineDialogBinding
import com.example.talentara.view.utils.FactoryViewModel

class TimelineActivity : AppCompatActivity() {


    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityTimelineBinding
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var bindingDialog: CustomCreateTimelineDialogBinding
    private lateinit var bindingDeleteDialog: CustomDeleteTimelineDialogBinding
    private lateinit var bindingUpdateDialog: CustomUpdateTimelineDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupProjectTimelineList()
        setupButtonAction()
        deleteTimeline()
        updateTimeline()
    }

    private fun updateTimeline() {
        timelineAdapter.setOnItemClickListener { item ->
                setupUpdateTimelineDialog(timelineId = item.timelineId ?: 0)
        }
    }

    private fun setupButtonAction() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }
            cvAddTimeline.setOnClickListener {
                setupAddTimelineDialog()
            }
        }
    }

    private fun setupAddTimelineDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingDialog = CustomCreateTimelineDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingDialog.root.findViewById<CardView>(R.id.CreateCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingDialog.btYes.setOnClickListener {
            dialog.dismiss()
            val projectPhase = bindingDialog.tilProjectPhase.editText?.text.toString()
            val startDate = bindingDialog.tilStartDate.editText?.text.toString()
            val endDate = bindingDialog.tilEndDate.editText?.text.toString()
            if (projectPhase.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val projectId = intent.getIntExtra(PROJECT_ID, 0)
                timelineViewModel.addTimeline(projectId, projectPhase, startDate, endDate)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            addTimelineObserver()
        }
        bindingDialog.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupUpdateTimelineDialog(timelineId: Int) {
        timelineViewModel.getTimelineDetail(timelineId)
        timelineViewModel.getTimelineDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val timeline = result.data.timelineDetail
                    bindingUpdateDialog.tilProjectPhase.editText?.setText(timeline?.projectPhase)
                    bindingUpdateDialog.tilStartDate.editText?.setText(timeline?.startDate)
                    bindingUpdateDialog.tilEndDate.editText?.setText(timeline?.endDate)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to get Timeline Detail",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error getting timeline detail: ${result.error}")
                }
            }
        }
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingUpdateDialog = CustomUpdateTimelineDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingUpdateDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingUpdateDialog.root.findViewById<CardView>(R.id.UpdateCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingUpdateDialog.btYes.setOnClickListener {
            dialog.dismiss()
            val projectPhase = bindingUpdateDialog.tilProjectPhase.editText?.text.toString()
            val startDate = bindingUpdateDialog.tilStartDate.editText?.text.toString()
            val endDate = bindingUpdateDialog.tilEndDate.editText?.text.toString()
            val evidence = bindingUpdateDialog.tilProjectEvidence.editText?.text.toString()
            if (projectPhase.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                timelineViewModel.updateTimeline(timelineId, projectPhase, startDate, endDate, evidence)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            updateTimelineObserver()
        }
        bindingUpdateDialog.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTimelineObserver() {
        timelineViewModel.updateTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Timeline updated successfully", Toast.LENGTH_SHORT).show()
                    timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update timeline", Toast.LENGTH_SHORT).show()
                    Log.e("TimelineActivity", "Error updating timeline: ${result.error}")
                }
            }
        }
    }

    private fun addTimelineObserver() {
        timelineViewModel.addTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Timeline added successfully", Toast.LENGTH_SHORT).show()
                    timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to add timeline", Toast.LENGTH_SHORT).show()
                    Log.e("TimelineActivity", "Error adding timeline: ${result.error}")
                }
            }
        }
    }

    private fun setupProjectTimelineList() {
        val projectId = intent.getIntExtra(PROJECT_ID, 0)
        timelineViewModel.getProjectTimeline(projectId)
        timelineViewModel.getProjectTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    timelineAdapter.updateData(
                        result.data.timelineProject?.filterNotNull() ?: emptyList()
                    )
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to get Project Timeline",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error getting project timeline: ${result.error}")
                }
            }
        }
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        Log.d("HistoryFragment", "Setup RecyclerView")
        timelineAdapter = TimelineAdapter(emptyList())
        binding.rvTimeline.apply {
            adapter = timelineAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun deleteTimeline() {
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val item = timelineAdapter.getItemAt(pos)

                val builder = AlertDialog.Builder(this@TimelineActivity)
                    .setTitle(getString(R.string.delete_this_timeline))
                    .setMessage(getString(R.string.timeline_will_be_deleted_permanently))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        timelineViewModel.deleteTimeline(
                            item.timelineId ?: return@setPositiveButton
                        )
                        deleteTimelineObserver()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        timelineAdapter.notifyItemChanged(pos)
                        dialog.dismiss()
                    }
                    .setOnCancelListener {
                        timelineAdapter.notifyItemChanged(pos)
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
        ItemTouchHelper(callback).attachToRecyclerView(binding.rvTimeline)
    }

    private fun deleteTimelineObserver() {
        timelineViewModel.deleteTimeline.observe(this@TimelineActivity) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@TimelineActivity,
                        "Timeline deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this@TimelineActivity,
                        "Failed to delete timeline",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "TimelineActivity",
                        "Error deleting timeline: ${result.error}"
                    )
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PROJECT_ID = "project_id"
    }
}