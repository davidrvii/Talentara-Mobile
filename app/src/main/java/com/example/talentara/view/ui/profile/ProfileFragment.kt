package com.example.talentara.view.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentProfileBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioActivity
import com.example.talentara.view.ui.profile.talent.TalentProfileFragment
import com.example.talentara.view.ui.profile.user.UserProfileFragment
import com.example.talentara.view.ui.talent.apply.TalentApplyActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(requireActivity())
    }
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var hasTalentAccess = false

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
    }

    private fun getUserDetail() {
        viewModel.getUserDetail()
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
                        binding.btnTalentApply.load(R.drawable.ic_project_manager_apply) {
                            placeholder(R.drawable.blank_avatar)
                            error(R.drawable.blank_avatar)
                        }
                    } else {
                        binding.btnTalentApply.visibility = View.GONE
                    }
                    hasTalentAccess = user?.talentAccess == 1
                    setupViewPagerWithTabs(hasTalentAccess)
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

        // Disable and grey-out Talent tab if no access
        val tabStrip = binding.tabLayout.getChildAt(0) as ViewGroup
        val talentTabView = tabStrip.getChildAt(1)
        talentTabView.isEnabled = hasTalentAccess
        talentTabView.alpha = if (hasTalentAccess) 1f else 0.5f

        // Disabled horizontal swipe gesture if no access
        if (!hasTalentAccess) {
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 1) {
                        binding.viewPager.currentItem = 0
                        Toast.makeText(binding.root.context, "You don't have access to Talent Profile", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        binding.viewPager.currentItem = 0
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}