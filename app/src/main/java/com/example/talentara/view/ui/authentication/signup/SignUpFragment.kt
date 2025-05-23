package com.example.talentara.view.ui.authentication.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.talentara.databinding.FragmentSignUpBinding
import com.example.talentara.view.ui.authentication.AuthenticationActivity
import com.example.talentara.view.ui.main.MainActivity

class SignUpFragment : Fragment() {

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
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

            /*val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Email & password wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(),
                    "Mendaftar: $email", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
}