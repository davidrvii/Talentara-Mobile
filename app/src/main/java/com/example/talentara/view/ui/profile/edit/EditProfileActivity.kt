package com.example.talentara.view.ui.profile.edit

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityEditProfileBinding
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.utils.FactoryViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        profileViewModel.getUserDetail()
        getUserInfoObserver()
        setupButtonAction()
    }

    private fun setupButtonAction() {
        with(binding) {
            btnBack.setOnClickListener {
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
        val userImage = chosenImageUri?.let { uri ->
            val bytes = contentResolver.openInputStream(uri)!!.readBytes()
            val req = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("user_image", "avatar.jpg", req)
        }

        if (false) {
            Toast.makeText(this, getString(R.string.nothing_to_change), Toast.LENGTH_SHORT).show()
            return
        }

        editProfileViewModel.updateUser(username, email, github, linkedIn, userImage)
        updateUserObserver()
    }

    private fun updateUserObserver() {
        editProfileViewModel.updateUser.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
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

    private fun getUserInfoObserver() {
        profileViewModel.getUserDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    val user = result.data.userDetail
                    binding.apply {
                        tilUsername.editText?.setText(user?.userName)
                        tilEmail.editText?.setText(user?.userEmail)
                        tilGithub.editText?.setText(user?.github)
                        tilLinkedIn.editText?.setText(user?.linkedin)
                        binding.ivUserImage.setImageURI(
                            user?.userImage
                                ?.takeIf { it.isNotBlank() }
                                ?.toUri()
                        )
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
