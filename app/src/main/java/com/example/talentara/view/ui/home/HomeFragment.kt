package com.example.talentara.view.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentHomeBinding
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.timeline.TimelineActivity
import com.example.talentara.view.utils.FactoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class HomeFragment : Fragment() {

    private val homeViewmodel: HomeViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var projectId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserBasic()
        getCurrentProject()
        getCurrentTimeline()
    }

    private fun getUserBasic() {
        homeViewmodel.getUserBasic()
        homeViewmodel.getUserBasic.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val user = result.data.usersBasic
                    binding.tvHeading.text = getString(R.string.home_heading, user?.userName)
                    Glide.with(this)
                        .load(user?.userImage)
                        .placeholder(R.drawable.blank_avatar)
                        .error(R.drawable.blank_avatar)
                        .into(binding.ivUserImage)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Failed to get User Data", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("HomeFragment", "Error getting user basic: ${result.error}")
                }
            }
        }
    }

    private fun getCurrentProject() {
        homeViewmodel.getCurrentProject()
        homeViewmodel.getCurrentProject.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val currentProject = result.data.currentProject
                    currentProject?.projectId?.let { projectId = it.toInt() }

                    if (currentProject == null) {
                        binding.tvCurrentProject.isVisible = false
                        binding.cvCurrentProject.isVisible = false
                        binding.cvCurrentTimeline.isVisible = false
                    } else {
                        binding.tvCurrentProject.isVisible = true
                        binding.cvCurrentProject.isVisible = true
                        binding.cvCurrentTimeline.isVisible = true

                        //Get First Item of Product Type and Platform
                        val firstProduct =
                            currentProject.productTypes?.split("|")?.firstOrNull() ?: "-"
                        val firstPlatform =
                            currentProject.platforms?.split("|")?.firstOrNull() ?: "-"

                        //Count Project Remaining Days
                        val daysRemaining: Int =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // API 26+
                                val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val today = LocalDate.now()
                                val end = currentProject.endDate
                                    ?.let { LocalDate.parse(it, fmt) }
                                end?.let { ChronoUnit.DAYS.between(today, it).toInt() } ?: 0
                            } else {
                                // API <26
                                val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val today = LegacyLocalDate.now()
                                val end =
                                    currentProject.endDate?.let { LegacyLocalDate.parse(it, fmt) }
                                end?.let { LegacyChronosUnit.DAYS.between(today, it).toInt() } ?: 0
                            }

                        binding.apply {
                            tvStatus.text = currentProject.statusName
                            tvProject.text = currentProject.projectName
                            tvClient.text = currentProject.clientName
                            tvProduct.text =
                                getString(R.string.project_product, firstProduct, firstPlatform)
                            tvRemaining.text = getString(R.string.remaining_days, daysRemaining)
                        }

                        binding.root.setOnClickListener {
                            val intent = Intent(context, ProjectDetailActivity::class.java).apply {
                                putExtra(ProjectDetailActivity.PROJECT_ID, currentProject.projectId)
                            }
                            startActivity(intent)
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Failed to get Current Project",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("HomeFragment", "Error getting current project: ${result.error}")
                }
            }
        }
    }

    private fun getCurrentTimeline() {
        homeViewmodel.getCurrentTimeline(projectId)
        homeViewmodel.getCurrentTimeline.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val currentTimeline = result.data.currentTimeline

                    //Count Timeline Worked Days
                    val daysWorked: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // API 26+
                        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val start = LocalDate.parse(currentTimeline?.startDate, fmt)
                        val completed = currentTimeline?.completedDate?.let { LocalDate.parse(it, fmt) }
                        completed?.let { ChronoUnit.DAYS.between(start, it).toInt() } ?: 0
                    } else {
                        // API <26
                        val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val start = LegacyLocalDate.parse(currentTimeline?.startDate, fmt)
                        val completed = currentTimeline?.completedDate?.let { LegacyLocalDate.parse(it, fmt) }
                        completed?.let { LegacyChronosUnit.DAYS.between(start, it).toInt() } ?: 0
                    }

                    binding.tvPhase.text = currentTimeline?.projectPhase
                    binding.tvEvidence.text = currentTimeline?.evidance
                    binding.tvDeadline.text = currentTimeline?.endDate
                    if (currentTimeline?.completedDate != null){
                        binding.tvComplete.text = getString(R.string.completed_in_d_days, daysWorked)
                    } else {
                        binding.tvComplete.text = getString(R.string.project_is_on_progress)
                    }

                    binding.cvCurrentTimeline.setOnClickListener {
                        val intent = Intent(context, TimelineActivity::class.java).apply {
                            putExtra(TimelineActivity.PROJECT_ID, projectId)
                        }
                        startActivity(intent)
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Failed to get Current Timeline",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("HomeFragment", "Error getting current timeline: ${result.error}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}