package com.example.talentara.view.ui.talent.apply

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.talentara.R
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.databinding.ActivityTalentApplyBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioActivity
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class TalentApplyActivity : AppCompatActivity() {

    private val talentApplyViewModel: TalentApplyViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityTalentApplyBinding

    private val selectedRoles = mutableSetOf<String>()
    private val selectedPlatforms = mutableSetOf<String>()
    private val selectedProductTypes = mutableSetOf<String>()
    private val selectedLanguages = mutableSetOf<String>()
    private val selectedTools = mutableSetOf<String>()

    private val portfolioItems = mutableListOf<ApiService.PortfolioItem>()

    private val portfolioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val clientName = data.getStringExtra("client_name")!!
            val portfolioName = data.getStringExtra("portfolio_name")!!
            val linkedin = data.getStringExtra("portfolio_linkedin")!!
            val githubLink = data.getStringExtra("portfolio_github")!!
            val description = data.getStringExtra("portfolio_desc")!!
            val portfolioLabel = data.getStringExtra("portfolio_label")!!
            val startDate = data.getStringExtra("start_date")!!
            val endDate = data.getStringExtra("end_date")!!
            val platforms = data.getStringArrayListExtra("platforms") ?: arrayListOf()
            val tools = data.getStringArrayListExtra("tools") ?: arrayListOf()
            val languages = data.getStringArrayListExtra("languages") ?: arrayListOf()
            val roles = data.getStringArrayListExtra("roles") ?: arrayListOf()
            val productTypes = data.getStringArrayListExtra("productTypes") ?: arrayListOf()
            val features = data.getStringArrayListExtra("features") ?: arrayListOf()

            // Build PortfolioItem and add to list
            val item = ApiService.PortfolioItem(
                clientName = clientName,
                portfolioName = portfolioName,
                linkedin = linkedin,
                github = githubLink,
                description = description,
                portfolioLabel = portfolioLabel,
                startDate = startDate,
                endDate = endDate,
                platforms = platforms,
                tools = tools,
                languages = languages,
                roles = roles,
                productTypes = productTypes,
                features = features
            )
            portfolioItems.add(item)

            //Get First Item of Product Type and Platform
            val firstProduct = productTypes.firstOrNull() ?: "-"
            val firstPlatform = platforms.firstOrNull() ?: "-"

            //Count Project Worked Days
            val daysWorked: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+
                val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(item.startDate, fmt)
                val completed = item.endDate.let { LocalDate.parse(it, fmt) }
                completed?.let { ChronoUnit.DAYS.between(start, it).toInt() } ?: 0
            } else {
                // API <26
                val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LegacyLocalDate.parse(item.startDate, fmt)
                val completed = item.endDate.let { LegacyLocalDate.parse(it, fmt) }
                completed?.let { LegacyChronosUnit.DAYS.between(start, it).toInt() } ?: 0
            }

            // Inflate and display item view
            val itemView = layoutInflater.inflate(
                R.layout.portfolio_item_btn_close,
                binding.portfolioContainer,
                false
            )
            itemView.findViewById<TextView>(R.id.tvProject).text = portfolioName
            itemView.findViewById<TextView>(R.id.tvClient).text = clientName
            itemView.findViewById<TextView>(R.id.tvProduct).text = itemView.context.getString(R.string.project_product, firstProduct, firstPlatform)
                itemView.findViewById<TextView>(R.id.tvCompleted).text = itemView.context.getString(R.string.completed_in_d_days, daysWorked)
            // Optionally set other fields if present
            val removeBtn = itemView.findViewById<ImageButton>(R.id.btnRemove)
            removeBtn.setOnClickListener {
                binding.portfolioContainer.removeView(itemView)
                portfolioItems.remove(item)
                buttonSet()
            }
            binding.portfolioContainer.addView(itemView)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTalentApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        getAllCategories()
        setupButtonAction()
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
            btnTalentApply.setOnClickListener {
                talentApply()
            }
            btnAddPortfolio.setOnClickListener {
                val intent =
                    Intent(this@TalentApplyActivity, NewPortfolioActivity::class.java).apply {
                        putExtra(NewPortfolioActivity.STATE, "TalentApply")
                    }
                portfolioLauncher.launch(intent)
            }
        }
    }

    private fun talentApply() {
        textFieldWatcher()
        val github = binding.tilGithubProfile.editText?.text.toString()
        val linkedIn = binding.tilLinkedInProfile.editText?.text.toString()
        val platform = selectedPlatforms
        val tools = selectedTools
        val languages = selectedLanguages
        val roles = selectedRoles
        val productTypes = selectedProductTypes
        val portfolios = portfolioItems

        val request = ApiService.AddTalentRequest(
            github = github,
            linkedIn = linkedIn,
            roles = roles.toList(),
            tools = tools.toList(),
            platforms = platform.toList(),
            productTypes = productTypes.toList(),
            languages = languages.toList(),
            portfolio = portfolios.toList()
        )
        lifecycleScope.launch {
            talentApplyViewModel.addTalent(request)
            addTalentObserver()
        }
    }

    private fun addTalentObserver() {
        talentApplyViewModel.addTalent.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    updateTalentAccess()
                }

                is Results.Error -> {
                    showLoading(false)
                    Log.e("TalentApplyActivity", "Error Add Talent: ${result.error}")
                }
            }
        }
    }

    private fun updateTalentAccess() {
        talentApplyViewModel.updateUserTalentAccess(1)
        talentApplyViewModel.updateUserTalentAccess.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                    Log.e("TalentApplyActivity", "Error Update Talent Access: ${result.error}")
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
            tilLinkedInProfile.editText?.addTextChangedListener(watcher)
            tilGithubProfile.editText?.addTextChangedListener(watcher)
            tilRole.editText?.addTextChangedListener(watcher)
            tilPlatform.editText?.addTextChangedListener(watcher)
            tilProductType.editText?.addTextChangedListener(watcher)
            tilLanguage.editText?.addTextChangedListener(watcher)
            tilTools.editText?.addTextChangedListener(watcher)

        }
    }

    private fun buttonSet() {
        val filled =
            binding.tilLinkedInProfile.editText!!.text.isNotEmpty() &&
                    binding.tilGithubProfile.editText!!.text.isNotEmpty() &&
                    selectedPlatforms.isNotEmpty() &&
                    selectedProductTypes.isNotEmpty() &&
                    selectedLanguages.isNotEmpty() &&
                    selectedTools.isNotEmpty() &&
                    selectedRoles.isNotEmpty() &&
                    portfolioItems.isNotEmpty()

        binding.btnTalentApply.isEnabled = filled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}