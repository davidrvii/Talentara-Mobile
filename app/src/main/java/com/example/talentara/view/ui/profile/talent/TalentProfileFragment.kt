package com.example.talentara.view.ui.profile.talent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.talent.TalentDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentTalentProfileBinding
import com.example.talentara.view.ui.portfolio.detail.ItemsAdapter
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class TalentProfileFragment : Fragment() {

    private val talentProfileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(requireActivity())
    }
    private var _binding: FragmentTalentProfileBinding? = null
    private val binding get() = _binding!!

    private val platformAdapter    = ItemsAdapter(emptyList())
    private val roleAdapter        = ItemsAdapter(emptyList())
    private val toolsAdapter       = ItemsAdapter(emptyList())
    private val productTypeAdapter = ItemsAdapter(emptyList())
    private val languageAdapter    = ItemsAdapter(emptyList())

    private var talentAvailability: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTalentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViews()
        getTalentDetail()
        setupActionButton()
    }

    private fun getTalentDetail() {
        talentProfileViewModel.getTalentDetail()
        talentProfileViewModel.getTalentDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val talent = result.data.talentDetail
                    talentAvailability = talent?.availability
                    binding.ivAvailibility.setImageResource(if (talent?.availability == 1) R.color.green else R.color.red)
                    binding.tvProjectCount.text = talent?.projectDone.toString()
                    binding.tvRatingCount.text = talent?.talentAvgRating.toString()
                    bindProfile(talent!!)

                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(),
                        getString(R.string.failed_to_get_talent_information), Toast.LENGTH_SHORT).show()
                    Log.e("TalentProfileFragment", "Error: ${result.error}")
                }
            }
        }
    }

    private fun bindProfile(talent: TalentDetailItem) {
        binding.apply {
            val rawPlatforms = talent.platforms ?: ""
            val platformsList = rawPlatforms
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            platformAdapter.updateData(platformsList)
        }
        binding.apply {
            val rawRoles = talent.roles ?: ""
            val rolesList = rawRoles
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            roleAdapter.updateData(rolesList)
        }
        binding.apply {
            val rawTools = talent.tools ?: ""
            val toolsList = rawTools
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            toolsAdapter.updateData(toolsList)
        }
        binding.apply {
            val rawProductTypes = talent.productTypes ?: ""
            val productTypesList = rawProductTypes
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            productTypeAdapter.updateData(productTypesList)
        }
        binding.apply {
            val rawLanguages = talent.languages ?: ""
            val languagesList = rawLanguages
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            languageAdapter.updateData(languagesList)
        }
    }

    private fun initRecyclerViews() {
        fun RecyclerView.setupFlex(adapter: ItemsAdapter) {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap      = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            this.adapter = adapter
        }

        binding.rvPlatform.setupFlex(platformAdapter)
        binding.rvRole.setupFlex(roleAdapter)
        binding.rvTools.setupFlex(toolsAdapter)
        binding.rvProductType.setupFlex(productTypeAdapter)
        binding.rvLanguage.setupFlex(languageAdapter)
    }

    private fun setupActionButton() {
        binding.cvAvailability.setOnClickListener {
            if (talentAvailability == 1) {
                talentAvailability = 0
                talentProfileViewModel.updateTalentAvailability(false)
            } else {
                talentAvailability = 1
                talentProfileViewModel.updateTalentAvailability(true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}