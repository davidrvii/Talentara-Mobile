package com.example.talentara.view.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.CustomProjectManagerDialogBinding
import com.example.talentara.databinding.FragmentProfileBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioActivity
import com.example.talentara.view.ui.profile.edit.EditProfileActivity
import com.example.talentara.view.ui.profile.edit.EditProfileViewModel
import com.example.talentara.view.ui.profile.talent.TalentProfileFragment
import com.example.talentara.view.ui.profile.user.UserProfileFragment
import com.example.talentara.view.ui.talent.apply.TalentApplyActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(requireActivity())
    }
    private val editProfileViewModel: EditProfileViewModel by viewModels {
        FactoryViewModel.getInstance(requireActivity())
    }
    private lateinit var bindingProjectManagerDialog: CustomProjectManagerDialogBinding
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var hasTalentAccess = false
    private var isTabInitialized = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserDetail()
        updateProfile()

        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getUserDetail()
            }
        }
        binding.ivEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }

    }

    private fun getUserDetail() {
        viewModel.getUserDetail()
        viewModel.getUserDetail.removeObservers(viewLifecycleOwner)
        viewModel.getUserDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val user = result.data.userDetail?.firstOrNull()
                    binding.tvUsername.text = user?.userName
                    binding.tvLinkedin.text = user?.linkedin ?: "Not provided"
                    binding.tvGithub.text = user?.github ?: "Not provided"
                    binding.tvGmail.text = user?.userEmail
                    binding.ivUserImage.load(user?.userImage) {
                        placeholder(R.drawable.blank_avatar)
                        error(R.drawable.blank_avatar)
                    }
                    if (user?.talentAccess == 0) {
                        binding.btnTalentApply.visibility = View.VISIBLE
                        binding.btnTalentApply.load(R.drawable.ic_talent_apply) {
                            placeholder(R.drawable.blank_avatar)
                            error(R.drawable.blank_avatar)
                        }
                        binding.btnTalentApply.setOnClickListener {
                            val intent = Intent(context, TalentApplyActivity::class.java).apply {
                                putExtra(NewPortfolioActivity.STATE, "TalentApply")
                            }
                            startActivity(intent)
                        }
                    } else if (user?.talentAccess == 1) {
                        viewModel.getTalentDetail()
                        viewModel.getTalentDetail.observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Results.Loading -> showLoading(true)
                                is Results.Success -> {
                                    showLoading(false)
                                    val talent = result.data.talentDetail?.firstOrNull()
                                    if (talent?.isProjectManager == 0) {
                                        binding.btnTalentApply.visibility = View.VISIBLE
                                        binding.btnTalentApply.load(R.drawable.ic_project_manager_apply) {
                                            placeholder(R.drawable.blank_avatar)
                                            error(R.drawable.blank_avatar)
                                        }
                                        binding.btnTalentApply.setOnClickListener {
                                            showCustomProjectManagerDialog()
                                        }
                                    } else {
                                        binding.btnTalentApply.visibility = View.GONE
                                    }
                                }
                                is Results.Error -> showLoading(false)
                            }
                        }
                    } else {
                        binding.btnTalentApply.visibility = View.GONE
                    }
                    hasTalentAccess = user?.talentAccess == 1
                    if (!isTabInitialized) {
                        setupViewPagerWithTabs(hasTalentAccess)
                        isTabInitialized = true
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_to_get_user_information), Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ProfileFragment", "Error: ${result.error}")
                }
            }
        }
    }

    private fun showCustomProjectManagerDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingProjectManagerDialog = CustomProjectManagerDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingProjectManagerDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingProjectManagerDialog.root.findViewById<CardView>(R.id.ApplyProjectManager)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingProjectManagerDialog.btYes.setOnClickListener {
            dialog.dismiss()
            viewModel.updateTalentIsProjectManager(1)
            viewModel.updateTalentIsProjectManager.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        getUserDetail()
                        showLoading(false)
                    }
                    is Results.Error -> showLoading(false)
                }
            }
        }
        bindingProjectManagerDialog.btCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupViewPagerWithTabs(hasTalentAccess: Boolean) {
        val adapter = object : FragmentStateAdapter(this) {
            private val pages = listOf(
                UserProfileFragment(),
                TalentProfileFragment()
            )

            override fun getItemCount() = pages.size
            override fun createFragment(position: Int) = pages[position]
        }
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0)
                getString(R.string.user_profile)
            else
                getString(R.string.talent_profile)
        }.attach()

        // Custom bold for selected tab, normal for others
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = (tab.view as ViewGroup).getChildAt(1) as? TextView
                tabView?.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = (tab.view as ViewGroup).getChildAt(1) as? TextView
                tabView?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Apply bold to first tab on start
        binding.tabLayout.post {
            val firstTab = binding.tabLayout.getTabAt(0)
            val firstTabView = (firstTab?.view as ViewGroup).getChildAt(1) as? TextView
            firstTabView?.setTypeface(null, Typeface.BOLD)
        }

        // Disable and grey-out Talent tab if no access
        val tabStrip = binding.tabLayout.getChildAt(0) as ViewGroup
        val talentTabView = tabStrip.getChildAt(1)
        talentTabView.isEnabled = hasTalentAccess
        talentTabView.alpha = if (hasTalentAccess) 1f else 0.5f

        // Disabled horizontal swipe gesture if no access
        if (!hasTalentAccess) {
            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 1) {
                        binding.viewPager.currentItem = 0
                        Toast.makeText(
                            binding.root.context,
                            "You don't have access to Talent Profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        binding.viewPager.currentItem = 0
    }

    private fun updateProfile() {
        editProfileViewModel.isDataDetailUpdate.observe(viewLifecycleOwner) {
            if (it == true) {
                editProfileViewModel.setDataDetailUpdate(false)
                getUserDetail()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.isDataDetailUpdate.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.setDataDetailUpdate(false)
                getUserDetail()
            }
        }
    }
}