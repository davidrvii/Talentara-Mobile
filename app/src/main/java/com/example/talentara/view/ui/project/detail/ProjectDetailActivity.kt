package com.example.talentara.view.ui.project.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.project.ProjectDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityProjectDetailBinding
import com.example.talentara.view.ui.portfolio.detail.ItemsAdapter
import com.example.talentara.view.ui.talent.detail.TalentDetailActivity
import com.example.talentara.view.ui.timeline.TimelineActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectDetailActivity : AppCompatActivity() {

    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityProjectDetailBinding

    private val featureAdapter = ItemsAdapter(emptyList())
    private val platformAdapter = ItemsAdapter(emptyList())
    private val talentAdapter = TalentAdapter(emptyList())
    private val toolsAdapter = ItemsAdapter(emptyList())
    private val productTypeAdapter = ItemsAdapter(emptyList())
    private val languageAdapter = ItemsAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtonAction()
        initRecyclerViews()
        getProjectDetail()
    }

    private fun setupButtonAction() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }
            cvProjectTimeline.setOnClickListener {
                val intent = Intent(this@ProjectDetailActivity, TimelineActivity::class.java).apply {
                    putExtra(TimelineActivity.PROJECT_ID, intent.getIntExtra(PROJECT_ID, 0))
                }
                startActivity(intent)
            }
        }
    }

    private fun initRecyclerViews() {
        fun RecyclerView.setupFlex(adapter: ItemsAdapter) {
            layoutManager = FlexboxLayoutManager(this@ProjectDetailActivity).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            this.adapter = adapter
        }

        binding.rvFeature.setupFlex(featureAdapter)
        binding.rvPlatform.setupFlex(platformAdapter)
        binding.rvTools.setupFlex(toolsAdapter)
        binding.rvProductType.setupFlex(productTypeAdapter)
        binding.rvLanguage.setupFlex(languageAdapter)

        binding.rvTalent.apply {
            layoutManager = LinearLayoutManager(this@ProjectDetailActivity,
                LinearLayoutManager.VERTICAL,
                false)
            adapter = talentAdapter
        }

        talentAdapter.setOnItemClickListener { talentString ->
            val id = talentString.substringBefore(":").toIntOrNull()
            if (id != null) {
                Intent(this, TalentDetailActivity::class.java).also {
                    it.putExtra("talent_id", id)
                    startActivity(it)
                }
            } else {
                Toast.makeText(this, "Invalid talent ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getProjectDetail() {
        val projectId = intent.getIntExtra(PROJECT_ID, 0)
        projectDetailViewModel.getProjectDetail(projectId)
        projectDetailViewModel.getProjectDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val project = result.data.projectDetail?.firstOrNull()
                    bindProject(project!!)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_portfolio_detail),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "ProjectDetailActivity",
                        "Error getting project detail: ${result.error}"
                    )
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

    private fun bindProject(project: ProjectDetailItem) {
        val formattedDeadline = formatDeadline(project.startDate.toString(),
            project.endDate.toString()
        )
        val deadline = formattedDeadline

        binding.apply {
            tvStatus.text = project.statusName.toString()
            tvEmail.text = project.clientEmail.toString()
            tvProjectClient.text = project.clientName.toString()
            tvProjectName.text = project.projectName
            tvProjectDescription.text = project.projectDesc
            tvProjectGithub.text = project.projectGithub ?: "-"
            tvProjectMeetLink.text = project.meetLink ?: "-"
            tvProjectPeriode.text = deadline
        }
        binding.apply {
            val rawFeatures = project.features ?: "-"
            val featuresList = rawFeatures
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            featureAdapter.updateData(featuresList)
        }
        binding.apply {
            val rawPlatforms = project.platforms ?: "-"
            val platformsList = rawPlatforms
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            platformAdapter.updateData(platformsList)
        }
        binding.apply {
            val rawTools = project.tools ?: "-"
            val toolsList = rawTools
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            toolsAdapter.updateData(toolsList)
        }
        binding.apply {
            val rawProductTypes = project.productTypes ?: "-"
            val productTypesList = rawProductTypes
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            productTypeAdapter.updateData(productTypesList)
        }
        binding.apply {
            val rawLanguages = project.languages ?: "-"
            val languagesList = rawLanguages
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            languageAdapter.updateData(languagesList)
        }
        project.talents?.let {
            val list = it.split("|")
                .map(String::trim)
                .filter(String::isNotEmpty)
            talentAdapter.updateData(list)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PROJECT_ID = "project_id"
    }
}