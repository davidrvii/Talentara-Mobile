package com.example.talentara.view.ui.project.offer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.talentara.R
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.model.response.project.ProjectOrderItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityProjectOfferBinding
import com.example.talentara.view.ui.portfolio.detail.ItemsAdapter
import com.example.talentara.view.ui.project.add.NewProjectViewModel
import com.example.talentara.view.ui.talent.detail.TalentDetailViewModel
import com.example.talentara.view.ui.timeline.TimelineViewModel
import com.example.talentara.view.utils.FactoryViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectOfferActivity : AppCompatActivity() {

    private val projectOfferViewModel: ProjectOfferViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val talentDetailViewModel: TalentDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val newProjectViewmodel: NewProjectViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val timelineViewModel: TimelineViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val userPref by lazy {
        UserPreference.getInstance(dataStore)
    }
    private lateinit var binding: ActivityProjectOfferBinding
    private val featureAdapter = ItemsAdapter(emptyList())
    private val platformAdapter = ItemsAdapter(emptyList())
    private val roleAdapter = ItemRoleOrderAdapter(emptyList())
    private val toolsAdapter = ItemsAdapter(emptyList())
    private val productTypeAdapter = ItemsAdapter(emptyList())
    private val languageAdapter = ItemsAdapter(emptyList())

    private var projectId: Int = 0
    private var talentProjectDeclined: Int = 0
    private var roleName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectId = intent.getIntExtra(PROJECT_ID, 0)
        roleName = intent.getStringExtra(ROLE_NAME) ?: ""
        getProjectOrder()
        getTalentProjectDeclined()
        setupButtonAction()
    }

    private fun getTalentProjectDeclined() {
        lifecycleScope.launch {
            userPref.getSession()
                .map { it.userId }
                .collect { talentId ->
                    talentDetailViewModel.getTalentDetail(talentId)
                    getTalentProjectDeclinedObserver()
                }
        }
    }

    private fun getTalentProjectDeclinedObserver() {
        talentDetailViewModel.getTalentDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val projectDeclined = result.data.talentDetail?.firstOrNull()?.projectDeclined!!
                    binding.btCancel.text = getString(R.string.decline_d, projectDeclined)
                    talentProjectDeclined = projectDeclined
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun setupButtonAction() {
        with(binding) {
            btYes.setOnClickListener {
                projectOfferViewModel.getProjectOffer(projectId, roleName, 1)
                acceptProjectObserver()
                if (roleName == "Project Manager") {
                    timelineViewModel.updateProjectStatus(projectId, 2)
                    updateProjectStatusObserver()
                }
            }
            if (talentProjectDeclined < 3) {
                btCancel.setOnClickListener {
                    projectOfferViewModel.getProjectOffer(projectId, roleName, 0)
                    declineProjectObserver()
                }
            } else {
                btCancel.visibility = View.GONE
                btCancel.alpha = 0.5f
            }
        }
    }

    private fun acceptProjectObserver() {
        projectOfferViewModel.projectOffer.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    newProjectViewmodel.updateUserIsOnProject(1)
                    talentIsOnProjectObserver()

                    if (talentProjectDeclined > 0) {
                        projectOfferViewModel.updateTalentProjectDeclined(0)
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun updateProjectStatusObserver() {
        timelineViewModel.updateProjectStatus.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }
                is Results.Success -> {
                    showLoading(false)
                }
                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Failed to update project status", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TimelineActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun declineProjectObserver() {
        projectOfferViewModel.projectOffer.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    projectOfferViewModel.updateTalentProjectDeclined(talentProjectDeclined + 1)
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun talentIsOnProjectObserver() {
        newProjectViewmodel.updateUserIsOnProject.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun getProjectOrder() {
        projectOfferViewModel.getProjectOrder(projectId)
        projectOfferViewModel.getProjectOrder.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val projectOrder = result.data.projectOrder?.firstOrNull()
                    projectOrder?.statusId?.let {
                        if (it < 3) {
                            bindProject(projectOrder)
                        } else {
                            Toast.makeText(this@ProjectOfferActivity, "Project has started", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                is Results.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun bindProject(projectOrder: ProjectOrderItem) {
        binding.apply {
            val formattedDeadline = formatDeadline(projectOrder.startDate.toString(),
                projectOrder.endDate.toString()
            )
            val deadline = formattedDeadline
            tvGotOffer.text = getString(R.string.project_offer, roleName)
            tvProjectClient.text = projectOrder.clientName
            tvProjectName.text = projectOrder.projectName
            tvProjectDescription.text = projectOrder.projectDesc
            tvProjectGithub.text = projectOrder.projectGithub ?: "-"
            tvProjectMeetLink.text = projectOrder.meetLink ?: "-"
            tvProjectPeriode.text = deadline
        }
        binding.apply {
            val rawFeatures = projectOrder.features ?: ""
            val featuresList = rawFeatures
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            featureAdapter.updateData(featuresList)
        }
        binding.apply {
            val rawPlatforms = projectOrder.platforms ?: ""
            val platformsList = rawPlatforms
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            platformAdapter.updateData(platformsList)
        }
        binding.apply {
            val rawTools = projectOrder.tools ?: ""
            val toolsList = rawTools
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            toolsAdapter.updateData(toolsList)
        }
        binding.apply {
            val rawProductTypes = projectOrder.productTypes ?: ""
            val productTypesList = rawProductTypes
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            productTypeAdapter.updateData(productTypesList)
        }
        binding.apply {
            val rawLanguages = projectOrder.languages ?: ""
            val languagesList = rawLanguages
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            languageAdapter.updateData(languagesList)
        }
        binding.rvRole.apply {
            layoutManager = LinearLayoutManager(
                this@ProjectOfferActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = roleAdapter
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PROJECT_ID = "project_id"
        const val ROLE_NAME = "role_name"
    }
}