package com.example.talentara.view.ui.profile.edit

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.response.categories.GetAllCategoriesResponse
import com.example.talentara.data.model.result.Results
import com.example.talentara.data.remote.ApiService
import com.example.talentara.databinding.ActivityEditProfileBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioViewModel
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : AppCompatActivity() {

    private val selectedPlatforms = mutableSetOf<String>()
    private val selectedProductTypes = mutableSetOf<String>()
    private val selectedRoles = mutableSetOf<String>()
    private val selectedLanguages = mutableSetOf<String>()
    private val selectedTools = mutableSetOf<String>()

    private val talentProfileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newPortfolioViewModel: NewPortfolioViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val editProfileViewModel: EditProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityEditProfileBinding
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ivUserImage.setImageURI(it)
                chosenImageUri = it
            }
        }
    private var chosenImageUri: Uri? = null
    private var talentAccess: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        profileViewModel.getUserDetail()
        textFieldWatcher()
        getUserInfoObserver()
        setupButtonAction()
    }

    private fun setupButtonAction() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }

            userImage.setOnClickListener {
                pickImage.launch("image/*")
            }

            btnSaveChange.setOnClickListener {
                submitUpdate()
            }
        }
    }

    private fun submitUpdate() {
        val username = binding.tilUsername.editText?.text.toString()
        val email = binding.tilEmail.editText?.text.toString()
        val github = binding.tilGithub.editText?.text.toString()
        val linkedIn = binding.tilLinkedIn.editText?.text.toString()
        val userImage: MultipartBody.Part? = chosenImageUri?.let { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                bytes?.let {
                    val requestBody = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("user_image", "avatar.jpg", requestBody)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        if (talentAccess == 1) {
            val request = ApiService.UpdateTalentRequest(
                selectedRoles.toList(),
                selectedLanguages.toList(),
                selectedTools.toList(),
                selectedProductTypes.toList(),
                selectedPlatforms.toList()
            )

            editProfileViewModel.updateTalent(request)
            updateTalentObserver()

            editProfileViewModel.updateUser(username, email, github, linkedIn, userImage)
            updateUserObserver()
        } else {
            editProfileViewModel.updateUser(username, email, github, linkedIn, userImage)
            updateUserObserver()
        }
    }

    private fun updateUserObserver() {
        editProfileViewModel.updateUser.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    setResult(RESULT_OK)
                    Toast.makeText(
                        this,
                        getString(R.string.update_user_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_update_user_information), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateTalentObserver() {
        editProfileViewModel.updateTalent.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    Log.d("NewPortfolioActivity", "Success: ${result.data}")
                    showLoading(false)
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun getUserInfoObserver() {
        profileViewModel.getUserDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    val user = result.data.userDetail?.firstOrNull()
                    binding.apply {
                        tilUsername.editText?.setText(user?.userName)
                        tilEmail.editText?.setText(user?.userEmail)
                        tilGithub.editText?.setText(user?.github)
                        tilLinkedIn.editText?.setText(user?.linkedin)
                        ivUserImage.load(user?.userImage) {
                            placeholder(R.drawable.blank_avatar)
                            error(R.drawable.blank_avatar)
                        }
                        talentAccess = user?.talentAccess ?: 0
                        if (user?.talentAccess == 1) {
                            editTalent.visibility = View.VISIBLE
                            getTalentCategories()
                            getAllCategories()
                        } else {
                            editTalent.visibility = View.GONE
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_user_information), Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun getTalentCategories() {
        talentProfileViewModel.getTalentDetail()
        talentProfileViewModel.getTalentDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    val talent = result.data.talentDetail?.firstOrNull() ?: return@observe

                    bindChipGroup(talent.platforms, selectedPlatforms, binding.chipGroupPlatform)
                    bindChipGroup(
                        talent.productTypes,
                        selectedProductTypes,
                        binding.chipGroupProductType
                    )
                    bindChipGroup(talent.roles, selectedRoles, binding.chipGroupRole)
                    bindChipGroup(talent.languages, selectedLanguages, binding.chipGroupLanguage)
                    bindChipGroup(talent.tools, selectedTools, binding.chipGroupTools)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mengambil data talent", Toast.LENGTH_SHORT).show()
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

        raw?.split("|")?.map { it.trim() }?.filter { it.isNotEmpty() }?.forEach { value ->
            selectedSet.add(value)
            val chip = Chip(this).apply {
                text = value
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    chipGroup.removeView(this)
                    selectedSet.remove(value)
                }
            }
            chipGroup.addView(chip)
        }
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

    private fun textFieldWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonSet()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        with(binding) {
            tilEmail.editText?.addTextChangedListener(watcher)
            tilUsername.editText?.addTextChangedListener(watcher)
            tilGithub.editText?.addTextChangedListener(watcher)
            tilLinkedIn.editText?.addTextChangedListener(watcher)

            if (talentAccess == 1) {
                tilPlatform.editText?.addTextChangedListener(watcher)
                tilProductType.editText?.addTextChangedListener(watcher)
                tilLanguage.editText?.addTextChangedListener(watcher)
                tilTools.editText?.addTextChangedListener(watcher)
                tilRole.editText?.addTextChangedListener(watcher)
            }
        }
    }

    private fun buttonSet() {
        val email = binding.tilEmail.editText!!.text
        val username = binding.tilUsername.editText!!.text
        val github = binding.tilGithub.editText!!.text
        val linkedIn = binding.tilLinkedIn.editText!!.text

        val isFieldFilled = email.isNotEmpty() &&
                            username.isNotEmpty() &&
                            github.isNotEmpty() &&
                            linkedIn.isNotEmpty()

                if (talentAccess == 1) {
                            selectedPlatforms.isNotEmpty() &&
                            selectedProductTypes.isNotEmpty() &&
                            selectedLanguages.isNotEmpty() &&
                            selectedTools.isNotEmpty() &&
                            selectedRoles.isNotEmpty()
                }

        binding.btnSaveChange.isEnabled = isFieldFilled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
