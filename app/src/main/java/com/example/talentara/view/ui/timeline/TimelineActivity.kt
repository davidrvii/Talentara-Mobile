package com.example.talentara.view.ui.timeline

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
import com.example.talentara.view.ui.notifications.NotificationsViewModel
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.ui.project.add.NewProjectViewModel
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.project.detail.ProjectDetailViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimelineActivity : AppCompatActivity() {

    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newProjectViewModel: NewProjectViewModel by viewModels {
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

        getProjectAccess()
        checkProjectDetail()
        setupProjectTimelineList()
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

    private fun setupButtonAction() {
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
                Toast. makeText(
                    this@TimelineActivity,
                    "$projectAccess are not authorized to update this timeline",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        timelineAdapter.setOnManagerApproveClick { item ->
            val id = item.timelineId ?: return@setOnManagerApproveClick
            if (item.leaderApproved == 0) {
                showCustomManagerApproveDialog(id)
            } else {
                Toast.makeText(this@TimelineActivity, "Timeline already approved", Toast.LENGTH_SHORT).show()
            }
        }

        timelineAdapter.setOnClientApproveClick { item ->
            val id = item.timelineId ?: return@setOnClientApproveClick
            if (item.clientApproved == 0) {
                showCustomClientApproveDialog(id)
            } else {
                Toast.makeText(this@TimelineActivity, "Timeline already approved", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun showCustomManagerApproveDialog(id: Int) {
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

        val cardView = bindingApproveDialogBinding.root.findViewById<CardView>(R.id.DeleteCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingApproveDialogBinding.btYesApprove.setOnClickListener {
            dialog.dismiss()
            timelineViewModel.updateTimelineLeaderApprove(id, 1)
            updateTimelineLeaderApproveObserver(id)
        }

        bindingApproveDialogBinding.btCancelApprove.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCustomClientApproveDialog(timelineId: Int?) {
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

        val cardView = bindingApproveDialogBinding.root.findViewById<CardView>(R.id.DeleteCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingApproveDialogBinding.btYesApprove.setOnClickListener {
            dialog.dismiss()
            val id = timelineId ?: return@setOnClickListener
            timelineViewModel.updateTimelineClientApprove(id, 1)
            updateTimelineClientApproveObserver(id)
        }

        bindingApproveDialogBinding.btCancelApprove.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
            val startDateStored = (bindingDialog.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
            val endDateStored = (bindingDialog.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()
            if (projectPhase.isNotEmpty() && startDateStored.isNotEmpty() && endDateStored.isNotEmpty()) {
                val projectId = intent.getIntExtra(PROJECT_ID, 0)
                timelineViewModel.addTimeline(projectId, projectPhase, startDateStored, endDateStored)
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

    @SuppressLint("DefaultLocale")
    private fun showDatePicker(context: Context, targetEditText: TextInputEditText, @IdRes tagId: Int ) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateStored = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) // for db
                val dateDisplayed = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year) // for user

                targetEditText.setTag(tagId, dateStored)
                targetEditText.setText(dateDisplayed)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
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
                    val timeline = result.data.timelineDetail?.firstOrNull()
                    bindingUpdateDialog.tilProjectPhase.editText?.setText(timeline?.projectPhase)

                    timeline?.startDate?.let { start ->
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val parsedDate = inputFormat.parse(start)
                        val displayedDate = parsedDate?.let { outputFormat.format(it) } ?: ""
                        val rawDate = parsedDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: ""

                        bindingUpdateDialog.tilStartDate.editText?.setText(displayedDate)
                        bindingUpdateDialog.tilStartDate.editText?.setTag(R.id.startDateTag, rawDate)
                    }

                    timeline?.endDate?.let { end ->
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val parsedDate = inputFormat.parse(end)
                        val displayedDate = parsedDate?.let { outputFormat.format(it) } ?: ""
                        val rawDate = parsedDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: ""

                        bindingUpdateDialog.tilEndDate.editText?.setText(displayedDate)
                        bindingUpdateDialog.tilEndDate.editText?.setTag(R.id.endDateTag, rawDate)
                        Log.d("TimelineActivity", "End Date: $end")
                    }
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
            val startDateStored = (bindingUpdateDialog.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
            val endDateStored = (bindingUpdateDialog.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()
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

    @SuppressLint("NotifyDataSetChanged")
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
        timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
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
                        Toast.makeText(
                            this,
                            "No timeline yet for this project",
                            Toast.LENGTH_SHORT
                        ).show()
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
        Log.d("TimelineActivity", "Get Timeline Approve")
        timelineViewModel.getTimelineApprove(timelineId)
        timelineViewModel.getTimelineApprove.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    setupProjectTimelineList()
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
                        if (last.clientApproved == 1 && last.leaderApproved == 1) {
                            timelineViewModel.getProjectTimeline(intent.getIntExtra(PROJECT_ID, 0))
                            val completedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date())
                            val projectId = intent.getIntExtra(PROJECT_ID, 0)

                            timelineViewModel.updateProjectCompleted(projectId, completedDate)
                            updateProjectCompletedObserver()

                            if (projectAccess == "Client") {
                                Log.d("TimelineActivity", "Update User Is On Project")
                                newProjectViewModel.updateUserIsOnProject(false)
                                updateUserProjectDoneObserver()
                            } else {
                                for (id in talentId) {
                                    Log.d("TimelineActivity", "Update Talent Project Done")
                                    timelineViewModel.updateTalentProjectDone(1, id)
                                    updateTalentProjectDoneObserver(id)
                                }
                            }
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

    private fun updateUserProjectDoneObserver() {
        Log.d("TimelineActivity", "Update User Project Done")
        newProjectViewModel.updateUserIsOnProject.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Project Completed", Toast.LENGTH_SHORT).show()
                    notificationViewModel.addNotification(
                        title       = "Project Completed",
                        desc        = "You have completed your project",
                        type        = "PROJECT_DONE",
                        clickAction = "NONE"
                    )
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

    private fun updateTalentProjectDoneObserver(id: Int) {
        Log.d("TimelineActivity", "Update Talent Project Done")
        timelineViewModel.updateTalentProjectDone.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Talent project done updated", Toast.LENGTH_SHORT).show()
                    Log.d("TimelineActivity", "Add Notification Project Done")
                    notificationViewModel.addNotificationTalent(
                        talentId    = id,
                        title       = "Project Completed",
                        desc        = "You have completed your project",
                        type        = "PROJECT_DONE",
                        clickAction = "NONE"
                    )
                    getProjectDetail(id)
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

    private fun getProjectDetail(id: Int) {
        Log.d("TimelineActivity", "Get Project Detail")
        val projectId = intent.getIntExtra(ProjectDetailActivity.PROJECT_ID, 0)
        projectDetailViewModel.getProjectDetail(projectId)
        projectDetailViewModel.getProjectDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val project = result.data.projectDetail?.firstOrNull()
                    addPortfolio(project!!, id)

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

    private fun checkProjectDetail() {
        val projectId = intent.getIntExtra(ProjectDetailActivity.PROJECT_ID, 0)
        projectDetailViewModel.getProjectDetail(projectId)
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

    private fun addPortfolio(project: ProjectDetailItem, id: Int) {
        Log.d("TimelineActivity", "Add Portfolio")
        val rawFeatures = project.features ?: ""
        val featuresList = rawFeatures
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val rawPlatforms = project.platforms ?: ""
        val platformsList = rawPlatforms
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val rawTools = project.tools ?: ""
        val toolsList = rawTools
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val rawProductTypes = project.productTypes ?: ""
        val productTypesList = rawProductTypes
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val rawLanguages = project.languages ?: ""
        val languagesList = rawLanguages
            .split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val request = ApiService.AddPortfolioRequest(
            talentId = id,
            clientName = project.clientName!!,
            portfolioName = project.projectName!!,
            portfolioLinkedin = "Empty",
            portfolioGithub = project.projectGithub!!,
            portfolioDesc = project.projectDesc!!,
            portfolioLabel = "Talentara",
            startDate = project.startDate.toString(),
            endDate = project.endDate.toString(),
            platforms = platformsList.toList(),
            tools = toolsList.toList(),
            languages = languagesList.toList(),
            roles = listOf(projectAccess),
            productTypes = productTypesList.toList(),
            feature = featuresList.toList()
        )

        newPortfolioViewModel.addPortfolioTalent(request)
        addPortfolioViewModelObserver(id)
    }

    private fun addPortfolioViewModelObserver(id: Int) {
        Log.d("TimelineActivity", "Add Portfolio View Model Observer")
        newPortfolioViewModel.addPortfolioTalent.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Portfolio Successfully Added", Toast.LENGTH_SHORT).show()
                    Log.d("TimelineActivity", "Add Notification Portfolio")
                    notificationViewModel.addNotificationTalent(
                        talentId    = id,
                        title       = "New Portfolio Created",
                        desc        = "You have created a new portfolio",
                        type        = "NEW_PORTFOLIO",
                        clickAction = "NONE"
                    )
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error Adding Portfolio", Toast.LENGTH_SHORT).show()
                    Log.e("NewPortfolioActivity", "Error: ${result.error}")
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PROJECT_ID = "project_id"
    }
}