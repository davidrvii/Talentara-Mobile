package com.example.talentara.view.ui.authentication.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentSignUpBinding
import com.example.talentara.view.ui.authentication.AuthenticationActivity
import com.example.talentara.view.ui.authentication.AuthenticationViewModel
import com.example.talentara.view.utils.FactoryViewModel
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private val authViewModel: AuthenticationViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvFooterAction.setOnClickListener {
            (requireActivity() as AuthenticationActivity).switchPage(0)
        }

        binding.btnSignUp.setOnClickListener {
            textFieldWatcher()
            val username = binding.tilUsername.editText.toString().trim()
            val email = binding.tilEmail.editText.toString().trim()
            val pass = binding.tilPassword.editText.toString().trim()

            lifecycleScope.launch {
                try {
                    authViewModel.register(username, email, pass)
                    registerObserver()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_failed), Toast.LENGTH_SHORT
                    ).show()
                    Log.d("Register", e.message.toString())
                    registerFailed()
                }
            }
        }
    }

    private fun registerObserver() {
        authViewModel.register.observe(viewLifecycleOwner) { registerResponseResult ->
            when (registerResponseResult) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    registerSuccess()
                }

                is Results.Error -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_failed), Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    registerFailed()
                }
            }
        }
    }

    private fun registerSuccess() {
        Toast.makeText(requireContext(), getString(R.string.register_success), Toast.LENGTH_SHORT)
            .show()
        (requireActivity() as AuthenticationActivity).switchPage(0)
    }

    private fun registerFailed() {
        Toast.makeText(requireContext(), getString(R.string.register_failed), Toast.LENGTH_SHORT)
            .show()
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
            tilPassword.editText?.addTextChangedListener(watcher)
        }
    }

    private fun buttonSet() {
        val email = binding.tilEmail.editText!!.text
        val password = binding.tilPassword.editText!!.text

        val isFieldFilled = email.isNotEmpty() && password.isNotEmpty()

        binding.btnSignUp.isEnabled = isFieldFilled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}