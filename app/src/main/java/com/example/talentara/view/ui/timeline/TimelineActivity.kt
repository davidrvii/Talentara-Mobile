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
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimelineActivity : AppCompatActivity() {


    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityTimelineBinding
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var bindingDialog: CustomCreateTimelineDialogBinding
    private lateinit var bindingDeleteDialog: CustomDeleteTimelineDialogBinding
    private lateinit var bindingUpdateDialog: CustomUpdateTimelineDialogBinding

    private lateinit var projectAccess: String

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
        getProjectAccess()
    }

    private fun getProjectAccess() {
        timelineViewModel.getProjectAccess(intent.getIntExtra(PROJECT_ID, 0))
        timelineViewModel.getProjectAccess.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    projectAccess = result.data.access ?: "NoAccess"
                    timelineAdapter.setAccessLevel(projectAccess)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to get Project Access",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error getting project access: ${result.error}")
                }
            }
        }
    }

    private fun updateTimeline() {
        timelineAdapter.setOnItemClickListener { item ->
            if (projectAccess == "Project Manger") {
                setupUpdateTimelineDialog(timelineId = item.timelineId ?: 0)
            } else {
                Toast.makeText(
                    this,
                    "$projectAccess are not authorized to update this timeline",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                timelineViewModel.updateTimeline(
                    timelineId,
                    projectPhase,
                    startDate,
                    endDate,
                    evidence
                )
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

        timelineAdapter.setOnManagerApproveClick { item ->
            val id = item.timelineId ?: return@setOnManagerApproveClick
            timelineViewModel.updateTimelineLeaderApprove(id, (item.leaderApproved == true))
            updateTimelineLeaderApproveObserver(item.timelineId)
        }
        timelineAdapter.setOnClientApproveClick { item ->
            val id = item.timelineId ?: return@setOnClientApproveClick
            timelineViewModel.updateTimelineClientApprove(id, (item.clientApproved == true))
            updateTimelineClientApproveObserver(item.timelineId)
        }
    }

    private fun updateTimelineLeaderApproveObserver(timelineId: Int) {
        timelineViewModel.updateTimelineLeaderApprove.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Timeline Approved",
                        Toast.LENGTH_SHORT
                    ).show()
                    getTimelineApprove(timelineId)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to approve timeline",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error Leader Approve timeline: ${result.error}")
                }
            }
        }
    }

    private fun updateTimelineClientApproveObserver(timelineId: Int) {
        timelineViewModel.updateTimelineClientApprove.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Timeline Approved",
                        Toast.LENGTH_SHORT
                    ).show()
                    getTimelineApprove(timelineId)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to approve timeline",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error Client Approve timeline: ${result.error}")
                }
            }
        }
    }

    private fun getTimelineApprove(timelineId: Int) {
        timelineViewModel.getTimelineApprove(timelineId)
        timelineViewModel.getTimelineApprove.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                    val approvement = result.data.approvement
                    if (approvement?.leaderApproved == 1 && approvement.clientApproved == 1) {
                        val completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date())
                        timelineViewModel.updateTimelineCompletedDate(timelineId, completedDate)
                        updateTimelineCompletedDateObserver()
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to get Project Approve",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TimelineActivity", "Error getting project approve: ${result.error}")
                }
            }
        }
    }

    private fun updateTimelineCompletedDateObserver() {
        timelineViewModel.updateTimelineCompletedDate.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Timeline Completed", Toast.LENGTH_SHORT).show()
                    timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                    checkProjectCompleted()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed Completed timeline", Toast.LENGTH_SHORT).show()
                    Log.e("TimelineActivity", "Error Completed timeline: ${result.error}")
                }
            }
        }
    }

    private fun checkProjectCompleted() {
        timelineViewModel.getProjectTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val items = result.data.timelineProject?.filterNotNull() ?: emptyList()

                    if (items.isNotEmpty()) {
                        val last = items.last()
                        if (last.clientApproved == true && last.leaderApproved == true) {
                            timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                            val completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date())
                            val projectId = intent.getIntExtra(PROJECT_ID, 0)

                            timelineViewModel.updateTalentProjectDone(1)
                            updateTalentProjectDoneObserver()

                            timelineViewModel.updateProjectCompleted(projectId, completedDate)
                            updateProjectCompletedObserver()
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to get Project Timeline", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun updateProjectCompletedObserver() {
        timelineViewModel.updateProjectCompleted.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Project Completed", Toast.LENGTH_SHORT).show()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update project completed", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun updateTalentProjectDoneObserver() {
        timelineViewModel.updateTalentProjectDone.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Talent project done updated", Toast.LENGTH_SHORT).show()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update talent project done", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
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
                showCustomDeleteDialog(item.timelineId ?: 0)
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

    private fun showCustomDeleteDialog(timelineId: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingDeleteDialog = CustomDeleteTimelineDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingDeleteDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingDeleteDialog.root.findViewById<CardView>(R.id.DeleteCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingDeleteDialog.btYesDelete.setOnClickListener {
            dialog.dismiss()
            timelineViewModel.deleteTimeline(timelineId)
            deleteTimelineObserver()
        }
        bindingDeleteDialog.btCancelDelete.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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