package com.example.talentara.view.ui.profile.user

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.talentara.R
import com.example.talentara.databinding.CustomLogoutDialogBinding
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
    private lateinit var bindingDialog: CustomLogoutDialogBinding

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
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingDialog = CustomLogoutDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingDialog.root.findViewById<CardView>(R.id.LogoutCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingDialog.btYesLogout.setOnClickListener {
            dialog.dismiss()
            userProfileViewModel.logout()
            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        bindingDialog.btCancelLogout.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}