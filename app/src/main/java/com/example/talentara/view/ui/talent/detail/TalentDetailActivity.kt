package com.example.talentara.view.ui.talent.detail

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
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.response.talent.TalentDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityTalentDetailBinding
import com.example.talentara.view.ui.portfolio.detail.ItemsAdapter
import com.example.talentara.view.ui.profile.talent.PortfolioAdapter
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class TalentDetailActivity : AppCompatActivity() {

    private val talentDetailViewModel: TalentDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityTalentDetailBinding

    private val platformAdapter = ItemsAdapter(emptyList())
    private val roleAdapter = ItemsAdapter(emptyList())
    private val toolsAdapter = ItemsAdapter(emptyList())
    private val productTypeAdapter = ItemsAdapter(emptyList())
    private val languageAdapter = ItemsAdapter(emptyList())
    private val portfolioAdapter = PortfolioAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTalentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerViews()
        setupActionButton()
        getTalentDetail()
        getTalentPortfolio()
    }

    private fun initRecyclerViews() {
        fun RecyclerView.setupFlex(adapter: ItemsAdapter) {
            layoutManager = FlexboxLayoutManager(this@TalentDetailActivity).apply {
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
        binding.rvPortfolio.apply {
            layoutManager = LinearLayoutManager(this@TalentDetailActivity,
                RecyclerView.VERTICAL,
                false)
            adapter = portfolioAdapter
        }
    }

    private fun getTalentPortfolio() {
        val talentId = intent.getIntExtra(TALENT_ID, 0)
        talentDetailViewModel.getTalentPortfolio(talentId)
        talentDetailViewModel.getTalentPortfolio.observe(this) { res ->
            when (res) {
                is Results.Loading -> { }
                is Results.Success -> {
                    val items = res.data.talentPortfolio
                        .orEmpty()
                        .mapNotNull { it }
                    portfolioAdapter.updateData(items)
                }
                is Results.Error   -> {
                    Toast.makeText(this,
                        getString(R.string.failed_to_load_portfolio), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTalentDetail() {
        val talentId = intent.getIntExtra(TALENT_ID, 0)
        talentDetailViewModel.getTalentDetail(talentId)
        talentDetailViewModel.getTalentDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val talent = result.data.talentDetail?.firstOrNull()
                    binding.tvTalentName.text = talent?.userName
                    binding.tvLinkedin.text = talent?.linkedin
                    binding.tvGithub.text = talent?.github
                    binding.tvGmail.text = talent?.userEmail
                    binding.tvProjectCount.text = talent?.projectDone.toString()
                    binding.ivTalentImage.load(talent?.userImage) {
                        placeholder(R.drawable.blank_avatar)
                        error(R.drawable.blank_avatar)
                    }
                    bindTalent(talent!!)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_talent_information), Toast.LENGTH_SHORT
                    ).show()
                    Log.e("TalentDetailActivity", "Error: ${result.error}")
                }
            }
        }
    }

    private fun bindTalent(talent: TalentDetailItem) {
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

    private fun setupActionButton() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TALENT_ID = "talent_id"
    }
}