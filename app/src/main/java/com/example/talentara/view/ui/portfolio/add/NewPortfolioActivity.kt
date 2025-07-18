package com.example.talentara.view.ui.portfolio.add

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.response.talent.TalentDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.databinding.ActivityNewPortfolioBinding
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.ui.profile.edit.EditProfileViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class NewPortfolioActivity : AppCompatActivity() {

    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val editProfileViewModel: EditProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityNewPortfolioBinding

    private val selectedRoles = mutableSetOf<String>()
    private val selectedFeatures = mutableSetOf<String>()
    private val selectedPlatforms = mutableSetOf<String>()
    private val selectedProductTypes = mutableSetOf<String>()
    private val selectedLanguages = mutableSetOf<String>()
    private val selectedTools = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        userPreference = UserPreference.getInstance(dataStore)
        getAllCategories()
        setupButtonAction()
        textFieldWatcher()
    }

    private fun getAllCategories() {
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

    private fun setupAllAutoComplete(c: GetAllCategoriesResponse) {
        // Map every model to mutable list for custom runtime
        val roleNames = c.role.orEmpty().mapNotNull { it?.roleName }.toMutableList()
        val featureNames = c.feature.orEmpty().mapNotNull { it?.featureName }.toMutableList()
        val languageNames = c.language.orEmpty().mapNotNull { it?.languageName }.toMutableList()
        val toolsNames = c.tools.orEmpty().mapNotNull { it?.toolsName }.toMutableList()
        val platformNames = c.platform.orEmpty().mapNotNull { it?.platformName }.toMutableList()
        val productTypeNames =
            c.productType.orEmpty().mapNotNull { it?.productTypeName }.toMutableList()

        //Auto Complete For Role
        setupAutoComplete(
            binding.tilRole.editText as AutoCompleteTextView,
            roleNames,
            binding.chipGroupRole,
            selectedRoles
        )

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

    private fun setupButtonAction() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }

            binding.tilStartDate.editText?.apply {
                isFocusable = false
                isClickable = true
                setOnClickListener {
                    showDatePicker(this@NewPortfolioActivity, this as TextInputEditText, R.id.startDateTag)
                }
            }

            binding.tilEndDate.editText?.apply {
                isFocusable = false
                isClickable = true
                setOnClickListener {
                    showDatePicker(this@NewPortfolioActivity, this as TextInputEditText, R.id.endDateTag)
                }
            }

            btnUploadPortfolio.setOnClickListener {
                val state = intent.getStringExtra(STATE)
                Log.d("NewPortfolioActivity", "State: $state")
                when (state) {
                    "NewPortfolio" -> {
                        profileViewModel.getTalentDetail()
                        profileViewModel.getTalentDetail.observe(this@NewPortfolioActivity) { result ->
                            when (result) {
                                is Results.Loading -> {
                                    showLoading(true)
                                }

                                is Results.Success -> {
                                    Log.d("NewPortfolioActivity", "Success: ${result.data}")
                                    result.data.talentDetail?.firstOrNull()?.let { talent -> mergeAndUpdateTalent(talent) }
                                    showLoading(false)
                                }

                                is Results.Error -> {
                                    showLoading(false)
                                    Log.e("NewPortfolioActivity", "Error: ${result.error}")
                                }
                            }
                        }
                    }
                    "TalentApply" -> {
                        val clientName = binding.tilClientName.editText!!.text.toString().trim()
                        val portfolioName = binding.tilProjectName.editText!!.text.toString().trim()
                        val portfolioDesc = binding.tilProjectDescription.editText!!.text.toString().trim()
                        val portfolioLabel = "Portfolio"
                        val github = binding.tilProjectGithub.editText!!.text.toString().trim()
                        val linkedIn = binding.tilProjectLinkedIn.editText!!.text.toString().trim()
                        val startDateStored = binding.tilStartDate.editText?.getTag(R.id.startDateTag)?.toString()
                        val endDateStored = binding.tilEndDate.editText?.getTag(R.id.endDateTag)?.toString()
                        Log.d("NewPortfolioActivity", "Start Date Stored: $startDateStored")
                        Log.d("NewPortfolioActivity", "End Date Stored: $endDateStored")

                        val resultIntent = Intent().apply {
                            putExtra("client_name", clientName)
                            putExtra("portfolio_name", portfolioName)
                            putExtra("portfolio_linkedin", linkedIn)
                            putExtra("portfolio_github", github)
                            putExtra("portfolio_desc", portfolioDesc)
                            putExtra("portfolio_label", portfolioLabel)
                            putExtra("start_date", startDateStored)
                            putExtra("end_date", endDateStored)
                            putStringArrayListExtra("platforms", ArrayList(selectedPlatforms))
                            putStringArrayListExtra("tools", ArrayList(selectedTools))
                            putStringArrayListExtra("languages", ArrayList(selectedLanguages))
                            putStringArrayListExtra("roles", ArrayList(selectedRoles))
                            putStringArrayListExtra("productTypes", ArrayList(selectedProductTypes))
                            putStringArrayListExtra("features", ArrayList(selectedFeatures))
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    private fun mergeAndUpdateTalent(talent: TalentDetailItem) {
        showLoading(true)
        Log.d("NewPortfolioActivity", "Talent: $talent")
        // Get old list (NULL-safe)
        val oldRoles = talent.roles?.split("|")?.map { it.trim() }.orEmpty()
        val oldLanguages = talent.languages?.split("|")?.map { it.trim() }.orEmpty()
        val oldTools = talent.tools?.split("|")?.map { it.trim() }.orEmpty()
        val oldProductTypes = talent.productTypes?.split("|")?.map { it.trim() }.orEmpty()
        val oldPlatforms = talent.platforms?.split("|")?.map { it.trim() }.orEmpty()

        // Merge + deduce
        val mergedRoles = (oldRoles + selectedRoles).distinct()
        val mergedLanguages = (oldLanguages + selectedLanguages).distinct()
        val mergedTools = (oldTools + selectedTools).distinct()
        val mergedProductTypes = (oldProductTypes + selectedProductTypes).distinct()
        val mergedPlatforms = (oldPlatforms + selectedPlatforms).distinct()

        val request = ApiService.UpdateTalentRequest(
            roles = mergedRoles,
            languages = mergedLanguages,
            tools = mergedTools,
            productTypes = mergedProductTypes,
            platforms = mergedPlatforms
        )

        Log.d("NewPortfolioActivity", "Request Update Talent: $request")

        editProfileViewModel.updateTalent(request)
        updateTalentObserver()
    }

    private fun updateTalentObserver() {
        editProfileViewModel.updateTalent.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    Log.d("NewPortfolioActivity", "Success: ${result.data}")
                    uploadPortfolio()
                    showLoading(false)
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun uploadPortfolio() {
        showLoading(true)
        Log.d("NewPortfolioActivity", "Upload Portfolio")
        lifecycleScope.launch {
            val userId = userPreference.getSession().first().userId
            val clientName = binding.tilClientName.editText!!.text.toString().trim()
            val portfolioName = binding.tilProjectName.editText!!.text.toString().trim()
            val portfolioDesc = binding.tilProjectDescription.editText!!.text.toString().trim()
            val github = binding.tilProjectGithub.editText!!.text.toString().trim()
            val linkedIn = binding.tilProjectLinkedIn.editText!!.text.toString().trim()
            val startDateStored = binding.tilStartDate.editText?.getTag(R.id.startDateTag)?.toString()
            val endDateStored = binding.tilEndDate.editText?.getTag(R.id.endDateTag)?.toString()

            val request = ApiService.AddPortfolioRequest(
                talentId = userId,
                clientName = clientName,
                portfolioName = portfolioName,
                portfolioLinkedin = linkedIn,
                portfolioGithub = github,
                portfolioDesc = portfolioDesc,
                portfolioLabel = "Portfolio",
                startDate = startDateStored.toString(),
                endDate = endDateStored.toString(),
                platforms = selectedPlatforms.toList(),
                tools = selectedTools.toList(),
                languages = selectedLanguages.toList(),
                roles = selectedRoles.toList(),
                productTypes = selectedProductTypes.toList(),
                feature = selectedFeatures.toList()
            )

            Log.d("NewPortfolioActivity", "Request Add Portfolio: $request")

            newPortfolioViewModel.addPortfolio(request)
            addPortfolioViewModelObserver()
        }
    }

    private fun addPortfolioViewModelObserver() {
        newPortfolioViewModel.addPortfolio.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Portfolio Successfully Added", Toast.LENGTH_SHORT).show()
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error Adding Portfolio", Toast.LENGTH_SHORT).show()
                    Log.e("NewPortfolioActivity", "Error: ${result.error}")
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
            tilProjectLinkedIn.editText?.addTextChangedListener(watcher)
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
                    binding.tilProjectLinkedIn.editText!!.text.isNotEmpty() &&
                    selectedFeatures.isNotEmpty() &&
                    selectedPlatforms.isNotEmpty() &&
                    selectedProductTypes.isNotEmpty() &&
                    selectedLanguages.isNotEmpty() &&
                    selectedTools.isNotEmpty() &&
                    selectedRoles.isNotEmpty()

        binding.btnUploadPortfolio.isEnabled = filled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val STATE = "state"
    }
}