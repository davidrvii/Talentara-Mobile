package com.example.talentara.view.ui.authentication.signin

import android.content.Intent
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
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentSignInBinding
import com.example.talentara.view.ui.authentication.AuthenticationActivity
import com.example.talentara.view.ui.authentication.AuthenticationViewModel
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.utils.FactoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private val authViewModel: AuthenticationViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private val preference: UserPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvFooterAction.setOnClickListener {
            (requireActivity() as AuthenticationActivity).switchPage(0)
        }

        binding.btnSignIn.setOnClickListener {
            textFieldWatcher()
            val email = binding.tilEmail.editText!!.text.toString().trim()
            val password  = binding.tilPassword.editText!!.text.toString().trim()

            lifecycleScope.launch {
                try {
                    authViewModel.login(email, password)
                    loginObserver()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(),
                        getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    Log.e("SignInFragment", "Error: ${e.message}")
                    loginFailed()
                }
            }
        }
    }

    private fun loginObserver() {
        authViewModel.login.observe(viewLifecycleOwner) { loginResponseResult ->
            when (loginResponseResult) {
                is Results.Loading -> {
                    showLoading(true)
                }
                is Results.Success -> {
                    showLoading(false)
                    lifecycleScope.launch {
                        preference.saveUser(
                            loginResponseResult.data.loginResult?.userId,
                            loginResponseResult.data.loginResult?.token,
                            loginResponseResult.data.loginResult?.userEmail,
                            loginResponseResult.data.loginResult?.userName
                        )
                    }
                    loginSuccess()
                }
                is Results.Error -> {
                    Toast.makeText(requireContext(),
                        getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    showLoading(false)
                    Log.e("SignInFragment", "Error: ${loginResponseResult.error}")
                    loginFailed()
                }
            }
        }
    }

    private fun loginSuccess() {
        lifecycleScope.launch {
            val fcmToken = preference.getSession().first().fcmToken
            authViewModel.saveFcmToken(fcmToken)
        }
        authViewModel.saveFcmTokenResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {}
                is Results.Success -> {}
                is Results.Error -> Log.e("MainActivity", "Error saving FCM token: ${result.error}")
            }
        }
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loginFailed() {
        Toast.makeText(requireContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
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
        val email = binding.tilEmail.editText!!.text.toString().trim()
        val password  = binding.tilPassword.editText!!.text.toString().trim()

        val isFieldFilled = email.isNotEmpty() && password.isNotEmpty()

        binding.btnSignIn.isEnabled = isFieldFilled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}