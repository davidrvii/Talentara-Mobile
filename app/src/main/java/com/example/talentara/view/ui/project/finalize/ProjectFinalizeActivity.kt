package com.example.talentara.view.ui.project.finalize

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.talentara.R
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.data.remote.ApiService.ProjectRole
import com.example.talentara.databinding.ActivityProjectFinalizeBinding
import com.example.talentara.databinding.RoleItemBinding
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.ui.notifications.NotificationsViewModel
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.project.detail.ProjectDetailViewModel
import com.example.talentara.view.ui.timeline.TimelineViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ProjectFinalizeActivity : AppCompatActivity() {

    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val projectFinalizeViewModel: ProjectFinalizeViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val notificationViewModel: NotificationsViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityProjectFinalizeBinding
    private var clientId: Int = 0

    private val selectedRoles = mutableSetOf<String>()
    private val selectedFeatures = mutableSetOf<String>()
    private val selectedPlatforms = mutableSetOf<String>()
    private val selectedProductTypes = mutableSetOf<String>()
    private val selectedLanguages = mutableSetOf<String>()
    private val selectedTools = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectFinalizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        textFieldWatcher()
        getAllCategories()
        getProjectDetail()

        binding.tilStartDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@ProjectFinalizeActivity, this as TextInputEditText, R.id.startDateTag)
            }
        }

        binding.tilEndDate.editText?.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker(this@ProjectFinalizeActivity, this as TextInputEditText, R.id.endDateTag)
            }
        }

        binding.btnFinalizeProject.setOnClickListener { finalizeProject() }
        binding.cvBack.setOnClickListener { finish() }
    }

    private fun getAllCategories() {
        // Load categories for auto-complete
        newPortfolioViewModel.getAllCategories()
        newPortfolioViewModel.getAllCategories.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    setupAllAutoComplete(result.data)
                }

                is Results.Error -> {
                    showLoading(false)
                    Log.e("NewPortfolioActivity", "Error: ${result.error}")
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

    private fun getProjectDetail() {
        // Load project detail
        val projectId = intent.getIntExtra(ProjectDetailActivity.PROJECT_ID, 0)
        projectDetailViewModel.getProjectDetail(projectId)
        projectDetailViewModel.getProjectDetail.observe(this) { result ->
            when (result) {
                is Results.Success -> {
                    showLoading(false)
                    val project = result.data.projectDetail?.firstOrNull()
                    binding.tilClientName.editText!!.setText(project?.clientName)
                    binding.tilProjectName.editText!!.setText(project?.projectName)
                    binding.tilProjectDescription.editText!!.setText(project?.projectDesc)
                    formateDate(
                        project?.startDate,
                        binding.tilStartDate.editText as TextInputEditText,
                        R.id.startDateTag
                    )
                    formateDate(
                        project?.endDate,
                        binding.tilEndDate.editText as TextInputEditText,
                        R.id.endDateTag
                    )
                    clientId = project?.userId ?: 0

                    bindChipGroup(project?.features, selectedFeatures, binding.chipGroupFeature)
                    bindChipGroup(project?.platforms, selectedPlatforms, binding.chipGroupPlatform)
                    bindChipGroup(project?.productTypes, selectedProductTypes, binding.chipGroupProductType)
                    bindChipGroup(project?.languages, selectedLanguages, binding.chipGroupLanguage)
                    bindChipGroup(project?.tools, selectedTools, binding.chipGroupTools)
                    bindRoleItems(project?.rolesNeeded)
                }

                is Results.Error -> {
                    showLoading(false)
                    Log.e("ProjectFinalizeActivity", "Error: ${result.error}")
                }

                is Results.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun bindChipGroup(
        raw: String?,
        selectedSet: MutableSet<String>,
        chipGroup: ChipGroup,
    ) {
        selectedSet.clear()
        chipGroup.removeAllViews()

        val values = raw?.split("|")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.toSet()
            ?: emptySet()

        selectedSet.addAll(values)
        values.forEach { value ->
            val chip = Chip(this).apply {
                text = value
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    chipGroup.removeView(this)
                    selectedSet.remove(value)
                    buttonSet()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun bindRoleItems(rolesNeeded: String?) {
        if (rolesNeeded.isNullOrBlank()) return

        rolesNeeded.split("|").forEach { entry ->
            val regex = Regex("^(.*?)\\s*\\((\\d+)\\)$")
            val match = regex.find(entry.trim())

            val role = match?.groups?.get(1)?.value?.trim()
            val amount = match?.groups?.get(2)?.value?.toIntOrNull()

            if (role != null && amount != null && amount > 0 && selectedRoles.add(role)) {
                val itemBinding = RoleItemBinding.inflate(LayoutInflater.from(this), binding.roleContainer, false)
                itemBinding.tvRoleItem.text = role
                itemBinding.etProjectFeature.setText(amount.toString())
                itemBinding.root.tag = role

                itemBinding.btnRemove.setOnClickListener {
                    binding.roleContainer.removeView(itemBinding.root)
                    selectedRoles.remove(role)
                    buttonSet()
                }

                binding.roleContainer.addView(itemBinding.root)
            }
        }
    }

    private fun setupAllAutoComplete(c: GetAllCategoriesResponse) {
        // Map every model to mutable list for custom runtime
        val featureNames = c.feature.orEmpty().mapNotNull { it?.featureName }.toMutableList()
        val languageNames = c.language.orEmpty().mapNotNull { it?.languageName }.toMutableList()
        val toolsNames = c.tools.orEmpty().mapNotNull { it?.toolsName }.toMutableList()
        val platformNames = c.platform.orEmpty().mapNotNull { it?.platformName }.toMutableList()
        val productTypeNames = c.productType.orEmpty().mapNotNull { it?.productTypeName }.toMutableList()
        val roles = c.role.orEmpty().mapNotNull { it?.roleName }.toMutableList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        val actv = binding.tilRole.editText as AutoCompleteTextView
        actv.apply {
            threshold = 1
            setAdapter(adapter)
            setOnItemClickListener { parent, _, pos, _ ->
                val role = parent.getItemAtPosition(pos) as String
                addRoleItem(role)
                text.clear()
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val input = text.toString().trim()
                    if (input.isNotEmpty()) addRoleItem(input)
                    text.clear()
                }
                false
            }
        }

        //Auto Complete For Feature
        setupAutoComplete(
            binding.tilFeature.editText as AutoCompleteTextView,
            featureNames,
            binding.chipGroupFeature,
            selectedFeatures
        )

        //Auto Complete For Platform
        setupAutoComplete(
            binding.tilPlatform.editText as AutoCompleteTextView,
            platformNames,
            binding.chipGroupPlatform,
            selectedPlatforms
        )

        //Auto Complete For Product Type
        setupAutoComplete(
            binding.tilProductType.editText as AutoCompleteTextView,
            productTypeNames,
            binding.chipGroupProductType,
            selectedProductTypes
        )

        //Auto Complete For Language
        setupAutoComplete(
            binding.tilLanguage.editText as AutoCompleteTextView,
            languageNames,
            binding.chipGroupLanguage,
            selectedLanguages
        )

        //Auto Complete For Tools
        setupAutoComplete(
            binding.tilTools.editText as AutoCompleteTextView,
            toolsNames,
            binding.chipGroupTools,
            selectedTools
        )
    }

    private fun setupAutoComplete(
        actv: AutoCompleteTextView,
        options: MutableList<String>,
        chipGroup: ChipGroup,
        selectedSet: MutableSet<String>,
    ) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        actv.setAdapter(adapter)
        actv.threshold = 1

        actv.setOnItemClickListener { parent, _, pos, _ ->
            val value = parent.getItemAtPosition(pos) as String
            addChip(value, chipGroup, selectedSet)
            actv.text?.clear()
        }

        actv.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = actv.text.toString().trim()
                if (input.isNotEmpty()) {
                    //Add to option and refresh adapter if option not found
                    if (!options.contains(input)) {
                        options.add(input)
                        adapter.notifyDataSetChanged()
                    }
                    addChip(input, chipGroup, selectedSet)
                    actv.text.clear()
                }
            }
            false
        }
    }

    private fun addChip(
        value: String,
        chipGroup: ChipGroup,
        selectedSet: MutableSet<String>,
    ) {
        if (selectedSet.add(value)) {
            val chip = Chip(this).apply {
                text = value
                isCloseIconVisible = true
                chipBackgroundColor = ColorStateList.valueOf("#F0F0F0".toColorInt())
                setOnCloseIconClickListener {
                    chipGroup.removeView(this)
                    selectedSet.remove(value)
                    buttonSet()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun addRoleItem(role: String) {
        if (!selectedRoles.add(role)) return
        val itemBinding =
            RoleItemBinding.inflate(LayoutInflater.from(this), binding.roleContainer, false)
            itemBinding.tvRoleItem.text = role
            itemBinding.root.tag = role
            itemBinding.btnRemove.setOnClickListener {
            binding.roleContainer.removeView(itemBinding.root)
            selectedRoles.remove(role)
        }
        binding.roleContainer.addView(itemBinding.root)
    }

    private fun finalizeProject() {
        val startDateStored =
            (binding.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
        val endDateStored =
            (binding.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()
        val projectId = intent.getIntExtra(PROJECT_ID, 0)
        val roleList = mutableListOf<ProjectRole>()

        for (role in selectedRoles) {
            val view = binding.roleContainer.findViewWithTag<View>(role)
            val bind = RoleItemBinding.bind(view)
            val amtText = bind.etProjectFeature.text.toString().trim()
            val amt = amtText.toIntOrNull()
            if (amt == null || amt <= 0) {
                bind.etProjectFeature.error = getString(R.string.insert_amount_more_than_0)
                return
            }
            roleList.add(ProjectRole(roleName = role, amount = amt))
        }

        val request = ApiService.UpdateProjectRequest(
            clientName = binding.tilClientName.editText!!.text.toString().trim(),
            projectName = binding.tilProjectName.editText!!.text.toString().trim(),
            projectDesc = binding.tilProjectDescription.editText!!.text.toString().trim(),
            startDate = startDateStored,
            endDate = endDateStored,
            platform = selectedPlatforms.toList(),
            productType = selectedProductTypes.toList(),
            role = roleList,
            language = selectedLanguages.toList(),
            tools = selectedTools.toList(),
            feature = selectedFeatures.toList(),
            projectGithub = binding.tilProjectGithub.editText!!.text.toString().trim(),
            meetLink = binding.tilMetingLink.editText!!.text.toString().trim()
        )
        lifecycleScope.launch {
            projectFinalizeViewModel.updateProject(projectId, request)
        }
        updateProjectObserver(projectId)
    }

    private fun updateProjectObserver(projectId: Int) {
        projectFinalizeViewModel.updateProject.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)

                    timelineViewModel.updateProjectStatus(projectId, 3)
                    timelineViewModel.updateProjectStatus.observe(this) { result ->
                        when (result) {
                            is Results.Success -> Log.d("ProjectFinalizeActivity", "Update Project Status Success")
                            is Results.Error -> Log.e("ProjectFinalizeActivity", "Update Project Status Failed: ${result.error}")
                            else -> {}
                        }
                    }

                    notificationViewModel.addNotification(
                        title = "Project Finalized",
                        desc = "Waiting for team to join the project",
                        type = "PROJECT_FINALIZED",
                        clickAction = "NONE"
                    )
                    notificationViewModel.addNotification.observe(this) { result ->
                        when (result) {
                            is Results.Success -> Log.d("ProjectFinalizeActivity", "Create Manager Notification Success")
                            is Results.Error -> Log.e("ProjectFinalizeActivity", "Create Manager Notification Failed: ${result.error}")
                            else -> {}
                        }
                    }

                    lifecycleScope.launch {
                        val notifResult = notificationViewModel.addNotificationTalent(
                            userId = clientId,
                            title = "Project Finalized",
                            desc = "Waiting for team to join the project",
                            type = "PROJECT_FINALIZED",
                            clickAction = "NONE"
                        )

                        when (notifResult) {
                            is Results.Success -> Log.d("ProjectFinalizeActivity", "Create Notification Success for user $clientId")
                            is Results.Error -> Log.e("ProjectFinalizeActivity", "Create Notification Failed for user $clientId: ${notifResult.error}")
                            else -> {}
                        }

                        val intent = Intent(this@ProjectFinalizeActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Failed update project", Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ProjectFinalizeActivity", "Error: ${result.error}")
                }
            }
        }

    }

    private fun textFieldWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonSet()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        with(binding) {
            tilProjectName.editText?.addTextChangedListener(watcher)
            tilProjectDescription.editText?.addTextChangedListener(watcher)
            tilStartDate.editText?.addTextChangedListener(watcher)
            tilEndDate.editText?.addTextChangedListener(watcher)
            tilProjectGithub.editText?.addTextChangedListener(watcher)
            tilMetingLink.editText?.addTextChangedListener(watcher)
            tilClientName.editText?.addTextChangedListener(watcher)
            tilFeature.editText?.addTextChangedListener(watcher)
            tilPlatform.editText?.addTextChangedListener(watcher)
            tilProductType.editText?.addTextChangedListener(watcher)
            tilLanguage.editText?.addTextChangedListener(watcher)
            tilTools.editText?.addTextChangedListener(watcher)
            tilRole.editText?.addTextChangedListener(watcher)
        }
    }

    private fun buttonSet() {
        val filled =
            binding.tilClientName.editText!!.text.isNotEmpty() &&
                    binding.tilProjectName.editText!!.text.isNotEmpty() &&
                    binding.tilProjectDescription.editText!!.text.isNotEmpty() &&
                    binding.tilStartDate.editText!!.text.isNotEmpty() &&
                    binding.tilEndDate.editText!!.text.isNotEmpty() &&
                    binding.tilProjectGithub.editText!!.text.isNotEmpty() &&
                    selectedFeatures.isNotEmpty() &&
                    selectedPlatforms.isNotEmpty() &&
                    selectedProductTypes.isNotEmpty() &&
                    selectedLanguages.isNotEmpty() &&
                    selectedTools.isNotEmpty() &&
                    selectedRoles.isNotEmpty()

        binding.btnFinalizeProject.isEnabled = filled
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
    }
}