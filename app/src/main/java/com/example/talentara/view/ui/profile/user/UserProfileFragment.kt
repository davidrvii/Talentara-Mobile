package com.example.talentara.view.ui.profile.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.talentara.databinding.FragmentUserProfileBinding
import com.example.talentara.view.ui.authentication.AuthenticationActivity
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.utils.FactoryViewModel

class UserProfileFragment : Fragment() {

    private val userProfileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionButton()
    }

    private fun setupActionButton() {
        binding.cvLogout.setOnClickListener {
            userProfileViewModel.logout()
            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}