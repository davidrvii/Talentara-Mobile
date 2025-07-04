package com.example.talentara.view.ui.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentHomeBinding
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.ui.timeline.TimelineActivity
import com.example.talentara.view.utils.FactoryViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class HomeFragment : Fragment() {

    private val homeViewmodel: HomeViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
                    val user = result.data.usersBasic?.firstOrNull()
                    binding.tvHeading.text = getString(R.string.home_heading, user?.userName)
                    binding.ivUserImage.load(user?.userImage) {
                        placeholder(R.drawable.blank_avatar)
                        error(R.drawable.blank_avatar)
                    }
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
                    binding.cvCurrentProject.visibility = View.GONE
                    binding.cvNoCurrentProject.visibility = View.GONE
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val currentProject = result.data.currentProject?.firstOrNull()
                    Log.d("HomeFragment", "Current Project: $currentProject")
                    if (currentProject == null) {
                        binding.cvCurrentProject.visibility = View.GONE
                        binding.cvNoCurrentProject.visibility = View.VISIBLE
                    } else {
                        binding.cvCurrentProject.visibility = View.VISIBLE
                        binding.cvNoCurrentProject.visibility = View.GONE
                        getCurrentTimeline(currentProject.projectId?.toInt() ?: 0)
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
                                    ?.substring(0, 10)
                                    ?.let { LocalDate.parse(it, fmt) }
                                end?.let { ChronoUnit.DAYS.between(today, it).toInt() } ?: 0
                            } else {
                                // API <26
                                val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                                val today = LegacyLocalDate.now()
                                val end = currentProject.endDate
                                    ?.substring(0, 10)
                                    ?.let { LegacyLocalDate.parse(it, fmt) }
                                end?.let { LegacyChronosUnit.DAYS.between(today, it).toInt() } ?: 0
                            }

                        binding.apply {
                            binding.tvStatus.text = currentProject.statusName?.replace(" ", "\n")
                            tvProject.text = currentProject.projectName
                            tvClient.text = currentProject.clientName
                            tvProduct.text =
                                getString(R.string.project_product, firstProduct, firstPlatform)
                            tvRemaining.text = getString(R.string.remaining_days, daysRemaining)
                        }

                        binding.cvCurrentProject.setOnClickListener {
                            val intent = Intent(context, ProjectDetailActivity::class.java).apply {
                                putExtra(ProjectDetailActivity.PROJECT_ID, currentProject.projectId)
                            }
                            startActivity(intent)
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    binding.cvCurrentProject.visibility = View.GONE
                    binding.cvNoCurrentProject.visibility = View.VISIBLE
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

    private fun formatDeadline(start: String, end: String): String {
        // Input and output patterns
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd MMM yyyy"
        val locale = Locale.getDefault()

        return try {
            val sdfIn = SimpleDateFormat(inputPattern, Locale.US)
            val sdfOut = SimpleDateFormat(outputPattern, locale)

            val sDate = sdfIn.parse(start)
            val eDate = sdfIn.parse(end)

            if (sDate != null && eDate != null) {
                "${sdfOut.format(sDate)} - ${sdfOut.format(eDate)}"
            } else {
                // fallback to raw strings
                "$start - $end"
            }
        } catch (e: Exception) {
            // If parsing fails, show raw
            e.printStackTrace()
            "$start - $end"
        }
    }

    private fun getCurrentTimeline(projectId: Int) {
        if (projectId != 0) {
            homeViewmodel.getCurrentTimeline(projectId)
            homeViewmodel.getCurrentTimeline.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Results.Loading -> {
                        binding.Timeline.visibility = View.GONE
                        binding.cvNoCurrentTimelineYesProject.visibility = View.GONE
                        binding.cvNoCurrentTimelineNoProject.visibility = View.GONE
                        showLoading(true)
                    }

                    is Results.Success -> {
                        showLoading(false)
                        val currentTimeline = result.data.currentTimeline?.firstOrNull()
                        Log.d("HomeFragment", "Current Timeline: $currentTimeline")

                        if (currentTimeline == null) {
                            binding.Timeline.visibility = View.GONE
                            binding.cvNoCurrentTimelineYesProject.visibility = View.VISIBLE
                            binding.cvNoCurrentTimelineNoProject.visibility = View.GONE
                        } else {
                            binding.Timeline.visibility = View.VISIBLE
                            binding.cvNoCurrentTimelineYesProject.visibility = View.GONE
                            binding.cvNoCurrentTimelineNoProject.visibility = View.GONE

                            val formattedDeadline = formatDeadline(currentTimeline.startDate.toString(),
                                currentTimeline.endDate.toString()
                            )
                            val deadline = formattedDeadline

                            //Count Timeline Remaining Days
                            val daysRemaining: Int =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // API 26+
                                    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val today = LocalDate.now()
                                    val end = currentTimeline.endDate
                                        ?.substring(0, 10)
                                        ?.let { LocalDate.parse(it, fmt) }
                                    end?.let { ChronoUnit.DAYS.between(today, it).toInt() } ?: 0
                                } else {
                                    // API <26
                                    val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val today = LegacyLocalDate.now()
                                    val end = currentTimeline.endDate
                                        ?.substring(0, 10)
                                        ?.let { LegacyLocalDate.parse(it, fmt) }
                                    end?.let { LegacyChronosUnit.DAYS.between(today, it).toInt() } ?: 0
                                }

                            binding.tvPhase.text = currentTimeline.projectPhase
                            binding.tvEvidence.text = currentTimeline.evidance
                            binding.tvDeadline.text = getString(R.string.phase_deadline, deadline)
                            binding.tvComplete.text = getString(R.string.remaining_days, daysRemaining)
                            binding.tvEvidence.text = getString(R.string.evidence_s, currentTimeline.evidance ?: "-")

                            binding.Timeline.setOnClickListener {
                                val intent = Intent(context, TimelineActivity::class.java).apply {
                                    putExtra(TimelineActivity.PROJECT_ID, projectId)
                                }
                                startActivity(intent)
                            }
                        }
                    }

                    is Results.Error -> {
                        showLoading(false)
                        if (result.error.contains("HTTP 404")) {
                            binding.Timeline.visibility = View.GONE
                            binding.cvNoCurrentTimelineYesProject.visibility = View.VISIBLE
                            binding.cvNoCurrentTimelineNoProject.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "No timeline yet for this project",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
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
        } else {
            Toast.makeText(requireContext(), "Project ID is null", Toast.LENGTH_SHORT).show()
            binding.Timeline.visibility = View.GONE
            binding.cvNoCurrentTimelineYesProject.visibility = View.GONE
            binding.cvNoCurrentTimelineNoProject.visibility = View.VISIBLE
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