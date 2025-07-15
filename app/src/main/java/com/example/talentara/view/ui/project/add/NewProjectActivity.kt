package com.example.talentara.view.ui.project.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityNewProjectBinding
import com.example.talentara.view.ui.notifications.NotificationsViewModel
import com.example.talentara.view.ui.waiting.WaitingPageActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class NewProjectActivity : AppCompatActivity() {

    private val newProjectViewModel: NewProjectViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val notificationViewModel: NotificationsViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityNewProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        textFieldWatcher()
        setupActionButton()
    }

    private fun setupActionButton() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }
            btnFindProjectManager.setOnClickListener {
                setupDateField(binding.tilStartDate) { selected ->
                    binding.tilStartDate.editText?.setText(selected)
                }

                setupDateField(binding.tilEndDate) { selected ->
                    binding.tilEndDate.editText?.setText(selected)
                }

                addProject()
            }

            tilStartDate.editText?.apply {
                isFocusable = false
                isClickable = true
                setOnClickListener {
                    showDatePicker(this@NewProjectActivity, this as TextInputEditText, R.id.startDateTag)
                }
            }

            tilEndDate.editText?.apply {
                isFocusable = false
                isClickable = true
                setOnClickListener {
                    showDatePicker(this@NewProjectActivity, this as TextInputEditText, R.id.endDateTag)
                }
            }

        }
    }

    private fun addProject() {
        val prefixPurpose = binding.tilProjectPurpose.prefixText.toString()
        val inputPurpose = binding.tilProjectPurpose.editText!!.text.toString().trim()
        val linePurpose =
            if (inputPurpose.isNotEmpty()) "$prefixPurpose $inputPurpose" else prefixPurpose

        val prefixType = binding.tilProductType.prefixText.toString()
        val inputType = binding.tilProductType.editText!!.text.toString().trim()
        val lineProduct = if (inputType.isNotEmpty()) "$prefixType $inputType" else prefixType

        val prefixTarget = binding.tilPlatformTarget.prefixText.toString()
        val inputTarget = binding.tilPlatformTarget.editText!!.text.toString().trim()
        val linePlatform =
            if (inputTarget.isNotEmpty()) "$prefixTarget $inputTarget" else prefixTarget


        val prefixUser = binding.tilTargetUser.prefixText.toString()
        val inputUser = binding.tilTargetUser.editText!!.text.toString().trim()
        val lineTargetUser = if (inputUser.isNotEmpty()) "$prefixUser $inputUser" else prefixUser

        val prefixFeature = binding.tilKeyFeatures.prefixText.toString()
        val inputFeature = binding.tilKeyFeatures.editText!!.text.toString().trim()
        val lineKeyFeatures =
            if (inputFeature.isNotEmpty()) "$prefixFeature $inputFeature" else prefixFeature

        val additional = binding.tilAdditional.editText!!.text.toString().trim()

        val projectDesc = listOf(
            linePurpose,
            lineProduct,
            linePlatform,
            lineTargetUser,
            lineKeyFeatures,
            additional
        ).joinToString("\n")


        val clientName = binding.tilClientName.editText!!.text.toString()
        val projectName = binding.tilProjectName.editText!!.text.toString()
        val startDateStored =
            (binding.tilStartDate.editText?.getTag(R.id.startDateTag)).toString()
        val endDateStored =
            (binding.tilEndDate.editText?.getTag(R.id.endDateTag)).toString()

        newProjectViewModel.updateUserIsOnProject(1)
        newProjectViewModel.updateUserIsOnProject.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    newProjectViewModel.addProject(
                        clientName,
                        projectName,
                        projectDesc,
                        startDateStored,
                        endDateStored
                    )
                    addProjectObserver()
                }

                is Results.Error -> {
                    Toast.makeText(
                        this,
                        "Failed update user is on project", Toast.LENGTH_SHORT
                    ).show()
                    Log.e("NewProjectActivity", "Error: ${result.error}")
                    showLoading(false)
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

    private fun addProjectObserver() {
        newProjectViewModel.addProject.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Successfully create project", Toast.LENGTH_SHORT
                    ).show()

                    notificationViewModel.addNotification(
                        title       = "Create New Project",
                        desc        = "Waiting for project manager to join",
                        type        = "PROJECT_DONE",
                        clickAction = "NONE"
                    )

                    val intent = Intent(this, WaitingPageActivity::class.java).apply {
                        putExtra(
                            WaitingPageActivity.MESSAGE,
                            getString(R.string.finding_project_manager)
                        )
                    }
                    startActivity(intent)
                    finish()
                }

                is Results.Error -> {
                    Toast.makeText(
                        this,
                        getString(R.string.failed_create_project), Toast.LENGTH_SHORT
                    ).show()
                    Log.e("NewProjectActivity", "Error: ${result.error}")
                    showLoading(false)
                }
            }
        }
    }

    private fun setupDateField(
        textInputLayout: TextInputLayout,
        onDateChanged: (selected: String) -> Unit,
    ) {
        // get EditText
        val editText = textInputLayout.editText ?: return

        // non-focus to remove keyboard
        editText.isFocusable = false
        editText.isClickable = true

        editText.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formatted = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    onDateChanged(formatted)
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
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
            tilClientName.editText?.addTextChangedListener(watcher)
            tilProjectName.editText?.addTextChangedListener(watcher)
            tilProjectPurpose.editText?.addTextChangedListener(watcher)
            tilProductType.editText?.addTextChangedListener(watcher)
            tilPlatformTarget.editText?.addTextChangedListener(watcher)
            tilTargetUser.editText?.addTextChangedListener(watcher)
            tilKeyFeatures.editText?.addTextChangedListener(watcher)
            tilAdditional.editText?.addTextChangedListener(watcher)
            tilStartDate.editText?.addTextChangedListener(watcher)
            tilEndDate.editText?.addTextChangedListener(watcher)
        }
    }

    private fun buttonSet() {
        val clientName = binding.tilClientName.editText!!.text.toString()
        val projectName = binding.tilProjectName.editText!!.text.toString()
        val projectPurpose = binding.tilProjectPurpose.editText!!.text.toString()
        val projectType = binding.tilProductType.editText!!.text.toString()
        val platformTarget = binding.tilPlatformTarget.editText!!.text.toString()
        val targetUser = binding.tilTargetUser.editText!!.text.toString()
        val keyFeatures = binding.tilKeyFeatures.editText!!.text.toString()
        val additional = binding.tilAdditional.editText!!.text.toString()
        val startDate = binding.tilStartDate.editText!!.text.toString()
        val endDate = binding.tilEndDate.editText!!.text.toString()

        val isFieldFilled =
            clientName.isNotEmpty() &&
                    projectName.isNotEmpty() &&
                    projectPurpose.isNotEmpty() &&
                    projectType.isNotEmpty() &&
                    platformTarget.isNotEmpty() &&
                    targetUser.isNotEmpty() &&
                    keyFeatures.isNotEmpty() &&
                    additional.isNotEmpty() &&
                    startDate.isNotEmpty() &&
                    endDate.isNotEmpty()

        binding.btnFindProjectManager.isEnabled = isFieldFilled
        Log.d("buttonSet", "isEnabled: $isFieldFilled")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
