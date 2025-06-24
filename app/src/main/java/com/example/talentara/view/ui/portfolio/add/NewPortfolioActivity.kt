package com.example.talentara.view.ui.portfolio.add

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.talentara.R
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.databinding.ActivityNewPortfolioBinding
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

class NewPortfolioActivity : AppCompatActivity() {

    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        setupButtonAction()
        addPortfolioViewModelObserver()
    }

    private fun setupAllAutoComplete(c: GetAllCategoriesResponse) {
        // Map every model to List<String>
        val roleNames = c.role.orEmpty().mapNotNull { it?.roleName }
        val featureNames = c.feature.orEmpty().mapNotNull { it?.featureName }
        val languageNames = c.language.orEmpty().mapNotNull { it?.languageName }
        val toolsNames = c.tools.orEmpty().mapNotNull { it?.toolsName }
        val platformNames = c.platform.orEmpty().mapNotNull { it?.platformName }
        val productTypeNames = c.productType.orEmpty().mapNotNull { it?.productTypeName }

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
        options: List<String>,
        chipGroup: ChipGroup,
        selectedSet: MutableSet<String>,
    ) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, options
        )
        actv.setAdapter(adapter)

        actv.setOnItemClickListener { parent, _, pos, _ ->
            val value = parent.getItemAtPosition(pos) as String
            if (selectedSet.add(value)) {
                val chip = Chip(this).apply {
                    text = value
                    isCloseIconVisible = true
                    chipBackgroundColor =
                        ColorStateList.valueOf("#F0F0F0".toColorInt())
                    setOnCloseIconClickListener {
                        chipGroup.removeView(this)
                        selectedSet.remove(value)
                    }
                }
                chipGroup.addView(chip)
            }
            actv.text?.clear()
        }
    }

    private fun setupButtonAction() {
        textFieldWatcher()
        setupDateField(binding.tilStartDate) { selected ->
            binding.tilStartDate.editText?.setText(selected)
        }

        setupDateField(binding.tilEndDate) { selected ->
            binding.tilEndDate.editText?.setText(selected)
        }
        with(binding) {
            btnUploadPortfolio.setOnClickListener {
                val clientName = binding.tilClientName.editText!!.text.toString().trim()
                val portfolioName = binding.tilProjectName.editText!!.text.toString().trim()
                val portfolioDesc = binding.tilProjectDescription.editText!!.text.toString().trim()
                val github = binding.tilProjectGithub.editText!!.text.toString().trim()
                val linkedIn = binding.tilProjectLinkedIn.editText!!.text.toString().trim()
                val startDate = binding.tilStartDate.editText!!.text.toString().trim()
                val endDate = binding.tilEndDate.editText!!.text.toString().trim()

                val request = ApiService.AddPortfolioRequest(
                    clientName = clientName,
                    portfolioName = portfolioName,
                    portfolioLinkedin = linkedIn,
                    portfolioGithub = github,
                    portfolioDesc = portfolioDesc,
                    portfolioLabel = "Portfolio",
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                    platforms = selectedPlatforms.toList(),
                    tools = selectedTools.toList(),
                    languages = selectedLanguages.toList(),
                    roles = selectedRoles.toList(),
                    productTypes = selectedProductTypes.toList(),
                    feature = selectedFeatures.toList()
                )
                lifecycleScope.launch {
                    newPortfolioViewModel.addPortfolio(request)
                }
            }
            btnBack.setOnClickListener {
                finish()
            }
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

    private fun setupDateField(
        textInputLayout: TextInputLayout,
        onDateChanged: (selected: String) -> Unit
    ) {
        // get EditText
        val editText = textInputLayout.editText ?: return

        // non-focus to remove keyboard
        editText.isFocusable = false
        editText.isClickable  = true

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
            tilProjectName.editText?.addTextChangedListener(watcher)
            tilProjectDescription.editText?.addTextChangedListener(watcher)
            tilStartDate.editText?.addTextChangedListener(watcher)
            tilEndDate.editText?.addTextChangedListener(watcher)
            tilProjectGithub.editText?.addTextChangedListener(watcher)
            tilProjectLinkedIn.editText?.addTextChangedListener(watcher)
        }
    }

    private fun buttonSet() {
        val filled =
            binding.tilClientName.editText!!.text.isNotEmpty() &&
                    binding.tilProjectName.editText!!.text.isNotEmpty() &&
                    binding.tilProjectDescription.editText!!.text.isNotEmpty() &&
                    binding.tilStartDate    .editText!!.text.isNotEmpty() &&
                    binding.tilEndDate      .editText!!.text.isNotEmpty() &&
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
}