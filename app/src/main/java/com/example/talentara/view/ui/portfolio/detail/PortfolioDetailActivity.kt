package com.example.talentara.view.ui.portfolio.detail

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.talentara.R
import com.example.talentara.data.model.response.portfolio.PortfolioDetailItem
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityPortfolioDetailBinding
import com.example.talentara.databinding.CustomDeletePortfolioDialogBinding
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.text.SimpleDateFormat
import java.util.Locale

class PortfolioDetailActivity : AppCompatActivity() {

    private val portfolioDetailViewModel: PortfolioDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityPortfolioDetailBinding
    private lateinit var bindingDeleteDialog: CustomDeletePortfolioDialogBinding

    private val featureAdapter = ItemsAdapter(emptyList())
    private val platformAdapter = ItemsAdapter(emptyList())
    private val roleAdapter = ItemsAdapter(emptyList())
    private val toolsAdapter = ItemsAdapter(emptyList())
    private val productTypeAdapter = ItemsAdapter(emptyList())
    private val languageAdapter = ItemsAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPortfolioDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionButton()
        initRecyclerViews()
        getPortfolioDetail()
    }

    private fun setupActionButton() {
        with(binding) {
            cvBack.setOnClickListener {
                finish()
            }
            cvDeletePortfolio.setOnClickListener {
                showCustomDeleteDialog()
            }
        }
    }

    private fun showCustomDeleteDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        bindingDeleteDialog = CustomDeletePortfolioDialogBinding.inflate(layoutInflater)

        dialog.setContentView(bindingDeleteDialog.root)
        dialog.setCancelable(true)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val cardView = bindingDeleteDialog.root.findViewById<CardView>(R.id.DeleteCard)
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = (40 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(margin, 0, margin, 0)
        cardView.layoutParams = layoutParams

        bindingDeleteDialog.btYesDelete.setOnClickListener {
            dialog.dismiss()
            portfolioDetailViewModel.deletePortfolio(intent.getIntExtra(PORTFOLIO_ID, 0))
            deletePortfolioObserver()
        }
        bindingDeleteDialog.btCancelDelete.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deletePortfolioObserver() {
        portfolioDetailViewModel.portfolioDelete.observe(this@PortfolioDetailActivity) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@PortfolioDetailActivity,
                        "Portfolio Deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this@PortfolioDetailActivity,
                        getString(R.string.failed_to_delete_portfolio),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e(
                        "PortfolioDetailActivity",
                        "Error deleting portfolio: ${result.error}"
                    )
                }
            }
        }
    }

    private fun initRecyclerViews() {
        fun RecyclerView.setupFlex(adapter: ItemsAdapter) {
            layoutManager = FlexboxLayoutManager(this@PortfolioDetailActivity).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            this.adapter = adapter
        }

        binding.rvFeature.setupFlex(featureAdapter)
        binding.rvPlatform.setupFlex(platformAdapter)
        binding.rvRole.setupFlex(roleAdapter)
        binding.rvTools.setupFlex(toolsAdapter)
        binding.rvProductType.setupFlex(productTypeAdapter)
        binding.rvLanguage.setupFlex(languageAdapter)
    }


    private fun getPortfolioDetail() {
        val portfolioId = intent.getIntExtra(PORTFOLIO_ID, 0)
        portfolioDetailViewModel.getPortfolioDetail(portfolioId)
        portfolioDetailViewModel.portfolioDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {
                    showLoading(true)
                }

                is Results.Success -> {
                    showLoading(false)
                    val portfolio = result.data.portfolioDetail
                    bindPortfolio(portfolio!!)
                }

                is Results.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        getString(R.string.failed_to_get_portfolio_detail),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "PortfolioDetailActivity",
                        "Error getting portfolio detail: ${result.error}"
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

    private fun bindPortfolio(portfolio: PortfolioDetailItem) {
        val formattedDeadline = formatDeadline(portfolio.startDate.toString(),
            portfolio.endDate.toString()
        )
        val deadline = formattedDeadline

        binding.apply {
            tvStatus.text = portfolio.portfolioLabel
            tvProjectClient.text = portfolio.clientName
            tvProjectName.text = portfolio.portfolioName
            tvProjectDescription.text = portfolio.portfolioDesc
            tvProjectGithub.text = portfolio.portfolioGithub
            tvProjectPeriode.text = deadline
        }
        binding.apply {
            val rawFeatures = portfolio.features ?: ""
            val featuresList = rawFeatures
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            featureAdapter.updateData(featuresList)
        }
        binding.apply {
            val rawPlatforms = portfolio.platforms ?: ""
            val platformsList = rawPlatforms
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            platformAdapter.updateData(platformsList)
        }
        binding.apply {
            val rawRoles = portfolio.roles ?: ""
            val rolesList = rawRoles
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            roleAdapter.updateData(rolesList)
        }
        binding.apply {
            val rawTools = portfolio.tools ?: ""
            val toolsList = rawTools
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            toolsAdapter.updateData(toolsList)
        }
        binding.apply {
            val rawProductTypes = portfolio.productTypes ?: ""
            val productTypesList = rawProductTypes
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            productTypeAdapter.updateData(productTypesList)
        }
        binding.apply {
            val rawLanguages = portfolio.languages ?: ""
            val languagesList = rawLanguages
                .split("|")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            languageAdapter.updateData(languagesList)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val PORTFOLIO_ID = "portfolio_id"
    }
}