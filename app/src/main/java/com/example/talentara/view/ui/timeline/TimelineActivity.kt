package com.example.talentara.view.ui.timeline

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.project.ProjectDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.databinding.ActivityTimelineBinding
import com.example.talentara.databinding.CustomApproveDialogBinding
import com.example.talentara.databinding.CustomCreateTimelineDialogBinding
import com.example.talentara.databinding.CustomDeleteTimelineDialogBinding
import com.example.talentara.databinding.CustomUpdateTimelineDialogBinding
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.ui.notifications.NotificationsViewModel
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.project.detail.ProjectDetailViewModel
import com.example.talentara.view.ui.project.offer.ProjectOfferViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimelineActivity : AppCompatActivity() {

    private val projectOfferViewModel: ProjectOfferViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val notificationViewModel: NotificationsViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }

    private lateinit var binding: ActivityTimelineBinding
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var bindingDialog: CustomCreateTimelineDialogBinding
    private lateinit var bindingDeleteDialog: CustomDeleteTimelineDialogBinding
    private lateinit var bindingUpdateDialog: CustomUpdateTimelineDialogBinding
    private lateinit var bindingApproveDialogBinding: CustomApproveDialogBinding

    private lateinit var projectAccess: String
    private lateinit var projectStatus: String
    private lateinit var talentId: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.cvBack.setOnClickListener {
            finish()
        }

        binding.cvBack.setOnClickListener {
            finish()
        }

        getProjectAccess()
        checkProjectDetail()
        setupProjectTimelineList()
    }

    private fun getProjectAccess() {
        Log.d("TimelineActivity", "Get Project Access")
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
                    Log.d("TimelineActivity", "Project Access: $projectAccess")
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

    private fun checkProjectDetail() {
        Log.d("TimelineActivity", "Check Project Detail")
        projectDetailViewModel.getProjectDetail(
            intent.getIntExtra(
                ProjectDetailActivity.PROJECT_ID,
                0
            )
        )
        projectDetailViewModel.getProjectDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val project = result.data.projectDetail?.firstOrNull()
                    val talent = project?.talents
                    val talentsList = talent?.split("|")
                    talentId = talentsList?.map { entry ->
                        entry.substringBefore(":").toInt()
                    } ?: emptyList()
                    projectStatus = project?.statusName.toString()
                    Log.d("TimelineActivity", "Project Talent: $talentId")
                    Log.d("TimelineActivity", "Project Status: $projectStatus")
                    setupButtonAction()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_portfolio_detail),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "ProjectDetailActivity",
                        "Error getting project detail: ${result.error}"
                    )
                }
            }
        }
    }

    private fun setupButtonAction() {
        Log.d("TimelineActivity", "Setup Button Action")
        with(binding) {
            if (projectAccess == "Project Manager") {
                deleteTimeline()
                if (projectStatus == "Completed") {
                    cvAddTimeline.visibility = View.GONE
                } else {
                    cvAddTimeline.visibility = View.VISIBLE
                    cvAddTimeline.setOnClickListener {
                        setupAddTimelineDialog()
                    }
                }
            } else {
                cvAddTimeline.visibility = View.GONE
            }
        }

        timelineAdapter.setOnEditClick { item ->
            if (projectAccess == "Project Manager") {
                if (item.leaderApproved == 1 && item.clientApproved == 1) {
                    Toast.makeText(
                        this@TimelineActivity,
                        "Timeline already completed",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setupUpdateTimelineDialog(timelineId = item.timelineId ?: 0)
                }
            } else {
                Toast.makeText(
                    this@TimelineActivity,
                    "$projectAccess are not authorized to update this timeline",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        timelineAdapter.setOnManagerApproveClick { item ->
            val id = item.timelineId ?: return@setOnManagerApproveClick
            if (item.leaderApproved == 0) {
                showCustomApproveDialog(id, "Manager")
            } else {
                Toast.makeText(
                    this@TimelineActivity,
                    "Timeline already approved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        timelineAdapter.setOnClientApproveClick { item ->
            val id = item.timelineId ?: return@setOnClientApproveClick
            if (item.clientApproved == 0) {
                showCustomApproveDialog(id, "Client")
            } else {
                Toast.makeText(
                    this@TimelineActivity,
                    "Timeline already approved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupProjectTimelineList() {
        Log.d("TimelineActivity", "Setup Project Timeline List")
        timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
        timelineViewModel.getProjectTimeline.removeObservers(this)
        timelineViewModel.getProjectTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                    binding.cvNoTimeline.visibility = View.GONE
                }

                is Results.Success -> {
                    showLoading(false)
                    binding.cvNoTimeline.visibility = View.GONE
                    timelineAdapter.updateData(
                        result.data.timelineProject?.filterNotNull() ?: emptyList()
                    )
                }

                is Results.Error -> {
                    showLoading(false)
                    if (result.error.contains("HTTP 404")) {
                        binding.cvNoTimeline.visibility = View.VISIBLE
                        return@observe
                    } else {
                        binding.cvNoTimeline.visibility = View.VISIBLE
                        Toast.makeText(
                            this,
                            "Failed to get Project Timeline",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("TimelineActivity", "Error getting project timeline: ${result.error}")
                    }
                }
            }
        }
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        Log.d("TimelineActivity", "Setup RecyclerView")
        timelineAdapter = TimelineAdapter(emptyList())
        binding.rvTimeline.apply {
            adapter = timelineAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupAddTimelineDialog() {
        Log.d("TimelineActivity", "Setup Add Timeline Dialog")
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

        bindingDialog.tilStartDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@TimelineActivity, this as TextInputEditText, R.id.startDateTag)
            }
        }

        bindingDialog.tilEndDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@TimelineActivity, this as TextInputEditText, R.id.endDateTag)
            }
        }

        bindingDialog.btYes.setOnClickListener {
            dialog.dismiss()
            val projectPhase = bindingDialog.tilProjectPhase.editText?.text.toString()
            val startDateStored =
                (bindingDialog.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
            val endDateStored =
                (bindingDialog.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()
            if (projectPhase.isNotEmpty() && startDateStored.isNotEmpty() && endDateStored.isNotEmpty()) {
                val projectId = intent.getIntExtra(PROJECT_ID, 0)
                timelineViewModel.addTimeline(
                    projectId,
                    projectPhase,
                    startDateStored,
                    endDateStored
                )
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

    @SuppressLint("NotifyDataSetChanged")
    private fun addTimelineObserver() {
        Log.d("TimelineActivity", "Add Timeline Observer")
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

    private fun formateDate(
        rawUtcDate: String?,
        targetField: TextInputEditText,
        tagId: Int
    ) {
        if (rawUtcDate.isNullOrBlank()) return

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val tagFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val parsedDate = inputFormat.parse(rawUtcDate)

        val displayedDate = parsedDate?.let { outputFormat.format(it) } ?: ""
        val rawDate = parsedDate?.let { tagFormat.format(it) } ?: ""

        targetField.setText(displayedDate)
        targetField.setTag(tagId, rawDate)
    }

    private fun setupUpdateTimelineDialog(timelineId: Int) {
        Log.d("TimelineActivity", "Setup Update Timeline Dialog")
        timelineViewModel.getTimelineDetail(timelineId)
        timelineViewModel.getTimelineDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val timeline = result.data.timelineDetail?.firstOrNull()
                    bindingUpdateDialog.tilProjectPhase.editText?.setText(timeline?.projectPhase)
                    bindingUpdateDialog.tilProjectEvidence.editText?.setText(timeline?.evidence)
                    formateDate(
                        timeline?.startDate,
                        bindingUpdateDialog.tilStartDate.editText as TextInputEditText,
                        R.id.startDateTag
                    )
                    formateDate(
                        timeline?.endDate,
                        bindingUpdateDialog.tilEndDate.editText as TextInputEditText,
                        R.id.endDateTag
                    )
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

        bindingUpdateDialog.tilStartDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@TimelineActivity, this as TextInputEditText, R.id.startDateTag)
            }
        }

        bindingUpdateDialog.tilEndDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@TimelineActivity, this as TextInputEditText, R.id.endDateTag)
            }
        }

        bindingUpdateDialog.btYes.setOnClickListener {
            dialog.dismiss()
            val projectPhase = bindingUpdateDialog.tilProjectPhase.editText?.text.toString()
            val startDateStored =
                (bindingUpdateDialog.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
            val endDateStored =
                (bindingUpdateDialog.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()
            val evidence = bindingUpdateDialog.tilProjectEvidence.editText?.text.toString()
            if (projectPhase.isNotEmpty() && startDateStored.isNotEmpty() && endDateStored.isNotEmpty()) {
                timelineViewModel.updateTimeline(
                    timelineId,
                    projectPhase,
                    startDateStored,
                    endDateStored,
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
        Log.d("TimelineActivity", "Update Timeline Observer")
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

    private fun showCustomApproveDialog(id: Int, role: String) {
        Log.d("TimelineActivity", "Show Custom Approve Dialog")
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingApproveDialogBinding = CustomApproveDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bindingApproveDialogBinding.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingApproveDialogBinding.root.findViewById<CardView>(R.id.ApproveCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingApproveDialogBinding.btYesApprove.setOnClickListener {
            when (role) {
                "Manager" -> {
                    timelineViewModel.updateTimelineLeaderApprove(id, 1)
                    updateTimelineLeaderApproveObserver(id)
                }

                "Client" -> {
                    timelineViewModel.updateTimelineClientApprove(id, 1)
                    updateTimelineClientApproveObserver(id)
                }
            }
            dialog.dismiss()
        }
        bindingApproveDialogBinding.btCancelApprove.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTimelineLeaderApproveObserver(timelineId: Int) {
        Log.d("TimelineActivity", "Update Timeline Leader Approve")
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
        Log.d("TimelineActivity", "Update Timeline Client Approve")
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
        Log.d("TimelineActivity", "Get Timeline Approve")
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
                    if (approvement?.firstOrNull()?.leaderApproved == 1 && approvement.firstOrNull()?.clientApproved == 1) {
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
        Log.d("TimelineActivity", "Update Timeline Completed Date")
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
        Log.d("TimelineActivity", "Check Project Completed")
        timelineViewModel.getProjectTimeline.removeObservers(this)
        timelineViewModel.getProjectTimeline.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val items = result.data.timelineProject?.filterNotNull() ?: emptyList()

                    if (items.isNotEmpty()) {
                        val last = items.sortedBy { it.startDate }.lastOrNull()
                        val isCompleted = last?.clientApproved == 1 && last.leaderApproved == 1

                        if (isCompleted) {
                            val completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val projectId = intent.getIntExtra(PROJECT_ID, 0)
                            val userId = intent.getIntExtra(USER_ID, 0)

                            lifecycleScope.launch {
                                timelineViewModel.updateProjectCompleted(projectId, completedDate)
                                timelineViewModel.updateProjectStatus(projectId, 5)
                                updateProjectCompletedObserver()

                                //Update Client is_on_project
                                projectOfferViewModel.updateUserIsOnProjectDone(userId, 0)
                                updateUserProjectDoneObserver(userId)

                                //Update  Talent is_on_project
                                for (id in talentId) {
                                    Log.d("TimelineActivity", "Update Talent Project Done for $id from $talentId")
                                    timelineViewModel.updateTalentProjectDone(1, id)
                                    projectOfferViewModel.updateTalentIsOnProjectDone(id, 0)

                                    Log.d("TimelineActivity", "Add Notification Talent Project Completed")
                                    val notifResult = notificationViewModel.addNotificationTalent(
                                        userId = id,
                                        title = "Project Completed",
                                        desc = "You have completed your project",
                                        type = "PROJECT_DONE",
                                        clickAction = "NONE"
                                    )

                                    when (notifResult) {
                                        is Results.Success -> Log.d("Notification", "Success for talent $id")
                                        is Results.Error -> Log.e("Notification", "Failed for talent $id: ${notifResult.error}")
                                        else -> {}
                                    }
                                    updateTalentProjectCompletedObserver()
                                }
                                getProjectDetail(talentId)
                            }
                        } else {
                            Log.d("TimelineActivity", "Project not completed")
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

        timelineViewModel.updateProjectStatus.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }
                is Results.Success -> {
                    showLoading(false)
                }
                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update project status", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun updateUserProjectDoneObserver(userId: Int) {
        Log.d("TimelineActivity", "Update User Project Done")
        projectOfferViewModel.updateUserIsOnProjectDone.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    Log.d("TimelineActivity", "Update User Project Done")

                    Log.d("TimelineActivity", "Add Notification User Project Completed")
                    lifecycleScope.launch {
                        val notifResult = notificationViewModel.addNotificationTalent(
                            userId = userId,
                            title = "Project Completed",
                            desc = "Your project has been completed",
                            type = "PROJECT_DONE",
                            clickAction = "NONE"
                        )

                        when (notifResult) {
                            is Results.Success -> Log.d("TimelineActivity", "Add Notification Success for user $userId")
                            is Results.Error -> Log.e("TimelineActivity", "Add Notification Error: ${notifResult.error}")
                            else -> {}
                        }
                    }
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

    private fun updateTalentProjectCompletedObserver() {
        Log.d("TimelineActivity", "Update Talent Project Completed")
        timelineViewModel.updateTalentProjectDone.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Log.d("TimelineActivity", "Talent project done updated")
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update talent project done", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
        projectOfferViewModel.updateTalentIsOnProjectDone.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    Log.d("TimelineActivity", "Talent is not in project updated")
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed to update talent is not in project",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun getProjectDetail(talentId: List<Int>) {
        Log.d("TimelineActivity", "Get Project Detail")
        projectDetailViewModel.getProjectDetail(
            intent.getIntExtra(
                ProjectDetailActivity.PROJECT_ID,
                0
            )
        )
        projectDetailViewModel.getProjectDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val project = result.data.projectDetail?.firstOrNull()
                    addPortfolioTalent(project!!, talentId)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_portfolio_detail),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "ProjectDetailActivity",
                        "Error getting project detail: ${result.error}"
                    )
                }
            }
        }
    }

    private fun addPortfolioTalent(project: ProjectDetailItem, talentId: List<Int>) {
        Log.d("TimelineActivity", "Add Portfolio")

        lifecycleScope.launch {
            for (id in talentId) {
                val request = buildPortfolioRequest(project, id)

                when (val result = newPortfolioViewModel.addPortfolioTalent(request)) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        Log.d("TimelineActivity", "Portfolio Success for talent $id")

                        when (val notifResult = notificationViewModel.addNotificationTalent(
                            id,
                            "New Portfolio Created",
                            "You have created a new portfolio ${project.projectName}",
                            "NEW_PORTFOLIO",
                            "NONE"
                        )) {
                            is Results.Success -> Log.d("TimelineActivity", "Portfolio Notification added for $id")
                            is Results.Error -> Log.e("TimelineActivity", "Portfolio Notification failed for $id: ${notifResult.error}")
                            else -> {}
                        }
                    }
                    is Results.Error -> {
                        showLoading(false)
                        Log.e("TimelineActivity", "Portfolio Error for $id: ${result.error}")
                    }
                }
            }
        }
        val intent = Intent(this@TimelineActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }

    private fun buildPortfolioRequest(
        project: ProjectDetailItem,
        id: Int,
    ): ApiService.AddPortfolioRequest {
        fun extract(str: String?) =
            str?.split("|")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        return ApiService.AddPortfolioRequest(
            talentId = id,
            clientName = project.clientName.orEmpty(),
            portfolioName = project.projectName.orEmpty(),
            portfolioLinkedin = "Empty",
            portfolioGithub = project.projectGithub.orEmpty(),
            portfolioDesc = project.projectDesc.orEmpty(),
            portfolioLabel = "Talentara",
            startDate = project.startDate?.substringBefore("T") ?: "",
            endDate = project.endDate?.substringBefore("T") ?: "",
            platforms = extract(project.platforms),
            tools = extract(project.tools),
            languages = extract(project.languages),
            roles = listOf(projectAccess),
            productTypes = extract(project.productTypes),
            feature = extract(project.features)
        )
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
                showCustomDeleteDialog(item.timelineId ?: 0, pos)
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

    private fun showCustomDeleteDialog(timelineId: Int, position: Int) {
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
            timelineAdapter.notifyItemChanged(position)
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

    @SuppressLint("DefaultLocale")
    private fun showDatePicker(
        context: Context,
        targetEditText: TextInputEditText,
        @IdRes tagId: Int,
    ) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateStored =
                    String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) // for db
                val dateDisplayed =
                    String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year) // for user

                targetEditText.setTag(tagId, dateStored)
                targetEditText.setText(dateDisplayed)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PROJECT_ID = "project_id"
        const val USER_ID = "user_id"
    }
}