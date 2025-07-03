package com.example.talentara.view.ui.profile.talent

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.talentara.R
import com.example.talentara.data.model.response.portfolio.TalentPortfolioItem
import com.example.talentara.databinding.PortfolioItemBinding
import com.example.talentara.view.ui.portfolio.detail.PortfolioDetailActivity
import com.example.talentara.view.ui.project.detail.ProjectDetailActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.threeten.bp.LocalDate as LegacyLocalDate
import org.threeten.bp.format.DateTimeFormatter as LegacyDateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit as LegacyChronosUnit

class PortfolioAdapter(
    private var listPortfolio: List<TalentPortfolioItem>,
) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PortfolioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TalentPortfolioItem) {

            binding.root.setOnClickListener {
                //Get First Item of Product Type and Platform
                val firstProduct = item.productTypes?.split("|")?.firstOrNull() ?: "-"
                val firstPlatform = item.platforms?.split("|")?.firstOrNull() ?: "-"

                //Count Project Worked Days
                val daysWorked: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // API 26+
                    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val start = LocalDate.parse(item.startDate, fmt)
                    val completed = item.endDate?.let { LocalDate.parse(it, fmt) }
                    completed?.let { ChronoUnit.DAYS.between(start, it).toInt() } ?: 0
                } else {
                    // API <26
                    val fmt = LegacyDateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val start = LegacyLocalDate.parse(item.startDate, fmt)
                    val completed = item.endDate?.let { LegacyLocalDate.parse(it, fmt) }
                    completed?.let { LegacyChronosUnit.DAYS.between(start, it).toInt() } ?: 0
                }

                binding.tvProject.text = item.portfolioName.orEmpty()
                binding.tvClient.text = item.portfolioLinkedin.orEmpty()
                binding.tvProduct.text = itemView.context.getString(R.string.project_product, firstProduct, firstPlatform)
                binding.tvCompleted.text = itemView.context.getString(R.string.completed_in_d_days, daysWorked)
                if (item.portfolioLabel == "Portfolio") {
                    binding.ivPortfolioBadge.load(R.drawable.ic_portfolio_outside) {
                        placeholder(R.drawable.blank_avatar)
                        error(R.drawable.blank_avatar)
                    }
                } else if (item.portfolioLabel == "Talentara"){
                    binding.ivPortfolioBadge.load(R.drawable.ic_portfolio_inside) {
                        placeholder(R.drawable.blank_avatar)
                        error(R.drawable.blank_avatar)
                    }
                }
                val intent = Intent(itemView.context, ProjectDetailActivity::class.java).apply {
                    putExtra(PortfolioDetailActivity.PORTFOLIO_ID, item.portfolioId)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PortfolioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listPortfolio[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listPortfolio.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<TalentPortfolioItem>) {
        listPortfolio = newList
        notifyDataSetChanged()
    }
}