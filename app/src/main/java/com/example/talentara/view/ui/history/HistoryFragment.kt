package com.example.talentara.view.ui.history

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.FragmentHistoryBinding
import com.example.talentara.view.ui.home.HomeViewModel
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import com.example.talentara.view.utils.FactoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class HistoryFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        FactoryViewModel.getInstance(requireContext())
    }
    private lateinit var projectHistoryAdapter: ProjectHistoryAdapter
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrentProject()
        setupProjectHistoryList()
    }

    private fun getCurrentProject() {
        homeViewModel.getCurrentProject()
        homeViewModel.getCurrentProject.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                    binding.cvNoProject.visibility = View.GONE
                }

                is Results.Success -> {
                    showLoading(false)

                    val currentProject = result.data.currentProject?.firstOrNull()

                    if (currentProject == null) {
                        binding.cvNoProject.visibility = View.VISIBLE
                        binding.tvOngoing.visibility = View.GONE
                        binding.cvOngoingProject.visibility = View.GONE
                        binding.historyDivider.visibility = View.GONE
                    } else {
                        binding.tvOngoing.visibility = View.VISIBLE
                        binding.cvOngoingProject.visibility =  View.VISIBLE
                        binding.historyDivider.visibility =  View.VISIBLE
                        binding.cvNoProject.visibility = View.GONE

                        //Get First Item of Product Type and Platform
                        val firstProduct =
                            currentProject.productTypes?.split("|")?.firstOrNull() ?: "-"
                        val firstPlatform =
                            currentProject.platforms?.split("|")?.firstOrNull() ?: "-"

                        //Count Project Worked Days
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
                            val projectName = currentProject.projectName?.let {
                                val words = it.split(" ")
                                if (words.size <= 2) {
                                    it
                                } else {
                                    val firstLine = words.take(2).joinToString(" ")
                                    val secondLine = words.drop(2).joinToString(" ")
                                    "$firstLine\n$secondLine"
                                }
                            } ?: "-"

                            binding.tvStatus.text = currentProject.statusName?.replace(" ", "\n")
                            tvProject.text = projectName
                            tvClient.text = currentProject.clientName
                            tvProduct.text =
                                getString(R.string.project_product, firstProduct, firstPlatform)
                            tvRemaining.text = getString(R.string.completed_in_d_days, daysRemaining)
                        }

                        binding.cvOngoingProject.setOnClickListener {
                            val intent = Intent(context, ProjectDetailActivity::class.java).apply {
                                putExtra(ProjectDetailActivity.PROJECT_ID, currentProject.projectId)
                            }
                            startActivity(intent)
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                    binding.cvNoProject.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.failed_to_get_current_project),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("HistoryFragment", "Error getting current project: ${result.error}")
                }
            }
        }
    }

    private fun setupProjectHistoryList() {
        historyViewModel.getProjectHistory()
        historyViewModel.getProjectHistory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                    binding.cvNoProject.visibility = View.VISIBLE
                }

                is Results.Success -> {
                    showLoading(false)
                    binding.cvNoProject.visibility = View.GONE
                    projectHistoryAdapter.updateData(
                        result.data.historyProject?.filterNotNull() ?: emptyList()
                    )
                }

                is Results.Error -> {
                    showLoading(false)
                    if (result.error.contains("HTTP 404")) {
                        binding.cvNoProject.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.there_is_no_project),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.cvNoProject.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_to_get_project_history),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("HistoryFragment", "Error getting project history: ${result.error}")
                    }

                }
            }
        }
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        Log.d("HistoryFragment", "Setup RecyclerView")
        projectHistoryAdapter = ProjectHistoryAdapter(emptyList())
        binding.rvHistory.apply {
            adapter = projectHistoryAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}